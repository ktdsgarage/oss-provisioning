package com.oss.kos.api.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompletionRequest {
    private String orderId;
    private String completionStatus;
    private String completionDate;
}
