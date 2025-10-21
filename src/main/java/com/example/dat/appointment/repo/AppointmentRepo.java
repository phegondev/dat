package com.example.dat.appointment.repo;

import com.example.dat.appointment.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepo extends JpaRepository<Appointment, Long> {



    //fetch appointment of the doctor
    List<Appointment> findByDoctor_User_IdOrderByIdDesc(Long userId);

    //fetch appointment of the patient
    List<Appointment> findByPatient_User_IdOrderByIdDesc(Long userId);

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.doctor.id = :doctorId " +
            "AND a.status = 'SCHEDULED' " + // Only check for scheduled/confirmed appointments
            "AND (" +
            // Case 1: Existing appointment starts during the new slot
            "    (a.startTime < :newEndTime AND a.endTime > :newStartTime)" +
            ")")
    List<Appointment> findConflictingAppointments(
            @Param("doctorId") Long doctorId,
            @Param("newStartTime") LocalDateTime newStartTime,
            @Param("newEndTime") LocalDateTime newEndTime
    );

}
