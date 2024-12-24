package com.oss.kos.api.application.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class WorkflowEventDTO {
    private String eventType;
    private String orderType;
    private String orderId;
    private String productCode;
    private String customerId;
    private LocalDateTime eventTime;
    private String sourceSystem;
    private Object payload;
}
