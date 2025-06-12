package com.gimnasio.systemgym.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal; // Para manejar precios con precisión

@Entity
@Table(name = "membresias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Membresia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_plan", unique = true, nullable = false, length = 100)
    private String nombrePlan; // Ej. "Mensual", "Anual Premium"

    @Column(name = "descripcion", columnDefinition = "TEXT") // TEXT para descripciones largas
    private String descripcion;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2) // Precision 10, 2 decimales
    private BigDecimal precio; // Usamos BigDecimal para dinero, evita problemas de punto flotante

    @Column(name = "duracion_dias", nullable = false)
    private Integer duracionDias; // Ej. 30, 90, 365

    @Column(name = "activo", nullable = false)
    private Boolean activo; // Si la membresía está disponible para compra

}