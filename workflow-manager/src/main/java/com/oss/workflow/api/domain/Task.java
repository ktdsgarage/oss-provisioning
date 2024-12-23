package com.oss.workflow.api.domain;

import com.oss.workflow.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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
    
    @Enumerated(EnumType.STRING)
    private TaskType type;
    
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    
    private Integer retryCount = 0;
    private LocalDateTime lastRetryDate;
    private LocalDateTime completedDate;

    public void incrementRetryCount() {
        this.retryCount++;
        this.lastRetryDate = LocalDateTime.now();
    }

    public void complete() {
        this.status = TaskStatus.COMPLETED;
        this.completedDate = LocalDateTime.now();
    }
}
