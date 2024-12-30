// internet-service/src/main/java/com/oss/internet/api/application/InternetService.java
package com.oss.internet.api.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oss.common.agent.TaskResult;
import com.oss.common.dto.ErrorEvent;
import com.oss.common.event.WorkflowEvent;
import com.oss.internet.api.application.dto.TaskParameters;
import com.oss.internet.api.domain.*;
import com.oss.internet.api.infrastructure.*;
import com.oss.internet.api.domain.Device;
import com.oss.internet.enums.DeviceStatus;
import com.oss.internet.enums.FacilityStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InternetService {

    private final FacilityRepository facilityRepository;
    private final DeviceRepository deviceRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public Object processFacility(TaskParameters params) {
        Facility facility = new Facility();
        facility.setOrderId(params.getOrderId());
        facility.setType(params.getFacilityType());
        facility.setStatus(FacilityStatus.PROCESSING);

        facilityRepository.save(facility);
        kafkaTemplate.send("internet.facility", facility);

        return facility;
    }

    @Transactional
    public Object processDevice(TaskParameters params) {
        Device device = new Device();
        device.setOrderId(params.getOrderId());
        device.setDeviceId(params.getDeviceId());
        device.setStatus(DeviceStatus.CONFIGURING);

        deviceRepository.save(device);
        kafkaTemplate.send("internet.device", device);

        return device;
    }

    @Transactional
    public Object updateFacilityBook(TaskParameters params) {
        kafkaTemplate.send("internet.facilitybook", params);
        return params;
    }

    @Transactional
    public Object processTask(String taskId, Object parameters) {
        TaskParameters taskParams = convertToTaskParameters(parameters);

        switch (taskParams.getTaskType()) {
            case FACILITY_ALLOCATION:
                return processFacility(taskParams);
            case DEVICE_CONFIG:
                return processDevice(taskParams);
            case FACILITY_BOOK:
                return updateFacilityBook(taskParams);
            default:
                throw new IllegalArgumentException("Unsupported task type: " + taskParams.getTaskType());
        }
    }

    @Transactional
    public void cancelTask(String taskId) {
        // 취소 로직 구현
    }

    @Transactional(readOnly = true)
    public TaskResult getTaskStatus(String taskId) {
        return TaskResult.builder()
                .taskId(taskId)
                .success(true)
                .message("Task is processing")
                .build();
    }

    private TaskParameters convertToTaskParameters(Object parameters) {
        try {
            if (parameters instanceof TaskParameters) {
                return (TaskParameters) parameters;
            }
            return objectMapper.convertValue(parameters, TaskParameters.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert parameters", e);
        }
    }

    /**
     * 워크플로우 이벤트를 발행합니다.
     * @param topic 이벤트를 발행할 Kafka 토픽
     * @param event 발행할 워크플로우 이벤트
     */
    public void publishEvent(String topic, WorkflowEvent event) {
        try {
            log.debug("Publishing event to topic {}: {}", topic, event);

            // 토픽이 null이거나 비어있는지 확인
            if (topic == null || topic.trim().isEmpty()) {
                throw new IllegalArgumentException("Topic cannot be null or empty");
            }

            // 이벤트가 null인지 확인
            if (event == null) {
                throw new IllegalArgumentException("Event cannot be null");
            }

            // 이벤트 유효성 검증
            validateEvent(event);

            // 이벤트 발행 - CompletableFuture를 직접 사용
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

    /**
     * 워크플로우 이벤트의 유효성을 검증합니다.
     * @param event 검증할 워크플로우 이벤트
     */
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

    @Transactional
    public void handleError(String workflowId, String message, Exception e) {
        log.error("Error in workflow {}: {} - {}", workflowId, message, e.getMessage(), e);

        // 에러 정보 생성
        ErrorEvent errorEvent = ErrorEvent.builder()
                .workflowId(workflowId)
                .errorType(e.getClass().getSimpleName())
                .errorMessage(e.getMessage())
                .errorDetail(getStackTrace(e))
                .occurredAt(LocalDateTime.now())
                .build();

        try {
            // 에러 이벤트 발행
            WorkflowEvent event = WorkflowEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .workflowId(workflowId)
                    .eventType("WORKFLOW_ERROR")
                    .timestamp(LocalDateTime.now())
                    .payload(objectMapper.writeValueAsString(errorEvent))
                    .build();

            kafkaTemplate.send("internet.workflow.error", event);

        } catch (JsonProcessingException jsonEx) {
            log.error("Failed to serialize error event", jsonEx);
        }
    }

    // 스택트레이스를 문자열로 변환하는 유틸리티 메소드
    private String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
