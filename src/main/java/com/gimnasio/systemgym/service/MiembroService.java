package com.gimnasio.systemgym.service;

import com.gimnasio.systemgym.model.Miembro;
import com.gimnasio.systemgym.repository.MiembroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MiembroService {

    private final MiembroRepository miembroRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MiembroService(MiembroRepository miembroRepository, PasswordEncoder passwordEncoder) {
        this.miembroRepository = miembroRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Miembro registrarNuevoMiembro(Miembro miembro) {

        if (miembroRepository.findByDni(miembro.getDni()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un miembro con este DNI.");
        }
        if (miembroRepository.findByEmail(miembro.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un miembro con este email.");
        }


        if (miembro.getFechaRegistro() == null) {
            miembro.setFechaRegistro(LocalDateTime.now());
        }

        if (miembro.getActivo() == null) {
            miembro.setActivo(true);
        }


        if (miembro.getPassword() == null || miembro.getPassword().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria para el nuevo miembro.");
        }

        miembro.setPassword(passwordEncoder.encode(miembro.getPassword()));

        return miembroRepository.save(miembro);
    }

    @Transactional(readOnly = true)
    public Optional<Miembro> obtenerMiembroPorId(Long id) {
        return miembroRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Miembro> obtenerMiembroPorDni(String dni) {
        return miembroRepository.findByDni(dni);
    }

    @Transactional(readOnly = true)
    public Optional<Miembro> obtenerMiembroPorEmail(String email) {
        return miembroRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public List<Miembro> obtenerTodosLosMiembros() {
        return miembroRepository.findAll();
    }

    @Transactional
    public Miembro actualizarMiembro(Miembro miembroActualizado) {

        if (miembroActualizado.getId() == null) {
            throw new IllegalArgumentException("El ID del miembro a actualizar no puede ser nulo.");
        }

        Miembro existente = miembroRepository.findById(miembroActualizado.getId())
                .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado con ID: " + miembroActualizado.getId()));


        existente.setNombre(miembroActualizado.getNombre());
        existente.setApellido(miembroActualizado.getApellido());
        existente.setEmail(miembroActualizado.getEmail());
        existente.setTelefono(miembroActualizado.getTelefono());
        existente.setFechaNacimiento(miembroActualizado.getFechaNacimiento());
        existente.setSexo(miembroActualizado.getSexo());

        if (miembroActualizado.getActivo() != null) {
            existente.setActivo(miembroActualizado.getActivo());
        }


        if (miembroActualizado.getPassword() != null && !miembroActualizado.getPassword().isEmpty()) {
            existente.setPassword(passwordEncoder.encode(miembroActualizado.getPassword()));
        }

        return miembroRepository.save(existente);
    }

    @Transactional
    public Miembro actualizarPasswordMiembro(Long id, String nuevaPassword) {
        Miembro miembro = miembroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado con ID: " + id));
        if (nuevaPassword == null || nuevaPassword.isEmpty()) {
            throw new IllegalArgumentException("La nueva contraseña no puede ser nula o vacía.");
        }
        miembro.setPassword(passwordEncoder.encode(nuevaPassword));
        return miembroRepository.save(miembro);
    }

    @Transactional
    public void eliminarMiembro(Long id) {
        if (!miembroRepository.existsById(id)) {
            throw new IllegalArgumentException("El miembro con ID " + id + " no existe.");
        }
        miembroRepository.deleteById(id);
    }

    @Transactional
    public Miembro cambiarEstadoActivoMiembro(Long id, Boolean activo) {
        Miembro miembro = miembroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado."));
        miembro.setActivo(activo);
        return miembroRepository.save(miembro);
    }
}