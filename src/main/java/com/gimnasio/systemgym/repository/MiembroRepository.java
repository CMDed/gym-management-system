package com.gimnasio.systemgym.repository;

import com.gimnasio.systemgym.model.Miembro; // Importa la clase Miembro
import org.springframework.data.jpa.repository.JpaRepository; // Importa JpaRepository
import org.springframework.stereotype.Repository; // Importa @Repository

@Repository // Opcional, pero buena práctica para indicar que es un componente de persistencia
public interface MiembroRepository extends JpaRepository<Miembro, Long> {
    // Spring Data JPA generará automáticamente los métodos CRUD (save, findById, findAll, delete, etc.)
    // También puedes añadir métodos personalizados aquí, como:
    // Optional<Miembro> findByDni(String dni);
    // List<Miembro> findByActivoTrue();
}