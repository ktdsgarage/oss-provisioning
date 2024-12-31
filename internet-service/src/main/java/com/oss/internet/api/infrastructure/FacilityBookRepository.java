package com.oss.internet.api.infrastructure;

import com.oss.internet.api.domain.FacilityBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FacilityBookRepository extends JpaRepository<FacilityBook, Long> {
    Optional<FacilityBook> findByOrderId(String orderId);
}