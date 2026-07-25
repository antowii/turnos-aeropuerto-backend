package com.aeropuerto.distribucion.controller;

import com.aeropuerto.distribucion.model.Trabajador;
import com.aeropuerto.distribucion.service.TrabajadorService;
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
