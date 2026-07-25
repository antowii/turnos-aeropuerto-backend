package com.aeropuerto.distribucion.repository;

import com.aeropuerto.distribucion.model.Tienda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
    public interface TiendaRepository extends JpaRepository<Tienda, Long> {
}
