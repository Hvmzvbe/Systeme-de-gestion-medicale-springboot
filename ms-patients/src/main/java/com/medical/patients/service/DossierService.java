package com.medical.patients.service;


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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DossierService {

    private final DossierRepository dossierRepository;
    private final PatientRepository patientRepository;

    // Créer un nouveau dossier
    public DossierDTO createDossier(DossierDTO dossierDTO) {
        log.info("Création d'un nouveau dossier pour le patient : {}", dossierDTO.getPatientId());
        Patient patient = patientRepository.findById(dossierDTO.getPatientId())
                .orElseThrow(() -> new EntityNotFoundException("Patient introuvable"));

        DossierMedical dossier = dtoToEntity(dossierDTO, patient);
        DossierMedical saved = dossierRepository.save(dossier);
        return entityToDTO(saved);
    }

    // Récupérer un dossier par ID
    public DossierDTO getDossierById(Long id) {
        log.info("Récupération du dossier avec l'ID : {}", id);
        DossierMedical dossier = dossierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dossier introuvable : " + id));
        return entityToDTO(dossier);
    }

    // Tous les dossiers d'un patient
    public List<DossierDTO> getDossiersByPatientId(Long patientId) {
        log.info("Récupération des dossiers du patient : {}", patientId);
        return dossierRepository.findByPatientId(patientId).stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    // Derniers N dossiers d'un patient (pour la config personnalisée)
    public List<DossierDTO> getLastNDossiersByPatient(Long patientId, int limit) {
        log.info("Récupération des {} derniers dossiers du patient : {}", limit, patientId);
        return dossierRepository.findLastNDossiersByPatient(patientId, limit).stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    // Mettre à jour un dossier
    public DossierDTO updateDossier(Long id, DossierDTO dossierDTO) {
        log.info("Mise à jour du dossier avec l'ID : {}", id);
        DossierMedical dossier = dossierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dossier introuvable : " + id));

        dossier.setTypeConsultation(dossierDTO.getTypeConsultation());
        dossier.setDateConsultation(dossierDTO.getDateConsultation());
        dossier.setMedecin(dossierDTO.getMedecin());
        dossier.setDiagnostic(dossierDTO.getDiagnostic());
        dossier.setTraitement(dossierDTO.getTraitement());
        dossier.setObservations(dossierDTO.getObservations());
        dossier.setResultatTest(dossierDTO.getResultatTest());

        DossierMedical updated = dossierRepository.save(dossier);
        return entityToDTO(updated);
    }

    // Supprimer un dossier
    public void deleteDossier(Long id) {
        log.info("Suppression du dossier avec l'ID : {}", id);
        if (!dossierRepository.existsById(id)) {
            throw new EntityNotFoundException("Dossier introuvable : " + id);
        }
        dossierRepository.deleteById(id);
    }

    private DossierDTO entityToDTO(DossierMedical dossier) {
        return DossierDTO.builder()
                .id(dossier.getId())
                .patientId(dossier.getPatient().getId())
                .typeConsultation(dossier.getTypeConsultation())
                .dateConsultation(dossier.getDateConsultation())
                .medecin(dossier.getMedecin())
                .diagnostic(dossier.getDiagnostic())
                .traitement(dossier.getTraitement())
                .observations(dossier.getObservations())
                .resultatTest(dossier.getResultatTest())
                .dateCreation(dossier.getDateCreation())
                .dateModification(dossier.getDateModification())
                .build();
    }

    private DossierMedical dtoToEntity(DossierDTO dto, Patient patient) {
        return DossierMedical.builder()
                .patient(patient)
                .typeConsultation(dto.getTypeConsultation())
                .dateConsultation(dto.getDateConsultation())
                .medecin(dto.getMedecin())
                .diagnostic(dto.getDiagnostic())
                .traitement(dto.getTraitement())
                .observations(dto.getObservations())
                .resultatTest(dto.getResultatTest())
                .build();
    }
}