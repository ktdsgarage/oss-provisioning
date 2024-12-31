package com.oss.iptv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.oss.common.aspect",
        "com.oss.common.config",
        "com.oss.iptv"  // workflow, internet, iptv, kos
})
public class IPTVServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(IPTVServiceApplication.class, args);
    }
}
