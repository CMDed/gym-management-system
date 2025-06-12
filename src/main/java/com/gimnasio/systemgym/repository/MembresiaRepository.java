package com.gimnasio.systemgym.repository;

import com.gimnasio.systemgym.model.Membresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface MembresiaRepository extends JpaRepository<Membresia, Long> {
    Optional<Membresia> findByNombrePlan(String nombrePlan);
    List<Membresia> findByActivoTrue(); // Encuentra todas las membresías donde 'activo' es true
}