package com.medical.patients.controller;

import com.medical.patients.dto.PatientDTO;
import com.medical.patients.dto.DossierDTO;
import com.medical.patients.dto.ApiResponse;
import com.medical.patients.service.PatientService;
import com.medical.patients.service.DossierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
@RestController
@RequestMapping("/api/health")
@Slf4j
public class HealthController {

    @GetMapping
    public ResponseEntity<ApiResponse<String>> health() {
        log.info("GET /api/health - Health check");
        return ResponseEntity.ok(ApiResponse.ok("MS Patients - OK", "Service en bonne santé"));
    }
}