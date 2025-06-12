package com.gimnasio.systemgym.model;

import jakarta.persistence.*; // Importa las anotaciones de JPA
import lombok.Data; // Importa la anotación @Data de Lombok
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate; // Para fecha de nacimiento
import java.time.LocalDateTime; // Para fecha de registro

@Entity // Indica que esta clase es una entidad JPA y se mapea a una tabla de BD
@Table(name = "miembros") // Especifica el nombre de la tabla en la BD
@Data // Anotación de Lombok: Genera getters, setters, toString, equals y hashCode automáticamente
@NoArgsConstructor // Anotación de Lombok: Genera un constructor sin argumentos
@AllArgsConstructor // Anotación de Lombok: Genera un constructor con todos los argumentos
public class Miembro {

    @Id // Marca el campo como la clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Indica que el ID será auto-generado por la BD (IDENTITY para bases de datos como MySQL/PostgreSQL)
    private Long id;

    @Column(name = "dni", unique = true, nullable = false, length = 20) // Mapea a la columna 'dni', es única, no nula y tiene longitud máxima
    private String dni;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @Column(name = "email", unique = true, nullable = false, length = 150)
    private String email;

    @Column(name = "sexo", nullable = false, length = 10)
    private String sexo;

    @Column(name = "telefono", nullable = false, length = 20)
    private String telefono;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento; // Usamos LocalDate para solo la fecha

    @Column(name = "password", nullable = false, length = 255)
    private String password; // Almacenaremos el hash de la contraseña

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro; // Usamos LocalDateTime para fecha y hora

    @Column(name = "activo", nullable = false)
    private Boolean activo; // Para saber si el miembro está activo

    // Nota: Lombok @Data ya genera los getters, setters, constructores, etc.
    // No es necesario escribirlos explícitamente, lo cual es la magia de Lombok.
}