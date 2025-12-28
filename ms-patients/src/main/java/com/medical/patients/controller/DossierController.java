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
@RequestMapping("/api/dossiers")
@RequiredArgsConstructor
@Slf4j
public class DossierController extends BaseController {

    private final DossierService dossierService;

    @PostMapping
    public ResponseEntity<ApiResponse<DossierDTO>> createDossier(@Valid @RequestBody DossierDTO dossierDTO) {
        log.info("POST /api/dossiers - Création dossier");
        DossierDTO created = dossierService.createDossier(dossierDTO);
        return success(created, "Dossier créé avec succès", HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DossierDTO>> getDossierById(@PathVariable Long id) {
        log.info("GET /api/dossiers/{} - Récupération dossier", id);
        DossierDTO dossier = dossierService.getDossierById(id);
        return success(dossier, "Dossier récupéré avec succès", HttpStatus.OK);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<DossierDTO>>> getDossiersByPatientId(@PathVariable Long patientId) {
        log.info("GET /api/dossiers/patient/{} - Récupération dossiers patient", patientId);
        List<DossierDTO> dossiers = dossierService.getDossiersByPatientId(patientId);
        return success(dossiers, "Dossiers du patient récupérés", HttpStatus.OK);
    }

    @GetMapping("/patient/{patientId}/last")
    public ResponseEntity<ApiResponse<List<DossierDTO>>> getLastDossiersByPatient(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "10") int limit) {
        log.info("GET /api/dossiers/patient/{}/last - Derniers {} dossiers", patientId, limit);
        List<DossierDTO> dossiers = dossierService.getLastNDossiersByPatient(patientId, limit);
        return success(dossiers, "Derniers dossiers récupérés", HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DossierDTO>> updateDossier(
            @PathVariable Long id,
            @Valid @RequestBody DossierDTO dossierDTO) {
        log.info("PUT /api/dossiers/{} - Mise à jour dossier", id);
        DossierDTO updated = dossierService.updateDossier(id, dossierDTO);
        return success(updated, "Dossier mis à jour avec succès", HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDossier(@PathVariable Long id) {
        log.info("DELETE /api/dossiers/{} - Suppression dossier", id);
        dossierService.deleteDossier(id);
        return success(null, "Dossier supprimé avec succès", HttpStatus.OK);
    }
}
