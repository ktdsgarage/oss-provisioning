package com.oss.workflow.api.application;

import com.oss.workflow.api.application.dto.*;
import com.oss.workflow.api.domain.Workflow;
import com.oss.workflow.api.domain.Task;
import com.oss.workflow.api.infrastructure.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final WorkflowRepository workflowRepository;

    @Transactional(readOnly = true)
    public NextProcessDTO getNextProcess(String orderId) {
        Workflow workflow = workflowRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Workflow not found"));
        
        Task currentTask = workflow.getCurrentTask();
        Task nextTask = workflow.getNextTask();
        
        return NextProcessDTO.builder()
                .currentProcess(currentTask.getType().name())
                .nextProcess(nextTask != null ? nextTask.getType().name() : null)
                .estimatedTime(calculateEstimatedTime(nextTask))
                .build();
    }

    @Transactional(readOnly = true) 
    public RetryStatusDTO getRetryStatus(String taskId) {
        Task task = workflowRepository.findTaskById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
                
        return RetryStatusDTO.builder()
                .taskId(task.getTaskId())
                .retryCount(task.getRetryCount())
                .lastRetryTime(task.getLastRetryDate().toString())
                .retryStatus(task.getStatus().name())
                .build();
    }

    @Transactional
    public OrderResponseDTO createOrder(NewOrderRequest request) {
        Workflow workflow = new Workflow();
        workflow.setOrderType(request.getOrderType());
        workflowRepository.save(workflow);
        
        return OrderResponseDTO.builder()
                .orderId(workflow.getOrderId())
                .status("CREATED")
                .message("Order created successfully")
                .build();
    }

    private String calculateEstimatedTime(Task task) {
        if (task == null) return null;
        // 각 Task 타입별 예상 소요시간 계산 로직
        return "30분";
    }
}
