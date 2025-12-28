package com.medical.patients.service;

import com.medical.patients.dto.PatientDTO;
import com.medical.patients.dto.DossierDTO;
import com.medical.patients.entity.Patient;
import com.medical.patients.entity.DossierMedical;
import com.medical.patients.repository.PatientRepository;
import com.medical.patients.repository.DossierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

// ========== PATIENT SERVICE ==========
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;

    // Créer un nouveau patient
    public PatientDTO createPatient(PatientDTO patientDTO) {
        log.info("Création d'un nouveau patient : {}", patientDTO.getNom());
        Patient patient = dtoToEntity(patientDTO);
        Patient saved = patientRepository.save(patient);
        return entityToDTO(saved);
    }

    // Récupérer un patient par ID
    public PatientDTO getPatientById(Long id) {
        log.info("Récupération du patient avec l'ID : {}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient introuvable : " + id));
        return entityToDTO(patient);
    }

    // Récupérer tous les patients
    public List<PatientDTO> getAllPatients() {
        log.info("Récupération de tous les patients");
        return patientRepository.findAll().stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    // Chercher par numéro de sécurité sociale
    public PatientDTO getPatientByNumeroSecu(String numeroSecu) {
        log.info("Recherche patient par numéro sécu : {}", numeroSecu);
        Patient patient = patientRepository.findByNumeroSecu(numeroSecu)
                .orElseThrow(() -> new EntityNotFoundException("Patient introuvable"));
        return entityToDTO(patient);
    }

    // Chercher par nom
    public List<PatientDTO> searchByNom(String nom) {
        log.info("Recherche patients par nom : {}", nom);
        return patientRepository.findByNomContainingIgnoreCase(nom).stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    // Patients avec allergies
    public List<PatientDTO> getPatientsAvecAllergies() {
        log.info("Récupération des patients avec allergies");
        return patientRepository.findPatientsAvecAllergies().stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    // Patients avec maladies chroniques
    public List<PatientDTO> getPatientsAvecMaladiesChroniques() {
        log.info("Récupération des patients avec maladies chroniques");
        return patientRepository.findPatientsAvecMaladiesChroniques().stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    // Mettre à jour un patient
    public PatientDTO updatePatient(Long id, PatientDTO patientDTO) {
        log.info("Mise à jour du patient avec l'ID : {}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient introuvable : " + id));

        patient.setNom(patientDTO.getNom());
        patient.setPrenom(patientDTO.getPrenom());
        patient.setAdresse(patientDTO.getAdresse());
        patient.setCodePostal(patientDTO.getCodePostal());
        patient.setVille(patientDTO.getVille());
        patient.setTelephone(patientDTO.getTelephone());
        patient.setEmail(patientDTO.getEmail());
        patient.setAllergies(patientDTO.getAllergies());
        patient.setMaladiesChroniques(patientDTO.getMaladiesChroniques());

        Patient updated = patientRepository.save(patient);
        return entityToDTO(updated);
    }

    // Supprimer un patient
    public void deletePatient(Long id) {
        log.info("Suppression du patient avec l'ID : {}", id);
        if (!patientRepository.existsById(id)) {
            throw new EntityNotFoundException("Patient introuvable : " + id);
        }
        patientRepository.deleteById(id);
    }

    private PatientDTO entityToDTO(Patient patient) {
        return PatientDTO.builder()
                .id(patient.getId())
                .nom(patient.getNom())
                .prenom(patient.getPrenom())
                .numeroSecu(patient.getNumeroSecu())
                .dateNaissance(patient.getDateNaissance())
                .sexe(patient.getSexe())
                .adresse(patient.getAdresse())
                .codePostal(patient.getCodePostal())
                .ville(patient.getVille())
                .telephone(patient.getTelephone())
                .email(patient.getEmail())
                .allergies(patient.getAllergies())
                .maladiesChroniques(patient.getMaladiesChroniques())
                .dateCreation(patient.getDateCreation())
                .dateModification(patient.getDateModification())
                .build();
    }

    private Patient dtoToEntity(PatientDTO dto) {
        return Patient.builder()
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .numeroSecu(dto.getNumeroSecu())
                .dateNaissance(dto.getDateNaissance())
                .sexe(dto.getSexe())
                .adresse(dto.getAdresse())
                .codePostal(dto.getCodePostal())
                .ville(dto.getVille())
                .telephone(dto.getTelephone())
                .email(dto.getEmail())
                .allergies(dto.getAllergies())
                .maladiesChroniques(dto.getMaladiesChroniques())
                .build();
    }
}
