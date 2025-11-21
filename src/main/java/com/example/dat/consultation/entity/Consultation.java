package com.example.dat.consultation.entity;


import com.example.dat.appointment.entity.Appointment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "consultations")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Consultation {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime consultationDate;

    @Lob
    private String subjectiveNotes;

    @Lob
    private String objectiveFindings;

    @Lob
    private String assessment;

    @Lob
    private String plan;

    @OneToOne
    @JoinColumn(name = "appointment_id", unique = true, nullable = false)
    private Appointment appointment;
}








