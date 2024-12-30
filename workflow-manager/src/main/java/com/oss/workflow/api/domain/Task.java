// workflow-manager/src/main/java/com/oss/workflow/api/domain/Task.java
package com.oss.workflow.api.domain;

import com.oss.workflow.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
public class Task extends BaseEntity {

    @Column(nullable = false)
    private String taskId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id")
    private Workflow workflow;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_def_id")
    private TaskDef taskDef;

    @Enumerated(EnumType.STRING)
    private TaskType type;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Column(name = "service_name")
    private String serviceName;

    @Column(columnDefinition = "TEXT")
    private String parameters;

    public void setTaskDef(TaskDef taskDef) {
        this.taskDef = taskDef;
        this.type = TaskType.valueOf(taskDef.getTaskType());
        this.serviceName = taskDef.getServiceName();
    }

    public void incrementRetryCount() {
        this.retryCount++;
    }
}