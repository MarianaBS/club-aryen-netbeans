package com.club.aryen.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "inscripcion")
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "socio_id")
    private Socio socio;
    @ManyToOne
    @JoinColumn(name = "actividad_id")
    private Actividad actividad;
    private LocalDate fecha = LocalDate.now();

    public Long getId() {
        return id;
    }

    public Socio getSocio() {
        return socio;
    }

    public void setSocio(Socio s) {
        this.socio = s;
    }

    public Actividad getActividad() {
        return actividad;
    }

    public void setActividad(Actividad a) {
        this.actividad = a;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate f) {
        this.fecha = f;
    }
}
