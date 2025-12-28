package com.medical.patients.repository;

import com.medical.patients.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// ========== PATIENT REPOSITORY ==========
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // Chercher un patient par numéro de sécurité sociale
    Optional<Patient> findByNumeroSecu(String numeroSecu);

    // Chercher des patients par nom et prénom
    List<Patient> findByNomAndPrenom(String nom, String prenom);

    // Chercher des patients par nom (recherche partielle)
    List<Patient> findByNomContainingIgnoreCase(String nom);

    // Chercher des patients par email
    Optional<Patient> findByEmail(String email);

    // Chercher des patients par ville
    List<Patient> findByVilleIgnoreCase(String ville);

    // Chercher des patients nés après une date
    List<Patient> findByDateNaissanceAfter(LocalDate date);

    // Requête personnalisée : patients avec allergies
    @Query("SELECT p FROM Patient p WHERE p.allergies IS NOT NULL AND p.allergies != ''")
    List<Patient> findPatientsAvecAllergies();

    // Requête personnalisée : patients avec maladies chroniques
    @Query("SELECT p FROM Patient p WHERE p.maladiesChroniques IS NOT NULL AND p.maladiesChroniques != ''")
    List<Patient> findPatientsAvecMaladiesChroniques();
}
