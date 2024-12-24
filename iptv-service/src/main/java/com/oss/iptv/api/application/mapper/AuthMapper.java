package com.oss.iptv.api.application.mapper;

import com.oss.iptv.api.domain.Auth;
import com.oss.iptv.api.application.dto.AuthDTO;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {
    
    public AuthDTO toDTO(Auth auth) {
        return AuthDTO.builder()
                .authId(auth.getAuthId())
                .orderId(auth.getOrderId())
                .status(auth.getStatus())
                .channelConfig(auth.getChannelConfig())
                .validationDate(auth.getValidationDate())
                .build();
    }
    
    public Auth toEntity(AuthDTO dto) {
        Auth auth = new Auth();
        auth.setAuthId(dto.getAuthId());
        auth.setOrderId(dto.getOrderId());
        auth.setStatus(dto.getStatus());
        auth.setChannelConfig(dto.getChannelConfig());
        auth.setValidationDate(dto.getValidationDate());
        return auth;
    }
}
