package com.club.aryen.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "socio")
public class Socio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String nombre;
    @NotBlank
    private String apellido;
    @Email
    @NotBlank
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String dni;
    private LocalDate fechaAlta = LocalDate.now();
    private boolean activo = true;
    @ManyToMany
    @JoinTable(name = "socio_actividad", joinColumns = @JoinColumn(name = "socio_id"), inverseJoinColumns = @JoinColumn(name = "actividad_id"))
    private Set<Actividad> actividades = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = true)
    private Usuario usuario;

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDate f) {
        this.fechaAlta = f;
    }

    public Set<Actividad> getActividades() {
        return actividades;
    }

    public void setActividades(Set<Actividad> a) {
        this.actividades = a;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Socio)) {
            return false;
        }
        Socio socio = (Socio) o;
        return id != null && id.equals(socio.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
