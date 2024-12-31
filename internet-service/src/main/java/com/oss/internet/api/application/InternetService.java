// InternetService.java
package com.oss.internet.api.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oss.common.agent.TaskResult;
import com.oss.common.dto.ErrorEvent;
import com.oss.common.event.WorkflowEvent;
import com.oss.common.constants.KafkaConstants;
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
        kafkaTemplate.send(KafkaConstants.TOPIC_INTERNET_FACILITY_ALLOCATED, facility);

        return facility;
    }

    @Transactional
    public Object processDevice(TaskParameters params) {
        Device device = new Device();
        device.setOrderId(params.getOrderId());
        device.setDeviceId(params.getDeviceId());
        device.setStatus(DeviceStatus.CONFIGURING);

        deviceRepository.save(device);
        kafkaTemplate.send(KafkaConstants.TOPIC_INTERNET_DEVICE_CONFIGURED, device);

        return device;
    }

    @Transactional
    public Object updateFacilityBook(TaskParameters params) {
        kafkaTemplate.send(KafkaConstants.TOPIC_INTERNET_FACILITYBOOK_UPDATED, params);
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

            kafkaTemplate.send(KafkaConstants.TOPIC_INTERNET_WORKFLOW_ERROR, event);

        } catch (JsonProcessingException jsonEx) {
            log.error("Failed to serialize error event", jsonEx);
        }
    }

    private String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}