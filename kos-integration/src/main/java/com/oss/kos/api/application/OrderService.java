package com.oss.kos.api.application;

import com.oss.kos.api.application.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ACLService aclService;

    @Transactional
    public ProvisioningResponseDTO processOrder(ProvisioningRequest request) {
        // ACL 변환 및 이벤트 발행
        Object event = aclService.transformRequest(request);
        kafkaTemplate.send(determineTopicByOrderType(request.getOrderType()), event);
        
        return ProvisioningResponseDTO.builder()
                .orderId(generateOrderId())
                .status("ACCEPTED")
                .message("Order request accepted successfully")
                .build();
    }

    @Transactional
    public CompletionResponseDTO completeOrder(String orderId, CompletionRequest request) {
        // 완료 처리 및 응답
        return CompletionResponseDTO.builder()
                .orderId(orderId)
                .status("COMPLETED")
                .message("Order completed successfully")
                .build();
    }

    private String determineTopicByOrderType(String orderType) {
        return orderType.equalsIgnoreCase("INTERNET") ? "internet.request" : "iptv.request";
    }

    private String generateOrderId() {
        return "ORD" + System.currentTimeMillis();
    }
}
