package com.oss.internet.api.application;

import com.oss.common.event.WorkflowEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InternetEventHandlerImpl implements InternetEventHandler {
    
    private final InternetService internetService;

    @KafkaListener(topics = "internet.facility.allocated")
    @Override
    public void handleFacilityAllocated(WorkflowEvent event) {
        // Handle facility allocation event
    }

    @KafkaListener(topics = "internet.device.configured")
    @Override
    public void handleDeviceConfigured(WorkflowEvent event) {
        // Handle device configuration event
    }

    @KafkaListener(topics = "internet.facilitybook.updated")
    @Override
    public void handleFacilityBookUpdated(WorkflowEvent event) {
        // Handle facility book update event
    }
}
