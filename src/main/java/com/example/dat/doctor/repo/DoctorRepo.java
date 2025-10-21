package com.example.dat.doctor.repo;

import com.example.dat.doctor.entity.Doctor;
import com.example.dat.enums.Specialization;
import com.example.dat.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepo extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByUser(User user);

    List<Doctor> findBySpecialization(Specialization specialization);

}
