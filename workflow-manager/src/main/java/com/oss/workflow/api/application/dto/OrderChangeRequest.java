package com.oss.workflow.api.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderChangeRequest {
    private String changeType;
    private String changeDetails;
    private String requestDate;
}
