package com.oss.internet.api.domain;

import com.oss.internet.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "facility_books")
@Getter
@Setter
@NoArgsConstructor
public class FacilityBook extends BaseEntity {

    @Column(nullable = false)
    private String bookId;

    @Column(nullable = false)
    private String orderId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id")
    private Facility facility;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;

    @Enumerated(EnumType.STRING)
    private BookStatus status;

    @Column(length = 1000)
    private String remarks;

    private String operatorId;

    @Column(length = 4000)
    private String workDetails;
}
