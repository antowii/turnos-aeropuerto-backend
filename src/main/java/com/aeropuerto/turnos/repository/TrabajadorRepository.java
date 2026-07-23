package com.aeropuerto.turnos.repository;

import com.aeropuerto.turnos.model.Trabajador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
    public interface TrabajadorRepository extends JpaRepository<Trabajador, Long> {
}
