package com.medical.appointmentsservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AppointmentRequest {
    @NotNull
    private Long doctorId;

    @NotNull
    private Long patientId;

    @NotNull
    private LocalDateTime appointmentDateTime;

    @NotBlank
    private String reason;
}

