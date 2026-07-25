package com.aeropuerto.distribucion.service;

import com.aeropuerto.distribucion.model.Tienda;
import com.aeropuerto.distribucion.repository.TiendaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TiendaService {
    private final TiendaRepository tiendaRepository;

    public TiendaService(TiendaRepository tiendaRepository) {
        this.tiendaRepository = tiendaRepository;
    }

    //Metodo 1: Obtener todas las tiendas
    public List<Tienda> obtenerTodas() {
        return tiendaRepository.findAll();
    }

    //Metodo 2: Guardar la tienda
    public Tienda guardar(Tienda tienda) {
        return tiendaRepository.save(tienda);
    }

    //Metodo 3: Buscar una tienda por su ID
    public Tienda obtenerPorId(Long id) {
        return tiendaRepository.findById(id).orElse(null);
    }
}
