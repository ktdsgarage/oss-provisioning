package com.oss.internet.api.application.dto;

import com.oss.internet.api.domain.BookStatus;
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
