package com.oss.internet.api.application;

import com.oss.internet.enums.TaskType;
import com.oss.common.event.WorkflowEvent;
import com.oss.internet.api.application.dto.TaskParameters;
import com.oss.common.constants.KafkaConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InternetEventHandler {

    private final InternetService internetService;

    @KafkaListener(topics = KafkaConstants.TOPIC_INTERNET_WORKFLOW_CREATED, groupId = KafkaConstants.GROUP_INTERNET)
    public void handleWorkflowCreated(WorkflowEvent event) {
        try {
            TaskParameters taskParams = TaskParameters.builder()
                    .taskId(event.getWorkflowId())
                    .taskType(TaskType.FACILITY_ALLOCATION)
                    .orderId(event.getOrderId())
                    .build();

            internetService.processTask(event.getWorkflowId(), taskParams);
        } catch (Exception e) {
            internetService.handleError(event.getWorkflowId(), "Failed to process workflow", e);
        }
    }

    @KafkaListener(topics = KafkaConstants.TOPIC_INTERNET_FACILITY_ALLOCATED, groupId = KafkaConstants.GROUP_INTERNET)
    public void handleFacilityAllocated(WorkflowEvent event) {
        try {
            TaskParameters taskParams = TaskParameters.builder()
                    .taskId(event.getWorkflowId())
                    .taskType(TaskType.DEVICE_CONFIG)
                    .orderId(event.getOrderId())
                    .build();

            internetService.processTask(event.getWorkflowId(), taskParams);
        } catch (Exception e) {
            internetService.handleError(event.getWorkflowId(), "Failed to handle facility allocation", e);
        }
    }

    @KafkaListener(topics = KafkaConstants.TOPIC_INTERNET_DEVICE_CONFIGURED, groupId = KafkaConstants.GROUP_INTERNET)
    public void handleDeviceConfigured(WorkflowEvent event) {
        try {
            TaskParameters taskParams = TaskParameters.builder()
                    .taskId(event.getWorkflowId())
                    .taskType(TaskType.FACILITY_BOOK)
                    .orderId(event.getOrderId())
                    .build();

            internetService.processTask(event.getWorkflowId(), taskParams);
        } catch (Exception e) {
            internetService.handleError(event.getWorkflowId(), "Failed to handle device configuration", e);
        }
    }

    @KafkaListener(topics = KafkaConstants.TOPIC_INTERNET_FACILITYBOOK_UPDATED, groupId = KafkaConstants.GROUP_INTERNET)
    public void handleFacilityBookUpdated(WorkflowEvent event) {
        try {
            WorkflowEvent completedEvent = WorkflowEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .workflowId(event.getWorkflowId())
                    .orderId(event.getOrderId())
                    .eventType(KafkaConstants.EVENT_WORKFLOW_COMPLETED)
                    .timestamp(LocalDateTime.now())
                    .build();

            internetService.publishEvent(KafkaConstants.TOPIC_WORKFLOW_COMPLETED, completedEvent);
        } catch (Exception e) {
            internetService.handleError(event.getWorkflowId(), "Failed to handle facility book update", e);
        }
    }
}
