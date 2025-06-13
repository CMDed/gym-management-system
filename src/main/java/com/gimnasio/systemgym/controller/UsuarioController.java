package com.gimnasio.systemgym.controller;

import com.gimnasio.systemgym.model.Usuario;
import com.gimnasio.systemgym.service.UsuarioService;
import com.gimnasio.systemgym.dto.UsuarioDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<?> crearUsuario(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        try {
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setUsername(usuarioDTO.getUsername());
            nuevoUsuario.setPassword(usuarioDTO.getPassword());
            nuevoUsuario.setRol(usuarioDTO.getRol());
            nuevoUsuario.setNombre(usuarioDTO.getNombre());
            nuevoUsuario.setApellido(usuarioDTO.getApellido());
            nuevoUsuario.setEmail(usuarioDTO.getEmail());
            nuevoUsuario.setActivo(usuarioDTO.getActivo());


            Usuario usuarioGuardado = usuarioService.crearUsuario(nuevoUsuario);
            return new ResponseEntity<>(usuarioGuardado, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor al crear usuario: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //End point para obtener el usuario por su ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUsuarioPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.obtenerUsuarioPorId(id);
        if (usuario.isPresent()) {
            return new ResponseEntity<>(usuario.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerTodosLosUsuarios() {
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioDTO usuarioDTO) { // <-- CAMBIO ESENCIAL AQUÍ

        if (usuarioDTO.getId() == null || !id.equals(usuarioDTO.getId())) {
            return new ResponseEntity<>("El ID en la URL no coincide con el ID del usuario en el cuerpo de la petición.", HttpStatus.BAD_REQUEST);
        }
        try {

            Usuario usuarioExistente = usuarioService.obtenerUsuarioPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado para actualización."));


            usuarioExistente.setUsername(usuarioDTO.getUsername());
            usuarioExistente.setEmail(usuarioDTO.getEmail());
            usuarioExistente.setRol(usuarioDTO.getRol());
            usuarioExistente.setNombre(usuarioDTO.getNombre());
            usuarioExistente.setApellido(usuarioDTO.getApellido());
            usuarioExistente.setActivo(usuarioDTO.getActivo());


            if (usuarioDTO.getPassword() != null && !usuarioDTO.getPassword().isEmpty()) {
                usuarioExistente.setPassword(usuarioDTO.getPassword());
            }

            Usuario usuarioActualizado = usuarioService.actualizarUsuario(usuarioExistente);
            return new ResponseEntity<>(usuarioActualizado, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor al actualizar usuario: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<?> actualizarPassword(@PathVariable Long id, @RequestBody String nuevaPassword) {
        try {
            Usuario usuarioActualizado = usuarioService.actualizarPassword(id, nuevaPassword);
            return new ResponseEntity<>(usuarioActualizado, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor al actualizar contraseña: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            return new ResponseEntity<>("Usuario eliminado exitosamente", HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor al eliminar usuario: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}