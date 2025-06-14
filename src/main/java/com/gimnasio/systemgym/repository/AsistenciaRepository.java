package com.gimnasio.systemgym.repository;

import com.gimnasio.systemgym.model.Asistencia;
import com.gimnasio.systemgym.model.Miembro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
    List<Asistencia> findByMiembro(Miembro miembro);
}