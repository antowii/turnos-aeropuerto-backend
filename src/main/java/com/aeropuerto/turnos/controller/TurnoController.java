package com.aeropuerto.turnos.controller;

import com.aeropuerto.turnos.model.Tienda;
import com.aeropuerto.turnos.model.Trabajador;
import com.aeropuerto.turnos.service.TiendaService;
import com.aeropuerto.turnos.service.TrabajadorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/turnos")
public class TurnoController {
    private final TrabajadorService trabajadorService;
    private final TiendaService tiendaService;

    public TurnoController(TrabajadorService trabajadorService, TiendaService tiendaService) {
        this.trabajadorService = trabajadorService;
        this.tiendaService = tiendaService;
    }

    @GetMapping("/evaluar")
    public String evaluarTurno(
            @RequestParam Long idTrabajador,
            @RequestParam(required = false) Long idTienda,
            @RequestParam(required = false) String terminalDestino) {
        Trabajador trabajador = trabajadorService.obtenerPorId(idTrabajador);
        if (trabajador == null) {
            return "ERROR: Trabajador no encontrado.";
        }

        //Regla 1: Asistencia
        if (!trabajadorService.estaDisponible(trabajador, LocalDate.now())) {
            return "RECHAZADO: El trabajador tiene una ausencia registrada hoy.";
        }

        String cargo = trabajador.getCargo();
        if (cargo == null) {
            cargo = "MSP"; // Por si algún trabajador no tiene cargo
        }

        // --- FLUJO SUPERVISOR ---
        if (cargo.equalsIgnoreCase("Supervisor")) {
            return "APROBADO";
        }

        // --- FLUJO ENCARGADO ---
        if (cargo.equalsIgnoreCase("Encargado")) {
            if (terminalDestino == null) {
                return "ERROR: Para Encargado debes enviar el parámetro 'terminalDestino'.";
            }
            if (!trabajadorService.cumpleRotacion(trabajador, terminalDestino)) {
                return "RECHAZADO: El Encargado no puede repetir el terminal " + terminalDestino;
            }
            return "APROBADO: El Encargado se asignó al terminal " + terminalDestino;
        }

        // --- FLUJO NORMAL (Vendedores, Joyeras, Capitanes) ---
        if (idTienda == null) {
            return "ERROR: El cargo '" + cargo + "' requiere enviar el 'idTienda'.";
        }

        Tienda tienda = tiendaService.obtenerPorId(idTienda);
        if (tienda == null) {
            return "ERROR: Tienda no encontrada.";
        }

        //Regla 2: TICA
        if (!trabajadorService.cumpleRequisitoTica(trabajador, tienda)) {
            return "RECHAZADO: La tienda exige TICA y el trabajador no la tiene";
        }

        //Regla 3: Rotación
        if (!trabajadorService.cumpleRotacion(trabajador, tienda)) {
            return "RECHAZADO: El trabajador no puede repetir el terminal " + tienda.getTerminal();
        }

        //Regla 4: Compatibilidad de Rol
        if (!trabajadorService.esTiendaValidaParaRol(trabajador, tienda)) {
            return "RECHAZADO: El cargo '" + cargo + "' no tiene permitido trabajar en la tienda '" + tienda.getNombre() + "'.";
        }

        return "APROBADO: El trabajador puede estar en la tienda " + tienda.getNombre();
    }
}
