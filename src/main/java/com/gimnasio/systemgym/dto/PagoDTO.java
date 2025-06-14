package com.gimnasio.systemgym.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoDTO {

    @NotNull(message = "El ID del miembro no puede ser nulo.")
    private Long miembroId;

    @NotNull(message = "El ID de la inscripción no puede ser nulo.")
    private Long inscripcionMembresiaId;

    @NotNull(message = "El monto no puede ser nulo.")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor que cero.")
    private BigDecimal monto;

    @NotBlank(message = "El método de pago no puede estar en blanco.")
    private String metodoPago;

    private String referenciaTransaccion;

    @NotBlank(message = "El estado del pago no puede estar en blanco.")
    private String estado;

    // private String numeroTarjeta;
    // private String fechaVencimiento;
    // private String cvv;
}