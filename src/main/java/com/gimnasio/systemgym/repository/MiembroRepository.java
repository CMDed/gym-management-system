package com.gimnasio.systemgym.repository;

import com.gimnasio.systemgym.model.Miembro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MiembroRepository extends JpaRepository<Miembro, Long> {
    Optional<Miembro> findByCorreo(String correo);
    Optional<Miembro> findByNumeroIdentificacion(String numeroIdentificacion);
}