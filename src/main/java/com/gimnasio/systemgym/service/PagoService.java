package com.gimnasio.systemgym.service;

import com.gimnasio.systemgym.model.Pago;
import com.gimnasio.systemgym.model.Miembro;
import com.gimnasio.systemgym.model.InscripcionMembresia;
import com.gimnasio.systemgym.repository.PagoRepository;
import com.gimnasio.systemgym.repository.InscripcionMembresiaRepository;
import com.gimnasio.systemgym.service.MiembroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final MiembroService miembroService;
    private final InscripcionMembresiaRepository inscripcionMembresiaRepository;

    @Autowired
    public PagoService(PagoRepository pagoRepository, MiembroService miembroService,
                       InscripcionMembresiaRepository inscripcionMembresiaRepository) {
        this.pagoRepository = pagoRepository;
        this.miembroService = miembroService;
        this.inscripcionMembresiaRepository = inscripcionMembresiaRepository;
    }

    @Transactional
    public Pago registrarPago(Long miembroId, Long inscripcionId, BigDecimal montoPagado, String metodoPago, String referenciaTransaccion, String estado) {
        Miembro miembro = miembroService.obtenerMiembroPorId(miembroId)
                .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado."));

        InscripcionMembresia inscripcion = inscripcionMembresiaRepository.findById(inscripcionId)
                .orElseThrow(() -> new IllegalArgumentException("Inscripción de membresía no encontrada."));

        Pago pago = new Pago();
        pago.setMiembro(miembro);
        pago.setInscripcionMembresia(inscripcion);
        pago.setMonto(montoPagado);
        pago.setFechaPago(LocalDateTime.now());
        pago.setMetodoPago(metodoPago);
        pago.setReferenciaTransaccion(referenciaTransaccion);
        pago.setEstado(estado);

        Pago nuevoPago = pagoRepository.save(pago);

        return nuevoPago;
    }

    @Transactional
    public void eliminarPago(Long id) {
        if (!pagoRepository.existsById(id)) {
            throw new IllegalArgumentException("El pago con ID " + id + " no existe.");
        }
        pagoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Pago> obtenerPagoPorId(Long id) {
        return pagoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Pago> obtenerPagosPorMiembro(Long miembroId) {
        Miembro miembro = miembroService.obtenerMiembroPorId(miembroId)
                .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado."));
        return pagoRepository.findByMiembro(miembro);
    }

    @Transactional(readOnly = true)
    public List<Pago> obtenerTodosLosPagos() {
        return pagoRepository.findAll();
    }
}