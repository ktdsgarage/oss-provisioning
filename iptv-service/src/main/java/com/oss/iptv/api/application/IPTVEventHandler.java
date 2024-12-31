// IPTVEventHandler.java
package com.oss.iptv.api.application;

import com.oss.common.event.WorkflowEvent;
import com.oss.iptv.api.domain.TaskParameters;
import com.oss.common.constants.KafkaConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.oss.iptv.enums.TaskType;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class IPTVEventHandler {
    private final IPTVService iptvService;

    @KafkaListener(topics = KafkaConstants.TOPIC_IPTV_WORKFLOW_CREATED, groupId = KafkaConstants.GROUP_IPTV)
    public void handleWorkflowCreated(WorkflowEvent event) {
        try {
            TaskParameters taskParams = TaskParameters.builder()
                    .taskId(event.getWorkflowId())
                    .taskType(TaskType.AUTHENTICATION)
                    .orderId(event.getOrderId())
                    .build();

            iptvService.processTask(event.getWorkflowId(), taskParams);
        } catch (Exception e) {
            iptvService.handleError(event.getWorkflowId(), "Failed to process workflow", e);
        }
    }

    @KafkaListener(topics = KafkaConstants.TOPIC_IPTV_AUTH_COMPLETED, groupId = KafkaConstants.GROUP_IPTV)
    public void handleAuthCompleted(WorkflowEvent event) {
        try {
            TaskParameters taskParams = TaskParameters.builder()
                    .taskId(event.getWorkflowId())
                    .taskType(TaskType.DEVICE_CONFIG)
                    .orderId(event.getOrderId())
                    .build();

            iptvService.processTask(event.getWorkflowId(), taskParams);

            WorkflowEvent authCompletedEvent = WorkflowEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .workflowId(event.getWorkflowId())
                    .orderId(event.getOrderId())
                    .eventType(KafkaConstants.EVENT_AUTH_COMPLETED)
                    .timestamp(LocalDateTime.now())
                    .build();

            iptvService.publishEvent(KafkaConstants.TOPIC_IPTV_AUTH_SUCCESS, authCompletedEvent);
        } catch (Exception e) {
            iptvService.handleError(event.getWorkflowId(), "Failed to handle auth completion", e);
        }
    }

    @KafkaListener(topics = KafkaConstants.TOPIC_IPTV_CHANNEL_CONFIGURED, groupId = KafkaConstants.GROUP_IPTV)
    public void handleChannelConfigured(WorkflowEvent event) {
        try {
            TaskParameters taskParams = TaskParameters.builder()
                    .taskId(event.getWorkflowId())
                    .taskType(TaskType.FACILITY_BOOK)
                    .orderId(event.getOrderId())
                    .build();

            iptvService.processTask(event.getWorkflowId(), taskParams);

            WorkflowEvent channelConfiguredEvent = WorkflowEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .workflowId(event.getWorkflowId())
                    .orderId(event.getOrderId())
                    .eventType(KafkaConstants.EVENT_CHANNEL_CONFIGURED)
                    .timestamp(LocalDateTime.now())
                    .build();

            iptvService.publishEvent(KafkaConstants.TOPIC_IPTV_CHANNEL_SUCCESS, channelConfiguredEvent);
        } catch (Exception e) {
            iptvService.handleError(event.getWorkflowId(), "Failed to handle channel configuration", e);
        }
    }

    @KafkaListener(topics = KafkaConstants.TOPIC_IPTV_FACILITYBOOK_UPDATED, groupId = KafkaConstants.GROUP_IPTV)
    public void handleFacilityBookUpdated(WorkflowEvent event) {
        try {
            WorkflowEvent completedEvent = WorkflowEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .workflowId(event.getWorkflowId())
                    .orderId(event.getOrderId())
                    .eventType(KafkaConstants.EVENT_WORKFLOW_COMPLETED)
                    .timestamp(LocalDateTime.now())
                    .build();

            iptvService.publishEvent(KafkaConstants.TOPIC_WORKFLOW_COMPLETED, completedEvent);

            WorkflowEvent iptvCompletedEvent = WorkflowEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .workflowId(event.getWorkflowId())
                    .orderId(event.getOrderId())
                    .eventType(KafkaConstants.EVENT_IPTV_SERVICE_COMPLETED)
                    .timestamp(LocalDateTime.now())
                    .build();

            iptvService.publishEvent(KafkaConstants.TOPIC_IPTV_SERVICE_COMPLETED, iptvCompletedEvent);
        } catch (Exception e) {
            iptvService.handleError(event.getWorkflowId(), "Failed to handle facility book update", e);
        }
    }
}