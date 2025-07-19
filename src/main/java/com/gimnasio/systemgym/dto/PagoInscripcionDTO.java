package com.gimnasio.systemgym.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoInscripcionDTO {

    @NotNull(message = "El ID del miembro no puede ser nulo")
    private Long miembroId;

    @NotNull(message = "El ID de la membresía no puede ser nulo")
    private Long membresiaId;

    @NotBlank(message = "El número de tarjeta no puede estar en blanco")
    @Pattern(regexp = "^[0-9]{16}$", message = "El número de tarjeta debe contener 16 dígitos.")
    private String numeroTarjeta;

    @NotBlank(message = "La fecha de vencimiento no puede estar en blanco")
    @Pattern(regexp = "^(0[1-9]|1[0-2])\\/[0-9]{2}$", message = "La fecha de vencimiento debe estar en formato MM/AA.")
    private String fechaVencimiento;

    @NotBlank(message = "El CVV no puede estar en blanco")
    @Pattern(regexp = "^[0-9]{3,4}$", message = "El CVV debe contener 3 o 4 dígitos.")
    private String cvv;

}