// workflow-manager/src/main/java/com/oss/workflow/api/application/scheduler/WorkflowScheduler.java
package com.oss.workflow.api.application.scheduler;

import com.oss.workflow.api.application.supervisor.WorkflowSupervisor;
import com.oss.workflow.api.domain.*;
import com.oss.workflow.api.infrastructure.TaskRepository;
import com.oss.workflow.api.infrastructure.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WorkflowScheduler {

    private final WorkflowRepository workflowRepository;
    private final TaskRepository taskRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final WorkflowSupervisor supervisor;
    private final SchedulePolicy policy;

    @Scheduled(fixedDelay = 1000)
    public void scheduleTasks() {
        List<Workflow> activeWorkflows = workflowRepository.findByStatus(WorkflowStatus.IN_PROGRESS);

        for (Workflow workflow : activeWorkflows) {
            Task currentTask = workflow.getCurrentTask();
            if (currentTask == null || currentTask.getStatus() == TaskStatus.COMPLETED) {
                scheduleNextTask(workflow);
            }
        }
    }

    private void scheduleNextTask(Workflow workflow) {
        Task nextTask = workflow.getNextTask();
        if (nextTask != null) {
            nextTask.setStatus(TaskStatus.SCHEDULED);
            taskRepository.save(nextTask);

            // 태스크 실행 요청
            String topic = nextTask.getServiceName() + ".task.execute";
            kafkaTemplate.send(topic, createTaskMessage(nextTask));

            // 수퍼바이저에게 모니터링 요청
            supervisor.monitorTask(nextTask.getTaskId(), policy.getDefaultTimeoutSeconds());
        } else {
            // 모든 태스크 완료
            workflow.setStatus(WorkflowStatus.COMPLETED);
            workflowRepository.save(workflow);
        }
    }

    private TaskMessage createTaskMessage(Task task) {
        return TaskMessage.builder()
                .taskId(task.getTaskId())
                .taskType(task.getType())
                .parameters(task.getParameters())
                .build();
    }
}
