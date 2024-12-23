package com.oss.iptv.api.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ChannelConfigDTO {
    private String packageId;
    private String packageName;
    private List<Channel> channels;
    
    @Getter
    @Builder
    public static class Channel {
        private String channelId;
        private String channelName;
        private String genre;
        private boolean isHD;
    }
}
