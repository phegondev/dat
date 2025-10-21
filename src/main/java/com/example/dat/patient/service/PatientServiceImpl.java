package com.example.dat.patient.service;

import com.example.dat.enums.BloodGroup;
import com.example.dat.enums.Genotype;
import com.example.dat.exceptions.NotFoundException;
import com.example.dat.patient.dto.PatientDTO;
import com.example.dat.patient.entity.Patient;
import com.example.dat.patient.repo.PatientRepo;
import com.example.dat.res.Response;
import com.example.dat.users.entity.User;
import com.example.dat.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class PatientServiceImpl implements PatientService{

    private final PatientRepo patientRepo;
    private final UserService userService;
    private final ModelMapper modelMapper;


    @Override
    public Response<PatientDTO> getPatientProfile() {

        User user = userService.getCurrentUser();

        Patient patient = patientRepo.findByUser(user)
                .orElseThrow(()-> new NotFoundException("Patient Not Found"));

        return Response.<PatientDTO>builder()
                .statusCode(200)
                .message("Patient profile retrieved successfully.")
                .data(modelMapper.map(patient, PatientDTO.class))
                .build();
    }

    @Override
    public Response<?> updatePatientProfile(PatientDTO patientDTO) {


        User currentUser = userService.getCurrentUser();

        Patient patient = patientRepo.findByUser(currentUser)
                .orElseThrow(() -> new NotFoundException("Patient profile not found."));


        // Basic fields (firstName, lastName,)
        if (StringUtils.hasText(patientDTO.getFirstName())) {
            patient.setFirstName(patientDTO.getFirstName());
        }
        if (StringUtils.hasText(patientDTO.getLastName())) {
            patient.setLastName(patientDTO.getLastName());
        }
        if (StringUtils.hasText(patientDTO.getPhone())) {
            patient.setPhone(patientDTO.getPhone());
        }

        // LocalDate field
        Optional.ofNullable(patientDTO.getDateOfBirth()).ifPresent(patient::setDateOfBirth);

        // Medical fields (knownAllergies, bloodGroup, genotype)
        if (StringUtils.hasText(patientDTO.getKnownAllergies())) {
            patient.setKnownAllergies(patientDTO.getKnownAllergies());
        }

        // Enum fields (BloodGroup, Genotype)
        Optional.ofNullable(patientDTO.getBloodGroup()).ifPresent(patient::setBloodGroup);
        Optional.ofNullable(patientDTO.getGenotype()).ifPresent(patient::setGenotype);

        patientRepo.save(patient);

        return Response.builder()
                .statusCode(200)
                .message("Patient profile updated successfully.")
                .build();


    }

    @Override
    public Response<PatientDTO> getPatientById(Long patientId) {

        Patient patient = patientRepo.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Patient not found with ID: " + patientId));

        PatientDTO patientDTO = modelMapper.map(patient, PatientDTO.class);

        return Response.<PatientDTO>builder()
                .statusCode(200)
                .message("Patient retrieved successfully.")
                .data(patientDTO)
                .build();
    }

    @Override
    public Response<List<BloodGroup>> getAllBloodGroupEnums() {

        List<BloodGroup> bloodGroups = Arrays.asList(BloodGroup.values());

        return Response.<List<BloodGroup>>builder()
                .statusCode(200)
                .message("BloodGroups retrieved successfully")
                .data(bloodGroups)
                .build();
    }

    @Override
    public Response<List<Genotype>> getAllGenotypeEnums() {

        List<Genotype> genotypes = Arrays.asList(Genotype.values());

        return Response.<List<Genotype>>builder()
                .statusCode(200)
                .message("Genotypes retrieved successfully")
                .data(genotypes)
                .build();
    }
}
