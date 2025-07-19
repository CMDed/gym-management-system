package com.gimnasio.systemgym.dto;

import com.gimnasio.systemgym.model.Miembro.Sexo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MiembroDTO {

    private Long id;

    @NotBlank(message = "El tipo de identificación no puede estar en blanco")
    @Size(max = 50, message = "El tipo de identificación no puede exceder los 50 caracteres")
    private String tipoIdentificacion;

    @NotBlank(message = "El número de identificación no puede estar en blanco")
    @Pattern(regexp = "^[0-9]+$", message = "El número de identificación debe contener solo números.")
    @Size(min = 6, max = 20, message = "El número de identificación debe tener entre 6 y 20 caracteres.")
    private String numeroIdentificacion;

    @NotBlank(message = "El nombre no puede estar en blanco")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido no puede estar en blanco")
    @Size(max = 100, message = "El apellido no puede exceder los 100 caracteres")
    private String apellido;

    @NotBlank(message = "El correo electrónico no puede estar en blanco")
    @Email(message = "El formato del correo electrónico es inválido")
    @Size(max = 150, message = "El correo electrónico no puede exceder los 150 caracteres")
    private String correo;

    @Pattern(regexp = "^[0-9]{9}$", message = "El teléfono debe ser un número de 9 dígitos.")
    @Size(max = 20, message = "El teléfono no puede exceder los 20 caracteres")
    private String telefono;

    @NotNull(message = "El sexo no puede ser nulo")
    private Sexo sexo;

    @NotNull(message = "La fecha de nacimiento no puede ser nula")
    @PastOrPresent(message = "La fecha de nacimiento no puede ser en el futuro")
    private LocalDate fechaNacimiento;

    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contrasena;

    @NotNull(message = "El estado activo no puede ser nulo")
    private Boolean activo;

    private Long membresiaId;

    @Pattern(regexp = "^[0-9]{16}$", message = "El número de tarjeta debe contener 16 dígitos.")
    private String numeroTarjeta;

    @Pattern(regexp = "^(0[1-9]|1[0-2])\\/[0-9]{2}$", message = "La fecha de vencimiento debe estar en formato MM/AA.")
    private String fechaVencimiento;

    @Pattern(regexp = "^[0-9]{3,4}$", message = "El CVV debe contener 3 o 4 dígitos.")
    private String cvv;
}