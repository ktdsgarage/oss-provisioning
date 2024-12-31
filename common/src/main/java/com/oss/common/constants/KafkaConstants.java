// common/src/main/java/com/oss/common/constants/KafkaConstants.java
package com.oss.common.constants;

public class KafkaConstants {
    // Internet Service Topics
    public static final String TOPIC_INTERNET_WORKFLOW_CREATED = "internet.workflow.created";
    public static final String TOPIC_INTERNET_FACILITY = "internet.facility";
    public static final String TOPIC_INTERNET_DEVICE = "internet.device";
    public static final String TOPIC_INTERNET_FACILITYBOOK = "internet.facilitybook";
    public static final String TOPIC_INTERNET_FACILITY_ALLOCATED = "internet.facility.allocated";
    public static final String TOPIC_INTERNET_DEVICE_CONFIGURED = "internet.device.configured";
    public static final String TOPIC_INTERNET_FACILITYBOOK_UPDATED = "internet.facilitybook.updated";
    public static final String TOPIC_INTERNET_WORKFLOW_ERROR = "internet.workflow.error";

    // IPTV Service Topics
    public static final String TOPIC_IPTV_WORKFLOW_CREATED = "iptv.workflow.created";
    public static final String TOPIC_IPTV_AUTH = "iptv.auth";
    public static final String TOPIC_IPTV_FACILITYBOOK = "iptv.facilitybook";
    public static final String TOPIC_IPTV_AUTH_COMPLETED = "iptv.auth.completed";
    public static final String TOPIC_IPTV_AUTH_CANCELLED = "iptv.auth.cancelled";
    public static final String TOPIC_IPTV_AUTH_SUCCESS = "iptv.auth.success";
    public static final String TOPIC_IPTV_CHANNEL_CONFIGURED = "iptv.channel.configured";
    public static final String TOPIC_IPTV_CHANNEL_SUCCESS = "iptv.channel.success";
    public static final String TOPIC_IPTV_FACILITYBOOK_UPDATED = "iptv.facilitybook.updated";
    public static final String TOPIC_IPTV_SERVICE_COMPLETED = "iptv.service.completed";
    public static final String TOPIC_IPTV_WORKFLOW_ERROR = "iptv.workflow.error";

    // Workflow Service Topics
    public static final String TOPIC_WORKFLOW_ORDER_REQUESTED = "workflow.order.requested";
    public static final String TOPIC_WORKFLOW_CREATED = "workflow.created";
    public static final String TOPIC_WORKFLOW_STARTED = "workflow.started";
    public static final String TOPIC_WORKFLOW_CANCELLED = "workflow.cancelled";
    public static final String TOPIC_WORKFLOW_ERROR = "workflow.error";
    public static final String TOPIC_WORKFLOW_COMPLETED = "workflow.completed";

    // Consumer Groups
    public static final String GROUP_INTERNET = "internet-group";
    public static final String GROUP_IPTV = "iptv-group";
    public static final String GROUP_WORKFLOW = "workflow-group";

    // Event Types
    public static final String EVENT_ORDER_REQUESTED = "ORDER_REQUESTED";
    public static final String EVENT_WORKFLOW_CREATED = "WORKFLOW_CREATED";
    public static final String EVENT_WORKFLOW_STARTED = "WORKFLOW_STARTED";
    public static final String EVENT_WORKFLOW_CANCELLED = "WORKFLOW_CANCELLED";
    public static final String EVENT_WORKFLOW_ERROR = "WORKFLOW_ERROR";
    public static final String EVENT_WORKFLOW_COMPLETED = "WORKFLOW_COMPLETED";
    public static final String EVENT_AUTH_COMPLETED = "AUTH_COMPLETED";
    public static final String EVENT_CHANNEL_CONFIGURED = "CHANNEL_CONFIGURED";
    public static final String EVENT_IPTV_SERVICE_COMPLETED = "IPTV_SERVICE_COMPLETED";
}
