package com.gimnasio.systemgym.service;

import com.gimnasio.systemgym.model.Miembro;
import com.gimnasio.systemgym.repository.MiembroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // ¡IMPORTAR!
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // ¡IMPORTAR!

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MiembroService {

    private final MiembroRepository miembroRepository;
    private final PasswordEncoder passwordEncoder; // Inyectar PasswordEncoder aquí

    @Autowired
    public MiembroService(MiembroRepository miembroRepository, PasswordEncoder passwordEncoder) {
        this.miembroRepository = miembroRepository;
        this.passwordEncoder = passwordEncoder; // Asignar
    }

    @Transactional
    public Miembro registrarNuevoMiembro(Miembro miembro) {
        // Validación de unicidad centralizada en el Service
        if (miembroRepository.findByNumeroIdentificacion(miembro.getNumeroIdentificacion()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un miembro con este número de identificación.");
        }
        if (miembroRepository.findByCorreo(miembro.getCorreo()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un miembro con este correo electrónico.");
        }

        // ¡Encriptar la contraseña aquí, antes de guardar!
        miembro.setContrasena(passwordEncoder.encode(miembro.getContrasena()));

        // Establecer valores por defecto si no vienen del DTO o son gestionados por el backend
        if (miembro.getActivo() == null) {
            miembro.setActivo(true);
        }
        if (miembro.getFechaRegistro() == null) {
            miembro.setFechaRegistro(LocalDateTime.now());
        }

        return miembroRepository.save(miembro);
    }

    @Transactional
    public Miembro actualizarMiembro(Miembro miembro) {
        // Cargar el miembro existente para asegurar que no se sobrescriban campos no actualizados
        Miembro existente = miembroRepository.findById(miembro.getId())
                .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado para actualización."));

        // Validación de unicidad para actualización (excluyendo el propio miembro)
        Optional<Miembro> existingByNumeroIdentificacion = miembroRepository.findByNumeroIdentificacion(miembro.getNumeroIdentificacion());
        if (existingByNumeroIdentificacion.isPresent() && !existingByNumeroIdentificacion.get().getId().equals(miembro.getId())) {
            throw new IllegalArgumentException("Ya existe otro miembro con este número de identificación.");
        }
        Optional<Miembro> existingByCorreo = miembroRepository.findByCorreo(miembro.getCorreo());
        if (existingByCorreo.isPresent() && !existingByCorreo.get().getId().equals(miembro.getId())) {
            throw new IllegalArgumentException("Ya existe otro miembro con este correo electrónico.");
        }

        // Actualizar campos individuales del miembro existente
        existente.setTipoIdentificacion(miembro.getTipoIdentificacion());
        existente.setNumeroIdentificacion(miembro.getNumeroIdentificacion());
        existente.setNombre(miembro.getNombre());
        existente.setApellido(miembro.getApellido());
        existente.setCorreo(miembro.getCorreo());
        existente.setSexo(miembro.getSexo());
        existente.setTelefono(miembro.getTelefono());
        existente.setFechaNacimiento(miembro.getFechaNacimiento());

        // Solo actualizar la contraseña si se proporciona una nueva (ya encriptada por el controlador o servicio)
        // Ojo: Si el DTO envía la contraseña encriptada, este check no es suficiente.
        // Lo ideal es que el DTO solo contenga la contraseña en texto plano para que el servicio la encripte.
        // Dado que el controlador la está encriptando, aquí no deberíamos volver a encriptarla.
        // PERO, para evitar que MiembroService reciba una contraseña ya encriptada del DTO que luego
        // se intenta encriptar de nuevo, y si el DTO tiene un campo password, entonces es mejor que
        // la encriptación se haga justo antes de 'save' y solo si ha cambiado.
        // Mejor simplificamos: el DTO envía la contraseña en texto plano, y el Service la encripta.
        // Por eso la hemos movido de MiembroController a MiembroService para 'registrarNuevoMiembro'.
        // Para 'actualizarMiembro', si la contraseña se envía en el DTO, debería venir en texto plano.
        // Pero tu MiembroController ya la está encriptando. Lo dejaré en el Service de nuevo.

        if (miembro.getContrasena() != null && !miembro.getContrasena().isEmpty()) {
            // Asume que si llega aquí, ya está encriptada si viene del controlador,
            // o que la encriptas aquí si el controlador no lo hace.
            // Dada tu implementación actual, el controlador la encripta.
            // Para simplificar, si se envía una contraseña en el DTO para actualizar,
            // el DTO debe tener la contraseña en texto plano y se encripta aquí.
            // Si el DTO no tiene contrasena, no la actualizamos.
            existente.setContrasena(passwordEncoder.encode(miembro.getContrasena())); // Vuelve a encriptar si se proporciona
        }
        existente.setActivo(miembro.getActivo()); // Asume que este valor se puede actualizar

        return miembroRepository.save(existente);
    }


    @Transactional(readOnly = true)
    public Optional<Miembro> obtenerMiembroPorId(Long id) {
        return miembroRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Miembro> obtenerTodosLosMiembros() {
        return miembroRepository.findAll();
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
        return miembroRepository.findByCorreo(correo);
    }

    @Transactional(readOnly = true)
    public Optional<Miembro> obtenerMiembroPorNumeroIdentificacion(String numeroIdentificacion) {
        return miembroRepository.findByNumeroIdentificacion(numeroIdentificacion);
    }
}