package com.oss.internet.api.application.dto;

import com.oss.internet.api.domain.BookStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FacilityBookResponseDTO {
    private String bookId;
    private String orderId;
    private String facilityId;
    private String deviceId;
    private BookStatus status;
    private String remarks;
    private String operatorId;
    private String workDetails;
    private LocalDateTime processedAt;
    private String resultMessage;
}
