package com.oss.internet.api.application;

import com.oss.internet.api.application.dto.*;
import com.oss.internet.api.domain.*;
import com.oss.internet.api.domain.Facility;
import com.oss.internet.api.domain.Device;
import com.oss.internet.api.infrastructure.FacilityRepository;
import com.oss.internet.api.infrastructure.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InternetService {

    private final FacilityRepository facilityRepository;
    private final DeviceRepository deviceRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public void processFacility(FacilityRequest request) {
        Facility facility = new Facility();
        facility.setOrderId(request.getOrderId());
        facility.setType(request.getFacilityType());
        facility.setStatus(FacilityStatus.PROCESSING);
        
        facilityRepository.save(facility);
        
        kafkaTemplate.send("internet.facility", facility);
    }

    @Transactional
    public void processDevice(DeviceRequest request) {
        Device device = new Device();
        device.setOrderId(request.getOrderId());
        device.setDeviceId(request.getDeviceId());
        device.setStatus(DeviceStatus.CONFIGURING);
        
        deviceRepository.save(device);
        
        kafkaTemplate.send("internet.device", device);
    }

    @Transactional
    public void updateFacilityBook(FacilityBookRequest request) {
        // 원부 정보 업데이트 로직
        kafkaTemplate.send("internet.facilitybook", request);
    }
}
