package com.aeropuerto.turnos.controller;

import com.aeropuerto.turnos.model.Tienda;
import com.aeropuerto.turnos.service.TiendaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tiendas")
public class TiendaController {
    private final TiendaService tiendaService;
    public TiendaController(TiendaService tiendaService) {
        this.tiendaService = tiendaService;
    }

    @GetMapping
        public List<Tienda> listarTodas() {
        return tiendaService.obtenerTodas();
    }

    @PostMapping
    public Tienda crearTienda(@RequestBody Tienda tienda) {
        return tiendaService.guardar(tienda);
    }
}
