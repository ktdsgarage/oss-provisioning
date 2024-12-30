// workflow-manager/src/main/java/com/oss/workflow/api/domain/TaskDef.java
package com.oss.workflow.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "task_definitions")
@Getter
@Setter
public class TaskDef {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String taskType;
    private String serviceName;
    private Integer timeoutSeconds;
    private Integer maxRetries;
    private String description;
    private Boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_def_id")
    private WorkflowDef workflowDef;
}
