package com.aeropuerto.turnos.repository;

import com.aeropuerto.turnos.model.Asistencia;
import com.aeropuerto.turnos.model.Trabajador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    boolean existsByTrabajadorAndFecha(Trabajador trabajador, LocalDate fecha);
}
