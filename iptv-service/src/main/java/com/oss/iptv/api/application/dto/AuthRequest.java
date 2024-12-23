package com.oss.iptv.api.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {
    private String orderId;
    private String productCode;
    private String customerType;
}
