package com.oss.internet.api.infrastructure;

import com.oss.internet.api.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByOrderId(String orderId);
    Optional<Device> findByDeviceId(String deviceId);
}
