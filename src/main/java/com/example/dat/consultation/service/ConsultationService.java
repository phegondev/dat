package com.example.dat.consultation.service;

import com.example.dat.consultation.dto.ConsultationDTO;
import com.example.dat.res.Response;

import java.util.List;

public interface ConsultationService {

    Response<ConsultationDTO> createConsultation(ConsultationDTO consultationDTO);

    Response<ConsultationDTO> getConsultationByAppointmentId(Long appointmentId);

    Response<List<ConsultationDTO>> getConsultationHistoryForPatient(Long patientId);

}
