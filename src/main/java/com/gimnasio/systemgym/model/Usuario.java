package com.gimnasio.systemgym.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password; // Almacenaremos el hash de la contraseña

    @Column(name = "rol", nullable = false, length = 50)
    private String rol; // Ej. "RECEPCIONISTA", "ADMINISTRADOR"

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @Column(name = "email", unique = true, length = 150) // Email puede ser null para algunos usuarios
    private String email;

    @Column(name = "activo", nullable = false)
    private Boolean activo; // Si la cuenta de usuario está activa

}