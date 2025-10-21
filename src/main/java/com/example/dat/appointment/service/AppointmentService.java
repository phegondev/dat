package com.example.dat.appointment.service;

import com.example.dat.appointment.dto.AppointmentDTO;
import com.example.dat.res.Response;

import java.util.List;

public interface AppointmentService {

    Response<AppointmentDTO> bookAppointment(AppointmentDTO appointmentDTO);

    Response<List<AppointmentDTO>> getMyAppointments();

    Response<AppointmentDTO> cancelAppointment(Long appointmentId);

    Response<?> completeAppointment(Long appointmentId);

}
