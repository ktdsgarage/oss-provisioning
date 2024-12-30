package com.oss.common.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorEvent {
    private String workflowId;
    private String errorType;
    private String errorMessage;
    private String errorDetail;
    private LocalDateTime occurredAt;
}