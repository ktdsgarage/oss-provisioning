package com.oss.internet.api.domain;

import com.oss.internet.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "devices")
@Getter
@Setter
@NoArgsConstructor
public class Device extends BaseEntity {
    
    @Column(nullable = false)
    private String deviceId;
    
    @Column(nullable = false)
    private String orderId;
    
    @Enumerated(EnumType.STRING)
    private DeviceType type;
    
    @Enumerated(EnumType.STRING)
    private DeviceStatus status;
    
    @Column(length = 4000)
    private String configuration;
    
    @OneToOne(mappedBy = "device", cascade = CascadeType.ALL)
    private FacilityBook facilityBook;
}
