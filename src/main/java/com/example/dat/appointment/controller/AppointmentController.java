package com.example.dat.appointment.controller;


import com.example.dat.appointment.dto.AppointmentDTO;
import com.example.dat.appointment.service.AppointmentService;
import com.example.dat.res.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<Response<AppointmentDTO>> bookAppointment(@RequestBody @Valid AppointmentDTO appointmentDTO){
        return ResponseEntity.ok(appointmentService.bookAppointment(appointmentDTO));
    }

    @GetMapping
    public  ResponseEntity<Response<List<AppointmentDTO>>> getMyAppointments(){
        return ResponseEntity.ok(appointmentService.getMyAppointments());
    }

    @PutMapping("/cancel/{appointmentId}")
    public  ResponseEntity<Response<AppointmentDTO>> cancelAppointment(@PathVariable Long appointmentId){
        return ResponseEntity.ok(appointmentService.cancelAppointment(appointmentId));
    }

    @PutMapping("/complete/{appointmentId}")
    @PreAuthorize(("hasAuthority('DOCTOR')"))
    public  ResponseEntity<Response<?>> completeAppointment(@PathVariable Long appointmentId){
        return ResponseEntity.ok(appointmentService.completeAppointment(appointmentId));
    }

}











