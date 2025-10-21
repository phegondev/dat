package com.example.dat.patient.service;

import com.example.dat.enums.BloodGroup;
import com.example.dat.enums.Genotype;
import com.example.dat.patient.dto.PatientDTO;
import com.example.dat.res.Response;

import java.util.List;

public interface PatientService {


    Response<PatientDTO> getPatientProfile();

    Response<?> updatePatientProfile(PatientDTO patientDTO);

    Response<PatientDTO> getPatientById(Long patientId);

    Response<List<BloodGroup>> getAllBloodGroupEnums();
    Response<List<Genotype>>getAllGenotypeEnums();

}
