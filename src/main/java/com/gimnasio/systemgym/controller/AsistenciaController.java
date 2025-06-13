package com.gimnasio.systemgym.controller;

import com.gimnasio.systemgym.model.Asistencia;
import com.gimnasio.systemgym.service.AsistenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/asistencias")
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    @Autowired
    public AsistenciaController(AsistenciaService asistenciaService) {
        this.asistenciaService = asistenciaService;
    }

    @PostMapping
    public ResponseEntity<?> registrarAsistencia(
            @RequestParam Long miembroId,
            @RequestParam Long registradorId) {
        try {
            Asistencia nuevaAsistencia = asistenciaService.registrarAsistencia(miembroId, registradorId);
            return new ResponseEntity<>(nuevaAsistencia, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor al registrar asistencia: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //End point para obtener la asistencia por el ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerAsistenciaPorId(@PathVariable Long id) {
        Optional<Asistencia> asistencia = asistenciaService.obtenerAsistenciaPorId(id);
        if (asistencia.isPresent()) {
            return new ResponseEntity<>(asistencia.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Asistencia no encontrada", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarAsistencia(@PathVariable Long id) {
        try {
            asistenciaService.eliminarAsistencia(id);
            return new ResponseEntity<>("Asistencia eliminada exitosamente", HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor al eliminar asistencia: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<Asistencia>> obtenerTodasLasAsistencias() {
        List<Asistencia> asistencias = asistenciaService.obtenerTodasLasAsistencias();
        return new ResponseEntity<>(asistencias, HttpStatus.OK);
    }

    @GetMapping("/miembro/{miembroId}")
    public ResponseEntity<?> obtenerAsistenciasPorMiembro(@PathVariable Long miembroId) {
        try {
            List<Asistencia> asistencias = asistenciaService.obtenerAsistenciasPorMiembro(miembroId);
            return new ResponseEntity<>(asistencias, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor al buscar asistencias por miembro: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}