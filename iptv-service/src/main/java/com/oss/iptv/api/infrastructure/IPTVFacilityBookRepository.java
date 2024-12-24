package com.oss.iptv.api.infrastructure;

import com.oss.iptv.api.domain.IPTVFacilityBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IPTVFacilityBookRepository extends JpaRepository<IPTVFacilityBook, Long> {
    Optional<IPTVFacilityBook> findByOrderId(String orderId);

    Optional<IPTVFacilityBook> findByBookId(String bookId);
}

