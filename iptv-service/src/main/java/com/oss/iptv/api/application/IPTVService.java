package com.oss.iptv.api.application;

import com.oss.iptv.api.application.dto.*;
import com.oss.iptv.api.domain.Auth;
import com.oss.iptv.api.domain.AuthStatus;
import com.oss.iptv.api.infrastructure.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IPTVService {

    private final AuthRepository authRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public void processAuth(AuthRequest request) {
        Auth auth = new Auth();
        auth.setOrderId(request.getOrderId());
        auth.setStatus(AuthStatus.PROCESSING);
        
        authRepository.save(auth);
        
        kafkaTemplate.send("iptv.auth", auth);
    }

    @Transactional
    public void updateFacilityBook(FacilityBookRequest request) {
        // 원부 정보 업데이트 로직
        kafkaTemplate.send("iptv.facilitybook", request);
    }
}
