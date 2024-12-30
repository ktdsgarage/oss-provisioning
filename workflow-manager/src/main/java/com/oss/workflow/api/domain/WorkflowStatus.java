package com.oss.workflow.api.domain;

public enum WorkflowStatus {
    NEW,            // 신규
    IN_PROGRESS,    // 진행 중
    COMPLETED,      // 완료
    FAILED,         // 실패
    CANCELLED,      // 취소됨
    SUSPENDED       // 일시 중단
}