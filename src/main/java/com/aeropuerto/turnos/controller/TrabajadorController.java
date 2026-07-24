package com.aeropuerto.turnos.controller;

import com.aeropuerto.turnos.model.Trabajador;
import com.aeropuerto.turnos.service.TrabajadorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trabajadores")
public class TrabajadorController {
    private final TrabajadorService trabajadorService;
    public TrabajadorController(TrabajadorService trabajadorService) {
        this.trabajadorService = trabajadorService;
    }

    @GetMapping
        public List<Trabajador> listarTodos() {
        return trabajadorService.obtenerTodos();
    }

    @PostMapping
    public Trabajador crearTrabajador(@RequestBody Trabajador trabajador) {
        return trabajadorService.guardar(trabajador);
    }
}
