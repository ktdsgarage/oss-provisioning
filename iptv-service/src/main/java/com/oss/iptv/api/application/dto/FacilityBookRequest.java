package com.oss.iptv.api.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilityBookRequest {
    private String orderId;
    private Long authId;  // String -> Long으로 변경
    private String status;
}