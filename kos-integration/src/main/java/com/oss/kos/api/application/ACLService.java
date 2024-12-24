package com.oss.kos.api.application;

import com.oss.kos.api.application.dto.*;
import com.oss.kos.api.domain.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ACLService {
    
    public Object transformRequest(ProvisioningRequest request) {
        // KOS 요청을 내부 이벤트로 변환
        return WorkflowEventDTO.builder()
                .eventType("ORDER_CREATED")
                .orderType(request.getOrderType())
                .productCode(request.getProductCode())
                .customerId(request.getCustomerId())
                .build();
    }

    public CompletionResponse transformResponse(Object event) {
        // 내부 이벤트를 KOS 응답으로 변환
        return CompletionResponse.builder()
                .orderId(generateOrderId())
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
