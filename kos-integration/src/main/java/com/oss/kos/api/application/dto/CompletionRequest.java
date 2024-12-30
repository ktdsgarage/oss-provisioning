package com.oss.kos.api.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CompletionRequest {
    private String orderId;
    private String completionStatus;
    private String completionDate;
}
