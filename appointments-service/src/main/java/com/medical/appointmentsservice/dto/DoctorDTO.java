package com.medical.appointmentsservice.dto;

import lombok.*;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String specialization;
    private String phone;
    private Double rating;
    private LocalTime availableFrom;
    private LocalTime availableTo;
}