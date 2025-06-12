package com.gimnasio.systemgym.repository;

import com.gimnasio.systemgym.model.InscripcionMembresia;
import com.gimnasio.systemgym.model.Miembro; // Importa Miembro
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InscripcionMembresiaRepository extends JpaRepository<InscripcionMembresia, Long> {
    List<InscripcionMembresia> findByMiembro(Miembro miembro); // Buscar inscripciones por miembro
    List<InscripcionMembresia> findByEstado(String estado); // Buscar inscripciones por estado
}