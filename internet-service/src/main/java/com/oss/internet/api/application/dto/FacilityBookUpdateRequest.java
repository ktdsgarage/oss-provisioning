package com.oss.internet.api.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilityBookUpdateRequest {
    private String bookId;
    private BookStatus newStatus;
    private String remarks;
    private String workDetails;
}
