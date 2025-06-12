package com.gimnasio.systemgym.repository;

import com.gimnasio.systemgym.model.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
    // Métodos CRUD básicos se generan automáticamente
}