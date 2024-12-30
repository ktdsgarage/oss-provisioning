package com.oss.workflow.api.domain;

public enum TaskStatus {
    PENDING,        // 대기
    SCHEDULED,      // 예약됨
    IN_PROGRESS,    // 실행 중
    COMPLETED,      // 완료
    FAILED,         // 실패
    CANCELLED,      // 취소됨
    RETRYING,       // 재시도 중
    TIMEOUT         // 시간 초과
}