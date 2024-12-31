// IPTVService.java
package com.oss.iptv.api.application;

import com.oss.common.agent.TaskResult;
import com.oss.common.event.WorkflowEvent;
import com.oss.iptv.api.application.dto.AuthRequest;
import com.oss.common.dto.ErrorEvent;
import com.oss.common.constants.KafkaConstants;
import com.oss.iptv.api.application.dto.ChannelConfigRequest;
import com.oss.iptv.api.application.dto.FacilityBookRequest;
import com.oss.iptv.api.domain.Auth;
import com.oss.iptv.api.domain.ChannelConfig;
import com.oss.iptv.api.domain.IPTVFacilityBook;
import com.oss.iptv.enums.AuthStatus;
import com.oss.iptv.enums.BookStatus;
import com.oss.iptv.enums.ConfigStatus;
import com.oss.iptv.api.infrastructure.AuthRepository;
import com.oss.iptv.api.infrastructure.ChannelConfigRepository;
import com.oss.iptv.api.infrastructure.IPTVFacilityBookRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

@Slf4j
@Service
@RequiredArgsConstructor
public class IPTVService {

    private final AuthRepository authRepository;
    private final ChannelConfigRepository channelConfigRepository;
    private final IPTVFacilityBookRepository iptvFacilityBookRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public Auth processTask(String taskId, Object parameters) {
        if (parameters instanceof AuthRequest) {
            return processAuth((AuthRequest) parameters);
        } else if (parameters instanceof ChannelConfigRequest) {
            processChannelConfig((ChannelConfigRequest) parameters);
            return null;
        } else if (parameters instanceof FacilityBookRequest) {
            updateFacilityBook((FacilityBookRequest) parameters);
            return null;
        } else {
            throw new IllegalArgumentException("Unsupported parameters type");
        }
    }

    @Transactional
    public Auth processAuth(AuthRequest request) {
        Auth auth = new Auth();
        auth.setAuthId(UUID.randomUUID().toString());
        auth.setOrderId(request.getOrderId());
        auth.setStatus(AuthStatus.PROCESSING);

        auth = authRepository.save(auth);
        kafkaTemplate.send(KafkaConstants.TOPIC_IPTV_AUTH_COMPLETED, auth);

        return auth;
    }

    @Transactional
    public ChannelConfig processChannelConfig(ChannelConfigRequest request) {
        Auth auth = authRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Auth not found"));

        ChannelConfig config = new ChannelConfig();
        config.setConfigId(UUID.randomUUID().toString());
        config.setOrderId(request.getOrderId());
        config.setAuth(auth);
        config.setChannelList(request.getChannelConfig());
        config.setStatus(ConfigStatus.CONFIGURING);

        config = channelConfigRepository.save(config);
        kafkaTemplate.send(KafkaConstants.TOPIC_IPTV_CHANNEL_CONFIGURED, config);

        return config;
    }

    @Transactional
    public IPTVFacilityBook updateFacilityBook(FacilityBookRequest request) {
        Auth auth = authRepository.findById(request.getAuthId())
                .orElseThrow(() -> new RuntimeException("Auth not found"));

        ChannelConfig config = channelConfigRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Channel config not found"));

        IPTVFacilityBook facilityBook = new IPTVFacilityBook();
        facilityBook.setBookId(UUID.randomUUID().toString());
        facilityBook.setOrderId(request.getOrderId());
        facilityBook.setAuth(auth);
        facilityBook.setChannelConfig(config);
        facilityBook.setStatus(BookStatus.valueOf(request.getStatus()));
        facilityBook = iptvFacilityBookRepository.save(facilityBook);
        kafkaTemplate.send(KafkaConstants.TOPIC_IPTV_FACILITYBOOK_UPDATED, facilityBook);

        return facilityBook;
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
                    .eventType(KafkaConstants.EVENT_WORKFLOW_ERROR)
                    .timestamp(LocalDateTime.now())
                    .payload(objectMapper.writeValueAsString(errorEvent))
                    .build();

            kafkaTemplate.send(KafkaConstants.TOPIC_IPTV_WORKFLOW_ERROR, event);

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
}
