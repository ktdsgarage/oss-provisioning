package com.oss.internet.api.application.dto;

import com.oss.internet.api.domain.DeviceStatus;
import com.oss.internet.api.domain.DeviceType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeviceDTO {
    private String deviceId;
    private String orderId;
    private DeviceType type;
    private DeviceStatus status;
    private String configuration;
}
