package com.oss.iptv.api.presentation;

import com.oss.iptv.api.application.IPTVService;
import com.oss.iptv.api.application.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "IPTV 서비스", description = "IPTV 개통 관련 API")
@RestController
@RequestMapping("/iptv")
@RequiredArgsConstructor
public class IPTVServiceController {

    private final IPTVService iptvService;
    
    @Operation(summary = "인증 처리", description = "IPTV 인증 처리를 진행합니다")
    @PostMapping("/auth")
    public ResponseEntity<Void> processAuth(@RequestBody AuthRequest request) {
        iptvService.processAuth(request);
        return ResponseEntity.ok().build();
    }
    
    @Operation(summary = "원부 처리", description = "IPTV 원부 정보를 처리합니다")
    @PostMapping("/facility-books")
    public ResponseEntity<Void> updateFacilityBook(@RequestBody FacilityBookRequest request) {
        iptvService.updateFacilityBook(request);
        return ResponseEntity.ok().build();
    }
}
