package com.oss.workflow.api.application;

import com.oss.common.event.WorkflowEvent;
import com.oss.workflow.api.application.dto.NewOrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventHandler {

    private final WorkflowService workflowService;

    @KafkaListener(topics = "internet.request")
    public void handleInternetOrder(WorkflowEvent event) {
        NewOrderRequest request = new NewOrderRequest();
        request.setOrderType("INTERNET");
        request.setProductCode(event.getPayload());
        request.setCustomerId(event.getWorkflowId());

        workflowService.createOrder(request);
    }

    @KafkaListener(topics = "iptv.request")
    public void handleIPTVOrder(WorkflowEvent event) {
        NewOrderRequest request = new NewOrderRequest();
        request.setOrderType("IPTV");
        request.setProductCode(event.getPayload());
        request.setCustomerId(event.getWorkflowId());

        workflowService.createOrder(request);
    }
}