package com.medical.appointmentsservice.service;


import com.medical.appointmentsservice.dto.*;
import com.medical.appointmentsservice.entity.*;
import com.medical.appointmentsservice.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Value("${mes-config-ms.appointments-last}")
    private int appointmentsLastDays;

    public AppointmentDTO createAppointment(AppointmentRequest request) {
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé"));

        // Vérifier la disponibilité
        if (!isTimeAvailable(doctor, request.getAppointmentDateTime())) {
            throw new RuntimeException("Créneau non disponible");
        }

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatientId(request.getPatientId());
        appointment.setAppointmentDateTime(request.getAppointmentDateTime());
        appointment.setReason(request.getReason());
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("RDV créé : {} pour patient {}", savedAppointment.getId(), request.getPatientId());

        return convertToDTO(savedAppointment);
    }

    public AppointmentDTO getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé"));
        return convertToDTO(appointment);
    }

    public List<AppointmentDTO> getAppointmentsByPatient(Long patientId) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(appointmentsLastDays);
        LocalDateTime endDate = LocalDateTime.now().plusMonths(3);

        return appointmentRepository.findByPatientId(patientId)
                .stream()
                .filter(a -> a.getAppointmentDateTime().isAfter(startDate) &&
                        a.getAppointmentDateTime().isBefore(endDate))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getUpcomingAppointments() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextMonth = now.plusMonths(1);

        return appointmentRepository.findByAppointmentDateTimeBetween(now, nextMonth)
                .stream()
                .filter(a -> a.getStatus() == AppointmentStatus.SCHEDULED)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AppointmentDTO updateAppointmentStatus(Long id, String status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé"));

        appointment.setStatus(AppointmentStatus.valueOf(status));
        Appointment updatedAppointment = appointmentRepository.save(appointment);

        return convertToDTO(updatedAppointment);
    }

    public AppointmentDTO cancelAppointment(Long id) {
        return updateAppointmentStatus(id, "CANCELLED");
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
        log.info("Rendez-vous supprimé : {}", id);
    }

    private boolean isTimeAvailable(Doctor doctor, LocalDateTime dateTime) {
        LocalTime requestedTime = dateTime.toLocalTime();

        if (requestedTime.isBefore(doctor.getAvailableFrom()) ||
                requestedTime.isAfter(doctor.getAvailableTo())) {
            return false;
        }

        // Vérifier les conflits d'horaires (RDV de 30 min)
        LocalDateTime startWindow = dateTime.minusMinutes(15);
        LocalDateTime endWindow = dateTime.plusMinutes(45);

        return appointmentRepository.findByDoctorIdAndAppointmentDateTimeBetween(
                doctor.getId(), startWindow, endWindow
        ).isEmpty();
    }

    private AppointmentDTO convertToDTO(Appointment appointment) {
        return new AppointmentDTO(
                appointment.getId(),
                appointment.getDoctor().getId(),
                appointment.getPatientId(),
                appointment.getAppointmentDateTime(),
                appointment.getReason(),
                appointment.getStatus().name(),
                appointment.getNotes()
        );
    }
}