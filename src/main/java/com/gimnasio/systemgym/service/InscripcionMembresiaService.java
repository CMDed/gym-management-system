package com.gimnasio.systemgym.service;

import com.gimnasio.systemgym.model.InscripcionMembresia;
import com.gimnasio.systemgym.model.Miembro;
import com.gimnasio.systemgym.model.Membresia;
import com.gimnasio.systemgym.repository.InscripcionMembresiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InscripcionMembresiaService {

    private final InscripcionMembresiaRepository inscripcionMembresiaRepository;
    private final MiembroService miembroService;
    private final MembresiaService membresiaService;

    @Autowired
    public InscripcionMembresiaService(InscripcionMembresiaRepository inscripcionMembresiaRepository,
                                       MiembroService miembroService,
                                       MembresiaService membresiaService) {
        this.inscripcionMembresiaRepository = inscripcionMembresiaRepository;
        this.miembroService = miembroService;
        this.membresiaService = membresiaService;
    }


    @Transactional
    public InscripcionMembresia crearInscripcionInicial(Long miembroId, Long membresiaId) {
        Miembro miembro = miembroService.obtenerMiembroPorId(miembroId)
                .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado para la inscripción."));
        Membresia membresia = membresiaService.obtenerMembresiaPorId(membresiaId)
                .orElseThrow(() -> new IllegalArgumentException("Membresía no encontrada para la inscripción."));

        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFin = fechaInicio.plusDays(membresia.getDuracionDias());

        InscripcionMembresia inscripcion = new InscripcionMembresia();
        inscripcion.setMiembro(miembro);
        inscripcion.setMembresia(membresia);
        inscripcion.setFechaInicio(fechaInicio);
        inscripcion.setFechaFin(fechaFin);
        inscripcion.setPrecioPagado(BigDecimal.ZERO);
        inscripcion.setEstado("PENDIENTE_PAGO");
        inscripcion.setFechaCreacion(LocalDateTime.now());

        return inscripcionMembresiaRepository.save(inscripcion);
    }


    @Transactional
    public InscripcionMembresia crearInscripcion(Long miembroId, Long membresiaId, BigDecimal precioPagado) {
        Miembro miembro = miembroService.obtenerMiembroPorId(miembroId)
                .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado."));
        Membresia membresia = membresiaService.obtenerMembresiaPorId(membresiaId)
                .orElseThrow(() -> new IllegalArgumentException("Membresía no encontrada."));

        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFin = fechaInicio.plusDays(membresia.getDuracionDias());

        InscripcionMembresia inscripcion = new InscripcionMembresia();
        inscripcion.setMiembro(miembro);
        inscripcion.setMembresia(membresia);
        inscripcion.setFechaInicio(fechaInicio);
        inscripcion.setFechaFin(fechaFin);
        inscripcion.setPrecioPagado(precioPagado);
        inscripcion.setEstado("ACTIVA");
        inscripcion.setFechaCreacion(LocalDateTime.now());

        return inscripcionMembresiaRepository.save(inscripcion);
    }

    @Transactional(readOnly = true)
    public Optional<InscripcionMembresia> obtenerInscripcionPorId(Long id) {
        return inscripcionMembresiaRepository.findById(id);
    }

    @Transactional
    public void eliminarInscripcion(Long id) {
        if (!inscripcionMembresiaRepository.existsById(id)) {
            throw new IllegalArgumentException("La inscripción con ID " + id + " no existe.");
        }
        inscripcionMembresiaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<InscripcionMembresia> obtenerInscripcionesPorMiembro(Long miembroId) {
        Miembro miembro = miembroService.obtenerMiembroPorId(miembroId)
                .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado."));
        return inscripcionMembresiaRepository.findByMiembro(miembro);
    }

    @Transactional(readOnly = true)
    public List<InscripcionMembresia> obtenerTodasLasInscripciones() {
        return inscripcionMembresiaRepository.findAll();
    }

    @Transactional
    public InscripcionMembresia actualizarEstadoInscripcion(Long id, String nuevoEstado) {
        InscripcionMembresia inscripcion = inscripcionMembresiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inscripción no encontrada."));
        inscripcion.setEstado(nuevoEstado);
        return inscripcionMembresiaRepository.save(inscripcion);
    }


    @Transactional
    public InscripcionMembresia completarPagoInscripcion(Long inscripcionId, BigDecimal montoPagado) {
        InscripcionMembresia inscripcion = inscripcionMembresiaRepository.findById(inscripcionId)
                .orElseThrow(() -> new IllegalArgumentException("Inscripción de membresía no encontrada."));

        inscripcion.setPrecioPagado(montoPagado);
        inscripcion.setEstado("ACTIVA");


        return inscripcionMembresiaRepository.save(inscripcion);
    }


    @Transactional
    public void actualizarEstadosMembresiasVencidas() {
        List<InscripcionMembresia> inscripcionesActivas = inscripcionMembresiaRepository.findByEstado("ACTIVA");
        LocalDate hoy = LocalDate.now();

        for (InscripcionMembresia inscripcion : inscripcionesActivas) {
            if (inscripcion.getFechaFin().isBefore(hoy)) {
                inscripcion.setEstado("VENCIDA");
                inscripcionMembresiaRepository.save(inscripcion);
            }
        }
    }

    @Transactional(readOnly = true)
    public Optional<InscripcionMembresia> obtenerMembresiaActivaPorMiembro(Long miembroId) {
        Miembro miembro = miembroService.obtenerMiembroPorId(miembroId)
                .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado."));

        return inscripcionMembresiaRepository.findTopByMiembroAndEstadoOrderByFechaFinDesc(miembro, "ACTIVA");
    }

    // --- MÉTODO PARA OBTENER LA INSCRIPCIÓN PENDIENTE DE PAGO ---
    @Transactional(readOnly = true)
    public Optional<InscripcionMembresia> obtenerInscripcionPendientePorMiembro(Long miembroId) {
        Miembro miembro = miembroService.obtenerMiembroPorId(miembroId)
                .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado."));

        return inscripcionMembresiaRepository.findTopByMiembroAndEstadoOrderByFechaCreacionDesc(miembro, "PENDIENTE_PAGO");

    }


}