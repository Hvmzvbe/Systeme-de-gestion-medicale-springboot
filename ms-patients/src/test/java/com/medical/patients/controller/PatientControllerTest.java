package com.medical.patients.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.patients.dto.PatientDTO;
import com.medical.patients.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
@DisplayName("Tests du Controller Patient")
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PatientService patientService;

    private PatientDTO testPatientDTO;

    @BeforeEach
    void setUp() {
        testPatientDTO = PatientDTO.builder()
                .id(1L)
                .nom("Dupont")
                .prenom("Jean")
                .numeroSecu("123456789012345")
                .dateNaissance(LocalDate.of(1985, 5, 15))
                .sexe("M")
                .adresse("12 Rue de la Santé")
                .codePostal("75014")
                .ville("Paris")
                .telephone("0612345678")
                .email("jean.dupont@email.com")
                .build();
    }

    @Test
    @DisplayName("POST /api/patients - Devrait créer un patient")
    void testCreatePatient() throws Exception {
        // Arrange
        when(patientService.createPatient(any(PatientDTO.class)))
                .thenReturn(testPatientDTO);

        // Act & Assert
        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPatientDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nom").value("Dupont"))
                .andExpect(jsonPath("$.data.prenom").value("Jean"));
    }

    @Test
    @DisplayName("GET /api/patients/{id} - Devrait récupérer un patient")
    void testGetPatientById() throws Exception {
        // Arrange
        when(patientService.getPatientById(1L)).thenReturn(testPatientDTO);

        // Act & Assert
        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.nom").value("Dupont"));
    }

    @Test
    @DisplayName("GET /api/patients - Devrait récupérer tous les patients")
    void testGetAllPatients() throws Exception {
        // Arrange
        when(patientService.getAllPatients())
                .thenReturn(Arrays.asList(testPatientDTO));

        // Act & Assert
        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].nom").value("Dupont"));
    }

    @Test
    @DisplayName("PUT /api/patients/{id} - Devrait mettre à jour un patient")
    void testUpdatePatient() throws Exception {
        // Arrange
        when(patientService.updatePatient(anyLong(), any(PatientDTO.class)))
                .thenReturn(testPatientDTO);

        // Act & Assert
        mockMvc.perform(put("/api/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPatientDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("DELETE /api/patients/{id} - Devrait supprimer un patient")
    void testDeletePatient() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /api/patients - Devrait valider les données (validation échouée)")
    void testCreatePatient_ValidationFailed() throws Exception {
        // Arrange
        PatientDTO invalidPatient = PatientDTO.builder()
                .nom("") // Nom vide - devrait échouer
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPatient)))
                .andExpect(status().isBadRequest());
    }
}