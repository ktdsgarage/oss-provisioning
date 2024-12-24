package com.oss.kos.api.application.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class CompletionResponse {
    private String orderId;
    private String status;
    private String message;
    private LocalDateTime completionTime;
    private String operatorId;
    private String resultCode;
    private String resultMessage;
}
