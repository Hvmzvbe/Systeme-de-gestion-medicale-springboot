package com.medical.appointmentsservice.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {
    private Long id;
    private Long doctorId;
    private Long patientId;
    private LocalDateTime appointmentDateTime;
    private String reason;
    private String status;
    private String notes;
}
