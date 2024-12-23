package com.oss.kos.api.application;

import com.oss.kos.api.application.dto.*;
import org.springframework.stereotype.Service;

@Service
public class ACLService {
    
    public Object transformRequest(ProvisioningRequest request) {
        // KOS 요청을 내부 이벤트로 변환
        return new Object(); // 실제 구현에서는 적절한 이벤트 객체 반환
    }

    public CompletionResponse transformResponse(Object event) {
        // 내부 이벤트를 KOS 응답으로 변환
        return new CompletionResponse(); // 실제 구현에서는 적절한 응답 객체 반환
    }
}
