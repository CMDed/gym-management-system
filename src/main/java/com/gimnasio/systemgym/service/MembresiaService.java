package com.gimnasio.systemgym.service;

import com.gimnasio.systemgym.model.Membresia;
import com.gimnasio.systemgym.repository.MembresiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MembresiaService {

    private final MembresiaRepository membresiaRepository;

    @Autowired
    public MembresiaService(MembresiaRepository membresiaRepository) {
        this.membresiaRepository = membresiaRepository;
    }

    @Transactional
    public Membresia crearMembresia(Membresia membresia) {
        if (membresiaRepository.findByNombrePlan(membresia.getNombrePlan()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una membresía con este nombre de plan.");
        }
        if (membresia.getActivo() == null) {
            membresia.setActivo(true);
        }
        return membresiaRepository.save(membresia);
    }

    @Transactional(readOnly = true)
    public Optional<Membresia> obtenerMembresiaPorId(Long id) {
        return membresiaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Membresia> obtenerMembresiaPorNombrePlan(String nombrePlan) {
        return membresiaRepository.findByNombrePlan(nombrePlan);
    }

    @Transactional(readOnly = true)
    public List<Membresia> obtenerTodasLasMembresias() {
        return membresiaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Membresia> obtenerMembresiasActivas() {
        return membresiaRepository.findByActivoTrue();
    }

    @Transactional
    public Membresia actualizarMembresia(Membresia membresiaActualizada) {
        if (membresiaActualizada.getId() == null || !membresiaRepository.existsById(membresiaActualizada.getId())) {
            throw new IllegalArgumentException("La membresía a actualizar no existe o no tiene un ID válido.");
        }

        Membresia existente = membresiaRepository.findById(membresiaActualizada.getId())
                .orElseThrow(() -> new IllegalArgumentException("Membresía no encontrada para actualización."));

        Optional<Membresia> existingByName = membresiaRepository.findByNombrePlan(membresiaActualizada.getNombrePlan());
        if (existingByName.isPresent() && !existingByName.get().getId().equals(membresiaActualizada.getId())) {
            throw new IllegalArgumentException("Ya existe otra membresía con este nombre de plan.");
        }

        existente.setNombrePlan(membresiaActualizada.getNombrePlan());
        existente.setDescripcion(membresiaActualizada.getDescripcion());
        existente.setPrecio(membresiaActualizada.getPrecio());
        existente.setDuracionDias(membresiaActualizada.getDuracionDias());
        existente.setActivo(membresiaActualizada.getActivo());

        return membresiaRepository.save(existente);
    }

    @Transactional
    public void eliminarMembresia(Long id) {
        if (!membresiaRepository.existsById(id)) {
            throw new IllegalArgumentException("La membresía con ID " + id + " no existe.");
        }
        membresiaRepository.deleteById(id);
    }

    @Transactional
    public Membresia cambiarEstadoActivoMembresia(Long id, boolean activo) {
        Membresia membresia = membresiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Membresía no encontrada."));
        membresia.setActivo(activo);
        return membresiaRepository.save(membresia);
    }
}