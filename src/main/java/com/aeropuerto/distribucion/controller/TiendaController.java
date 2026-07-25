package com.aeropuerto.distribucion.controller;

import com.aeropuerto.distribucion.model.Tienda;
import com.aeropuerto.distribucion.service.TiendaService;
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
