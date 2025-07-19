package com.gimnasio.systemgym.repository;

import com.gimnasio.systemgym.model.Miembro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface MiembroRepository extends JpaRepository<Miembro, Long> {
    Optional<Miembro> findByCorreo(String correo);

    Optional<Miembro> findByNumeroIdentificacion(String numeroIdentificacion);

    List<Miembro> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrNumeroIdentificacionContainingIgnoreCase(String nombre, String apellido, String numeroIdentificacion);

    @Query("SELECT m FROM Miembro m " +
            "LEFT JOIN FETCH m.inscripcionesMembresia im " +
            "LEFT JOIN FETCH im.membresia")
    List<Miembro> findAllWithInscriptionsAndMembershipDetails();

    @Query("SELECT m FROM Miembro m " +
            "LEFT JOIN FETCH m.inscripcionesMembresia im " +
            "LEFT JOIN FETCH im.membresia " +
            "WHERE LOWER(m.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "      LOWER(m.apellido) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "      LOWER(m.numeroIdentificacion) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Miembro> findBySearchQueryWithInscriptionsAndMembershipDetails(@Param("query") String query);

    @Query("SELECT m FROM Miembro m LEFT JOIN FETCH m.inscripcionesMembresia im LEFT JOIN FETCH im.membresia WHERE m.correo = :correo")
    Optional<Miembro> findByCorreoWithInscriptionsAndMembershipDetails(@Param("correo") String correo);

    @Query("SELECT m FROM Miembro m LEFT JOIN FETCH m.inscripcionesMembresia im LEFT JOIN FETCH im.membresia WHERE m.numeroIdentificacion = :numeroIdentificacion")
    Optional<Miembro> findByNumeroIdentificacionWithInscriptionsAndMembershipDetails(@Param("numeroIdentificacion") String numeroIdentificacion);
}