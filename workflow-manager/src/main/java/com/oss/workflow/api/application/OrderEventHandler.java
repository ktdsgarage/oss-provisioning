package com.oss.workflow.api.application;

import com.oss.common.dto.WorkflowEventDTO;
import com.oss.common.event.WorkflowEvent;
import com.oss.workflow.api.application.dto.NewOrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventHandler {

    private final WorkflowService workflowService;

    @KafkaListener(topics = "internet.request", groupId = "workflow-group")
    public void handleInternetOrder(WorkflowEventDTO event) {
        NewOrderRequest request = new NewOrderRequest();
        request.setOrderId(event.getOrderId());
        request.setOrderType("INTERNET");
        request.setProductCode(event.getProductCode());
        request.setCustomerId(event.getCustomerId());

        workflowService.createOrder(request);
    }

    @KafkaListener(topics = "iptv.request", groupId = "workflow-group")
    public void handleIPTVOrder(WorkflowEventDTO event) {
        NewOrderRequest request = new NewOrderRequest();
        request.setOrderId(event.getOrderId());
        request.setOrderType("IPTV");
        request.setProductCode(event.getProductCode());
        request.setCustomerId(event.getCustomerId());

        workflowService.createOrder(request);
    }
}