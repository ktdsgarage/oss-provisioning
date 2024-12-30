// kos-integration/src/main/java/com/oss/kos/api/application/OrderEventHandler.java
package com.oss.kos.api.application;

import com.oss.common.event.WorkflowEvent;
import com.oss.kos.api.domain.OrderStatus;
import com.oss.kos.api.application.dto.CompletionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderEventHandler {

    private final OrderService orderService;

    @KafkaListener(topics = "order.provisioned")
    public void handleOrderProvisioned(WorkflowEvent event) {
        try {
            // 개통 완료 이벤트 생성
            orderService.completeOrder(event.getOrderId(), CompletionRequest.builder()
                    .orderId(event.getOrderId())
                    .completionStatus(OrderStatus.COMPLETED.name())
                    .completionDate(event.getTimestamp().toString())
                    .build());
        } catch (Exception e) {
            orderService.handleError(event.getWorkflowId(), "Failed to handle order provisioned", e);
        }
    }

    @KafkaListener(topics = "order.completed")
    public void handleOrderCompleted(WorkflowEvent event) {
        try {
            // KOS에 최종 완료 통보
            orderService.publishEvent("workflow.completed", WorkflowEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .workflowId(event.getWorkflowId())
                    .orderId(event.getOrderId())
                    .eventType("WORKFLOW_COMPLETED")
                    .timestamp(LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            orderService.handleError(event.getWorkflowId(), "Failed to handle order completion", e);
        }
    }
}