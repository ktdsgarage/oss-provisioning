package com.oss.workflow.api.application;

import com.oss.common.dto.WorkflowEventDTO;
import com.oss.common.event.WorkflowEvent;
import com.oss.workflow.api.domain.WorkflowStatus;
import com.oss.workflow.api.application.dto.NewOrderRequest;
import com.oss.common.constants.KafkaConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventHandler {

    private final WorkflowService workflowService;

    // 신규 요청 이벤트 처리
    @KafkaListener(topics = "internet.request", groupId = KafkaConstants.GROUP_WORKFLOW)
    public void handleInternetOrder(WorkflowEventDTO event) {
        log.debug("Received internet order event: {}", event);
        try {
            validateEvent(event);
            NewOrderRequest request = createOrderRequest(event, "INTERNET");
            workflowService.createOrder(request);
            log.info("Successfully processed internet order: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Failed to process internet order: {}", event.getOrderId(), e);
            workflowService.handleError(event.getOrderId(), "Failed to handle internet order", e);
        }
    }

    @KafkaListener(topics = "iptv.request", groupId = KafkaConstants.GROUP_WORKFLOW)
    public void handleIPTVOrder(WorkflowEventDTO event) {
        log.debug("Received IPTV order event: {}", event);
        try {
            validateEvent(event);
            NewOrderRequest request = createOrderRequest(event, "IPTV");
            workflowService.createOrder(request);
            log.info("Successfully processed IPTV order: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Failed to process IPTV order: {}", event.getOrderId(), e);
            workflowService.handleError(event.getOrderId(), "Failed to handle IPTV order", e);
        }
    }

    // 시설 처리 완료 이벤트 처리
    @KafkaListener(topics = KafkaConstants.TOPIC_INTERNET_FACILITY_ALLOCATED,
            groupId = KafkaConstants.GROUP_WORKFLOW)
    public void handleFacilityAllocated(WorkflowEvent event) {
        try {
            workflowService.updateWorkflowStatus(event.getWorkflowId(),
                    WorkflowStatus.IN_PROGRESS,
                    "Facility allocation completed");
        } catch (Exception e) {
            workflowService.handleError(event.getWorkflowId(),
                    "Failed to handle facility allocation", e);
        }
    }

    // 장비 설정 완료 이벤트 처리
    @KafkaListener(topics = KafkaConstants.TOPIC_INTERNET_DEVICE_CONFIGURED,
            groupId = KafkaConstants.GROUP_WORKFLOW)
    public void handleDeviceConfigured(WorkflowEvent event) {
        try {
            workflowService.updateWorkflowStatus(event.getWorkflowId(),
                    WorkflowStatus.IN_PROGRESS,
                    "Device configuration completed");
        } catch (Exception e) {
            workflowService.handleError(event.getWorkflowId(),
                    "Failed to handle device configuration", e);
        }
    }

    // 원부 처리 완료 이벤트 처리
    @KafkaListener(topics = KafkaConstants.TOPIC_INTERNET_FACILITYBOOK_UPDATED,
            groupId = KafkaConstants.GROUP_WORKFLOW)
    public void handleFacilityBookUpdated(WorkflowEvent event) {
        try {
            workflowService.updateWorkflowStatus(event.getWorkflowId(),
                    WorkflowStatus.COMPLETED,
                    "Facility book update completed");
        } catch (Exception e) {
            workflowService.handleError(event.getWorkflowId(),
                    "Failed to handle facility book update", e);
        }
    }

    // IPTV 인증 완료 이벤트 처리
    @KafkaListener(topics = KafkaConstants.TOPIC_IPTV_AUTH_COMPLETED,
            groupId = KafkaConstants.GROUP_WORKFLOW)
    public void handleAuthCompleted(WorkflowEvent event) {
        try {
            workflowService.updateWorkflowStatus(event.getWorkflowId(),
                    WorkflowStatus.IN_PROGRESS,
                    "Authentication completed");
        } catch (Exception e) {
            workflowService.handleError(event.getWorkflowId(),
                    "Failed to handle auth completion", e);
        }
    }

    // IPTV 채널 설정 완료 이벤트 처리
    @KafkaListener(topics = KafkaConstants.TOPIC_IPTV_CHANNEL_CONFIGURED,
            groupId = KafkaConstants.GROUP_WORKFLOW)
    public void handleChannelConfigured(WorkflowEvent event) {
        try {
            workflowService.updateWorkflowStatus(event.getWorkflowId(),
                    WorkflowStatus.IN_PROGRESS,
                    "Channel configuration completed");
        } catch (Exception e) {
            workflowService.handleError(event.getWorkflowId(),
                    "Failed to handle channel configuration", e);
        }
    }

    // IPTV 서비스 완료 이벤트 처리
    @KafkaListener(topics = KafkaConstants.TOPIC_IPTV_SERVICE_COMPLETED,
            groupId = KafkaConstants.GROUP_WORKFLOW)
    public void handleServiceCompleted(WorkflowEvent event) {
        try {
            workflowService.updateWorkflowStatus(event.getWorkflowId(),
                    WorkflowStatus.COMPLETED,
                    "IPTV service completed");
        } catch (Exception e) {
            workflowService.handleError(event.getWorkflowId(),
                    "Failed to handle service completion", e);
        }
    }

    private NewOrderRequest createOrderRequest(WorkflowEventDTO event, String orderType) {
        NewOrderRequest request = new NewOrderRequest();
        request.setOrderId(event.getOrderId());
        request.setOrderType(orderType);
        request.setProductCode(event.getProductCode());
        request.setCustomerId(event.getCustomerId());
        return request;
    }

    private void validateEvent(WorkflowEventDTO event) {
        if (event.getOrderId() == null || event.getOrderId().trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty");
        }
        if (event.getProductCode() == null || event.getProductCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Product code cannot be null or empty");
        }
        if (event.getCustomerId() == null || event.getCustomerId().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty");
        }
    }
}
