package com.oss.internet.api.presentation;

import com.oss.internet.api.application.InternetService;
import com.oss.internet.api.application.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인터넷 서비스", description = "인터넷 개통 관련 API")
@RestController
@RequestMapping("/internet") 
@RequiredArgsConstructor
public class InternetServiceController {

    private final InternetService internetService;
    
    @Operation(summary = "시설 처리", description = "인터넷 시설 처리를 진행합니다")
    @PostMapping("/facilities")
    public ResponseEntity<Void> processFacility(@RequestBody FacilityRequest request) {
        internetService.processFacility(request);
        return ResponseEntity.ok().build();
    }
    
    @Operation(summary = "장치 처리", description = "인터넷 장치 처리를 진행합니다")
    @PostMapping("/devices") 
    public ResponseEntity<Void> processDevice(@RequestBody DeviceRequest request) {
        internetService.processDevice(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "원부 처리", description = "인터넷 원부 정보를 처리합니다")
    @PostMapping("/facility-books")
    public ResponseEntity<Void> updateFacilityBook(@RequestBody FacilityBookRequest request) {
        internetService.updateFacilityBook(request);
        return ResponseEntity.ok().build();
    }
}
