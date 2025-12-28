package com.medical.patients.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

// ========== DTO PATIENT ==========
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientDTO {

    private Long id;

    @NotBlank(message = "Le nom est requis")
    @Size(min = 2, max = 50, message = "Le nom doit avoir entre 2 et 50 caractères")
    private String nom;

    @NotBlank(message = "Le prénom est requis")
    @Size(min = 2, max = 50, message = "Le prénom doit avoir entre 2 et 50 caractères")
    private String prenom;

    @NotBlank(message = "Le numéro de sécurité sociale est requis")
    @Pattern(regexp = "^\\d{15}$", message = "Numéro de sécurité sociale invalide")
    private String numeroSecu;

    @NotNull(message = "La date de naissance est requise")
    @PastOrPresent(message = "La date de naissance doit être dans le passé")
    private LocalDate dateNaissance;

    @Pattern(regexp = "^[MF]$", message = "Le sexe doit être M ou F")
    private String sexe;

    @Size(max = 100)
    private String adresse;

    @Pattern(regexp = "^\\d{5}$|^$", message = "Code postal invalide")
    private String codePostal;

    @Size(max = 50)
    private String ville;

    @Pattern(regexp = "^[0-9\\s\\-\\+\\(\\)]*$|^$", message = "Numéro de téléphone invalide")
    private String telephone;

    @Email(message = "Email invalide")
    @Size(max = 100)
    private String email;

    private String allergies;

    private String maladiesChroniques;

    private LocalDateTime dateCreation;

    private LocalDateTime dateModification;
}
