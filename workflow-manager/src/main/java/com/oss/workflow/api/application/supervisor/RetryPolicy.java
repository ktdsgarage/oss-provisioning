// workflow-manager/src/main/java/com/oss/workflow/api/application/supervisor/RetryPolicy.java
package com.oss.workflow.api.application.supervisor;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RetryPolicy {
    private Integer maxRetries;
    private Integer initialDelaySeconds;
    private Integer maxDelaySeconds;
    private Double backoffMultiplier;
    private Boolean exponentialBackoff;
}
