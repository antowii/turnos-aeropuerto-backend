package com.aeropuerto.distribucion.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Asistencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //Se conectan las dos tablas
    @ManyToOne
    @JoinColumn(name = "trabajador_id")
    private Trabajador trabajador;

    private String motivo; // Ej: "Vacaciones", "Licencia", "Cumpleaños"
    private LocalDate fecha;
}
