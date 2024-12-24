package com.oss.internet.api.application;

import com.oss.common.event.WorkflowEvent;

public interface InternetEventHandler {
    void handleFacilityAllocated(WorkflowEvent event);
    void handleDeviceConfigured(WorkflowEvent event);
    void handleFacilityBookUpdated(WorkflowEvent event);
}
