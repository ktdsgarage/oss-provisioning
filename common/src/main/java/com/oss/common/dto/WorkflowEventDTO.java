package com.oss.common.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowEventDTO {
    private String eventId;
    private String workflowId;
    private String orderId;
    private String eventType;
    private String customerId;
    private String productCode;
    private String payload;
}
