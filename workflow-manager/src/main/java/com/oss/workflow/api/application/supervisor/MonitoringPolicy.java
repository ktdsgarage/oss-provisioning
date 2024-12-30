// workflow-manager/src/main/java/com/oss/workflow/api/application/supervisor/MonitoringPolicy.java
package com.oss.workflow.api.application.supervisor;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MonitoringPolicy {
    private Integer healthCheckIntervalSeconds;
    private Integer taskTimeoutSeconds;
    private Integer maxConsecutiveFailures;
    private Boolean alertEnabled;
}
