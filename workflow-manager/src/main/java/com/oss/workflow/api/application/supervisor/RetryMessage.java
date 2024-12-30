// workflow-manager/src/main/java/com/oss/workflow/api/application/supervisor/RetryMessage.java
package com.oss.workflow.api.application.supervisor;

import com.oss.workflow.api.domain.TaskType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RetryMessage {
    private String taskId;
    private TaskType type;
    private Integer retryCount;
    private String parameters;
    private String serviceName;
}
