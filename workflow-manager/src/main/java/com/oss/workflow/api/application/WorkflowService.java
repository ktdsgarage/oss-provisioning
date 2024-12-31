package com.oss.workflow.api.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oss.common.event.WorkflowEvent;
import com.oss.workflow.api.application.dto.*;
import com.oss.workflow.api.application.scheduler.WorkflowScheduler;
import com.oss.workflow.api.application.supervisor.WorkflowSupervisor;
import com.oss.workflow.api.domain.*;
import com.oss.workflow.api.infrastructure.TaskRepository;
import com.oss.workflow.api.infrastructure.WorkflowRepository;
import com.oss.common.constants.KafkaConstants;
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
        String payload = serializeToJson(request);

        publishEvent(KafkaConstants.TOPIC_WORKFLOW_ORDER_REQUESTED,
                null,
                request.getOrderId(),
                KafkaConstants.EVENT_ORDER_REQUESTED,
                payload);

        return OrderResponseDTO.builder()
                .orderId(request.getOrderId())
                .status(WorkflowStatus.NEW.name())
                .message("Order request accepted")
                .build();
    }

    @Transactional
    public void cancelWorkflow(String workflowId) {
        publishEvent(KafkaConstants.TOPIC_WORKFLOW_CANCELLED,
                workflowId,
                null,
                KafkaConstants.EVENT_WORKFLOW_CANCELLED,
                workflowId);
    }

    @Transactional
    public OrderChangeResponseDTO changeOrder(String orderId, OrderChangeRequest request) {
        String payload = serializeToJson(request);

        publishEvent(KafkaConstants.TOPIC_WORKFLOW_ORDER_REQUESTED,
                null,
                orderId,
                KafkaConstants.EVENT_ORDER_REQUESTED,
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

    @KafkaListener(topics = KafkaConstants.TOPIC_WORKFLOW_ORDER_REQUESTED, groupId = KafkaConstants.GROUP_WORKFLOW)
    @Transactional
    public void handleOrderRequested(WorkflowEvent event) {
        try {
            NewOrderRequest request = objectMapper.readValue(event.getPayload(), NewOrderRequest.class);
            String workflowId = createWorkflow(request.getOrderType(), request);

            if("INTERNET".equals(request.getOrderType())) {
                publishEvent(KafkaConstants.TOPIC_INTERNET_WORKFLOW_CREATED,
                        workflowId,
                        event.getOrderId(),
                        KafkaConstants.EVENT_WORKFLOW_CREATED,
                        event.getPayload());
            } else {
                publishEvent(KafkaConstants.TOPIC_IPTV_WORKFLOW_CREATED,
                        workflowId,
                        event.getOrderId(),
                        KafkaConstants.EVENT_WORKFLOW_CREATED,
                        event.getPayload());
            }
        } catch (Exception e) {
            log.error("Failed to handle order requested event", e);
            publishEvent(KafkaConstants.TOPIC_WORKFLOW_ERROR,
                    null,
                    event.getOrderId(),
                    KafkaConstants.EVENT_WORKFLOW_ERROR,
                    e.getMessage());
        }
    }

    @KafkaListener(topics = KafkaConstants.TOPIC_WORKFLOW_CREATED, groupId = KafkaConstants.GROUP_WORKFLOW)
    public void handleWorkflowCreated(WorkflowEvent event) {
        try {
            Workflow workflow = workflowRepository.findByWorkflowId(event.getWorkflowId())
                    .orElseThrow(() -> new IllegalArgumentException("Workflow not found: " + event.getWorkflowId()));

            startWorkflow(workflow);

            publishEvent(KafkaConstants.TOPIC_WORKFLOW_STARTED,
                    workflow.getWorkflowId(),
                    event.getOrderId(),
                    KafkaConstants.EVENT_WORKFLOW_STARTED,
                    event.getPayload());
        } catch (Exception e) {
            log.error("Failed to handle workflow created event", e);
            publishEvent(KafkaConstants.TOPIC_WORKFLOW_ERROR,
                    event.getWorkflowId(),
                    event.getOrderId(),
                    KafkaConstants.EVENT_WORKFLOW_ERROR,
                    e.getMessage());
        }
    }

    @KafkaListener(topics = KafkaConstants.TOPIC_WORKFLOW_CANCELLED, groupId = KafkaConstants.GROUP_WORKFLOW)
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

            publishEvent(KafkaConstants.TOPIC_WORKFLOW_CANCELLED,
                    workflow.getWorkflowId(),
                    event.getOrderId(),
                    KafkaConstants.EVENT_WORKFLOW_CANCELLED,
                    "Workflow cancelled successfully");
        } catch (Exception e) {
            log.error("Failed to handle cancel requested event", e);
            publishEvent(KafkaConstants.TOPIC_WORKFLOW_ERROR,
                    event.getWorkflowId(),
                    event.getOrderId(),
                    KafkaConstants.EVENT_WORKFLOW_ERROR,
                    e.getMessage());
        }
    }

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
                .eventId(UUID.randomUUID().toString())
                .workflowId(workflowId)
                .orderId(orderId)
                .eventType(eventType)
                .payload(payload)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send(topic, event);
    }

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
