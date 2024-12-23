package com.oss.internet.api.application.mapper;

import com.oss.internet.api.domain.FacilityBook;
import com.oss.internet.api.application.dto.FacilityBookDTO;
import com.oss.internet.api.application.dto.FacilityBookResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class FacilityBookMapper {
    
    public FacilityBookDTO toDTO(FacilityBook facilityBook) {
        return FacilityBookDTO.builder()
                .bookId(facilityBook.getBookId())
                .orderId(facilityBook.getOrderId())
                .facilityId(facilityBook.getFacility().getFacilityId())
                .deviceId(facilityBook.getDevice() != null ? facilityBook.getDevice().getDeviceId() : null)
                .status(facilityBook.getStatus())
                .build();
    }
    
    public FacilityBookResponseDTO toResponseDTO(FacilityBook facilityBook) {
        return FacilityBookResponseDTO.builder()
                .bookId(facilityBook.getBookId())
                .orderId(facilityBook.getOrderId())
                .facilityId(facilityBook.getFacility().getFacilityId())
                .deviceId(facilityBook.getDevice() != null ? facilityBook.getDevice().getDeviceId() : null)
                .status(facilityBook.getStatus())
                .remarks(facilityBook.getRemarks())
                .operatorId(facilityBook.getOperatorId())
                .workDetails(facilityBook.getWorkDetails())
                .processedAt(facilityBook.getUpdatedAt())
                .resultMessage(generateResultMessage(facilityBook))
                .build();
    }
    
    private String generateResultMessage(FacilityBook facilityBook) {
        return switch (facilityBook.getStatus()) {
            case COMPLETED -> "원부 구축이 완료되었습니다.";
            case FAILED -> "원부 구축 중 오류가 발생했습니다.";
            case IN_PROGRESS -> "원부 구축이 진행 중입니다.";
            case PENDING -> "원부 구축이 대기 중입니다.";
            case CANCELLED -> "원부 구축이 취소되었습니다.";
        };
    }
}
