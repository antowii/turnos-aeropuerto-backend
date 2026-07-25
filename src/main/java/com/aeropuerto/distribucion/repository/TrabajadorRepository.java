package com.aeropuerto.distribucion.repository;

import com.aeropuerto.distribucion.model.Trabajador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
    public interface TrabajadorRepository extends JpaRepository<Trabajador, Long> {
}
