package com.gimnasio.systemgym.controller;

import com.gimnasio.systemgym.model.Miembro;
import com.gimnasio.systemgym.model.InscripcionMembresia;
import com.gimnasio.systemgym.service.MiembroService;
import com.gimnasio.systemgym.service.InscripcionMembresiaService;
import com.gimnasio.systemgym.dto.MiembroDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/miembros")
public class MiembroController {

    private final MiembroService miembroService;
    private final InscripcionMembresiaService inscripcionMembresiaService;
    // private final PasswordEncoder passwordEncoder;

    @Autowired
    public MiembroController(MiembroService miembroService,
                             InscripcionMembresiaService inscripcionMembresiaService) {
        this.miembroService = miembroService;
        this.inscripcionMembresiaService = inscripcionMembresiaService;
        // this.passwordEncoder = passwordEncoder;

    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarMiembro(@Valid @RequestBody MiembroDTO miembroDTO) {
        try {
            Miembro nuevoMiembro = new Miembro();
            nuevoMiembro.setTipoIdentificacion(miembroDTO.getTipoIdentificacion());
            nuevoMiembro.setNumeroIdentificacion(miembroDTO.getNumeroIdentificacion());
            nuevoMiembro.setNombre(miembroDTO.getNombre());
            nuevoMiembro.setApellido(miembroDTO.getApellido());
            nuevoMiembro.setCorreo(miembroDTO.getCorreo());
            nuevoMiembro.setSexo(miembroDTO.getSexo());
            nuevoMiembro.setTelefono(miembroDTO.getTelefono());
            nuevoMiembro.setFechaNacimiento(miembroDTO.getFechaNacimiento());
            nuevoMiembro.setContrasena(miembroDTO.getContrasena());
            nuevoMiembro.setActivo(true);
            nuevoMiembro.setFechaRegistro(LocalDateTime.now());

            Miembro miembroGuardado = miembroService.registrarNuevoMiembro(nuevoMiembro);

            Long membresiaId = miembroDTO.getMembresiaId();
            if (membresiaId == null) {
                return new ResponseEntity<>("El ID de la membresía es requerido para la inscripción.", HttpStatus.BAD_REQUEST);
            }

            InscripcionMembresia nuevaInscripcion = inscripcionMembresiaService.crearInscripcionInicial(
                    miembroGuardado.getId(),
                    membresiaId
            );

            class RegistroResponse {
                public Long miembroId;
                public Long inscripcionId;
                public String mensaje;

                public RegistroResponse(Long miembroId, Long inscripcionId, String mensaje) {
                    this.miembroId = miembroId;
                    this.inscripcionId = inscripcionId;
                    this.mensaje = "Registro exitoso";
                }
            }
            return new ResponseEntity<>(new RegistroResponse(miembroGuardado.getId(), nuevaInscripcion.getId(), "Registro exitoso"), HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error interno del servidor al registrar miembro: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarMiembro(@PathVariable Long id, @Valid @RequestBody MiembroDTO miembroDTO) {
        if (miembroDTO.getId() == null || !id.equals(miembroDTO.getId())) {
            return new ResponseEntity<>("El ID en la URL no coincide con el ID del miembro en el cuerpo de la petición.", HttpStatus.BAD_REQUEST);
        }
        try {
            Miembro miembroExistente = miembroService.obtenerMiembroPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado para actualización."));

            miembroExistente.setTipoIdentificacion(miembroDTO.getTipoIdentificacion());
            miembroExistente.setNumeroIdentificacion(miembroDTO.getNumeroIdentificacion());
            miembroExistente.setNombre(miembroDTO.getNombre());
            miembroExistente.setApellido(miembroDTO.getApellido());
            miembroExistente.setCorreo(miembroDTO.getCorreo());
            miembroExistente.setSexo(miembroDTO.getSexo());
            miembroExistente.setTelefono(miembroDTO.getTelefono());
            miembroExistente.setFechaNacimiento(miembroDTO.getFechaNacimiento());

            if (miembroDTO.getContrasena() != null && !miembroDTO.getContrasena().isEmpty()) {
                miembroExistente.setContrasena(miembroDTO.getContrasena());
            }
            miembroExistente.setActivo(miembroDTO.getActivo());

            Miembro miembroActualizado = miembroService.actualizarMiembro(miembroExistente);
            return new ResponseEntity<>(miembroActualizado, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error interno del servidor al actualizar miembro: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}