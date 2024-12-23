package com.oss.internet.api.domain;

public enum TimeSlot {
    MORNING("09:00-12:00"),
    AFTERNOON("13:00-17:00"),
    EVENING("17:00-21:00");
    
    private final String timeRange;
    
    TimeSlot(String timeRange) {
        this.timeRange = timeRange;
    }
    
    public String getTimeRange() {
        return timeRange;
    }
}
