package com.oss.workflow.api.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeploymentStatusDTO {
    private String deploymentId;
    private String status;
    private String version;
}
