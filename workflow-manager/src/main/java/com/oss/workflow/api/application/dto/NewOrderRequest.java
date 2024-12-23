package com.oss.workflow.api.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewOrderRequest {
    private String orderType;
    private String productCode;
    private String customerId;
    private String requestDate;
}
