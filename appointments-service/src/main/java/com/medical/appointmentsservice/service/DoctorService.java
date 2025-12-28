package com.medical.appointmentsservice.service;

import com.medical.appointmentsservice.dto.DoctorDTO;
import com.medical.appointmentsservice.entity.Doctor;
import com.medical.appointmentsservice.repository.DoctorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    public DoctorDTO createDoctor(DoctorDTO doctorDTO) {
        Doctor doctor = new Doctor();
        doctor.setFirstName(doctorDTO.getFirstName());
        doctor.setLastName(doctorDTO.getLastName());
        doctor.setEmail(doctorDTO.getEmail());
        doctor.setSpecialization(doctorDTO.getSpecialization());
        doctor.setPhone(doctorDTO.getPhone());
        doctor.setAvailableFrom(doctorDTO.getAvailableFrom());
        doctor.setAvailableTo(doctorDTO.getAvailableTo());
        doctor.setRating(BigDecimal.valueOf(4.5));

        Doctor savedDoctor = doctorRepository.save(doctor);
        log.info("Médecin créé : {}", savedDoctor.getId());

        return convertToDTO(savedDoctor);
    }

    public DoctorDTO getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé"));
        return convertToDTO(doctor);
    }

    public List<DoctorDTO> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<DoctorDTO> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public DoctorDTO updateDoctor(Long id, DoctorDTO doctorDTO) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé"));

        doctor.setFirstName(doctorDTO.getFirstName());
        doctor.setLastName(doctorDTO.getLastName());
        doctor.setPhone(doctorDTO.getPhone());
        doctor.setAvailableFrom(doctorDTO.getAvailableFrom());
        doctor.setAvailableTo(doctorDTO.getAvailableTo());

        Doctor updatedDoctor = doctorRepository.save(doctor);
        return convertToDTO(updatedDoctor);
    }

    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
        log.info("Médecin supprimé : {}", id);
    }

    private DoctorDTO convertToDTO(Doctor doctor) {
        return new DoctorDTO(
                doctor.getId(),
                doctor.getFirstName(),
                doctor.getLastName(),
                doctor.getEmail(),
                doctor.getSpecialization(),
                doctor.getPhone(),
                doctor.getRating(),
                doctor.getAvailableFrom(),
                doctor.getAvailableTo()
        );
    }
}