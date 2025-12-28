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

@Slf4j
@RequiredArgsConstructor
public class BaseController {
    protected <T> ResponseEntity<ApiResponse<T>> success(T data, String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(ApiResponse.ok(data, message));
    }

    protected <T> ResponseEntity<ApiResponse<T>> error(String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(ApiResponse.error(message));
    }
}