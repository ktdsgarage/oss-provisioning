package com.oss.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// common/src/main/java/com/oss/common/event/WorkflowEvent.java
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowEvent {
    private String eventId;
    private String workflowId;
    private String orderId;
    private String eventType;
    private LocalDateTime timestamp;
    private String payload;
}
