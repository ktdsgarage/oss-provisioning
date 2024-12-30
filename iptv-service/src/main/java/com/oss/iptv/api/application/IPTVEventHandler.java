package com.oss.iptv.api.application;

import com.oss.common.event.WorkflowEvent;
import com.oss.iptv.api.domain.TaskParameters;
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

    @KafkaListener(topics = "iptv.workflow.created")
    public void handleWorkflowCreated(WorkflowEvent event) {
        try {
            // 워크플로우 생성 시 최초 인증 처리 시작
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

    @KafkaListener(topics = "iptv.auth.completed")
    public void handleAuthCompleted(WorkflowEvent event) {
        try {
            // 인증 완료 후 채널 설정 시작
            TaskParameters taskParams = TaskParameters.builder()
                    .taskId(event.getWorkflowId())
                    .taskType(TaskType.DEVICE_CONFIG)
                    .orderId(event.getOrderId())
                    .build();

            iptvService.processTask(event.getWorkflowId(), taskParams);

            // 인증 완료 이벤트 발행
            WorkflowEvent authCompletedEvent = WorkflowEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .workflowId(event.getWorkflowId())
                    .orderId(event.getOrderId())
                    .eventType("AUTH_COMPLETED")
                    .timestamp(LocalDateTime.now())
                    .build();

            iptvService.publishEvent("iptv.auth.success", authCompletedEvent);
        } catch (Exception e) {
            iptvService.handleError(event.getWorkflowId(), "Failed to handle auth completion", e);
        }
    }

    @KafkaListener(topics = "iptv.channel.configured")
    public void handleChannelConfigured(WorkflowEvent event) {
        try {
            // 채널 설정 완료 후 원부 처리 시작
            TaskParameters taskParams = TaskParameters.builder()
                    .taskId(event.getWorkflowId())
                    .taskType(TaskType.FACILITY_BOOK)
                    .orderId(event.getOrderId())
                    .build();

            iptvService.processTask(event.getWorkflowId(), taskParams);

            // 채널 설정 완료 이벤트 발행
            WorkflowEvent channelConfiguredEvent = WorkflowEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .workflowId(event.getWorkflowId())
                    .orderId(event.getOrderId())
                    .eventType("CHANNEL_CONFIGURED")
                    .timestamp(LocalDateTime.now())
                    .build();

            iptvService.publishEvent("iptv.channel.success", channelConfiguredEvent);
        } catch (Exception e) {
            iptvService.handleError(event.getWorkflowId(), "Failed to handle channel configuration", e);
        }
    }

    @KafkaListener(topics = "iptv.facilitybook.updated")
    public void handleFacilityBookUpdated(WorkflowEvent event) {
        try {
            // 원부 처리 완료 - 워크플로우 완료 이벤트 발행
            WorkflowEvent completedEvent = WorkflowEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .workflowId(event.getWorkflowId())
                    .orderId(event.getOrderId())
                    .eventType("WORKFLOW_COMPLETED")
                    .timestamp(LocalDateTime.now())
                    .build();

            iptvService.publishEvent("workflow.completed", completedEvent);

            // IPTV 서비스 완료 이벤트도 발행
            WorkflowEvent iptvCompletedEvent = WorkflowEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .workflowId(event.getWorkflowId())
                    .orderId(event.getOrderId())
                    .eventType("IPTV_SERVICE_COMPLETED")
                    .timestamp(LocalDateTime.now())
                    .build();

            iptvService.publishEvent("iptv.service.completed", iptvCompletedEvent);
        } catch (Exception e) {
            iptvService.handleError(event.getWorkflowId(), "Failed to handle facility book update", e);
        }
    }

}
