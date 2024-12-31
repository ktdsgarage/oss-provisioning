package com.oss.workflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.oss.common.aspect",
        "com.oss.common.config",
        "com.oss.workflow"  // workflow, internet, iptv, kos
})
public class WorkflowManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkflowManagerApplication.class, args);
    }
}
