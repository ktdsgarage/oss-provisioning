package com.oss.common.event;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class WorkflowEvent {
    private String eventId;
    private String workflowId;
    private String orderId;
    private String eventType;
    private LocalDateTime timestamp;
    private String payload;
}
