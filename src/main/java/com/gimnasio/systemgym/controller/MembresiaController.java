package com.gimnasio.systemgym.controller;

import com.gimnasio.systemgym.model.Membresia;
import com.gimnasio.systemgym.service.MembresiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/membresias")
public class MembresiaController {

    private final MembresiaService membresiaService;

    @Autowired
    public MembresiaController(MembresiaService membresiaService) {
        this.membresiaService = membresiaService;
    }

    @PostMapping
    public ResponseEntity<?> crearMembresia(@RequestBody Membresia membresia) {
        try {
            Membresia nuevaMembresia = membresiaService.crearMembresia(membresia);
            return new ResponseEntity<>(nuevaMembresia, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor al crear membresía: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //End point para obtener membresia por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerMembresiaPorId(@PathVariable Long id) {
        Optional<Membresia> membresia = membresiaService.obtenerMembresiaPorId(id);
        if (membresia.isPresent()) {
            return new ResponseEntity<>(membresia.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Membresía no encontrada", HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping
    public ResponseEntity<List<Membresia>> obtenerTodasLasMembresias() {
        List<Membresia> membresias = membresiaService.obtenerTodasLasMembresias();
        return new ResponseEntity<>(membresias, HttpStatus.OK);
    }

    @GetMapping("/activas")
    public ResponseEntity<List<Membresia>> obtenerMembresiasActivas() {
        List<Membresia> membresias = membresiaService.obtenerMembresiasActivas();
        return new ResponseEntity<>(membresias, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarMembresia(@PathVariable Long id, @RequestBody Membresia membresia) {
        if (!id.equals(membresia.getId())) {
            return new ResponseEntity<>("El ID en la URL no coincide con el ID de la membresía en el cuerpo de la petición.", HttpStatus.BAD_REQUEST);
        }
        try {
            Membresia membresiaActualizada = membresiaService.actualizarMembresia(membresia);
            return new ResponseEntity<>(membresiaActualizada, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor al actualizar membresía: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarMembresia(@PathVariable Long id) {
        try {
            membresiaService.eliminarMembresia(id);
            return new ResponseEntity<>("Membresía eliminada exitosamente", HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor al eliminar membresía: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}