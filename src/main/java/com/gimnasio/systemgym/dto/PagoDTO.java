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

    // Renombramos a inscripcionMembresiaId para claridad y consistencia con la entidad Pago
    @NotNull(message = "El ID de la inscripción no puede ser nulo.")
    private Long inscripcionMembresiaId; // ¡Importante: en el JS lo llamas 'membresiaId' pero debe ser 'inscripcionMembresiaId'!

    @NotNull(message = "El monto no puede ser nulo.")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor que cero.")
    private BigDecimal monto;

    @NotBlank(message = "El método de pago no puede estar en blanco.")
    private String metodoPago;

    private String referenciaTransaccion; // Opcional, puede ser nulo

    // El estado inicial de un pago recién registrado suele ser "PENDIENTE" o "COMPLETADO"
    // Dependiendo de tu flujo, podrías establecerlo en el servicio o desde el DTO.
    // Para simplificar, lo recibiremos del DTO por ahora.
    @NotBlank(message = "El estado del pago no puede estar en blanco.")
    private String estado; // Ej. "COMPLETADO", "PENDIENTE", "FALLIDO"

    // Puedes añadir campos para los datos de la tarjeta si quisieras procesarlos de alguna forma,
    // pero recuerda NUNCA guardarlos directamente en tu base de datos de forma no encriptada.
    // private String numeroTarjeta;
    // private String fechaVencimiento;
    // private String cvv;
}