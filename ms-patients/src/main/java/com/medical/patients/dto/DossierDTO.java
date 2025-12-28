package com.medical.patients.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DossierDTO {

    private Long id;

    @NotNull(message = "L'ID patient est requis")
    private Long patientId;

    @NotBlank(message = "Le type de consultation est requis")
    @Size(min = 2, max = 100)
    private String typeConsultation;

    @NotNull(message = "La date de consultation est requise")
    @PastOrPresent(message = "La date doit être dans le passé ou d'aujourd'hui")
    private LocalDate dateConsultation;

    @Size(max = 100)
    private String medecin;

    private String diagnostic;

    private String traitement;

    private String observations;

    @Size(max = 100)
    private String resultatTest;

    private LocalDateTime dateCreation;

    private LocalDateTime dateModification;
}