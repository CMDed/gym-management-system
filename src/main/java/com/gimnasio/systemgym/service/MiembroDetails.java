package com.gimnasio.systemgym.service;

import com.gimnasio.systemgym.model.Miembro;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections; // Importar para retornar una lista vacía si no hay autoridades

public class MiembroDetails implements UserDetails {

    // --- ¡CRÍTICO! AÑADE ESTA REFERENCIA AL MIEMBRO COMPLETO ---
    private Miembro miembro;

    // --- PROPIEDADES INDIVIDUALES (ya las tienes) ---
    private String tipoIdentificacion;
    private String numeroIdentificacion;
    private String contrasena;
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private Miembro.Sexo sexo;
    private String telefono;
    private String correo;
    private Boolean activo; // Agregué el estado activo del miembro

    private Collection<? extends GrantedAuthority> authorities; // Puede ser Collections.emptyList() si no usas roles

    // --- CONSTRUCTOR ---
    public MiembroDetails(Miembro miembro) {
        // --- ¡CRÍTICO! INICIALIZA EL OBJETO MIEMBRO AQUÍ ---
        this.miembro = miembro;

        // Inicializa las propiedades individuales desde el objeto Miembro
        this.tipoIdentificacion = miembro.getTipoIdentificacion();
        this.numeroIdentificacion = miembro.getNumeroIdentificacion();
        this.contrasena = miembro.getContrasena();
        this.nombre = miembro.getNombre();
        this.apellido = miembro.getApellido();
        this.fechaNacimiento = miembro.getFechaNacimiento();
        this.sexo = miembro.getSexo();
        this.telefono = miembro.getTelefono();
        this.correo = miembro.getCorreo();
        this.activo = miembro.getActivo(); // Inicializa el estado activo

        // Si tu aplicación no maneja roles/autoridades complejas, puedes inicializarlo así:
        this.authorities = Collections.emptyList();
    }

    // --- ¡CRÍTICO! AÑADE ESTE GETTER PARA EL OBJETO MIEMBRO ---
    public Miembro getMiembro() {
        return miembro;
    }

    // --- Métodos de la interfaz UserDetails ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return contrasena;
    }

    @Override
    public String getUsername() {
        return numeroIdentificacion; // El username para Spring Security es el DNI/Número de Identificación
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.activo; // Utiliza el campo 'activo' del Miembro para determinar si está habilitado
    }

    // --- GETTERS para todos los campos de Miembro que necesites en la vista ---
    public String getTipoIdentificacion() {
        return tipoIdentificacion;
    }

    public String getNumeroIdentificacion() {
        return numeroIdentificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public Miembro.Sexo getSexo() {
        return sexo;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }
}