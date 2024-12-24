package com.oss.kos.api.domain;

import com.oss.kos.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order extends BaseEntity {

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String orderType;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false)
    private String productCode;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime requestDate;
    private LocalDateTime completionDate;
}
