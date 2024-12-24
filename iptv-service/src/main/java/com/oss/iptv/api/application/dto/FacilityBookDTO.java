package com.oss.iptv.api.application.dto;

import com.oss.iptv.api.domain.BookStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FacilityBookDTO {
    private String bookId;
    private String orderId;
    private String authId;
    private BookStatus status;
    private String remarks;
    private String operatorId;
    private String workDetails;
}
