package com.oss.internet.api.application.dto;

import com.oss.internet.api.domain.BookStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FacilityBookDTO {
    private String bookId;
    private String orderId;
    private String facilityId;
    private String deviceId;
    private BookStatus status;
}
