package com.oss.iptv.api.application;

import com.oss.common.event.WorkflowEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IPTVEventHandlerImpl implements IPTVEventHandler {
    
    private final IPTVService iptvService;

    @KafkaListener(topics = "iptv.auth.completed")
    @Override
    public void handleAuthCompleted(WorkflowEvent event) {
        // Handle auth completion event
    }

    @KafkaListener(topics = "iptv.channel.configured")
    @Override
    public void handleChannelConfigured(WorkflowEvent event) {
        // Handle channel configuration event
    }

    @KafkaListener(topics = "iptv.facilitybook.updated")
    @Override
    public void handleFacilityBookUpdated(WorkflowEvent event) {
        // Handle facility book update event
    }
}
