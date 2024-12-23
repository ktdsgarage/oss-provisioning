package com.oss.internet.api.application.dto;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class FacilityRequestValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return FacilityRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        FacilityRequest request = (FacilityRequest) target;
        
        // 건물 유형이 있는 경우 층수 필수
        if (request.getBuildingType() != null && request.getFloor() == null) {
            errors.rejectValue("floor", "field.required", "건물 유형이 있는 경우 층수는 필수입니다");
        }
        
        // 기존 시설물 ID가 있는 경우 유효성 검사
        if (request.getExistingFacilityId() != null && !isValidFacilityId(request.getExistingFacilityId())) {
            errors.rejectValue("existingFacilityId", "field.invalid", "유효하지 않은 시설물 ID입니다");
        }
        
        // 작업 희망일자 유효성 검사
        if (request.getRequestedDate() != null && !isValidDateFormat(request.getRequestedDate())) {
            errors.rejectValue("requestedDate", "field.invalid", "유효하지 않은 날짜 형식입니다 (YYYY-MM-DD)");
        }
        
        // 작업 시간대 유효성 검사
        if (request.getRequestedTimeSlot() != null && !isValidTimeSlot(request.getRequestedTimeSlot())) {
            errors.rejectValue("requestedTimeSlot", "field.invalid", "유효하지 않은 시간대입니다 (MORNING, AFTERNOON, EVENING)");
        }
    }
    
    private boolean isValidFacilityId(String facilityId) {
        return facilityId.matches("FAC\\d{11}");
    }
    
    private boolean isValidDateFormat(String date) {
        return date.matches("\\d{4}-\\d{2}-\\d{2}");
    }
    
    private boolean isValidTimeSlot(String timeSlot) {
        return timeSlot.matches("MORNING|AFTERNOON|EVENING");
    }
}
