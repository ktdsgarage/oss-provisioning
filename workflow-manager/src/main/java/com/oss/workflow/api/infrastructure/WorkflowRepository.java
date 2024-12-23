package com.oss.workflow.api.infrastructure;

import com.oss.workflow.api.domain.Workflow;
import com.oss.workflow.api.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WorkflowRepository extends JpaRepository<Workflow, Long> {
    Optional<Workflow> findByOrderId(String orderId);
    
    @Query("SELECT t FROM Task t WHERE t.taskId = :taskId")
    Optional<Task> findTaskById(@Param("taskId") String taskId);
}
