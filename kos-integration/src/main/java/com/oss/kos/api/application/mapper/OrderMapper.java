package com.oss.kos.api.application.mapper;

import com.oss.kos.api.domain.Order;
import com.oss.kos.api.application.dto.CompletionResponse;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
    
    public CompletionResponse toCompletionResponse(Order order) {
        return CompletionResponse.builder()
                .orderId(order.getOrderId())
                .status(order.getStatus().name())
                .message("Order processed")
                .completionTime(order.getCompletionDate())
                .operatorId(order.getUpdatedBy())
                .resultCode("200")
                .resultMessage("Success")
                .build();
    }
}
