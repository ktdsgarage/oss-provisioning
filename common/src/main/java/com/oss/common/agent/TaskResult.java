// common/src/main/java/com/oss/common/agent/TaskResult.java
package com.oss.common.agent;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskResult {
    private String taskId;
    private boolean success;
    private String message;
    private Object result;
}
