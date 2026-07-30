package com.aeropuerto.distribucion.repository;

import com.aeropuerto.distribucion.model.Distribucion;
import com.aeropuerto.distribucion.model.Tienda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface DistribucionRepository extends JpaRepository<Distribucion, Long> {
    long countByTiendaAndFecha(Tienda tienda, LocalDate fecha);
    boolean existsByTrabajadorCargoAndTerminalAsignadoAndFecha(String cargo, String terminalAsignado, LocalDate fecha);
    // Spring Boot buscará si existe un registro cruzando el Nombre de la Tienda, el Cargo y la Fecha
    boolean existsByTiendaNombreIgnoreCaseAndTrabajadorCargoIgnoreCaseAndFecha(String nombreTienda, String cargo, LocalDate fecha);
}
