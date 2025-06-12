package com.gimnasio.systemgym.controller;

import com.gimnasio.systemgym.model.Miembro;
import com.gimnasio.systemgym.service.MiembroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/miembros")
public class MiembroController {

    private final MiembroService miembroService;

    @Autowired
    public MiembroController(MiembroService miembroService) {
        this.miembroService = miembroService;
    }

    @PostMapping
    public ResponseEntity<?> registrarMiembro(@RequestBody Miembro miembro) {
        try {
            Miembro nuevoMiembro = miembroService.registrarNuevoMiembro(miembro);
            return new ResponseEntity<>(nuevoMiembro, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor al registrar miembro: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Obtener assitencia por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerMiembroPorId(@PathVariable Long id) {
        Optional<Miembro> miembro = miembroService.obtenerMiembroPorId(id);
        if (miembro.isPresent()) {
            return new ResponseEntity<>(miembro.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Miembro no encontrado", HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping
    public ResponseEntity<List<Miembro>> obtenerTodosLosMiembros() {
        List<Miembro> miembros = miembroService.obtenerTodosLosMiembros();
        return new ResponseEntity<>(miembros, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarMiembro(@PathVariable Long id, @RequestBody Miembro miembro) {
        if (!id.equals(miembro.getId())) {
            return new ResponseEntity<>("El ID en la URL no coincide con el ID del miembro en el cuerpo de la petición.", HttpStatus.BAD_REQUEST);
        }
        try {
            Miembro miembroActualizado = miembroService.actualizarMiembro(miembro);
            return new ResponseEntity<>(miembroActualizado, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor al actualizar miembro: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarMiembro(@PathVariable Long id) {
        try {
            miembroService.eliminarMiembro(id);
            return new ResponseEntity<>("Miembro eliminado exitosamente", HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor al eliminar miembro: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}