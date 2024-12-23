package com.oss.workflow.api.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NextProcessDTO {
    private String currentProcess;
    private String nextProcess; 
    private String estimatedTime;
}
