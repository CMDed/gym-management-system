package com.gimnasio.systemgym.repository;

import com.gimnasio.systemgym.model.Miembro; // Importa la clase Miembro
import org.springframework.data.jpa.repository.JpaRepository; // Importa JpaRepository
import org.springframework.stereotype.Repository; // Importa @Repository

import java.util.Optional; // Importa Optional

@Repository // Opcional, pero buena práctica para indicar que es un componente de persistencia
public interface MiembroRepository extends JpaRepository<Miembro, Long> {

    Optional<Miembro> findByDni(String dni); // Método para buscar por DNI
    Optional<Miembro> findByEmail(String email); // Método para buscar por Email
}