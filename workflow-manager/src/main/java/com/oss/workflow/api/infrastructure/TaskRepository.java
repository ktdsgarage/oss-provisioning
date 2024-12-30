// workflow-manager/src/main/java/com/oss/workflow/api/infrastructure/TaskRepository.java
package com.oss.workflow.api.infrastructure;

import com.oss.workflow.api.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Optional<Task> findByTaskId(String taskId);
}
