package com.oss.workflow.api.presentation;

import com.oss.workflow.api.application.WorkflowService;
import com.oss.workflow.api.application.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "워크플로우 관리", description = "워크플로우 처리 관련 API")
@RestController
@RequestMapping("/workflow")
@RequiredArgsConstructor 
public class WorkflowController {

    private final WorkflowService workflowService;
    
    @Operation(summary = "다음 공정 조회", description = "진행 중인 오더의 다음 공정을 조회합니다")
    @GetMapping("/orders/{orderId}/next-process")
    public ResponseEntity<NextProcessDTO> getNextProcess(@PathVariable String orderId) {
        return ResponseEntity.ok(workflowService.getNextProcess(orderId));
    }
    
    @Operation(summary = "Task 재처리 상태 조회", description = "실패한 Task의 재처리 상태를 조회합니다") 
    @GetMapping("/tasks/{taskId}/retry-status")
    public ResponseEntity<RetryStatusDTO> getRetryStatus(@PathVariable String taskId) {
        return ResponseEntity.ok(workflowService.getRetryStatus(taskId));
    }

    @Operation(summary = "신규 개통 요청", description = "신규 개통 오더를 생성합니다")
    @PostMapping("/orders")
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody NewOrderRequest request) {
        return ResponseEntity.ok(workflowService.createOrder(request));
    }

    @Operation(summary = "오더 변경", description = "진행 중인 오더 정보를 변경합니다")
    @PutMapping("/orders/{orderId}")
    public ResponseEntity<OrderChangeResponseDTO> changeOrder(
            @PathVariable String orderId,
            @RequestBody OrderChangeRequest request) {
        return ResponseEntity.ok(workflowService.changeOrder(orderId, request));
    }

    @Operation(summary = "배포 상태 조회", description = "시스템 배포 상태를 조회합니다")
    @GetMapping("/deployment/status") 
    public ResponseEntity<DeploymentStatusDTO> getDeploymentStatus() {
        return ResponseEntity.ok(workflowService.getDeploymentStatus());
    }
}
