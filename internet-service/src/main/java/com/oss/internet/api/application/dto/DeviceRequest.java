package com.oss.internet.api.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceRequest {
    private String orderId;
    private String deviceId;
    private String configuration;
}
