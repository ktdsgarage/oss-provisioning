package com.oss.internet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.oss.common.aspect",
        "com.oss.common.config",
        "com.oss.internet"  // workflow, internet, iptv, kos
})
public class InternetServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(InternetServiceApplication.class, args);
    }
}
