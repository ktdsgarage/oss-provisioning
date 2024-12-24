package com.oss.kos.api.application;

import com.oss.common.event.WorkflowEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventHandlerImpl implements OrderEventHandler {
    
    private final OrderService orderService;

    @KafkaListener(topics = "order.provisioned")
    @Override
    public void handleOrderProvisioned(WorkflowEvent event) {
        // Handle order provisioned event
    }

    @KafkaListener(topics = "order.completed")
    @Override
    public void handleOrderCompleted(WorkflowEvent event) {
        // Handle order completion event
    }
}
