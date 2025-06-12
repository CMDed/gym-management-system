package com.gimnasio.systemgym.repository;

import com.gimnasio.systemgym.model.Pago;
import com.gimnasio.systemgym.model.Miembro; // Importa Miembro
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByMiembro(Miembro miembro); // Buscar pagos por miembro
}