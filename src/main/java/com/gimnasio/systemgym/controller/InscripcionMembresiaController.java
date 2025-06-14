package com.gimnasio.systemgym.controller;

import com.gimnasio.systemgym.model.InscripcionMembresia;
import com.gimnasio.systemgym.service.InscripcionMembresiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionMembresiaController {

    private final InscripcionMembresiaService inscripcionMembresiaService;

    @Autowired
    public InscripcionMembresiaController(InscripcionMembresiaService inscripcionMembresiaService) {
        this.inscripcionMembresiaService = inscripcionMembresiaService;
    }


    @PostMapping("/iniciar")
    public ResponseEntity<?> iniciarNuevaInscripcion(
            @RequestParam Long miembroId,
            @RequestParam Long membresiaId) {
        try {
            InscripcionMembresia nuevaInscripcion = inscripcionMembresiaService.crearInscripcionInicial(miembroId, membresiaId);
            return new ResponseEntity<>(nuevaInscripcion, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al iniciar la inscripción: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @PutMapping("/{id}/completar-pago")
    public ResponseEntity<?> completarPagoInscripcion(
            @PathVariable Long id,
            @RequestParam BigDecimal montoPagado) {
        try {
            InscripcionMembresia inscripcionActualizada = inscripcionMembresiaService.completarPagoInscripcion(id, montoPagado);
            return new ResponseEntity<>(inscripcionActualizada, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al completar el pago de la inscripción: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/directa")
    public ResponseEntity<?> crearInscripcionDirecta(
                                                      @RequestParam Long miembroId,
                                                      @RequestParam Long membresiaId,
                                                      @RequestParam BigDecimal precioPagado) {
        try {
            InscripcionMembresia nuevaInscripcion = inscripcionMembresiaService.crearInscripcion(miembroId, membresiaId, precioPagado);
            return new ResponseEntity<>(nuevaInscripcion, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor al crear inscripción directa: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerInscripcionPorId(@PathVariable Long id) {
        Optional<InscripcionMembresia> inscripcion = inscripcionMembresiaService.obtenerInscripcionPorId(id);
        if (inscripcion.isPresent()) {
            return new ResponseEntity<>(inscripcion.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Inscripción no encontrada", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<InscripcionMembresia>> obtenerTodasLasInscripciones() {
        List<InscripcionMembresia> inscripciones = inscripcionMembresiaService.obtenerTodasLasInscripciones();
        return new ResponseEntity<>(inscripciones, HttpStatus.OK);
    }

    @GetMapping("/miembro/{miembroId}")
    public ResponseEntity<?> obtenerInscripcionesPorMiembro(@PathVariable Long miembroId) {
        try {
            List<InscripcionMembresia> inscripciones = inscripcionMembresiaService.obtenerInscripcionesPorMiembro(miembroId);
            return new ResponseEntity<>(inscripciones, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor al buscar inscripciones por miembro: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarInscripcion(@PathVariable Long id) {
        try {
            inscripcionMembresiaService.eliminarInscripcion(id);
            return new ResponseEntity<>("Inscripción eliminada exitosamente", HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor al eliminar inscripción: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstadoInscripcion(
            @PathVariable Long id,
            @RequestParam String nuevoEstado) {
        try {
            InscripcionMembresia inscripcionActualizada = inscripcionMembresiaService.actualizarEstadoInscripcion(id, nuevoEstado);
            return new ResponseEntity<>(inscripcionActualizada, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor al actualizar estado de inscripción: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}