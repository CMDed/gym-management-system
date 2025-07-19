package com.gimnasio.systemgym.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class UsuarioDTO {

    private Long id;

    @NotBlank(message = "El nombre de usuario (username) no puede estar en blanco")
    @Size(min = 4, max = 50, message = "El nombre de usuario debe tener entre 4 y 50 caracteres")
    private String username;

    @NotBlank(message = "La contraseña no puede estar en blanco")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "El rol no puede estar en blanco")
    @Size(max = 50, message = "El rol no puede exceder los 50 caracteres")
    private String rol;

    @NotBlank(message = "El nombre no puede estar en blanco")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido no puede estar en blanco")
    @Size(max = 100, message = "El apellido no puede exceder los 100 caracteres")
    private String apellido;

    @NotBlank(message = "El email no puede estar en blanco")
    @Email(message = "El formato del email es inválido")
    @Size(max = 150, message = "El email no puede exceder los 150 caracteres")
    private String email;

    @NotNull(message = "El estado activo no puede ser nulo")
    private Boolean activo;

    @Size(max = 10, message = "El DNI no puede exceder los 10 caracteres")
    private String dni;

    @Size(max = 20, message = "El teléfono no puede exceder los 20 caracteres")
    private String telefono;

    private LocalDate fechaContratacion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDate getFechaContratacion() {
        return fechaContratacion;
    }

    public void setFechaContratacion(LocalDate fechaContratacion) {
        this.fechaContratacion = fechaContratacion;
    }
}