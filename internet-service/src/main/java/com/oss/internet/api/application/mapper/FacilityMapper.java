package com.oss.internet.api.application.mapper;

import com.oss.internet.api.domain.Facility;
import com.oss.internet.api.application.dto.FacilityDTO;
import org.springframework.stereotype.Component;

@Component
public class FacilityMapper {
    
    public FacilityDTO toDTO(Facility facility) {
        return FacilityDTO.builder()
                .facilityId(facility.getFacilityId())
                .orderId(facility.getOrderId())
                .type(facility.getType())
                .status(facility.getStatus())
                .address(facility.getAddress())
                .specifications(facility.getSpecifications())
                .build();
    }
    
    public Facility toEntity(FacilityDTO dto) {
        Facility facility = new Facility();
        facility.setFacilityId(dto.getFacilityId());
        facility.setOrderId(dto.getOrderId());
        facility.setType(dto.getType());
        facility.setStatus(dto.getStatus());
        facility.setAddress(dto.getAddress());
        facility.setSpecifications(dto.getSpecifications());
        return facility;
    }
}}
