package com.gimnasio.systemgym.service;

import com.gimnasio.systemgym.model.Miembro;
import com.gimnasio.systemgym.repository.MiembroRepository;
import org.springframework.beans.factory.annotation.Autowired; // Para inyección de dependencias
import org.springframework.stereotype.Service; // Para marcar la clase como un servicio
import org.springframework.transaction.annotation.Transactional; // Para manejo de transacciones

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service // Marca esta clase como un componente de servicio de Spring
public class MiembroService {

    private final MiembroRepository miembroRepository;

    // Inyección de dependencias a través del constructor (forma recomendada)
    @Autowired
    public MiembroService(MiembroRepository miembroRepository) {
        this.miembroRepository = miembroRepository;
    }

    // --- Métodos de Lógica de Negocio ---

    @Transactional // Garantiza que la operación se realice dentro de una transacción de BD
    public Miembro registrarNuevoMiembro(Miembro miembro) {
        // Aquí puedes añadir lógica de validación antes de guardar
        if (miembroRepository.findByDni(miembro.getDni()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un miembro con este DNI.");
        }
        if (miembroRepository.findByEmail(miembro.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un miembro con este email.");
        }
        // Asignar fecha de registro y establecer como activo por defecto
        miembro.setFechaRegistro(LocalDateTime.now());
        if (miembro.getActivo() == null) { // Para evitar sobrescribir si ya viene con un valor
            miembro.setActivo(true);
        }
        // NOTA: La contraseña DEBE ser encriptada antes de guardarla en un entorno real.
        // Esto lo haremos cuando configuremos Spring Security para la encriptación.
        return miembroRepository.save(miembro);
    }

    @Transactional(readOnly = true) // readOnly=true optimiza para solo lectura
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
        // Lógica para actualizar un miembro existente
        if (miembroActualizado.getId() == null || !miembroRepository.existsById(miembroActualizado.getId())) {
            throw new IllegalArgumentException("El miembro a actualizar no existe o no tiene un ID válido.");
        }
        // Podrías cargar el miembro existente, copiar los datos actualizados y guardar
        Miembro existente = miembroRepository.findById(miembroActualizado.getId())
                .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado para actualización."));

        // Actualiza solo los campos permitidos o relevantes para la actualización
        existente.setNombre(miembroActualizado.getNombre());
        existente.setApellido(miembroActualizado.getApellido());
        existente.setEmail(miembroActualizado.getEmail());
        existente.setTelefono(miembroActualizado.getTelefono());
        existente.setFechaNacimiento(miembroActualizado.getFechaNacimiento());
        existente.setSexo(miembroActualizado.getSexo());
        // No actualizamos DNI ni FechaRegistro en una actualización estándar,
        // pero la contraseña sí debería tener su propio método de actualización.

        return miembroRepository.save(existente);
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