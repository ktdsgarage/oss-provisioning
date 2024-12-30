// iptv-service/src/main/java/com/oss/iptv/api/application/IPTVService.java

package com.oss.iptv.api.application;

import com.oss.common.agent.TaskResult;
import com.oss.common.event.WorkflowEvent;
import com.oss.iptv.api.application.dto.AuthRequest;
import com.oss.common.dto.ErrorEvent;
import com.oss.iptv.api.application.dto.FacilityBookRequest;
import com.oss.iptv.api.domain.Auth;
import com.oss.iptv.api.domain.AuthStatus;
import com.oss.iptv.api.infrastructure.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class IPTVService {

    private final AuthRepository authRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public Auth processTask(String taskId, Object parameters) {
        // 파라미터 타입에 따라 적절한 처리 수행
        if (parameters instanceof AuthRequest) {
            return processAuth((AuthRequest) parameters);
        } else if (parameters instanceof FacilityBookRequest) {
            return updateFacilityBook((FacilityBookRequest) parameters);
        } else {
            throw new IllegalArgumentException("Unsupported parameters type");
        }
    }

    @Transactional
    public void cancelTask(String taskId) {
        // 태스크 취소 로직
        Auth auth = authRepository.findByTaskId(taskId)
                .orElseThrow(() -> new RuntimeException("Auth not found"));
        auth.setStatus(AuthStatus.CANCELLED);
        authRepository.save(auth);

        // 취소 이벤트 발행
        kafkaTemplate.send("iptv.auth.cancelled", auth);
    }

    @Transactional(readOnly = true)
    public TaskResult getTaskStatus(String taskId) {
        Auth auth = authRepository.findByTaskId(taskId)
                .orElseThrow(() -> new RuntimeException("Auth not found"));

        return TaskResult.builder()
                .taskId(taskId)
                .success(auth.getStatus() == AuthStatus.AUTHORIZED)
                .message(auth.getStatus().name())
                .result(auth)
                .build();
    }

    @Transactional
    public Auth processAuth(AuthRequest request) {
        Auth auth = new Auth();
        auth.setOrderId(request.getOrderId());
        auth.setStatus(AuthStatus.PROCESSING);

        auth = authRepository.save(auth);  // 저장된 Auth 반환
        kafkaTemplate.send("iptv.auth", auth);

        return auth;  // Auth 반환
    }

    @Transactional
    public Auth updateFacilityBook(FacilityBookRequest request) {
        // 원장을 업데이트하기 위한 인증 정보 조회
        Auth auth = authRepository.findById(request.getAuthId())
                .orElseThrow(() -> new RuntimeException("Auth not found"));

        // facility book 업데이트 로직
        kafkaTemplate.send("iptv.facilitybook", request);

        return auth;  // Auth 반환
    }

    @Transactional
    public void handleError(String workflowId, String message, Exception e) {
        log.error("Error in workflow {}: {} - {}", workflowId, message, e.getMessage(), e);

        ErrorEvent errorEvent = ErrorEvent.builder()
                .workflowId(workflowId)
                .errorType(e.getClass().getSimpleName())
                .errorMessage(e.getMessage())
                .errorDetail(getStackTrace(e))
                .occurredAt(LocalDateTime.now())
                .build();

        try {
            WorkflowEvent event = WorkflowEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .workflowId(workflowId)
                    .eventType("WORKFLOW_ERROR")
                    .timestamp(LocalDateTime.now())
                    .payload(objectMapper.writeValueAsString(errorEvent))
                    .build();

            kafkaTemplate.send("iptv.workflow.error", event);

        } catch (JsonProcessingException jsonEx) {
            log.error("Failed to serialize error event", jsonEx);
        }
    }

    public void publishEvent(String topic, WorkflowEvent event) {
        try {
            log.debug("Publishing event to topic {}: {}", topic, event);

            if (topic == null || topic.trim().isEmpty()) {
                throw new IllegalArgumentException("Topic cannot be null or empty");
            }

            if (event == null) {
                throw new IllegalArgumentException("Event cannot be null");
            }

            validateEvent(event);

            kafkaTemplate.send(topic, event.getWorkflowId(), event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.debug("Successfully published event: {}", result.getProducerRecord().key());
                        } else {
                            String errorMsg = String.format("Failed to publish event: %s", event);
                            log.error(errorMsg, ex);
                            handleError(event.getWorkflowId(), errorMsg, (Exception)ex);
                        }
                    });
        } catch (Exception e) {
            String errorMsg = "Error publishing event";
            log.error(errorMsg + ": {}", e.getMessage(), e);
            handleError(event.getWorkflowId(), errorMsg, e);
            throw new RuntimeException("Failed to publish event", e);
        }
    }

    private void validateEvent(WorkflowEvent event) {
        if (event.getEventId() == null || event.getEventId().trim().isEmpty()) {
            throw new IllegalArgumentException("Event ID cannot be null or empty");
        }

        if (event.getWorkflowId() == null || event.getWorkflowId().trim().isEmpty()) {
            throw new IllegalArgumentException("Workflow ID cannot be null or empty");
        }

        if (event.getEventType() == null || event.getEventType().trim().isEmpty()) {
            throw new IllegalArgumentException("Event type cannot be null or empty");
        }

        if (event.getTimestamp() == null) {
            throw new IllegalArgumentException("Timestamp cannot be null");
        }
    }

    private String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
