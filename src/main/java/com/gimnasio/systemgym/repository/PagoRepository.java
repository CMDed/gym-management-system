package com.gimnasio.systemgym.repository;

import com.gimnasio.systemgym.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    // Métodos CRUD básicos se generan automáticamente
}