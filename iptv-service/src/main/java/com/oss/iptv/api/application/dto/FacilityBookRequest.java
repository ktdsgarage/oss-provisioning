package com.oss.iptv.api.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilityBookRequest {
    private String orderId;
    private String authId;
    private String status;
}
