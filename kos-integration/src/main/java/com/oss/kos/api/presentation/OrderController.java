package com.oss.kos.api.presentation;

import com.oss.kos.api.application.OrderService;
import com.oss.kos.api.application.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "KOS 연동", description = "KOS 시스템 연동 관련 API")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    
    @Operation(summary = "개통 요청 수신", description = "KOS 시스템으로부터 개통 요청을 수신합니다")
    @PostMapping("/provisioning")
    public ResponseEntity<ProvisioningResponseDTO> receiveOrder(@RequestBody ProvisioningRequest request) {
        return ResponseEntity.ok(orderService.processOrder(request));
    }
    
    @Operation(summary = "개통 완료 통보", description = "개통 완료 정보를 KOS 시스템으로 전송합니다")
    @PostMapping("/{orderId}/completion")
    public ResponseEntity<CompletionResponseDTO> completeOrder(
            @PathVariable String orderId,
            @RequestBody CompletionRequest request) {
        return ResponseEntity.ok(orderService.completeOrder(orderId, request));
    }
}
