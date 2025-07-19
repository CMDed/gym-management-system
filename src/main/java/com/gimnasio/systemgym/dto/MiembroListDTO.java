package com.gimnasio.systemgym.dto;

import com.gimnasio.systemgym.model.Miembro;
import com.gimnasio.systemgym.model.InscripcionMembresia;
import com.gimnasio.systemgym.model.Membresia;

import java.time.LocalDate;

public class MiembroListDTO {
    private Long id;
    private String nombreCompleto;
    private String numeroIdentificacion;
    private String correo;
    private String telefono;
    private Miembro.Sexo sexo;
    private LocalDate fechaNacimiento;
    private Boolean activo;

    private String nombreMembresiaActiva;
    private LocalDate fechaInicioMembresiaActiva;
    private LocalDate fechaFinMembresiaActiva;
    private String estadoMembresiaActiva;

    public MiembroListDTO() {}

    public MiembroListDTO(Miembro miembro, InscripcionMembresia membresiaActiva) {
        this.id = miembro.getId();
        this.nombreCompleto = miembro.getNombre() + " " + miembro.getApellido();
        this.numeroIdentificacion = miembro.getNumeroIdentificacion();
        this.correo = miembro.getCorreo();
        this.telefono = miembro.getTelefono();
        this.sexo = miembro.getSexo();
        this.fechaNacimiento = miembro.getFechaNacimiento();
        this.activo = miembro.getActivo();

        if (membresiaActiva != null) {
            Membresia membresiaAsociada = membresiaActiva.getMembresia();

            if (membresiaAsociada != null) {
                this.nombreMembresiaActiva = membresiaAsociada.getNombrePlan();
            } else {
                this.nombreMembresiaActiva = "Membresía no cargada (objeto null)";
            }
            this.fechaInicioMembresiaActiva = membresiaActiva.getFechaInicio();
            this.fechaFinMembresiaActiva = membresiaActiva.getFechaFin();
            this.estadoMembresiaActiva = membresiaActiva.getEstado() != null ? membresiaActiva.getEstado().toString() : "Desconocido";
        } else {
            this.nombreMembresiaActiva = "N/A";
            this.fechaInicioMembresiaActiva = null;
            this.fechaFinMembresiaActiva = null;
            this.estadoMembresiaActiva = "Inactivo";
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getNumeroIdentificacion() { return numeroIdentificacion; }
    public void setNumeroIdentificacion(String numeroIdentificacion) { this.numeroIdentificacion = numeroIdentificacion; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public Miembro.Sexo getSexo() { return sexo; }
    public void setSexo(Miembro.Sexo sexo) { this.sexo = sexo; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public String getNombreMembresiaActiva() { return nombreMembresiaActiva; }
    public void setNombreMembresiaActiva(String nombreMembresiaActiva) { this.nombreMembresiaActiva = nombreMembresiaActiva; }
    public LocalDate getFechaInicioMembresiaActiva() { return fechaInicioMembresiaActiva; }
    public void setFechaInicioMembresiaActiva(LocalDate fechaInicioMembresiaActiva) { this.fechaInicioMembresiaActiva = fechaInicioMembresiaActiva; }
    public LocalDate getFechaFinMembresiaActiva() { return fechaFinMembresiaActiva; }
    public void setFechaFinMembresiaActiva(LocalDate fechaFinMembresiaActiva) { this.fechaFinMembresiaActiva = fechaFinMembresiaActiva; }
    public String getEstadoMembresiaActiva() { return estadoMembresiaActiva; }
    public void setEstadoMembresiaActiva(String estadoMembresiaActiva) { this.estadoMembresiaActiva = estadoMembresiaActiva; }
}