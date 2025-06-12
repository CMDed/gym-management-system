package com.gimnasio.systemgym.controller;

import com.gimnasio.systemgym.model.Pago;
import com.gimnasio.systemgym.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService pagoService;

    @Autowired
    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping
    public ResponseEntity<?> registrarPago(
            @RequestParam Long miembroId,
            @RequestParam Long inscripcionId,
            @RequestParam BigDecimal monto,
            @RequestParam String metodoPago,
            @RequestParam(required = false) String referenciaTransaccion,
            @RequestParam String estado) {
        try {
            Pago nuevoPago = pagoService.registrarPago(miembroId, inscripcionId, monto, metodoPago, referenciaTransaccion, estado);
            return new ResponseEntity<>(nuevoPago, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor al registrar pago: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //End point para obtener el pago por el ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPagoPorId(@PathVariable Long id) {
        Optional<Pago> pago = pagoService.obtenerPagoPorId(id);
        if (pago.isPresent()) {
            return new ResponseEntity<>(pago.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Pago no encontrado", HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping
    public ResponseEntity<List<Pago>> obtenerTodosLosPagos() {
        List<Pago> pagos = pagoService.obtenerTodosLosPagos();
        return new ResponseEntity<>(pagos, HttpStatus.OK);
    }

    @GetMapping("/miembro/{miembroId}")
    public ResponseEntity<?> obtenerPagosPorMiembro(@PathVariable Long miembroId) {
        try {
            List<Pago> pagos = pagoService.obtenerPagosPorMiembro(miembroId);
            return new ResponseEntity<>(pagos, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor al buscar pagos por miembro: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}