package com.gimnasio.systemgym.repository;

import com.gimnasio.systemgym.model.Membresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembresiaRepository extends JpaRepository<Membresia, Long> {
    // Métodos CRUD básicos se generan automáticamente
}