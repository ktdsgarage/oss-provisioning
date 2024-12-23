package com.oss.internet.api.application.dto;

import com.oss.internet.api.domain.FacilityStatus;
import com.oss.internet.api.domain.FacilityType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FacilityDTO {
    private String facilityId;
    private String orderId;
    private FacilityType type;
    private FacilityStatus status;
    private String address;
    private String specifications;
}
