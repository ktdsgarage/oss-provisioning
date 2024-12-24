package com.oss.internet.api.application.mapper;

import com.oss.internet.api.domain.Device;
import com.oss.internet.api.application.dto.DeviceDTO;
import org.springframework.stereotype.Component;

@Component
public class DeviceMapper {
    
    public DeviceDTO toDTO(Device device) {
        return DeviceDTO.builder()
                .deviceId(device.getDeviceId())
                .orderId(device.getOrderId())
                .type(device.getType())
                .status(device.getStatus())
                .configuration(device.getConfiguration())
                .build();
    }
    
    public Device toEntity(DeviceDTO dto) {
        Device device = new Device();
        device.setDeviceId(dto.getDeviceId());
        device.setOrderId(dto.getOrderId());
        device.setType(dto.getType());
        device.setStatus(dto.getStatus());
        device.setConfiguration(dto.getConfiguration());
        return device;
    }
}
