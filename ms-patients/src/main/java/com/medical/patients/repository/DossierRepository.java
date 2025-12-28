package com.medical.patients.repository;

import com.medical.patients.entity.DossierMedical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DossierRepository extends JpaRepository<DossierMedical, Long> {

    // Tous les dossiers d'un patient
    List<DossierMedical> findByPatientId(Long patientId);

    // Dossiers d'un patient par type de consultation
    List<DossierMedical> findByPatientIdAndTypeConsultation(Long patientId, String typeConsultation);

    // Dossiers d'un patient dans une plage de dates
    List<DossierMedical> findByPatientIdAndDateConsultationBetween(
            Long patientId,
            LocalDate dateDebut,
            LocalDate dateFin);

    // Dossiers d'un patient triés par date décroissante
    @Query("SELECT d FROM DossierMedical d WHERE d.patient.id = :patientId ORDER BY d.dateConsultation DESC")
    List<DossierMedical> findDossiersByPatientOrderByDateDesc(@Param("patientId") Long patientId);

    // Derniers N dossiers d'un patient
    @Query(value = "SELECT * FROM dossiers_medicaux WHERE patient_id = :patientId ORDER BY date_consultation DESC LIMIT :limit",
            nativeQuery = true)
    List<DossierMedical> findLastNDossiersByPatient(@Param("patientId") Long patientId, @Param("limit") int limit);

    // Dossiers par médecin
    List<DossierMedical> findByMedecinIgnoreCase(String medecin);

    // Dossiers par type de consultation
    List<DossierMedical> findByTypeConsultationIgnoreCase(String typeConsultation);

    // Compte les dossiers d'un patient
    long countByPatientId(Long patientId);
}