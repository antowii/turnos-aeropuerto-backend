package com.aeropuerto.distribucion.service;

import com.aeropuerto.distribucion.model.Distribucion;
import com.aeropuerto.distribucion.model.Tienda;
import com.aeropuerto.distribucion.model.Trabajador;
import com.aeropuerto.distribucion.repository.DistribucionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DistribucionService {
    private final DistribucionRepository distribucionRepository;

    public DistribucionService(DistribucionRepository distribucionRepository) {
        this.distribucionRepository = distribucionRepository;
    }

    //Metodo 1: Validar si la tienda tiene cupo hoy
    public boolean hayCapacidad(Tienda tienda, LocalDate fecha) {
        long ocupantesActuales = distribucionRepository.countByTiendaAndFecha(tienda, fecha);
        //Retorna 'true' solo si los actuales son menores a la capacidad máxima
        return ocupantesActuales < tienda.getCapacidadMaxima();
    }

    //Metodo 2: Guardar el ticket en la base de datos
    public Distribucion registrarDistribucion(Trabajador trabajador, Tienda tienda, String terminalDestino, LocalDate fecha) {
        Distribucion distribucion = new Distribucion();
        distribucion.setTrabajador(trabajador);
        distribucion.setTienda(tienda);
        distribucion.setTerminalAsignado(terminalDestino);
        distribucion.setFecha(fecha);
        return distribucionRepository.save(distribucion);
    }

    //Metodo 3: Validar y actualizar
    public boolean terminalYaTieneEncargado(String terminal, LocalDate fecha) {
        //Busca si ya existe alguien con cargo "Encargado" en ese terminal hoy
        return distribucionRepository.existsByTrabajadorCargoAndTerminalAsignadoAndFecha("Encargado", terminal, fecha);
    }
}
