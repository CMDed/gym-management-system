package com.gimnasio.systemgym.service;

import com.gimnasio.systemgym.model.Membresia;
import com.gimnasio.systemgym.model.Miembro;
import com.gimnasio.systemgym.model.InscripcionMembresia;
import com.gimnasio.systemgym.model.Pago;
import com.gimnasio.systemgym.repository.InscripcionMembresiaRepository;
import com.gimnasio.systemgym.repository.MembresiaRepository;
import com.gimnasio.systemgym.repository.MiembroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class InscripcionMembresiaService {

    private final InscripcionMembresiaRepository inscripcionMembresiaRepository;
    private final MiembroRepository miembroRepository;
    private final MembresiaRepository membresiaRepository;
    private final PagoService pagoService;

    @Autowired
    public InscripcionMembresiaService(InscripcionMembresiaRepository inscripcionMembresiaRepository,
                                       MiembroRepository miembroRepository,
                                       MembresiaRepository membresiaRepository,
                                       PagoService pagoService) {
        this.inscripcionMembresiaRepository = inscripcionMembresiaRepository;
        this.miembroRepository = miembroRepository;
        this.membresiaRepository = membresiaRepository;
        this.pagoService = pagoService;
    }

    @Transactional
    public InscripcionMembresia crearInscripcionInicial(Long miembroId, Long membresiaId) {
        if (miembroId == null || membresiaId == null) {
            throw new IllegalArgumentException("ID de miembro y membresía no pueden ser nulos.");
        }

        Miembro miembro = miembroRepository.findById(miembroId)
                .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado."));
        Membresia membresia = membresiaRepository.findById(membresiaId)
                .orElseThrow(() -> new IllegalArgumentException("Membresía no encontrada."));

        boolean hasActiveOrPending = inscripcionMembresiaRepository
                .findByMiembroAndEstadoIn(miembro, Arrays.asList("ACTIVA", "PENDIENTE_PAGO"))
                .stream()
                .anyMatch(i -> i.getFechaFin().isAfter(LocalDate.now()));

        if (hasActiveOrPending) {
            throw new IllegalArgumentException("El miembro ya tiene una inscripción activa o pendiente de pago.");
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

        System.out.println("DEBUG: completando pago para Inscripcion ID: " + inscripcion.getId());
        System.out.println("DEBUG: Estado inicial: " + inscripcion.getEstado() + ", FechaFin inicial: " + inscripcion.getFechaFin());

        if (!inscripcion.getEstado().equals("PENDIENTE_PAGO")) {
            throw new IllegalArgumentException("La inscripción no está en estado 'PENDIENTE_PAGO'. Estado actual: " + inscripcion.getEstado());
        }

        inscripcion.setPrecioPagado(montoPagado);
        inscripcion.setEstado("COMPLETADO");
        inscripcion.setFechaInicio(LocalDate.now());

        if (inscripcion.getMembresia() != null && inscripcion.getMembresia().getDuracionDias() != null) {
            inscripcion.setFechaFin(inscripcion.getFechaInicio().plusDays(inscripcion.getMembresia().getDuracionDias()));
            System.out.println("DEBUG: Duracion en dias de la membresia: " + inscripcion.getMembresia().getDuracionDias());
            System.out.println("DEBUG: FechaFin CALCULA: " + inscripcion.getFechaFin());
        } else {
            System.err.println("Advertencia: Membresia o duración de la membresia es NULL. Inscripcion ID: " + inscripcion.getId());
        }

        if (inscripcion.getFechaCreacion() == null) {
            inscripcion.setFechaCreacion(LocalDateTime.now());
        }

        InscripcionMembresia inscripcionGuardada = inscripcionMembresiaRepository.save(inscripcion);
        System.out.println("DEBUG: Estado FINAL despues de save: " + inscripcionGuardada.getEstado() + ", FechaFin FINAL: " + inscripcionGuardada.getFechaFin());
        return inscripcionGuardada;
    }

    private boolean simularProcesamientoPago(String numeroTarjeta, String fechaVencimiento, String cvv, BigDecimal monto) {
        if (numeroTarjeta.equals("4000000000000001")) {
            return false;
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return true;
    }

    @Transactional
    public InscripcionMembresia crearOActualizarInscripcionConPago(
            Long miembroId,
            Long membresiaId,
            String numeroTarjeta,
            String fechaVencimiento,
            String cvv) {

        Miembro miembro = miembroRepository.findById(miembroId)
                .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado."));
        Membresia membresia = membresiaRepository.findById(membresiaId)
                .orElseThrow(() -> new IllegalArgumentException("Membresía no encontrada."));

        InscripcionMembresia inscripcionExistente = inscripcionMembresiaRepository
                .findTopByMiembroAndEstadoOrderByFechaCreacionDesc(miembro, "PENDIENTE_PAGO")
                .orElse(null);

        InscripcionMembresia inscripcion;
        if (inscripcionExistente != null && inscripcionExistente.getMembresia().getId().equals(membresiaId)) {
            inscripcion = inscripcionExistente;
        } else {
            boolean hasActive = inscripcionMembresiaRepository
                    .findTopByMiembroAndEstadoOrderByFechaFinDesc(miembro, "ACTIVA")
                    .filter(i -> i.getFechaFin().isAfter(LocalDate.now()) || i.getFechaFin().isEqual(LocalDate.now()))
                    .isPresent();

            if (hasActive) {
            }

            inscripcion = new InscripcionMembresia();
            inscripcion.setMiembro(miembro);
            inscripcion.setMembresia(membresia);
            inscripcion.setFechaInicio(LocalDate.now());
            inscripcion.setFechaFin(LocalDate.now().plusDays(membresia.getDuracionDias()));
            inscripcion.setPrecioPagado(BigDecimal.ZERO);
            inscripcion.setEstado("PENDIENTE_PAGO");
            inscripcion.setFechaCreacion(LocalDateTime.now());
            inscripcion = inscripcionMembresiaRepository.save(inscripcion);
        }

        if (numeroTarjeta != null && !numeroTarjeta.isEmpty() &&
                fechaVencimiento != null && !fechaVencimiento.isEmpty() &&
                cvv != null && !cvv.isEmpty()) {

            boolean pagoExitoso = simularProcesamientoPago(numeroTarjeta, fechaVencimiento, cvv, membresia.getPrecio());

            if (pagoExitoso) {
                String metodoPago = "Tarjeta de Crédito/Débito";
                String referenciaTransaccion = "TRX-" + System.currentTimeMillis();

                pagoService.registrarPago(miembroId, inscripcion.getId(), membresia.getPrecio(), metodoPago, referenciaTransaccion, "COMPLETADO");

                InscripcionMembresia inscripcionActualizada = this.completarPagoInscripcion(inscripcion.getId(), membresia.getPrecio());
                System.out.println("DEBUG: despues de completarPagoInscripcion - Estado: " + inscripcionActualizada.getEstado() + ", FechaFin: " + inscripcionActualizada.getFechaFin());

                return inscripcionActualizada;
            } else {
                pagoService.registrarPago(miembroId, inscripcion.getId(), membresia.getPrecio(), "Tarjeta de Crédito/Débito", "TRX-FAIL-" + System.currentTimeMillis(), "FALLIDO");
                throw new IllegalArgumentException("El procesamiento del pago ha fallado. La inscripción se mantiene como PENDIENTE_PAGO.");
            }
        } else {
            return inscripcion;
        }
    }

    @Transactional(readOnly = true)
    public Optional<InscripcionMembresia> obtenerInscripcionPorId(Long id) {
        return inscripcionMembresiaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<InscripcionMembresia> obtenerInscripcionesPorMiembro(Long miembroId) {
        Miembro miembro = miembroRepository.findById(miembroId)
                .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado."));
        return inscripcionMembresiaRepository.findByMiembroOrderByFechaCreacionDesc(miembro);
    }

    @Transactional(readOnly = true)
    public Optional<InscripcionMembresia> obtenerMembresiaActivaPorMiembro(Long miembroId) {
        Miembro miembro = miembroRepository.findById(miembroId)
                .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado."));

        return inscripcionMembresiaRepository.findTopByMiembroAndEstadoWithMembresiaOrderByFechaFinDesc(miembro, "COMPLETADO")
                .filter(insc -> insc.getFechaFin().isAfter(LocalDate.now()) || insc.getFechaFin().isEqual(LocalDate.now()));
    }

    @Transactional(readOnly = true)
    public Optional<InscripcionMembresia> obtenerInscripcionPendientePorMiembro(Long miembroId) {
        Miembro miembro = miembroRepository.findById(miembroId)
                .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado."));

        return inscripcionMembresiaRepository.findTopByMiembroAndEstadoWithMembresiaOrderByFechaCreacionDesc(miembro, "PENDIENTE_PAGO");
    }

    @Transactional
    public InscripcionMembresia actualizarEstadoInscripcion(Long inscripcionId, String nuevoEstado) {
        InscripcionMembresia inscripcion = inscripcionMembresiaRepository.findById(inscripcionId)
                .orElseThrow(() -> new IllegalArgumentException("Inscripción de membresía no encontrada."));

        inscripcion.setEstado(nuevoEstado);
        return inscripcionMembresiaRepository.save(inscripcion);
    }

    @Transactional
    public InscripcionMembresia crearInscripcion(Long miembroId, Long membresiaId, BigDecimal precioPagado) {
        if (miembroId == null || membresiaId == null) {
            throw new IllegalArgumentException("ID de miembro y membresía no pueden ser nulos.");
        }

        Miembro miembro = miembroRepository.findById(miembroId)
                .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado."));
        Membresia membresia = membresiaRepository.findById(membresiaId)
                .orElseThrow(() -> new IllegalArgumentException("Membresía no encontrada."));

        boolean hasActive = inscripcionMembresiaRepository
                .findTopByMiembroAndEstadoOrderByFechaFinDesc(miembro, "ACTIVA")
                .filter(i -> i.getFechaFin().isAfter(LocalDate.now()) || i.getFechaFin().isEqual(LocalDate.now()))
                .isPresent();

        if (hasActive) {
            throw new IllegalArgumentException("El miembro ya tiene una inscripción ACTIVA. No se puede crear otra inscripción directa activa.");
        }

        InscripcionMembresia inscripcion = new InscripcionMembresia();
        inscripcion.setMiembro(miembro);
        inscripcion.setMembresia(membresia);
        inscripcion.setFechaInicio(LocalDate.now());
        inscripcion.setFechaFin(LocalDate.now().plusDays(membresia.getDuracionDias()));
        inscripcion.setPrecioPagado(precioPagado);
        inscripcion.setEstado("COMPLETADO");
        inscripcion.setFechaCreacion(LocalDateTime.now());

        return inscripcionMembresiaRepository.save(inscripcion);
    }

    @Transactional(readOnly = true)
    public List<InscripcionMembresia> obtenerTodasLasInscripciones() {
        return inscripcionMembresiaRepository.findAll();
    }

    @Transactional
    public void eliminarInscripcion(Long id) {
        if (!inscripcionMembresiaRepository.existsById(id)) {
            throw new IllegalArgumentException("La inscripción con ID " + id + " no existe.");
        }
        inscripcionMembresiaRepository.deleteById(id);
    }

    @Transactional
    public void actualizarEstadosMembresiasVencidas() {
        List<InscripcionMembresia> inscripcionesActivasVencidas = inscripcionMembresiaRepository
                .findByEstadoAndFechaFinLessThanEqual("ACTIVA", LocalDate.now());

        for (InscripcionMembresia inscripcion : inscripcionesActivasVencidas) {
            inscripcion.setEstado("EXPIRADA");
            inscripcionMembresiaRepository.save(inscripcion);
        }
    }
}