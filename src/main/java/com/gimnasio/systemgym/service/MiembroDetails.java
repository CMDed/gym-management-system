package com.gimnasio.systemgym.service;

import com.gimnasio.systemgym.model.Miembro;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MiembroDetails implements UserDetails {

    private Miembro miembro;

    private String tipoIdentificacion;
    private String numeroIdentificacion;
    private String contrasena;
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private Miembro.Sexo sexo;
    private String telefono;
    private String correo;
    private Boolean activo;

    private Collection<? extends GrantedAuthority> authorities;

    public MiembroDetails(Miembro miembro) {
        this.miembro = miembro;

        this.tipoIdentificacion = miembro.getTipoIdentificacion();
        this.numeroIdentificacion = miembro.getNumeroIdentificacion();
        this.contrasena = miembro.getContrasena();
        this.nombre = miembro.getNombre();
        this.apellido = miembro.getApellido();
        this.fechaNacimiento = miembro.getFechaNacimiento();
        this.sexo = miembro.getSexo();
        this.telefono = miembro.getTelefono();
        this.correo = miembro.getCorreo();
        this.activo = miembro.getActivo();

        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + miembro.getRol().toUpperCase()));
    }

    public Miembro getMiembro() {
        return miembro;
    }

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
        return numeroIdentificacion;
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
        return this.activo;
    }

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