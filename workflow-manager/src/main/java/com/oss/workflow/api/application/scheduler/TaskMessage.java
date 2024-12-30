// workflow-manager/src/main/java/com/oss/workflow/api/application/scheduler/TaskMessage.java
package com.oss.workflow.api.application.scheduler;

import com.oss.workflow.api.domain.TaskType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskMessage {
    private String taskId;
    private TaskType taskType;
    private Object parameters;
}
