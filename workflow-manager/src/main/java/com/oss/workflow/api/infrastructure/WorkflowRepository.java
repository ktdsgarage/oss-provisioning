// workflow-manager/src/main/java/com/oss/workflow/api/infrastructure/WorkflowRepository.java
package com.oss.workflow.api.infrastructure;

import com.oss.workflow.api.domain.Workflow;
import com.oss.workflow.api.domain.WorkflowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WorkflowRepository extends JpaRepository<Workflow, Long> {
    List<Workflow> findByStatus(WorkflowStatus status);
    Optional<Workflow> findByWorkflowId(String workflowId);
}
