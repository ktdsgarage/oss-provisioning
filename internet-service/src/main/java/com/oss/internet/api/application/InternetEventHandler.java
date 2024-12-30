package com.oss.internet.api.application;

import com.oss.internet.enums.TaskType;
import com.oss.common.event.WorkflowEvent;
import com.oss.internet.api.application.dto.TaskParameters;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InternetEventHandler {

    private final InternetService internetService;

    @KafkaListener(topics = "internet.workflow.created")
    public void handleWorkflowCreated(WorkflowEvent event) {
        try {
            // 워크플로우 생성 시 최초 시설 처리 시작
            TaskParameters taskParams = TaskParameters.builder()
                    .taskId(event.getWorkflowId())
                    .taskType(TaskType.FACILITY_ALLOCATION)
                    .orderId(event.getOrderId())
                    .build();

            internetService.processTask(event.getWorkflowId(), taskParams);
        } catch (Exception e) {
            // 에러 이벤트 발행
            internetService.handleError(event.getWorkflowId(), "Failed to process workflow", e);
        }
    }

    @KafkaListener(topics = "internet.facility.allocated")
    public void handleFacilityAllocated(WorkflowEvent event) {
        try {
            // 시설 할당 완료 후 장비 설정 시작
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

    @KafkaListener(topics = "internet.device.configured")
    public void handleDeviceConfigured(WorkflowEvent event) {
        try {
            // 장비 설정 완료 후 원부 처리 시작
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

    @KafkaListener(topics = "internet.facilitybook.updated")
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

            internetService.publishEvent("workflow.completed", completedEvent);
        } catch (Exception e) {
            internetService.handleError(event.getWorkflowId(), "Failed to handle facility book update", e);
        }
    }
}