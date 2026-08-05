package com.aeropuerto.distribucion.service;

import com.aeropuerto.distribucion.model.Tienda;
import com.aeropuerto.distribucion.model.Trabajador;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TrabajadorServiceTest {
    private final TrabajadorService trabajadorService = new TrabajadorService(null, null);

    @Test
    public void testCumpleRequisitoTica_TiendaExigeTica_TrabajadorTieneTica_DebeAprobar() {
        //1. Arrange: Preparar los datos
        Trabajador trabajador = new Trabajador();
        trabajador.setTieneTica(true);

        Tienda tienda = new Tienda();
        tienda.setRequiereTica(true);

        //2. Act: Ejecutar el metodo a probar
        boolean resultado = trabajadorService.cumpleRequisitoTica(trabajador, tienda);

        //3. Assert: Comprobar el resultado
        assertTrue(resultado, "El trabajador debe ser aprobado porque tiene TICA");
    }

    @Test
    public void testCumpleRequisitoTica_TiendaExigeTica_TrabajadorSinTica_DebeRechazar() {
        //1. Arrange: Preparar los datos
        Trabajador trabajador = new Trabajador();
        trabajador.setTieneTica(false); // El trabajador NO tiene TICA

        Tienda tienda = new Tienda();
        tienda.setRequiereTica(true); // La tienda SI exige TICA

        //2. Act: Ejecutar el metodo
        boolean resultado = trabajadorService.cumpleRequisitoTica(trabajador, tienda);

        //3. Assert: Comprobar el rechazo
        assertFalse(resultado, "El sistema debió rechazar al trabajador porque NO tiene TICA");
    }

    @Test
    public void testEsTiendaValidaParaRol_VendedorEnNacional_DebeAprobar() {
        //1. Arrange: Preparar datos
        Trabajador trabajador = new Trabajador();
        trabajador.setCargo("Vendedor"); // Asignamos el cargo exacto

        Tienda tienda = new Tienda();
        tienda.setTerminal("Nacional"); // Asignamos el terminal exacto

        //2. Act: Ejecutar el metodo
        boolean resultado = trabajadorService.esTiendaValidaParaRol(trabajador, tienda);

        //3. Assert: Comprobar validación (usando el metodo directo)
        assertTrue(resultado, "El sistema debe aprobar al Vendedor en el terminal Nacional");
    }

    @Test
    public void testEsTiendaValidaParaRol_VendedorEnInternacional_DebeAprobar() {
        //1. Arrange: Preparar datos reales
        Trabajador trabajador = new Trabajador();
        trabajador.setCargo("Vendedor");

        Tienda tienda = new Tienda();
        tienda.setTerminal("Internacional");

        //2. Act: Ejecutar el metodo
        boolean resultado = trabajadorService.esTiendaValidaParaRol(trabajador, tienda);

        //3. Assert: Comprobar validacion correcta
        assertTrue(resultado, "El sistema debe aprobar al Vendedor en el terminal Internacional");
    }

    @Test
    public void testEsTiendaValidaParaRol_JoyeraEnTiendaNoPermitida_DebeRechazar() {
        //1. Arrange: Preparamos a la Joyera y una tienda prohibida para ella
        Trabajador trabajador = new Trabajador();
        trabajador.setCargo("Joyera");

        Tienda tienda = new Tienda();
        tienda.setNombre("Travel Andes"); // Tienda fuera de su rotación permitida

        //2. Act: Ejecutamos la validacion
        boolean resultado = trabajadorService.esTiendaValidaParaRol(trabajador, tienda);

        //3. Assert: Comprobamos que el sistema la bloquee (debe ser false)
        assertFalse(resultado, "El sistema debe rechazar a la Joyera si intenta ir a una tienda distinta a Manquehue, Joyería o Emprende");
    }


}
