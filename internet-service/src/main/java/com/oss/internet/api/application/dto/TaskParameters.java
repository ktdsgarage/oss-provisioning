// internet-service/src/main/java/com/oss/internet/api/application/dto/TaskParameters.java
package com.oss.internet.api.application.dto;

import com.oss.internet.enums.FacilityType;
import com.oss.internet.enums.TaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskParameters {
    private String taskId;
    private TaskType taskType;
    private String orderId;
    private String deviceId;
    private FacilityType facilityType;

    // Builder를 별도로 정의할 필요 없음 - Lombok이 생성
}