// workflow-manager/src/main/java/com/oss/workflow/api/domain/WorkflowDef.java
package com.oss.workflow.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workflow_definitions")
@Getter
@Setter
public class WorkflowDef {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String version;
    private Boolean active;

    @OneToMany(mappedBy = "workflowDef", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskDef> tasks = new ArrayList<>();
}
