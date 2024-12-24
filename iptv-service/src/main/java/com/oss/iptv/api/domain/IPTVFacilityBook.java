package com.oss.iptv.api.domain;

import com.oss.iptv.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "iptv_facility_books")
@Getter
@Setter
@NoArgsConstructor
public class IPTVFacilityBook extends BaseEntity {

    @Column(nullable = false)
    private String bookId;

    @Column(nullable = false)
    private String orderId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_id")
    private Auth auth;

    @Enumerated(EnumType.STRING)
    private BookStatus status;

    private String remarks;
    private String operatorId;
    private String workDetails;
}
