package com.oss.internet.api.infrastructure;

import com.oss.internet.api.domain.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FacilityRepository extends JpaRepository<Facility, Long> {
    Optional<Facility> findByOrderId(String orderId);
}
