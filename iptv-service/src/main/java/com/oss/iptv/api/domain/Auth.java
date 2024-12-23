package com.oss.iptv.api.domain;

import com.oss.iptv.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "auths")
@Getter
@Setter
@NoArgsConstructor
public class Auth extends BaseEntity {
    
    @Column(nullable = false)
    private String authId;
    
    @Column(nullable = false)
    private String orderId;
    
    @Enumerated(EnumType.STRING)
    private AuthStatus status;
    
    @Column(length = 4000)
    private String channelConfig;
    
    private LocalDateTime validationDate;
}
