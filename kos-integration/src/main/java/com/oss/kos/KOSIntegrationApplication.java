package com.oss.kos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.oss.common.aspect",
        "com.oss.common.config",
        "com.oss.kos"  // workflow, internet, iptv, kos
})
public class KOSIntegrationApplication {
    public static void main(String[] args) {
        SpringApplication.run(KOSIntegrationApplication.class, args);
    }
}
