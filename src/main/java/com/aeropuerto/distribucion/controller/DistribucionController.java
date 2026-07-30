package com.aeropuerto.distribucion.controller;

import com.aeropuerto.distribucion.model.Distribucion;
import com.aeropuerto.distribucion.model.Tienda;
import com.aeropuerto.distribucion.model.Trabajador;
import com.aeropuerto.distribucion.service.DistribucionService;
import com.aeropuerto.distribucion.service.TiendaService;
import com.aeropuerto.distribucion.service.TrabajadorService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/distribuciones")
public class DistribucionController {
    private final TrabajadorService trabajadorService;
    private final TiendaService tiendaService;
    private final DistribucionService distribucionService;

    public DistribucionController(TrabajadorService trabajadorService, TiendaService tiendaService, DistribucionService distribucionService) {
        this.trabajadorService = trabajadorService;
        this.tiendaService = tiendaService;
        this.distribucionService= distribucionService;
    }

    @GetMapping("/evaluar")
    public String evaluarDistribucion(
            @RequestParam Long idTrabajador,
            @RequestParam(required = false) Long idTienda,
            @RequestParam(required = false) String terminalDestino) {
        LocalDate hoy = LocalDate.now();

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
            if (distribucionService.terminalYaTieneEncargado(terminalDestino, hoy)) {
                return "RECHAZADO: Ya hay un Encargado cubriendo el terminal " + terminalDestino;
            }
            // ¡Guardamos al Encargado! (Le pasamos 'null' en la tienda, pero sí le pasamos el terminalDestino)
            distribucionService.registrarDistribucion(trabajador, null, terminalDestino, hoy);

            //Actualizamos la memoria del Encargado
            trabajador.setUltimoTerminalTrabajado(terminalDestino);
            //El Encargado no tiene tienda fija
            trabajador.setUltimaTiendaTrabajada(null);
            trabajadorService.actualizarTrabajador(trabajador);

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

        //Regla 5: Prioridad del Capitán en Emprende
        if (cargo.equalsIgnoreCase("Capitan") && tienda.getTerminal().equalsIgnoreCase("Internacional")) {
            // Si intentamos mandarlo a una tienda que NO es Emprende (ej. Espigon F o Manquehue)
            if (!tienda.getNombre().equalsIgnoreCase("Emprende")) {
                // Solo le exigimos ir a Emprende si el Capitán TIENE credencial TICA.
                if (trabajador.isTieneTica()) {
                    boolean emprendeCubierta = distribucionService.tiendaTieneCapitan("Emprende", hoy);
                    if (!emprendeCubierta) {
                        return "RECHAZADO: Por regla de prioridad, debes asignar un Capitán a 'Emprende' antes de enviarlo a '" + tienda.getNombre() + "'.";
                    }
                }
            }
        }

        //Regla 6: Capacidad de la Tienda
        if (!distribucionService.hayCapacidad(tienda, hoy)) {
            return "RECHAZADO: La tienda '" + tienda.getNombre() + "' ya alcanzó su capacidad máxima de " + tienda.getCapacidadMaxima() + " trabajadores.";
        }
        //Si pasó todas las validaciones, se guarda la distribucion
        distribucionService.registrarDistribucion(trabajador, tienda, tienda.getTerminal(), hoy);

        //Actualizamos la memoria del trabajador normal/Capitan
        trabajador.setUltimoTerminalTrabajado(tienda.getTerminal());
        trabajador.setUltimaTiendaTrabajada(tienda.getNombre());
        trabajadorService.actualizarTrabajador(trabajador);

        return "APROBADO: El trabajador puede estar en la tienda " + tienda.getNombre();
    }
    //Endpoint para ver la distribucion del día
    @GetMapping("/hoy")
    public List<Distribucion> obtenerDistribucionesDeHoy() {
        LocalDate hoy = LocalDate.now();
        return distribucionService.obtenerDistribucionesDelDia(hoy);
    }

    //Endpoint para sacar a un trabajador de su turno
    @DeleteMapping("/{id}")
    public String eliminarDistribucion(@PathVariable Long id) {
        distribucionService.eliminarDistribucion(id);
        return "EXITO: Distribución eliminada";
    }
}
