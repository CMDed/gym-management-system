package com.gimnasio.systemgym.repository;

import com.gimnasio.systemgym.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Para buscar por username

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Podemos añadir un método personalizado para buscar un usuario por su username
    Optional<Usuario> findByUsername(String username);
}