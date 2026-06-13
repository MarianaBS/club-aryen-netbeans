package com.club.aryen.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalTime;
import java.util.Objects;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "actividad")
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String nombre;

    private boolean activo = true;

    private String dia;

    @Column(name = "horario")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horario;

    // Nuevo campo: horario de fin
    @Column(name = "horario_fin")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horarioFin;

    private String profesor;

    // 0 = sin límite de cupo
    private int cupoMaximo = 0;

    // ── Getters y setters ──────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }


    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getDia() { return dia; }
    public void setDia(String dia) { this.dia = dia; }

    public LocalTime getHorario() { return horario; }
    public void setHorario(LocalTime horario) { this.horario = horario; }

    public LocalTime getHorarioFin() { return horarioFin; }
    public void setHorarioFin(LocalTime horarioFin) { this.horarioFin = horarioFin; }

    public String getProfesor() { return profesor; }
    public void setProfesor(String profesor) { this.profesor = profesor; }

    public int getCupoMaximo() { return cupoMaximo; }
    public void setCupoMaximo(int cupoMaximo) { this.cupoMaximo = cupoMaximo; }

    // ── Helper: ¿se superpone con otra actividad? ──────────
    // Dos actividades se superponen si son el mismo día y sus rangos se pisan.
    // Condición de superposición: inicio1 < fin2  &&  fin1 > inicio2
    public boolean seSuperponeCon(Actividad otra) {
        if (!this.dia.equalsIgnoreCase(otra.dia)) return false;
        if (this.horario == null || this.horarioFin == null
                || otra.horario == null || otra.horarioFin == null) return false;

        return this.horario.isBefore(otra.horarioFin)
                && this.horarioFin.isAfter(otra.horario);
    }
}
