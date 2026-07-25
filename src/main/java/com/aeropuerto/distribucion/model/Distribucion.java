package com.aeropuerto.distribucion.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Distribucion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate fecha;
    private String terminalAsignado;

    //Se conecta la distribucion con el trabajador asignado
    @ManyToOne
    private Trabajador trabajador;

    //Se conecta la distribucion con la tienda asignada
    @ManyToOne
    private Tienda tienda;
}
