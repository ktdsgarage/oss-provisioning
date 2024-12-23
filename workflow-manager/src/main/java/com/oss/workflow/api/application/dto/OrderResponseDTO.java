package com.oss.workflow.api.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderResponseDTO {
    private String orderId;
    private String status;
    private String message;
}
