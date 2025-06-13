package com.gimnasio.systemgym.controller;

import com.gimnasio.systemgym.model.Miembro;
import com.gimnasio.systemgym.model.InscripcionMembresia; // ¡IMPORTAR!
import com.gimnasio.systemgym.service.MiembroService;
import com.gimnasio.systemgym.service.InscripcionMembresiaService; // ¡IMPORTAR!
import com.gimnasio.systemgym.dto.MiembroDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// import org.springframework.security.crypto.password.PasswordEncoder; // <-- ¡ELIMINAR ESTA IMPORTACIÓN!

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/miembros")
public class MiembroController {

    private final MiembroService miembroService;
    private final InscripcionMembresiaService inscripcionMembresiaService; // ¡NUEVO CAMPO!
    // private final PasswordEncoder passwordEncoder; // <-- ¡ELIMINAR ESTE CAMPO!

    @Autowired
    public MiembroController(MiembroService miembroService,
                             InscripcionMembresiaService inscripcionMembresiaService) { // <-- ¡MODIFICAR CONSTRUCTOR!
        this.miembroService = miembroService;
        this.inscripcionMembresiaService = inscripcionMembresiaService;
        // this.passwordEncoder = passwordEncoder; // No se asigna aquí
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
            nuevoMiembro.setContrasena(miembroDTO.getContrasena()); // La encriptación se hace en el Service
            nuevoMiembro.setActivo(true); // Siempre activo al registrarse
            nuevoMiembro.setFechaRegistro(LocalDateTime.now()); // Fecha y hora actual del registro

            Miembro miembroGuardado = miembroService.registrarNuevoMiembro(nuevoMiembro); // El servicio valida unicidad y encripta

            // --- ¡CRÍTICO: CREAR LA INSCRIPCIÓN DE LA MEMBRESÍA AQUÍ! ---
            Long membresiaId = miembroDTO.getMembresiaId(); // Asegúrate de que MiembroDTO tiene este campo
            if (membresiaId == null) {
                return new ResponseEntity<>("El ID de la membresía es requerido para la inscripción.", HttpStatus.BAD_REQUEST);
            }

            // Llama al nuevo método para crear la inscripción con estado PENDIENTE_PAGO
            InscripcionMembresia nuevaInscripcion = inscripcionMembresiaService.crearInscripcionInicial(
                    miembroGuardado.getId(),
                    membresiaId
            );

            // Clase interna para la respuesta, incluyendo el ID de la inscripción
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

    // --- MANTÉN TUS OTROS MÉTODOS Y AJUSTA EL PUT/UPDATE ---
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

            // Solo actualiza la contraseña si se proporciona una nueva (no nula y no vacía)
            // y la encriptación se hace en el servicio.
            if (miembroDTO.getContrasena() != null && !miembroDTO.getContrasena().isEmpty()) {
                miembroExistente.setContrasena(miembroDTO.getContrasena()); // Envía la contraseña sin encriptar al servicio
            }
            miembroExistente.setActivo(miembroDTO.getActivo());

            Miembro miembroActualizado = miembroService.actualizarMiembro(miembroExistente); // El servicio se encarga de la encriptación si aplica
            return new ResponseEntity<>(miembroActualizado, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error interno del servidor al actualizar miembro: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ... (Mantén tus otros métodos @GetMapping, @DeleteMapping) ...
}