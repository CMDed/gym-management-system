package com.gimnasio.systemgym.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_miembro", nullable = false)
    private Miembro miembro;

    // Un pago puede estar asociado a una inscripción específica
    @ManyToOne
    @JoinColumn(name = "id_inscripcion_membresia", nullable = false)
    private InscripcionMembresia inscripcionMembresia;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    @Column(name = "metodo_pago", nullable = false, length = 50)
    private String metodoPago; // Ej. "Tarjeta", "Efectivo", "Transferencia"

    @Column(name = "referencia_transaccion", length = 100)
    private String referenciaTransaccion; // Puede ser null

    @Column(name = "estado", nullable = false, length = 50)
    private String estado; // Ej. "COMPLETADO", "FALLIDO", "REEMBOLSADO"

}