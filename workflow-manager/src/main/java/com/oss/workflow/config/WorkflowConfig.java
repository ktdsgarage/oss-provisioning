// workflow-manager/src/main/java/com/oss/workflow/config/WorkflowConfig.java
package com.oss.workflow.config;

import com.oss.workflow.api.application.scheduler.SchedulePolicy;
import com.oss.workflow.api.application.supervisor.MonitoringPolicy;
import com.oss.workflow.api.application.supervisor.RetryPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkflowConfig {

    @Bean
    public MonitoringPolicy monitoringPolicy() {
        return MonitoringPolicy.builder()
                .healthCheckIntervalSeconds(30)
                .taskTimeoutSeconds(300)
                .maxConsecutiveFailures(3)
                .alertEnabled(true)
                .build();
    }

    @Bean
    public RetryPolicy retryPolicy() {
        return RetryPolicy.builder()
                .maxRetries(3)
                .initialDelaySeconds(5)
                .maxDelaySeconds(60)
                .backoffMultiplier(2.0)
                .exponentialBackoff(true)
                .build();
    }

    @Bean
    public SchedulePolicy schedulePolicy() {
        return SchedulePolicy.builder()
                .maxConcurrentTasks(10)
                .defaultTimeoutSeconds(300)
                .minRetryIntervalSeconds(5)
                .maxRetryIntervalSeconds(60)
                .exponentialBackoff(true)
                .build();
    }
}
