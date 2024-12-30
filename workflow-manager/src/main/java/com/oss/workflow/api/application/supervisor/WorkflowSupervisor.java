// workflow-manager/src/main/java/com/oss/workflow/api/application/supervisor/WorkflowSupervisor.java
package com.oss.workflow.api.application.supervisor;

import com.oss.common.agent.TaskResult;
import com.oss.workflow.api.domain.*;
import com.oss.workflow.api.infrastructure.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class WorkflowSupervisor {

    private final TaskRepository taskRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final MonitoringPolicy monitoringPolicy;
    private final RetryPolicy retryPolicy;

    private final Map<String, TaskMonitorInfo> monitoringTasks = new ConcurrentHashMap<>();

    public void monitorTask(String taskId, Integer timeoutSeconds) {
        TaskMonitorInfo monitorInfo = TaskMonitorInfo.builder()
                .taskId(taskId)
                .startTime(LocalDateTime.now())
                .timeoutSeconds(timeoutSeconds)
                .build();

        monitoringTasks.put(taskId, monitorInfo);
    }

    @Scheduled(fixedDelay = 1000)
    public void checkTimeouts() {
        LocalDateTime now = LocalDateTime.now();

        monitoringTasks.forEach((taskId, monitorInfo) -> {
            if (isTimeout(monitorInfo, now)) {
                handleTimeout(taskId);
            }
        });
    }

    public void handleTaskComplete(TaskResult result) {
        Task task = taskRepository.findByTaskId(result.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (result.isSuccess()) {
            task.setStatus(TaskStatus.COMPLETED);
        } else {
            handleTaskFailure(task);
        }

        taskRepository.save(task);
        monitoringTasks.remove(task.getTaskId());
    }

    private void handleTimeout(String taskId) {
        Task task = taskRepository.findByTaskId(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        handleTaskFailure(task);
        taskRepository.save(task);
    }

    private void handleTaskFailure(Task task) {
        if (task.getRetryCount() < retryPolicy.getMaxRetries()) {
            retryTask(task);
        } else {
            task.setStatus(TaskStatus.FAILED);
        }
    }

    private void retryTask(Task task) {
        task.incrementRetryCount();
        int delaySeconds = calculateRetryDelay(task.getRetryCount());

        // 재시도 메시지 발행
        String topic = task.getServiceName() + ".task.retry";
        kafkaTemplate.send(topic, createRetryMessage(task));
    }

    private int calculateRetryDelay(int retryCount) {
        if (retryPolicy.getExponentialBackoff()) {
            return (int) Math.min(
                    retryPolicy.getInitialDelaySeconds() * Math.pow(retryPolicy.getBackoffMultiplier(), retryCount),
                    retryPolicy.getMaxDelaySeconds()
            );
        }
        return retryPolicy.getInitialDelaySeconds();
    }

    private boolean isTimeout(TaskMonitorInfo monitorInfo, LocalDateTime now) {
        return monitorInfo.getStartTime()
                .plusSeconds(monitorInfo.getTimeoutSeconds())
                .isBefore(now);
    }

    private Object createRetryMessage(Task task) {
        return RetryMessage.builder()
                .taskId(task.getTaskId())
                .type(task.getType())
                .retryCount(task.getRetryCount())
                .parameters(task.getParameters())
                .serviceName(task.getServiceName())
                .build();
    }

    @Getter
    @Builder
    private static class TaskMonitorInfo {
        private String taskId;
        private LocalDateTime startTime;
        private Integer timeoutSeconds;
    }

    public void cancelTask(String taskId) {
        monitoringTasks.remove(taskId);
        // Additional cancellation logic if needed
    }

}
