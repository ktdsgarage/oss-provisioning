// iptv-service/src/main/java/com/oss/iptv/api/infrastructure/AuthRepository.java
package com.oss.iptv.api.infrastructure;

import com.oss.iptv.api.domain.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    Optional<Auth> findByTaskId(String taskId);
}