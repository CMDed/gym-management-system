package com.gimnasio.systemgym.service;

import com.gimnasio.systemgym.model.Miembro;
import com.gimnasio.systemgym.model.InscripcionMembresia;
import com.gimnasio.systemgym.repository.MiembroRepository;
import com.gimnasio.systemgym.repository.InscripcionMembresiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class MiembroService {

    private final MiembroRepository miembroRepository;
    private final PasswordEncoder passwordEncoder;
    private final InscripcionMembresiaRepository inscripcionMembresiaRepository;

    @Autowired
    public MiembroService(MiembroRepository miembroRepository,
                          PasswordEncoder passwordEncoder,
                          InscripcionMembresiaRepository inscripcionMembresiaRepository) {
        this.miembroRepository = miembroRepository;
        this.passwordEncoder = passwordEncoder;
        this.inscripcionMembresiaRepository = inscripcionMembresiaRepository;
    }

    @Transactional
    public Miembro registrarNuevoMiembro(Miembro miembro) {
        if (miembroRepository.findByNumeroIdentificacion(miembro.getNumeroIdentificacion()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un miembro con este número de identificación.");
        }
        if (miembroRepository.findByCorreo(miembro.getCorreo()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un miembro con este correo electrónico.");
        }

        miembro.setContrasena(passwordEncoder.encode(miembro.getContrasena()));

        if (miembro.getActivo() == null) {
            miembro.setActivo(true);
        }
        if (miembro.getFechaRegistro() == null) {
            miembro.setFechaRegistro(LocalDateTime.now());
        }
        if (miembro.getRol() == null || miembro.getRol().isEmpty()) {
            miembro.setRol("MIEMBRO");
        }

        return miembroRepository.save(miembro);
    }

    @Transactional
    public Miembro actualizarMiembro(Miembro miembro) {
        Miembro existente = miembroRepository.findById(miembro.getId())
                .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado para actualización."));

        Optional<Miembro> existingByNumeroIdentificacion = miembroRepository.findByNumeroIdentificacion(miembro.getNumeroIdentificacion());
        if (existingByNumeroIdentificacion.isPresent() && !existingByNumeroIdentificacion.get().getId().equals(miembro.getId())) {
            throw new IllegalArgumentException("Ya existe otro miembro con este número de identificación.");
        }
        Optional<Miembro> existingByCorreo = miembroRepository.findByCorreo(miembro.getCorreo());
        if (existingByCorreo.isPresent() && !existingByCorreo.get().getId().equals(miembro.getId())) {
            throw new IllegalArgumentException("Ya existe otro miembro con este correo electrónico.");
        }

        existente.setTipoIdentificacion(miembro.getTipoIdentificacion());
        existente.setNumeroIdentificacion(miembro.getNumeroIdentificacion());
        existente.setNombre(miembro.getNombre());
        existente.setApellido(miembro.getApellido());
        existente.setCorreo(miembro.getCorreo());
        existente.setSexo(miembro.getSexo());
        existente.setTelefono(miembro.getTelefono());
        existente.setFechaNacimiento(miembro.getFechaNacimiento());

        if (miembro.getContrasena() != null && !miembro.getContrasena().isEmpty()) {
            existente.setContrasena(passwordEncoder.encode(miembro.getContrasena()));
        }
        existente.setActivo(miembro.getActivo());

        return miembroRepository.save(existente);
    }

    @Transactional(readOnly = true)
    public Optional<Miembro> obtenerMiembroPorId(Long id) {
        return miembroRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Miembro> obtenerTodosLosMiembros() {
        return miembroRepository.findAllWithInscriptionsAndMembershipDetails();
    }

    @Transactional
    public void eliminarMiembro(Long id) {
        if (!miembroRepository.existsById(id)) {
            throw new IllegalArgumentException("Miembro con ID " + id + " no encontrado para eliminar.");
        }
        miembroRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Miembro> obtenerMiembroPorCorreo(String correo) {
        return miembroRepository.findByCorreoWithInscriptionsAndMembershipDetails(correo);
    }

    @Transactional(readOnly = true)
    public Optional<Miembro> obtenerMiembroPorNumeroIdentificacion(String numeroIdentificacion) {
        return miembroRepository.findByNumeroIdentificacionWithInscriptionsAndMembershipDetails(numeroIdentificacion);
    }

    @Transactional(readOnly = true)
    public List<Miembro> buscarMiembros(String query) {
        if (query == null || query.trim().isEmpty()) {
            return miembroRepository.findAllWithInscriptionsAndMembershipDetails();
        }
        return miembroRepository.findBySearchQueryWithInscriptionsAndMembershipDetails(query);
    }

    @Transactional
    public void cambiarEstadoActivoMiembro(Long id, boolean activo) {
        Miembro miembro = miembroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado para cambiar estado."));
        miembro.setActivo(activo);
        miembroRepository.save(miembro);
    }

    @Transactional(readOnly = true)
    public List<InscripcionMembresia> obtenerHistorialMembresias(Long miembroId) {
        Miembro miembro = miembroRepository.findById(miembroId)
                .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado."));
        return inscripcionMembresiaRepository.findByMiembroOrderByFechaCreacionDesc(miembro);
    }

    @Transactional(readOnly = true)
    public Optional<InscripcionMembresia> obtenerMembresiaActivaActual(Miembro miembro) {
        System.out.println("DEBUG: Evaluando membresias activas para Miembro ID: " + miembro.getId());
        miembro.getInscripcionesMembresia().forEach(insc -> {
            System.out.println("DEBUG: Inscripcion ID: " + insc.getId() +
                    ", Estado: " + insc.getEstado() +
                    ", FechaInicio: " + insc.getFechaInicio() +
                    ", FechaFin: " + insc.getFechaFin() +
                    ", isAfterNow: " + (insc.getFechaFin() != null ? insc.getFechaFin().isAfter(LocalDate.now()) : "N/A"));
        });

        return miembro.getInscripcionesMembresia().stream()
                .filter(inscripcion -> "COMPLETADO".equalsIgnoreCase(inscripcion.getEstado()) &&
                        inscripcion.getFechaFin() != null &&
                        inscripcion.getFechaFin().isAfter(LocalDate.now()))
                .max(Comparator.comparing(InscripcionMembresia::getFechaFin));
    }

    @Transactional(readOnly = true)
    public Optional<InscripcionMembresia> obtenerMembresiaPendienteDePago(Miembro miembro) {
        return inscripcionMembresiaRepository.findTopByMiembroAndEstadoOrderByFechaCreacionDesc(miembro, "PENDIENTE_PAGO");
    }
}