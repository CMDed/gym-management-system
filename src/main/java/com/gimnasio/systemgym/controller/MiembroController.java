package com.gimnasio.systemgym.controller;

import com.gimnasio.systemgym.model.Miembro;
import com.gimnasio.systemgym.service.MiembroService;
import com.gimnasio.systemgym.dto.MiembroDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public ResponseEntity<?> registrarMiembro(@Valid @RequestBody MiembroDTO miembroDTO) {
        try {

            Miembro nuevoMiembro = new Miembro();
            nuevoMiembro.setDni(miembroDTO.getDni());
            nuevoMiembro.setNombre(miembroDTO.getNombre());
            nuevoMiembro.setApellido(miembroDTO.getApellido());
            nuevoMiembro.setEmail(miembroDTO.getEmail());
            nuevoMiembro.setSexo(miembroDTO.getSexo());
            nuevoMiembro.setTelefono(miembroDTO.getTelefono());
            nuevoMiembro.setFechaNacimiento(miembroDTO.getFechaNacimiento());
            nuevoMiembro.setPassword(miembroDTO.getPassword());
            nuevoMiembro.setActivo(miembroDTO.getActivo());


            Miembro miembroGuardado = miembroService.registrarNuevoMiembro(nuevoMiembro);
            return new ResponseEntity<>(miembroGuardado, HttpStatus.CREATED);
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
    public ResponseEntity<?> actualizarMiembro(@PathVariable Long id, @Valid @RequestBody MiembroDTO miembroDTO) {
        if (miembroDTO.getId() == null || !id.equals(miembroDTO.getId())) {
            return new ResponseEntity<>("El ID en la URL no coincide con el ID del miembro en el cuerpo de la petición.", HttpStatus.BAD_REQUEST);
        }
        try {

            Miembro miembroExistente = miembroService.obtenerMiembroPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado para actualización."));

            miembroExistente.setDni(miembroDTO.getDni());
            miembroExistente.setNombre(miembroDTO.getNombre());
            miembroExistente.setApellido(miembroDTO.getApellido());
            miembroExistente.setEmail(miembroDTO.getEmail());
            miembroExistente.setSexo(miembroDTO.getSexo());
            miembroExistente.setTelefono(miembroDTO.getTelefono());
            miembroExistente.setFechaNacimiento(miembroDTO.getFechaNacimiento());
            if (miembroDTO.getPassword() != null && !miembroDTO.getPassword().isEmpty()) {
                miembroExistente.setPassword(miembroDTO.getPassword());
            }
            miembroExistente.setActivo(miembroDTO.getActivo());


            Miembro miembroActualizado = miembroService.actualizarMiembro(miembroExistente);
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