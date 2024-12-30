// workflow-manager/src/main/java/com/oss/workflow/api/application/WorkflowService.java
package com.oss.workflow.api.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oss.common.event.WorkflowEvent;
import com.oss.workflow.api.application.dto.*;
import com.oss.workflow.api.application.scheduler.WorkflowScheduler;
import com.oss.workflow.api.application.supervisor.WorkflowSupervisor;
import com.oss.workflow.api.domain.*;
import com.oss.workflow.api.infrastructure.TaskRepository;
import com.oss.workflow.api.infrastructure.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final WorkflowRepository workflowRepository;
    private final TaskRepository taskRepository;
    private final WorkflowScheduler scheduler;
    private final WorkflowSupervisor supervisor;
    private final ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<String, WorkflowEvent> kafkaTemplate;

    @Transactional
    public OrderResponseDTO createOrder(NewOrderRequest request) {
        // 1. ORDER_REQUESTED 이벤트 발행
        String eventId = UUID.randomUUID().toString();
        String payload = serializeToJson(request);

        publishEvent("workflow.order.requested",
                null,
                request.getOrderId(),
                "ORDER_REQUESTED",
                payload);

        // 2. 응답 반환
        return OrderResponseDTO.builder()
                .orderId(request.getOrderId())
                .status(WorkflowStatus.NEW.name())
                .message("Order request accepted")
                .build();
    }

    @Transactional
    public void cancelWorkflow(String workflowId) {
        // WORKFLOW_CANCEL_REQUESTED 이벤트 발행
        publishEvent("workflow.cancel.requested",
                workflowId,
                null,
                "WORKFLOW_CANCEL_REQUESTED",
                workflowId);
    }

    @Transactional
    public OrderChangeResponseDTO changeOrder(String orderId, OrderChangeRequest request) {
        // ORDER_CHANGE_REQUESTED 이벤트 발행
        String payload = serializeToJson(request);

        publishEvent("workflow.order.change.requested",
                null,
                orderId,
                "ORDER_CHANGE_REQUESTED",
                payload);

        return OrderChangeResponseDTO.builder()
                .orderId(orderId)
                .status(WorkflowStatus.IN_PROGRESS.name())
                .changeType(request.getChangeType())
                .changeDetails(request.getChangeDetails())
                .requestDate(request.getRequestDate())
                .message("Order change request accepted")
                .build();
    }

    // 이벤트 핸들러들
    @KafkaListener(topics = "workflow.order.requested")
    @Transactional
    public void handleOrderRequested(WorkflowEvent event) {
        try {
            NewOrderRequest request = objectMapper.readValue(event.getPayload(), NewOrderRequest.class);
            String workflowId = createWorkflow(request.getOrderType(), request);

            // WORKFLOW_CREATED 이벤트 발행
            if("INTERNET".equals(request.getOrderType())) {
                publishEvent("internet.workflow.created",
                        workflowId,
                        event.getOrderId(),
                        "WORKFLOW_CREATED",
                        event.getPayload());
            } else {
                publishEvent("iptv.workflow.created",
                        workflowId,
                        event.getOrderId(),
                        "WORKFLOW_CREATED",
                        event.getPayload());
            }
        } catch (Exception e) {
            log.error("Failed to handle order requested event", e);
            publishEvent("workflow.error",
                    null,
                    event.getOrderId(),
                    "ORDER_CREATION_FAILED",
                    e.getMessage());
        }
    }

    @KafkaListener(topics = "workflow.created")
    public void handleWorkflowCreated(WorkflowEvent event) {
        try {
            Workflow workflow = workflowRepository.findByWorkflowId(event.getWorkflowId())
                    .orElseThrow(() -> new IllegalArgumentException("Workflow not found: " + event.getWorkflowId()));

            startWorkflow(workflow);

            // WORKFLOW_STARTED 이벤트 발행
            publishEvent("workflow.started",
                    workflow.getWorkflowId(),
                    event.getOrderId(),
                    "WORKFLOW_STARTED",
                    event.getPayload());
        } catch (Exception e) {
            log.error("Failed to handle workflow created event", e);
            publishEvent("workflow.error",
                    event.getWorkflowId(),
                    event.getOrderId(),
                    "WORKFLOW_START_FAILED",
                    e.getMessage());
        }
    }

    @KafkaListener(topics = "workflow.cancel.requested")
    public void handleCancelRequested(WorkflowEvent event) {
        try {
            Workflow workflow = workflowRepository.findByWorkflowId(event.getWorkflowId())
                    .orElseThrow(() -> new IllegalArgumentException("Workflow not found: " + event.getWorkflowId()));

            workflow.setStatus(WorkflowStatus.CANCELLED);

            Task currentTask = workflow.getCurrentTask();
            if (currentTask != null) {
                currentTask.setStatus(TaskStatus.CANCELLED);
                supervisor.cancelTask(currentTask.getTaskId());
            }

            workflow.setEndDate(LocalDateTime.now());
            workflowRepository.save(workflow);

            // WORKFLOW_CANCELLED 이벤트 발행
            publishEvent("workflow.cancelled",
                    workflow.getWorkflowId(),
                    event.getOrderId(),
                    "WORKFLOW_CANCELLED",
                    "Workflow cancelled successfully");
        } catch (Exception e) {
            log.error("Failed to handle cancel requested event", e);
            publishEvent("workflow.error",
                    event.getWorkflowId(),
                    event.getOrderId(),
                    "WORKFLOW_CANCEL_FAILED",
                    e.getMessage());
        }
    }

    @KafkaListener(topics = "workflow.order.change.requested")
    public void handleOrderChangeRequested(WorkflowEvent event) {
        try {
            OrderChangeRequest request = objectMapper.readValue(event.getPayload(), OrderChangeRequest.class);

            Workflow workflow = workflowRepository.findByWorkflowId(event.getWorkflowId())
                    .orElseThrow(() -> new IllegalArgumentException("Workflow not found: " + event.getWorkflowId()));

            workflow.setChangeType(request.getChangeType());
            workflowRepository.save(workflow);

            // ORDER_CHANGED 이벤트 발행
            publishEvent("workflow.order.changed",
                    workflow.getWorkflowId(),
                    event.getOrderId(),
                    "ORDER_CHANGED",
                    event.getPayload());
        } catch (Exception e) {
            log.error("Failed to handle order change requested event", e);
            publishEvent("workflow.error",
                    event.getWorkflowId(),
                    event.getOrderId(),
                    "ORDER_CHANGE_FAILED",
                    e.getMessage());
        }
    }

    // 유틸리티 메서드들
    private String createWorkflow(String orderType, Object parameters) {
        NewOrderRequest request = convertToOrderRequest(parameters);

        Workflow workflow = new Workflow();
        workflow.setWorkflowId("WF" + UUID.randomUUID().toString());
        workflow.setOrderType(orderType);
        workflow.setStatus(WorkflowStatus.NEW);
        workflow.setStartDate(LocalDateTime.now());
        workflow.setChangeType("NEW");
        workflow.setCustomerId(request.getCustomerId());
        workflow.setOrderId(request.getOrderId());

        try {
            workflow.setParameters(objectMapper.writeValueAsString(parameters));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize parameters", e);
        }

        Task task = new Task();
        task.setTaskId("T" + UUID.randomUUID().toString());
        task.setWorkflow(workflow);
        task.setStatus(TaskStatus.PENDING);
        task.setParameters(workflow.getParameters());
        workflow.getTasks().add(task);

        workflowRepository.save(workflow);

        return workflow.getWorkflowId();
    }

    private NewOrderRequest convertToOrderRequest(Object parameters) {
        try {
            if (parameters instanceof NewOrderRequest) {
                return (NewOrderRequest) parameters;
            }
            return objectMapper.convertValue(parameters, NewOrderRequest.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert parameters to NewOrderRequest", e);
        }
    }

    private void startWorkflow(Workflow workflow) {
        workflow.setStatus(WorkflowStatus.IN_PROGRESS);
        workflowRepository.save(workflow);
        scheduler.scheduleTasks();
    }

    private String serializeToJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object", e);
        }
    }

    private void publishEvent(String topic, String workflowId, String orderId, String eventType, String payload) {
        WorkflowEvent event = WorkflowEvent.builder()
                .eventId(UUID.randomUUID().toString())  // eventId는 이벤트 생성 시점에 할당
                .workflowId(workflowId)
                .orderId(orderId)
                .eventType(eventType)
                .payload(payload)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send(topic, event);
    }

    // Read-only 메서드들
    @Transactional(readOnly = true)
    public WorkflowStatus getWorkflowStatus(String workflowId) {
        return workflowRepository.findByWorkflowId(workflowId)
                .orElseThrow(() -> new IllegalArgumentException("Workflow not found: " + workflowId))
                .getStatus();
    }

    @Transactional(readOnly = true)
    public RetryStatusDTO getRetryStatus(String taskId) {
        Task task = taskRepository.findByTaskId(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        return RetryStatusDTO.builder()
                .taskId(taskId)
                .retryCount(task.getRetryCount())
                .lastRetryTime(task.getUpdatedAt().toString())
                .retryStatus(task.getStatus().name())
                .build();
    }

    @Transactional(readOnly = true)
    public NextProcessDTO getNextProcess(String orderId) {
        Workflow workflow = workflowRepository.findByWorkflowId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Workflow not found: " + orderId));

        Task currentTask = workflow.getCurrentTask();
        Task nextTask = workflow.getNextTask();

        return NextProcessDTO.builder()
                .currentProcess(currentTask != null ? currentTask.getType().name() : null)
                .nextProcess(nextTask != null ? nextTask.getType().name() : null)
                .estimatedTime(nextTask != null ? nextTask.getTaskDef().getTimeoutSeconds() + "s" : null)
                .build();
    }

    @Transactional(readOnly = true)
    public DeploymentStatusDTO getDeploymentStatus() {
        return DeploymentStatusDTO.builder()
                .deploymentId("OSS-Workflow-" + System.currentTimeMillis())
                .status("RUNNING")
                .version("1.0.0")
                .build();
    }
}
