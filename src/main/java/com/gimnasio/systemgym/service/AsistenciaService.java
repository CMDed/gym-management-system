package com.gimnasio.systemgym.service;

import com.gimnasio.systemgym.model.Asistencia;
import com.gimnasio.systemgym.model.Miembro;
import com.gimnasio.systemgym.model.Usuario;
import com.gimnasio.systemgym.repository.AsistenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final MiembroService miembroService;
    private final UsuarioService usuarioService;

    @Autowired
    public AsistenciaService(AsistenciaRepository asistenciaRepository,
                             MiembroService miembroService,
                             UsuarioService usuarioService) {
        this.asistenciaRepository = asistenciaRepository;
        this.miembroService = miembroService;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public Asistencia registrarAsistencia(Long miembroId, Long registradorId) {
        Miembro miembro = miembroService.obtenerMiembroPorId(miembroId)
                .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado."));
        Usuario registrador = usuarioService.obtenerUsuarioPorId(registradorId)
                .orElseThrow(() -> new IllegalArgumentException("Registrador (usuario) no encontrado."));

        Asistencia asistencia = new Asistencia();
        asistencia.setMiembro(miembro);
        asistencia.setRegistrador(registrador);
        asistencia.setFechaHoraEntrada(LocalDateTime.now());

        return asistenciaRepository.save(asistencia);
    }

    @Transactional
    public void eliminarAsistencia(Long id) {
        if (!asistenciaRepository.existsById(id)) {
            throw new IllegalArgumentException("La asistencia con ID " + id + " no existe.");
        }
        asistenciaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Asistencia> obtenerAsistenciaPorId(Long id) {
        return asistenciaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Asistencia> obtenerAsistenciasPorMiembro(Long miembroId) {
        Miembro miembro = miembroService.obtenerMiembroPorId(miembroId)
                .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado."));
        return asistenciaRepository.findByMiembro(miembro);
    }

    @Transactional(readOnly = true)
    public List<Asistencia> obtenerTodasLasAsistencias() {
        return asistenciaRepository.findAll();
    }
}