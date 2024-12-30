package com.oss.kos.api.application;

import com.oss.common.event.WorkflowEvent;
import com.oss.kos.api.application.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oss.common.dto.ErrorEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ACLService aclService;
    private final ObjectMapper objectMapper;

    @Transactional
    public ProvisioningResponseDTO processOrder(ProvisioningRequest request) {
        // ACL 변환 및 이벤트 발행
        Object event = aclService.transformRequest(request);
        kafkaTemplate.send(determineTopicByOrderType(request.getOrderType()), event);
        
        return ProvisioningResponseDTO.builder()
                .orderId(generateOrderId())
                .status("ACCEPTED")
                .message("Order request accepted successfully")
                .build();
    }

    @Transactional
    public CompletionResponseDTO completeOrder(String orderId, CompletionRequest request) {
        // 완료 처리 및 응답
        return CompletionResponseDTO.builder()
                .orderId(orderId)
                .status("COMPLETED")
                .message("Order completed successfully")
                .build();
    }

    private String determineTopicByOrderType(String orderType) {
        return orderType.equalsIgnoreCase("INTERNET") ? "internet.request" : "iptv.request";
    }

    private String generateOrderId() {
        return "ORD" + System.currentTimeMillis();
    }

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
            String payload = objectMapper.writeValueAsString(errorEvent);
            WorkflowEvent event = WorkflowEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .workflowId(workflowId)
                    .eventType("WORKFLOW_ERROR")
                    .timestamp(LocalDateTime.now())
                    .payload(payload)
                    .build();

            kafkaTemplate.send("kos.workflow.error", event);
        } catch (JsonProcessingException ex) {
            log.error("Failed to serialize error event", ex);
        }
    }

    public void publishEvent(String topic, WorkflowEvent event) {
        try {
            log.debug("Publishing event to topic {}: {}", topic, event);
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

    private String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
