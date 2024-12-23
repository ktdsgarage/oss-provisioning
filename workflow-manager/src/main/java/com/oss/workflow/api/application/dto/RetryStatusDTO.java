package com.oss.workflow.api.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RetryStatusDTO {
    private String taskId;
    private int retryCount;
    private String lastRetryTime;
    private String retryStatus;
}
