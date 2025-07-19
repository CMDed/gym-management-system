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

        Optional<InscripcionMembresia> activeOrPending = inscripcionMembresiaRepository
                .findTopByMiembroAndEstadoOrderByFechaFinDesc(miembro, "ACTIVA");

        if (activeOrPending.isPresent() && (activeOrPending.get().getFechaFin().isAfter(LocalDate.now()) || activeOrPending.get().getFechaFin().isEqual(LocalDate.now()))) {
            throw new IllegalArgumentException("El miembro ya tiene una membresía activa que no ha expirado. Finaliza el: " + activeOrPending.get().getFechaFin());
        }

        Optional<InscripcionMembresia> pendingPayment = inscripcionMembresiaRepository
                .findTopByMiembroAndEstadoOrderByFechaCreacionDesc(miembro, "PENDIENTE_PAGO");
        if (pendingPayment.isPresent()) {
            throw new IllegalArgumentException("El miembro ya tiene una inscripción pendiente de pago. ID: " + pendingPayment.get().getId());
        }

        InscripcionMembresia inscripcion = new InscripcionMembresia();
        inscripcion.setMiembro(miembro);
        inscripcion.setMembresia(membresia);
        inscripcion.setFechaInicio(LocalDate.now());
        inscripcion.setFechaFin(LocalDate.now().plusDays(membresia.getDuracionDias()));
        inscripcion.setPrecioPagado(BigDecimal.ZERO);
        inscripcion.setEstado("PENDIENTE_PAGO");
        inscripcion.setFechaCreacion(LocalDateTime.now());

        return inscripcionMembresiaRepository.save(inscripcion);
    }

    @Transactional
    public InscripcionMembresia completarPagoInscripcion(Long inscripcionId, BigDecimal montoPagado) {
        InscripcionMembresia inscripcion = inscripcionMembresiaRepository.findById(inscripcionId)
                .orElseThrow(() -> new IllegalArgumentException("Inscripción de membresía no encontrada."));

        if (!"PENDIENTE_PAGO".equalsIgnoreCase(inscripcion.getEstado())) {
            throw new IllegalArgumentException("La inscripción no está en estado 'PENDIENTE_PAGO'. Estado actual: " + inscripcion.getEstado());
        }

        if (montoPagado == null || montoPagado.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto pagado debe ser un valor positivo.");
        }

        if (montoPagado.compareTo(inscripcion.getMembresia().getPrecio()) < 0) {
            throw new IllegalArgumentException("El monto pagado es menor que el precio de la membresía (" + inscripcion.getMembresia().getPrecio() + ").");
        }

        inscripcion.setPrecioPagado(montoPagado);
        inscripcion.setEstado("ACTIVA");
        inscripcion.setFechaInicio(LocalDate.now());
        inscripcion.setFechaFin(LocalDate.now().plusDays(inscripcion.getMembresia().getDuracionDias()));

        return inscripcionMembresiaRepository.save(inscripcion);
    }

    @Transactional
    public InscripcionMembresia crearInscripcion(Long miembroId, Long membresiaId, BigDecimal precioPagado) {
        Miembro miembro = miembroService.obtenerMiembroPorId(miembroId)
                .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado."));
        Membresia membresia = membresiaService.obtenerMembresiaPorId(membresiaId)
                .orElseThrow(() -> new IllegalArgumentException("Membresía no encontrada."));

        if (precioPagado == null || precioPagado.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio pagado no puede ser nulo o negativo.");
        }

        Optional<InscripcionMembresia> activeMembership = inscripcionMembresiaRepository
                .findTopByMiembroAndEstadoOrderByFechaFinDesc(miembro, "ACTIVA")
                .filter(insc -> insc.getFechaFin().isAfter(LocalDate.now()) || insc.getFechaFin().isEqual(LocalDate.now()));

        if (activeMembership.isPresent()) {
            throw new IllegalArgumentException("El miembro ya tiene una membresía activa que termina el: " + activeMembership.get().getFechaFin());
        }

        if (precioPagado.compareTo(membresia.getPrecio()) < 0) {
            throw new IllegalArgumentException("El monto pagado (" + precioPagado + ") es menor que el precio de la membresía (" + membresia.getPrecio() + ").");
        }

        InscripcionMembresia inscripcion = new InscripcionMembresia();
        inscripcion.setMiembro(miembro);
        inscripcion.setMembresia(membresia);
        inscripcion.setFechaInicio(LocalDate.now());
        inscripcion.setFechaFin(LocalDate.now().plusDays(membresia.getDuracionDias()));
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
        return inscripcionMembresiaRepository.findByMiembroOrderByFechaCreacionDesc(miembro);
    }

    @Transactional(readOnly = true)
    public List<InscripcionMembresia> obtenerTodasLasInscripciones() {
        return inscripcionMembresiaRepository.findAll();
    }

    @Transactional
    public InscripcionMembresia actualizarEstadoInscripcion(Long id, String nuevoEstado) {
        InscripcionMembresia inscripcion = inscripcionMembresiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inscripción no encontrada."));

        String estadoNormalizado = nuevoEstado.toUpperCase();

        inscripcion.setEstado(estadoNormalizado);
        return inscripcionMembresiaRepository.save(inscripcion);
    }

    @Transactional
    public void actualizarEstadosMembresiasVencidas() {
        LocalDate hoy = LocalDate.now();
        List<InscripcionMembresia> inscripcionesActivasVencidas = inscripcionMembresiaRepository
                .findByEstadoAndFechaFinBefore("ACTIVA", hoy);

        for (InscripcionMembresia inscripcion : inscripcionesActivasVencidas) {
            inscripcion.setEstado("VENCIDA");
            inscripcionMembresiaRepository.save(inscripcion);
        }
    }

    @Transactional(readOnly = true)
    public Optional<InscripcionMembresia> obtenerMembresiaActivaPorMiembro(Long miembroId) {
        Miembro miembro = miembroService.obtenerMiembroPorId(miembroId)
                .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado."));

        return inscripcionMembresiaRepository.findTopByMiembroAndEstadoOrderByFechaFinDesc(miembro, "ACTIVA")
                .filter(insc -> insc.getFechaFin().isAfter(LocalDate.now()) || insc.getFechaFin().isEqual(LocalDate.now()));
    }

    @Transactional(readOnly = true)
    public Optional<InscripcionMembresia> obtenerInscripcionPendientePorMiembro(Long miembroId) {
        Miembro miembro = miembroService.obtenerMiembroPorId(miembroId)
                .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado."));

        return inscripcionMembresiaRepository.findTopByMiembroAndEstadoOrderByFechaCreacionDesc(miembro, "PENDIENTE_PAGO");
    }
}