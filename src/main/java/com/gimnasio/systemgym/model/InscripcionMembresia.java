package com.gimnasio.systemgym.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal; // Para el precio pagado

@Entity
@Table(name = "inscripciones_membresia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InscripcionMembresia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con Miembro (Clave Foránea: id_miembro)
    @ManyToOne // Muchas inscripciones para un Miembro
    @JoinColumn(name = "id_miembro", nullable = false) // Columna de la FK en esta tabla
    private Miembro miembro;

    // Relación con Membresia (Clave Foránea: id_membresia)
    @ManyToOne // Muchas inscripciones para un tipo de Membresia
    @JoinColumn(name = "id_membresia", nullable = false) // Columna de la FK en esta tabla
    private Membresia membresia;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "precio_pagado", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioPagado;

    @Column(name = "estado", nullable = false, length = 50)
    private String estado; // Ej. "ACTIVA", "VENCIDA", "CANCELADA", "PENDIENTE"

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

}