package com.example.dat.doctor.service;

import com.example.dat.doctor.dto.DoctorDTO;
import com.example.dat.enums.Specialization;
import com.example.dat.res.Response;

import java.util.List;

public interface DoctorService {


    Response<DoctorDTO> getDoctorProfile();
    Response<?>updateDoctorProfile(DoctorDTO doctorDTO);
    Response<List<DoctorDTO>> getAllDoctors();
    Response<DoctorDTO> getDoctorById(Long doctorId);

    Response<List<DoctorDTO>> searchDoctorsBySpecialization(Specialization specialization);
    Response<List<Specialization>> getAllSpecializationEnums();

}
