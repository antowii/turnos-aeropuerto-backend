package com.aeropuerto.distribucion.service;

import com.aeropuerto.distribucion.model.Tienda;
import com.aeropuerto.distribucion.model.Trabajador;
import com.aeropuerto.distribucion.repository.AsistenciaRepository;
import com.aeropuerto.distribucion.repository.TrabajadorRepository;
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

    //Metodo 3A: Validar rotacion SOLO con el nombre del terminal (Ideal para el Encargado)
    public boolean cumpleRotacion(Trabajador trabajador, String terminalNuevo) {
        // EXCEPCION: Joyera y Supervisor no rotan de terminal. (El Encargado SI rota, así que no está aquí)
        String cargo = trabajador.getCargo();
        if (cargo != null && (cargo.equalsIgnoreCase("Joyera") || cargo.equalsIgnoreCase("Supervisor"))) {
            return true;
        }

        String terminalAnterior = trabajador.getUltimoTerminalTrabajado();
        if (terminalAnterior == null) {
            return true;
        }
        return !terminalAnterior.equalsIgnoreCase(terminalNuevo);
    }

    //Metodo 3B: Validar rotacion cuando sí tenemos una Tienda (Vendedores normales)
    public boolean cumpleRotacion(Trabajador trabajador, Tienda tiendaNueva) {
        return cumpleRotacion(trabajador, tiendaNueva.getTerminal());
    }

    //Metodo 4: Validar si está disponible (no tiene ausencias hoy)
    public boolean estaDisponible(Trabajador trabajador, LocalDate fechaHoy) {
        boolean tieneAusencia = asistenciaRepository.existsByTrabajadorAndFecha(trabajador, fechaHoy);

        if (tieneAusencia) {
            return false;
        }
        return true;
    }

    //Metodo 5: Guardar un nuevo trabajador en la base de datos
    public Trabajador guardar(Trabajador trabajador) {
        return trabajadorRepository.save(trabajador);
    }

    //Metodo 6: Buscar a un trabajador por su ID
    public Trabajador obtenerPorId(Long id) {
        // findById busca por número. orElse(null) significa: si no lo encuentras, devuelve un vacío.
        return trabajadorRepository.findById(id).orElse(null);
    }

    //Metodo 7: Validar si la tienda es permitida segun el rol del trabajador (Joyeras)
    public boolean esTiendaValidaParaRol(Trabajador trabajador, Tienda tienda) {
        String cargo = trabajador.getCargo();
        String nombreTienda = tienda.getNombre();

        if (cargo == null) {
            return true;
        }

        if (cargo.equalsIgnoreCase("Joyera")) {
            return nombreTienda.equalsIgnoreCase("Manquehue") ||
                    nombreTienda.equalsIgnoreCase("Joyería") ||
                    nombreTienda.equalsIgnoreCase("Emprende");
        }
        return true;
    }
}
