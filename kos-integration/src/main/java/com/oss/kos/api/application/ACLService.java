// kos-integration/src/main/java/com/oss/kos/api/application/ACLService.java
package com.oss.kos.api.application;

import com.oss.common.dto.WorkflowEventDTO;
import com.oss.kos.api.application.dto.*;
import com.oss.kos.api.domain.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ACLService {

    public WorkflowEventDTO transformRequest(ProvisioningRequest request) {
        return WorkflowEventDTO.builder()
                .eventId(UUID.randomUUID().toString())
                .orderId(generateOrderId())
                .eventType("ORDER_CREATED")
                .customerId(request.getCustomerId())
                .productCode(request.getProductCode())
                .build();

    }

    public CompletionResponse transformResponse(WorkflowEventDTO event) {
        return CompletionResponse.builder()
                .orderId(event.getOrderId())
                .status(OrderStatus.COMPLETED.name())
                .message("Order completed successfully")
                .completionTime(LocalDateTime.now())
                .operatorId("SYSTEM")
                .resultCode("200")
                .resultMessage("Success")
                .build();
    }

    private String generateOrderId() {
        return "ORD" + System.currentTimeMillis();
    }
}