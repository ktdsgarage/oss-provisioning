package com.oss.internet.api.domain;

import com.oss.internet.enums.FacilityStatus;
import com.oss.internet.enums.FacilityType;
import com.oss.internet.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "facilities")
@Getter
@Setter
@NoArgsConstructor
public class Facility extends BaseEntity {
    
    @Column(nullable = false)
    private String facilityId;
    
    @Column(nullable = false)
    private String orderId;
    
    @Enumerated(EnumType.STRING)
    private FacilityType type;
    
    @Enumerated(EnumType.STRING)
    private FacilityStatus status;
    
    private String address;
    private String specifications;
    
    @OneToOne(mappedBy = "facility", cascade = CascadeType.ALL)
    private FacilityBook facilityBook;
}
