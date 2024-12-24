package com.oss.kos.api.domain;

public enum OrderStatus {
    CREATED("생성됨"),
    IN_PROGRESS("처리중"),
    COMPLETED("완료"),
    FAILED("실패"),
    CANCELLED("취소됨");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
