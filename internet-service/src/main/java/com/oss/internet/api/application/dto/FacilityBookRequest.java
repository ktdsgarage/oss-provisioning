package com.oss.internet.api.application.dto;

import com.oss.internet.api.domain.BookStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilityBookRequest {
    private String orderId;
    private String facilityId;
    private String deviceId;
    private BookStatus status;
    private String remarks;        // 특이사항
    private String operatorId;     // 작업자 ID
    private String workDetails;    // 작업 상세 내용
}
