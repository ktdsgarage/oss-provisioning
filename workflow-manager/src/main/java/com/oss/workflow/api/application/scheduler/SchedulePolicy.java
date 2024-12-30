// workflow-manager/src/main/java/com/oss/workflow/api/application/scheduler/SchedulePolicy.java
package com.oss.workflow.api.application.scheduler;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SchedulePolicy {
    private Integer maxConcurrentTasks;
    private Integer defaultTimeoutSeconds;
    private Integer minRetryIntervalSeconds;
    private Integer maxRetryIntervalSeconds;
    private Boolean exponentialBackoff;
}
