package com.gimnasio.systemgym.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "miembros")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Miembro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cambiado de 'dni' a 'tipo_identificacion'
    @Column(name = "tipo_identificacion", nullable = false, length = 50)
    private String tipoIdentificacion;

    // Cambiado de 'dni' a 'numero_identificacion' y unique = true
    @Column(name = "numero_identificacion", unique = true, nullable = false, length = 50)
    private String numeroIdentificacion;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    // Cambiado de 'email' a 'correo'
    @Column(name = "correo", unique = true, nullable = false, length = 150)
    private String correo;

    @Column(name = "telefono", length = 20) // Quité nullable=false si en el HTML es opcional
    private String telefono;

    // Cambiado de String a Enum Sexo
    @Enumerated(EnumType.STRING) // Almacena como String (MASCULINO, FEMENINO, OTRO)
    @Column(name = "sexo", nullable = false, length = 20)
    private Sexo sexo;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    // Cambiado de 'password' a 'contrasena'
    @Column(name = "contrasena", nullable = false, length = 255) // Length 255 es bueno para contraseñas codificadas
    private String contrasena;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    // *** NUEVO: Definición del Enum Sexo ***
    public enum Sexo {
        MASCULINO,
        FEMENINO,
        OTRO
    }
}