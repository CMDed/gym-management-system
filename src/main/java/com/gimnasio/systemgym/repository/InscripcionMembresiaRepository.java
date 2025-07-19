package com.gimnasio.systemgym.repository;

import com.gimnasio.systemgym.model.InscripcionMembresia;
import com.gimnasio.systemgym.model.Miembro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InscripcionMembresiaRepository extends JpaRepository<InscripcionMembresia, Long> {
    List<InscripcionMembresia> findByMiembro(Miembro miembro);
    List<InscripcionMembresia> findByEstado(String estado);

    Optional<InscripcionMembresia> findTopByMiembroAndEstadoOrderByFechaFinDesc(Miembro miembro, String estado);

    Optional<InscripcionMembresia> findTopByMiembroAndEstadoOrderByFechaCreacionDesc(Miembro miembro, String estado);

    List<InscripcionMembresia> findByMiembroOrderByFechaCreacionDesc(Miembro miembro);

    List<InscripcionMembresia> findByEstadoAndFechaFinBefore(String estado, LocalDate fecha);

    List<InscripcionMembresia> findByMiembroAndEstadoIn(Miembro miembro, List<String> estados);

    List<InscripcionMembresia> findByEstadoAndFechaFinLessThanEqual(String estado, LocalDate fechaFin);
}