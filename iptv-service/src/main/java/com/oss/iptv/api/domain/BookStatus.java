package com.oss.iptv.api.domain;

public enum BookStatus {
    PENDING("대기중"),
    IN_PROGRESS("처리중"),
    COMPLETED("완료"),
    FAILED("실패"),
    CANCELLED("취소됨");
    
    private final String description;
    
    BookStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
