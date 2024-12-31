package com.oss.iptv.api.infrastructure;

import com.oss.iptv.api.domain.ChannelConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ChannelConfigRepository extends JpaRepository<ChannelConfig, Long> {
    Optional<ChannelConfig> findByOrderId(String orderId);
    Optional<ChannelConfig> findByConfigId(String configId);
}