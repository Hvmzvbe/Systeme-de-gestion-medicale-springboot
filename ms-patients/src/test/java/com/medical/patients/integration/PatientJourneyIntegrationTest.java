package com.medical.patients.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.patients.dto.ApiResponse;
import com.medical.patients.dto.DossierDTO;
import com.medical.patients.dto.PatientDTO;
import com.medical.patients.entity.Patient;
import com.medical.patients.repository.PatientRepository;
import com.medical.patients.repository.DossierRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Tests d'Intégration - Parcours Patient Complet")
class PatientJourneyIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DossierRepository dossierRepository;

    private static Long savedPatientId;

    @BeforeEach
    void setUp() {
        // Nettoyer la base avant chaque test
        dossierRepository.deleteAll();
        patientRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Flux complet : Créer un patient")
    void testCreatePatient() throws Exception {
        // Arrange
        PatientDTO newPatient = PatientDTO.builder()
                .nom("Martin")
                .prenom("Sophie")
                .numeroSecu("298765432101234")
                .dateNaissance(LocalDate.of(1990, 3, 20))
                .sexe("F")
                .adresse("45 Avenue de la République")
                .codePostal("69001")
                .ville("Lyon")
                .telephone("0645789632")
                .email("sophie.martin@email.com")
                .allergies("Arachides")
                .maladiesChroniques("Asthme")
                .build();

        // Act & Assert
        MvcResult result = mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPatient)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.nom").value("Martin"))
                .andExpect(jsonPath("$.data.prenom").value("Sophie"))
                .andExpect(jsonPath("$.data.email").value("sophie.martin@email.com"))
                .andReturn();

        // Extraire l'ID pour les tests suivants
        String responseContent = result.getResponse().getContentAsString();
        ApiResponse response = objectMapper.readValue(responseContent, ApiResponse.class);
        savedPatientId = ((Number) ((java.util.LinkedHashMap) response.getData()).get("id")).longValue();

        // Vérifier en base de données
        assertThat(patientRepository.findById(savedPatientId)).isPresent();
    }

    @Test
    @Order(2)
    @DisplayName("Flux complet : Récupérer le patient créé")
    void testGetPatient() throws Exception {
        // Arrange - Créer un patient
        Patient patient = Patient.builder()
                .nom("Durand")
                .prenom("Pierre")
                .numeroSecu("198765432101234")
                .dateNaissance(LocalDate.of(1985, 6, 15))
                .sexe("M")
                .build();
        Patient saved = patientRepository.save(patient);

        // Act & Assert
        mockMvc.perform(get("/api/patients/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nom").value("Durand"))
                .andExpect(jsonPath("$.data.prenom").value("Pierre"));
    }

    @Test
    @Order(3)
    @DisplayName("Flux complet : Créer un dossier médical pour le patient")
    void testCreateDossierForPatient() throws Exception {
        // Arrange - Créer un patient
        Patient patient = Patient.builder()
                .nom("Bernard")
                .prenom("Marie")
                .numeroSecu("398765432101234")
                .dateNaissance(LocalDate.of(1988, 9, 10))
                .sexe("F")
                .build();
        Patient saved = patientRepository.save(patient);

        DossierDTO dossier = DossierDTO.builder()
                .patientId(saved.getId())
                .typeConsultation("Cardiologie")
                .dateConsultation(LocalDate.now())
                .medecin("Dr. Lefebvre")
                .diagnostic("Hypertension légère")
                .traitement("Régime alimentaire")
                .observations("Contrôle dans 3 mois")
                .resultatTest("Tension: 14/9")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/dossiers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dossier)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.typeConsultation").value("Cardiologie"))
                .andExpect(jsonPath("$.data.medecin").value("Dr. Lefebvre"));

        // Vérifier en base
        assertThat(dossierRepository.findByPatientId(saved.getId())).hasSize(1);
    }

    @Test
    @Order(4)
    @DisplayName("Flux complet : Parcours patient avec plusieurs dossiers")
    void testCompletePatientJourney() throws Exception {
        // 1. Créer un patient
        Patient patient = Patient.builder()
                .nom("Petit")
                .prenom("Jean")
                .numeroSecu("498765432101234")
                .dateNaissance(LocalDate.of(1975, 12, 5))
                .sexe("M")
                .telephone("0612345678")
                .email("jean.petit@email.com")
                .build();
        Patient savedPatient = patientRepository.save(patient);

        // 2. Créer plusieurs dossiers médicaux
        DossierDTO dossier1 = DossierDTO.builder()
                .patientId(savedPatient.getId())
                .typeConsultation("Médecine Générale")
                .dateConsultation(LocalDate.now().minusMonths(3))
                .medecin("Dr. Martin")
                .diagnostic("Grippe")
                .traitement("Paracétamol")
                .build();

        DossierDTO dossier2 = DossierDTO.builder()
                .patientId(savedPatient.getId())
                .typeConsultation("Ophtalmologie")
                .dateConsultation(LocalDate.now().minusMonths(1))
                .medecin("Dr. Dubois")
                .diagnostic("Myopie")
                .traitement("Lunettes prescrites")
                .build();

        // Créer les dossiers
        mockMvc.perform(post("/api/dossiers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dossier1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/dossiers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dossier2)))
                .andExpect(status().isCreated());

        // 3. Récupérer tous les dossiers du patient
        mockMvc.perform(get("/api/dossiers/patient/" + savedPatient.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));

        // 4. Mettre à jour le patient
        PatientDTO updatedPatient = PatientDTO.builder()
                .nom("Petit")
                .prenom("Jean")
                .numeroSecu("498765432101234")
                .dateNaissance(LocalDate.of(1975, 12, 5))
                .sexe("M")
                .telephone("0698765432") // Nouveau téléphone
                .email("j.petit@newemail.com") // Nouvel email
                .build();

        mockMvc.perform(put("/api/patients/" + savedPatient.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPatient)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.telephone").value("0698765432"))
                .andExpect(jsonPath("$.data.email").value("j.petit@newemail.com"));

        // 5. Vérifier que tout est cohérent en base
        assertThat(patientRepository.findById(savedPatient.getId())).isPresent();
        assertThat(dossierRepository.findByPatientId(savedPatient.getId())).hasSize(2);
    }

    @Test
    @Order(5)
    @DisplayName("Flux d'erreur : Créer un dossier pour un patient inexistant")
    void testCreateDossierForNonExistentPatient() throws Exception {
        // Arrange
        DossierDTO dossier = DossierDTO.builder()
                .patientId(99999L) // ID inexistant
                .typeConsultation("Cardiologie")
                .dateConsultation(LocalDate.now())
                .medecin("Dr. Test")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/dossiers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dossier)))
                .andExpect(status().isNotFound()); // Devrait être 404 Not Found idéalement
    }

    @Test
    @Order(6)
    @DisplayName("Recherche : Trouver des patients par nom")
    void testSearchPatients() throws Exception {
        // Arrange - Créer plusieurs patients
        patientRepository.save(Patient.builder()
                .nom("Dupont")
                .prenom("Alice")
                .numeroSecu("198765432101231")
                .dateNaissance(LocalDate.of(1990, 1, 1))
                .sexe("F")
                .build());

        patientRepository.save(Patient.builder()
                .nom("Dupontier")
                .prenom("Bob")
                .numeroSecu("198765432101232")
                .dateNaissance(LocalDate.of(1985, 2, 2))
                .sexe("M")
                .build());

        patientRepository.save(Patient.builder()
                .nom("Martin")
                .prenom("Charlie")
                .numeroSecu("198765432101233")
                .dateNaissance(LocalDate.of(1995, 3, 3))
                .sexe("M")
                .build());

        // Act & Assert - Rechercher "Dupont"
        mockMvc.perform(get("/api/patients/search")
                        .param("nom", "Dupont"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }
}