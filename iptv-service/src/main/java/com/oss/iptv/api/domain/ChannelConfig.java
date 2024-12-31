package com.oss.iptv.api.domain;

import com.oss.iptv.common.BaseEntity;
import com.oss.iptv.enums.ConfigStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "channel_configs")
@Getter
@Setter
@NoArgsConstructor
public class ChannelConfig extends BaseEntity {

    @Column(nullable = false)
    private String configId;

    @Column(nullable = false)
    private String orderId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_id")
    private Auth auth;

    private String channelPackage;
    private String channelList;
    private String quality;
    private String stbConfig;

    @Enumerated(EnumType.STRING)
    private ConfigStatus status;
}
