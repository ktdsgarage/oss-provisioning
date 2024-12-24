package com.oss.workflow.api.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderChangeResponseDTO {
    private String orderId;
    private String status;
    private String message;
    private String changeType;
    private String changeDetails;
    private String requestDate;
}
