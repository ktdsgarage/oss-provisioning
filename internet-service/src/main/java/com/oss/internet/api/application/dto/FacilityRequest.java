package com.oss.internet.api.application.dto;

import com.oss.internet.api.domain.FacilityType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "시설 처리 요청")
@Getter
@Setter
public class FacilityRequest {
    
    @Schema(description = "오더 ID", example = "ORD20241223001")
    @NotBlank(message = "오더 ID는 필수입니다")
    private String orderId;
    
    @Schema(description = "시설 유형", example = "OPTICAL_LINE")
    @NotNull(message = "시설 유형은 필수입니다")
    private FacilityType facilityType;
    
    @Schema(description = "설치 주소", example = "서울시 강남구 역삼동 123-45")
    @NotBlank(message = "설치 주소는 필수입니다")
    private String address;
    
    @Schema(description = "건물 유형", example = "APARTMENT")
    private String buildingType;
    
    @Schema(description = "설치 층수", example = "15")
    private Integer floor;
    
    @Schema(description = "상세 위치 정보", example = "1502호")
    private String locationDetail;
    
    @Schema(description = "기존 시설물 ID", example = "FAC20241223001")
    private String existingFacilityId;
    
    @Schema(description = "시설 사양", example = "1Gbps")
    private String specifications;
    
    @Schema(description = "특이사항", example = "지하 주차장 내 분배함 위치")
    private String remarks;
    
    @Schema(description = "작업자 ID", example = "EMP001")
    private String operatorId;
    
    @Schema(description = "희망 작업 일자", example = "2024-12-24")
    private String requestedDate;
    
    @Schema(description = "희망 작업 시간대", example = "MORNING")
    private String requestedTimeSlot;
}
