package com.medical.patients.service;

import com.medical.patients.dto.PatientDTO;
import com.medical.patients.entity.Patient;
import com.medical.patients.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Service Patient")
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient testPatient;
    private PatientDTO testPatientDTO;

    @BeforeEach
    void setUp() {
        testPatient = Patient.builder()
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
                .allergies("Pénicilline")
                .maladiesChroniques("Diabète")
                .dateCreation(LocalDateTime.now())
                .dateModification(LocalDateTime.now())
                .build();

        testPatientDTO = PatientDTO.builder()
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
                .allergies("Pénicilline")
                .maladiesChroniques("Diabète")
                .build();
    }

    @Test
    @DisplayName("Devrait créer un patient avec succès")
    void testCreatePatient() {
        // Arrange
        when(patientRepository.save(any(Patient.class))).thenReturn(testPatient);

        // Act
        PatientDTO result = patientService.createPatient(testPatientDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNom()).isEqualTo("Dupont");

        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    @DisplayName("Devrait récupérer un patient par ID")
    void testGetPatientById() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));

        // Act
        PatientDTO result = patientService.getPatientById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNom()).isEqualTo("Dupont");

        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Devrait lever une exception si patient non trouvé")
    void testGetPatientById_NotFound() {
        // Arrange
        when(patientRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> patientService.getPatientById(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Patient introuvable");

        verify(patientRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Devrait récupérer tous les patients")
    void testGetAllPatients() {
        // Arrange
        Patient patient2 = Patient.builder()
                .id(2L)
                .nom("Martin")
                .prenom("Marie")
                .numeroSecu("987654321098765")
                .dateNaissance(LocalDate.of(1990, 3, 20))
                .sexe("F")
                .dateCreation(LocalDateTime.now())
                .dateModification(LocalDateTime.now())
                .build();

        when(patientRepository.findAll()).thenReturn(Arrays.asList(testPatient, patient2));

        // Act
        List<PatientDTO> results = patientService.getAllPatients();

        // Assert
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getNom()).isEqualTo("Dupont");
        assertThat(results.get(1).getNom()).isEqualTo("Martin");

        verify(patientRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Devrait mettre à jour un patient")
    void testUpdatePatient() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(patientRepository.save(any(Patient.class))).thenReturn(testPatient);

        PatientDTO updateDTO = PatientDTO.builder()
                .nom("Dupont")
                .prenom("Jean-Claude")
                .numeroSecu("123456789012345")
                .dateNaissance(LocalDate.of(1985, 5, 15))
                .sexe("M")
                .adresse("15 Avenue des Champs")
                .codePostal("75008")
                .ville("Paris")
                .telephone("0698765432")
                .email("nouveau@email.com")
                .build();

        // Act
        PatientDTO result = patientService.updatePatient(1L, updateDTO);

        // Assert
        assertThat(result).isNotNull();
        verify(patientRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    @DisplayName("Devrait supprimer un patient")
    void testDeletePatient() {
        // Arrange
        when(patientRepository.existsById(1L)).thenReturn(true);
        doNothing().when(patientRepository).deleteById(1L);

        // Act
        patientService.deletePatient(1L);

        // Assert
        verify(patientRepository, times(1)).existsById(1L);
        verify(patientRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Devrait lever une exception si tentative de suppression d'un patient inexistant")
    void testDeletePatient_NotFound() {
        // Arrange
        when(patientRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> patientService.deletePatient(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Patient introuvable");

        verify(patientRepository, times(1)).existsById(999L);
        verify(patientRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Devrait rechercher des patients par nom")
    void testSearchByNom() {
        // Arrange
        when(patientRepository.findByNomContainingIgnoreCase("dup"))
                .thenReturn(Arrays.asList(testPatient));

        // Act
        List<PatientDTO> results = patientService.searchByNom("dup");

        // Assert
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getNom()).isEqualTo("Dupont");

        verify(patientRepository, times(1)).findByNomContainingIgnoreCase("dup");
    }
}