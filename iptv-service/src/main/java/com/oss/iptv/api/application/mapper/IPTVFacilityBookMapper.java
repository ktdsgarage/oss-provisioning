package com.oss.iptv.api.application.mapper;

import com.oss.iptv.api.domain.IPTVFacilityBook;
import com.oss.iptv.api.application.dto.FacilityBookDTO;
import org.springframework.stereotype.Component;

@Component
public class IPTVFacilityBookMapper {
    
    public FacilityBookDTO toDTO(IPTVFacilityBook facilityBook) {
        return FacilityBookDTO.builder()
                .bookId(facilityBook.getBookId())
                .orderId(facilityBook.getOrderId())
                .authId(facilityBook.getAuth().getAuthId())
                .status(facilityBook.getStatus())
                .remarks(facilityBook.getRemarks())
                .operatorId(facilityBook.getOperatorId())
                .workDetails(facilityBook.getWorkDetails())
                .build();
    }

    public IPTVFacilityBook toEntity(FacilityBookDTO dto) {
        IPTVFacilityBook facilityBook = new IPTVFacilityBook();
        facilityBook.setBookId(dto.getBookId());
        facilityBook.setOrderId(dto.getOrderId());
        facilityBook.setStatus(dto.getStatus());
        facilityBook.setRemarks(dto.getRemarks());
        facilityBook.setOperatorId(dto.getOperatorId());
        facilityBook.setWorkDetails(dto.getWorkDetails());
        return facilityBook;
    }
}
