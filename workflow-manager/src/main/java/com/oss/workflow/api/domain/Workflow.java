package com.oss.workflow.api.domain;

import com.oss.workflow.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workflows")
@Getter
@Setter
@NoArgsConstructor
public class Workflow extends BaseEntity {

    @Column(nullable = false)
    private String workflowId;
    
    @Column(nullable = false)
    private String orderType;
    
    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String changeType;

    @Column(nullable = false) 
    private String customerId;
    
    @Enumerated(EnumType.STRING)
    private WorkflowStatus status;
    
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    public Task getCurrentTask() {
        return tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.IN_PROGRESS)
                .findFirst()
                .orElse(null);
    }

    public Task getNextTask() {
        return tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.PENDING)
                .findFirst()
                .orElse(null);
    }
}
