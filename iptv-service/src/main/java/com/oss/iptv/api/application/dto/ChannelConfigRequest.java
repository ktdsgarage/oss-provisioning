package com.oss.iptv.api.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChannelConfigRequest {
    private String orderId;
    private String channelConfig;
}