package com.oss.iptv.api.application;

import com.oss.common.event.WorkflowEvent;

public interface IPTVEventHandler {
    void handleAuthCompleted(WorkflowEvent event);
    void handleChannelConfigured(WorkflowEvent event);
    void handleFacilityBookUpdated(WorkflowEvent event);
}
