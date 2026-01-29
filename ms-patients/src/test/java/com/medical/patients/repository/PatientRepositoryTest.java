package com.medical.patients.repository;

import com.medical.patients.entity.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "eureka.client.enabled=false",
        "spring.cloud.config.enabled=false"
})
@DisplayName("Tests du Repository Patient")
class PatientRepositoryTest {

    @Autowired
    private PatientRepository patientRepository;

    private Patient testPatient;

    @BeforeEach
    void setUp() {
        // Arrange - Créer un patient de test
        testPatient = Patient.builder()
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
    @DisplayName("Devrait sauvegarder un patient")
    void testSavePatient() {
        // Act
        Patient savedPatient = patientRepository.save(testPatient);

        // Assert
        assertThat(savedPatient).isNotNull();
        assertThat(savedPatient.getId()).isNotNull();
        assertThat(savedPatient.getNom()).isEqualTo("Dupont");
        assertThat(savedPatient.getDateCreation()).isNotNull();
    }

    @Test
    @DisplayName("Devrait trouver un patient par ID")
    void testFindById() {
        // Arrange
        Patient savedPatient = patientRepository.save(testPatient);

        // Act
        Optional<Patient> found = patientRepository.findById(savedPatient.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getNom()).isEqualTo("Dupont");
    }

    @Test
    @DisplayName("Devrait trouver un patient par numéro de sécurité sociale")
    void testFindByNumeroSecu() {
        // Arrange
        patientRepository.save(testPatient);

        // Act
        Optional<Patient> found = patientRepository.findByNumeroSecu("123456789012345");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getPrenom()).isEqualTo("Jean");
    }

    @Test
    @DisplayName("Devrait trouver les patients par nom (recherche partielle)")
    void testFindByNomContaining() {
        // Arrange
        patientRepository.save(testPatient);

        Patient patient2 = Patient.builder()
                .nom("Durand")
                .prenom("Marie")
                .numeroSecu("987654321098765")
                .dateNaissance(LocalDate.of(1990, 3, 20))
                .sexe("F")
                .build();
        patientRepository.save(patient2);

        // Act
        List<Patient> foundDupont = patientRepository.findByNomContainingIgnoreCase("dup");
        List<Patient> foundDu = patientRepository.findByNomContainingIgnoreCase("du");

        // Assert
        assertThat(foundDupont).hasSize(1);
        assertThat(foundDupont.get(0).getNom()).isEqualTo("Dupont");

        assertThat(foundDu).hasSize(2); // Dupont et Durand
    }

    @Test
    @DisplayName("Devrait trouver les patients avec allergies")
    void testFindPatientsAvecAllergies() {
        // Arrange
        patientRepository.save(testPatient);

        Patient patientSansAllergies = Patient.builder()
                .nom("Martin")
                .prenom("Paul")
                .numeroSecu("111222333444555")
                .dateNaissance(LocalDate.of(1980, 1, 1))
                .sexe("M")
                .allergies(null)
                .build();
        patientRepository.save(patientSansAllergies);

        // Act
        List<Patient> patientsAvecAllergies = patientRepository.findPatientsAvecAllergies();

        // Assert
        assertThat(patientsAvecAllergies).hasSize(1);
        assertThat(patientsAvecAllergies.get(0).getAllergies()).isEqualTo("Pénicilline");
    }

    @Test
    @DisplayName("Devrait supprimer un patient")
    void testDeletePatient() {
        // Arrange
        Patient savedPatient = patientRepository.save(testPatient);
        Long patientId = savedPatient.getId();

        // Act
        patientRepository.deleteById(patientId);

        // Assert
        Optional<Patient> deletedPatient = patientRepository.findById(patientId);
        assertThat(deletedPatient).isEmpty();
    }

    @Test
    @DisplayName("Devrait mettre à jour un patient")
    void testUpdatePatient() {
        // Arrange
        Patient savedPatient = patientRepository.save(testPatient);

        // Act
        savedPatient.setTelephone("0698765432");
        savedPatient.setEmail("nouveau.email@test.com");
        Patient updatedPatient = patientRepository.save(savedPatient);

        // Assert
        assertThat(updatedPatient.getTelephone()).isEqualTo("0698765432");
        assertThat(updatedPatient.getEmail()).isEqualTo("nouveau.email@test.com");
        assertThat(updatedPatient.getDateModification()).isAfter(updatedPatient.getDateCreation());
    }
}