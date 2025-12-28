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
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Slf4j
public class PatientController extends BaseController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<ApiResponse<PatientDTO>> createPatient(@Valid @RequestBody PatientDTO patientDTO) {
        log.info("POST /api/patients - Création patient");
        PatientDTO created = patientService.createPatient(patientDTO);
        return success(created, "Patient créé avec succès", HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientDTO>> getPatientById(@PathVariable Long id) {
        log.info("GET /api/patients/{} - Récupération patient", id);
        PatientDTO patient = patientService.getPatientById(id);
        return success(patient, "Patient récupéré avec succès", HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PatientDTO>>> getAllPatients() {
        log.info("GET /api/patients - Récupération tous patients");
        List<PatientDTO> patients = patientService.getAllPatients();
        return success(patients, "Liste des patients récupérée", HttpStatus.OK);
    }

    @GetMapping("/secu/{numeroSecu}")
    public ResponseEntity<ApiResponse<PatientDTO>> getPatientByNumeroSecu(@PathVariable String numeroSecu) {
        log.info("GET /api/patients/secu/{} - Recherche par numéro sécu", numeroSecu);
        PatientDTO patient = patientService.getPatientByNumeroSecu(numeroSecu);
        return success(patient, "Patient trouvé", HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PatientDTO>>> searchPatients(@RequestParam String nom) {
        log.info("GET /api/patients/search - Recherche par nom : {}", nom);
        List<PatientDTO> patients = patientService.searchByNom(nom);
        return success(patients, "Recherche effectuée", HttpStatus.OK);
    }

    @GetMapping("/with-allergies")
    public ResponseEntity<ApiResponse<List<PatientDTO>>> getPatientsWithAllergies() {
        log.info("GET /api/patients/with-allergies");
        List<PatientDTO> patients = patientService.getPatientsAvecAllergies();
        return success(patients, "Patients avec allergies récupérés", HttpStatus.OK);
    }

    @GetMapping("/with-chronic-diseases")
    public ResponseEntity<ApiResponse<List<PatientDTO>>> getPatientsWithChronicDiseases() {
        log.info("GET /api/patients/with-chronic-diseases");
        List<PatientDTO> patients = patientService.getPatientsAvecMaladiesChroniques();
        return success(patients, "Patients avec maladies chroniques récupérés", HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientDTO>> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientDTO patientDTO) {
        log.info("PUT /api/patients/{} - Mise à jour patient", id);
        PatientDTO updated = patientService.updatePatient(id, patientDTO);
        return success(updated, "Patient mis à jour avec succès", HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePatient(@PathVariable Long id) {
        log.info("DELETE /api/patients/{} - Suppression patient", id);
        patientService.deletePatient(id);
        return success(null, "Patient supprimé avec succès", HttpStatus.OK);
    }
}