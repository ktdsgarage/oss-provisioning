package com.oss.kos.api.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CompletionResponseDTO {
    private String orderId;
    private String status;
    private String message;
}
