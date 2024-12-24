package com.oss.kos.api.application;

import com.oss.common.event.WorkflowEvent;

public interface OrderEventHandler {
    void handleOrderProvisioned(WorkflowEvent event);
    void handleOrderCompleted(WorkflowEvent event);
}
