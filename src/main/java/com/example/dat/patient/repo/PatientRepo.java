package com.example.dat.patient.repo;

import com.example.dat.patient.entity.Patient;
import com.example.dat.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepo extends JpaRepository<Patient, Long> {

    Optional<Patient> findByUser(User user);
}
