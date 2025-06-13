package com.gimnasio.systemgym.repository;

import com.gimnasio.systemgym.model.Miembro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MiembroRepository extends JpaRepository<Miembro, Long> {
    Optional<Miembro> findByCorreo(String correo); // Buscar por el nuevo campo 'correo'
    Optional<Miembro> findByNumeroIdentificacion(String numeroIdentificacion); // Buscar por el nuevo campo 'numeroIdentificacion'
    // Puedes mantener findByDni si lo necesitas para algo, pero se recomienda usar numeroIdentificacion
}