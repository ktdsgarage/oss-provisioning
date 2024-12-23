package com.oss.iptv.api.application.dto;

import com.oss.iptv.api.domain.AuthStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AuthDTO {
    private String authId;
    private String orderId;
    private AuthStatus status;
    private String channelConfig;
    private LocalDateTime validationDate;
}
