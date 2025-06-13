package com.gimnasio.systemgym.repository;

import com.gimnasio.systemgym.model.InscripcionMembresia;
import com.gimnasio.systemgym.model.Miembro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; // Importar Optional

@Repository
public interface InscripcionMembresiaRepository extends JpaRepository<InscripcionMembresia, Long> {
    List<InscripcionMembresia> findByMiembro(Miembro miembro);
    List<InscripcionMembresia> findByEstado(String estado);

    // --- ¡NUEVO MÉTODO CRÍTICO! ---
    // Este método buscará la inscripción de un miembro que esté en estado 'ACTIVA'
    // Y la ordenará por fecha de fin descendente, por si hay más de una (aunque idealmente debería haber solo una activa)
    List<InscripcionMembresia> findByMiembroAndEstadoOrderByFechaFinDesc(Miembro miembro, String estado);

    // Si quieres una sola inscripción activa, puedes usar esto:
    Optional<InscripcionMembresia> findTopByMiembroAndEstadoOrderByFechaFinDesc(Miembro miembro, String estado);
}