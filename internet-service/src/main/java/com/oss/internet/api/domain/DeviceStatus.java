package com.oss.internet.api.domain;

public enum DeviceStatus {
    AVAILABLE,       // 사용 가능
    RESERVED,        // 예약됨
    IN_USE,          // 사용 중
    CONFIGURING,     // 설정 중
    CONFIGURED,      // 설정 완료
    ERROR,           // 오류 상태
    MAINTENANCE      // 유지보수 중
}
