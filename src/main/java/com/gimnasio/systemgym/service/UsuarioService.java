package com.gimnasio.systemgym.service;

import com.gimnasio.systemgym.model.Usuario;
import com.gimnasio.systemgym.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario crearUsuario(Usuario usuario) {
        if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            throw new IllegalArgumentException("El nombre de usuario ya existe.");
        }
        if (usuario.getEmail() != null && usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado.");
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        if (usuario.getActivo() == null) {
            usuario.setActivo(true);
        }
        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> obtenerUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    @Transactional
    public Usuario actualizarUsuario(Usuario usuarioActualizado) {
        if (usuarioActualizado.getId() == null || !usuarioRepository.existsById(usuarioActualizado.getId())) {
            throw new IllegalArgumentException("El usuario a actualizar no existe o no tiene un ID válido.");
        }
        Usuario existente = usuarioRepository.findById(usuarioActualizado.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado para actualización."));

        existente.setNombre(usuarioActualizado.getNombre());
        existente.setApellido(usuarioActualizado.getApellido());

        if (usuarioActualizado.getEmail() != null && !usuarioActualizado.getEmail().equals(existente.getEmail())) {
            if (usuarioRepository.findByEmail(usuarioActualizado.getEmail()).isPresent()) {
                throw new IllegalArgumentException("El nuevo email ya está registrado para otro usuario.");
            }
            existente.setEmail(usuarioActualizado.getEmail());
        }
        existente.setRol(usuarioActualizado.getRol());
        existente.setActivo(usuarioActualizado.getActivo());

        return usuarioRepository.save(existente);
    }

    @Transactional
    public Usuario actualizarPassword(Long id, String nuevaPassword) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("El usuario con ID " + id + " no existe.");
        }
        usuarioRepository.deleteById(id);
    }

    @Transactional
    public Usuario cambiarEstadoActivoUsuario(Long id, Boolean activo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        usuario.setActivo(activo);
        return usuarioRepository.save(usuario);
    }
}