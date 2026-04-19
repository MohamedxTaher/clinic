package hana.com.clinic_system.controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hana.com.clinic_system.entities.Appointment;
import hana.com.clinic_system.entities.AppointmentDTO;
import hana.com.clinic_system.repositories.AppointmentRepository;
import hana.com.clinic_system.repositories.DoctorRepository;
import hana.com.clinic_system.repositories.PatientRepository;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/appointments")
@SuppressWarnings("null")
public class AppointmentController {

    private static final Logger log = LoggerFactory.getLogger(AppointmentController.class);
    private static final String DEFAULT_STATUS = "Pending";

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public AppointmentController(AppointmentRepository appointmentRepository,
                                 PatientRepository patientRepository,
                                 DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    @GetMapping
    public ResponseEntity<List<AppointmentDTO>> getAllAppointments() {
        List<AppointmentDTO> appointments = appointmentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO> getAppointmentById(@PathVariable Integer id) {
        return appointmentRepository.findById(id)
                .map(appointment -> ResponseEntity.ok(convertToDTO(appointment)))
                .orElse(ResponseEntity.notFound().<AppointmentDTO>build());
    }

    @PostMapping
    public ResponseEntity<AppointmentDTO> createAppointment(@Valid @RequestBody AppointmentDTO appointmentDTO) {
        log.info("Creating appointment: date={}, time={}, patientId={}, doctorId={}", 
                 appointmentDTO.getDate(), appointmentDTO.getTime(), 
                 appointmentDTO.getPatientId(), appointmentDTO.getDoctorId());
        
        if (appointmentDTO.getDate() == null
                || appointmentDTO.getTime() == null
                || appointmentDTO.getPatientId() == null
                || appointmentDTO.getDoctorId() == null) {
            log.warn("Missing required fields in appointment creation");
            return ResponseEntity.badRequest().<AppointmentDTO>build();
        }

        Appointment appointment = new Appointment();
        appointment.setDate(appointmentDTO.getDate());
        appointment.setTime(appointmentDTO.getTime());
        appointment.setStatus(appointmentDTO.getStatus() != null ? appointmentDTO.getStatus() : DEFAULT_STATUS);

        if (!setPatientIfPresent(appointment, appointmentDTO.getPatientId())) {
            log.warn("Patient not found with id: {}", appointmentDTO.getPatientId());
            return ResponseEntity.badRequest().<AppointmentDTO>build();
        }
        if (!setDoctorIfPresent(appointment, appointmentDTO.getDoctorId())) {
            log.warn("Doctor not found with id: {}", appointmentDTO.getDoctorId());
            return ResponseEntity.badRequest().<AppointmentDTO>build();
        }

        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment saved successfully with id: {}", savedAppointment.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedAppointment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDTO> updateAppointment(@PathVariable Integer id,
                                                            @Valid @RequestBody AppointmentDTO appointmentDTO) {
        return appointmentRepository.findById(id)
                .map(existingAppointment -> {
                    if (!updateAppointmentFields(existingAppointment, appointmentDTO)) {
                        return ResponseEntity.badRequest().<AppointmentDTO>build();
                    }
                    Appointment savedAppointment = appointmentRepository.save(existingAppointment);
                    return ResponseEntity.ok(convertToDTO(savedAppointment));
                })
                .orElse(ResponseEntity.notFound().<AppointmentDTO>build());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteAppointment(@PathVariable Integer id) {
        log.info("Attempting to delete appointment with id: {}", id);
        int deleted = appointmentRepository.hardDeleteById(id);
        log.info("Delete result: {} rows affected", deleted);
        if (deleted == 0) {
            log.warn("Appointment with id {} not found", id);
            return ResponseEntity.notFound().build();
        }
        log.info("Successfully deleted appointment with id: {}", id);
        return ResponseEntity.noContent().build();
    }

    private AppointmentDTO convertToDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        dto.setDate(appointment.getDate());
        dto.setTime(appointment.getTime());
        dto.setStatus(appointment.getStatus());

        Optional.ofNullable(appointment.getPatient()).ifPresent(patient -> {
            dto.setPatientId(patient.getId());
            dto.setPatientName(patient.getName());
        });

        Optional.ofNullable(appointment.getDoctor()).ifPresent(doctor -> {
            dto.setDoctorId(doctor.getId());
            dto.setDoctorName(doctor.getName());
        });

        return dto;
    }

    private boolean updateAppointmentFields(Appointment existing, AppointmentDTO updates) {
        if (updates.getDate() != null) {
            existing.setDate(updates.getDate());
        }
        if (updates.getTime() != null) {
            existing.setTime(updates.getTime());
        }
        if (updates.getStatus() != null) {
            existing.setStatus(updates.getStatus());
        }
        if (updates.getPatientId() != null) {
            if (!setPatientIfPresent(existing, updates.getPatientId())) {
                return false;
            }
        }
        if (updates.getDoctorId() != null) {
            if (!setDoctorIfPresent(existing, updates.getDoctorId())) {
                return false;
            }
        }
        return true;
    }

    private boolean setPatientIfPresent(Appointment appointment, Integer patientId) {
        if (patientId == null) {
            return true;
        }
        return patientRepository.findById(patientId)
                .map(patient -> {
                    appointment.setPatient(patient);
                    return true;
                })
                .orElse(false);
    }

    private boolean setDoctorIfPresent(Appointment appointment, Integer doctorId) {
        if (doctorId == null) {
            return true;
        }
        return doctorRepository.findById(doctorId)
                .map(doctor -> {
                    appointment.setDoctor(doctor);
                    return true;
                })
                .orElse(false);
    }
}
