package com.oss.kos.api.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProvisioningRequest {
    private String orderType;
    private String productCode;
    private String customerId;
    private String address;
    private String requestDate;
}
