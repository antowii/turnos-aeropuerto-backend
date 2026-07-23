package com.aeropuerto.turnos.service;

import com.aeropuerto.turnos.model.Tienda;
import com.aeropuerto.turnos.model.Trabajador;
import com.aeropuerto.turnos.repository.AsistenciaRepository;
import com.aeropuerto.turnos.repository.TrabajadorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TrabajadorService {
    private final TrabajadorRepository trabajadorRepository;
    private final AsistenciaRepository asistenciaRepository;
    //Inyección de dependencias, Spring Boot nos pasa el repositorio automáticamente
    public TrabajadorService(TrabajadorRepository trabajadorRepository, AsistenciaRepository asistenciaRepository) {
        this.trabajadorRepository = trabajadorRepository;
        this.asistenciaRepository = asistenciaRepository;
    }
    //Metodo 1: Obtener a todos los trabajadores
    public List<Trabajador> obtenerTodos() {
        return trabajadorRepository.findAll();
    }

    //Metodo 2: Validar la regla de la credencial TICA
    public boolean cumpleRequisitoTica(Trabajador trabajador, Tienda tienda) {
        //Verificar si la tienda exige TICA
        if (tienda.isRequiereTica()) {
            // Si la exige, el trabajador debe tenerla para que devuelva 'true'
            // (Nota: Lombok genera 'isTieneTica()' para las variables booleanas)
            return trabajador.isTica();
        }
        //Si la tienda no exige TICA, cualquiera puede trabajar ahi
        return true;
    }

    //Metodo 3: Validar la rotacion de sectores
    public boolean cumpleRotacion(Trabajador trabajador, Tienda tiendaNueva) {
        String terminalAnterior = trabajador.getUltimoTerminalTrabajado();
        String terminalNuevo = tiendaNueva.getTerminal();

        //Si es nuevo puede trabajar en cualquiera
        if (terminalAnterior == null) {
            return true;
        }

        if (terminalAnterior.equalsIgnoreCase(terminalNuevo)) {
            return false; //No rotó, le toca el mismo
        }
        return true; // Si rotó
    }

    //Metodo 4: Validar si está disponible (no tiene ausencias hoy)
    public boolean estaDisponible(Trabajador trabajador, LocalDate fechaHoy) {
        boolean tieneAusencia = asistenciaRepository.existsByTrabajadorAndFecha(trabajador, fechaHoy);

        if (tieneAusencia) {
            return false;
        }
        return true;
    }
}
