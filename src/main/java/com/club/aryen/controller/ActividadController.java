/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.club.aryen.controller;

/**
 *
 * @author Marian
 */
import com.club.aryen.model.Actividad;
import com.club.aryen.model.Inscripcion;
import com.club.aryen.model.Socio;
import com.club.aryen.repository.ActividadRepository;
import com.club.aryen.repository.InscripcionRepository;
import com.club.aryen.repository.SocioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
//@RequestMapping("admin/actividades")
public class ActividadController {

    @Autowired
    private ActividadRepository actividadRepo;
    private SocioRepository socioRepo;
    private InscripcionRepository inscripcionRepo;

    @GetMapping("admin/actividades/nuevo")
    public String nuevaActividad(Model model) {
        model.addAttribute("actividad", new Actividad());
        return "admin/actividades"; // formulario de alta
    }

    @PostMapping("admin/actividades/guardar")
    public String guardarActividad(@ModelAttribute Actividad actividad) {
        actividadRepo.save(actividad);
        return "redirect:/admin/actividades/listar";
    }

    @GetMapping("admin/actividades/listar")
    public String listarActividades(Model model) {
        model.addAttribute("actividades", actividadRepo.findAll());
        return "admin/listaactividades"; // listado con botones
    }

    @GetMapping("admin/actividades/editar/{id}")
    public String editarActividad(@PathVariable Long id, Model model) {
        model.addAttribute("actividad", actividadRepo.findById(id).orElse(null));
        return "admin/actividades"; // reutiliza form
    }

    @GetMapping("admin/actividades/eliminar/{id}")
    public String eliminarActividad(@PathVariable Long id) {
        actividadRepo.deleteById(id);
        return "redirect:/admin/actividades/listar";
    }

    @GetMapping("socio/actividades/listar")
    public String listarActividadesSocio(Model model) {
        model.addAttribute("actividades", actividadRepo.findAll());
        return "socio/listaactividades"; // listado con botones
    }
    
    // Botón "Inscribirme" del listado de actividades del socio

    @GetMapping("/socio/actividades/inscribirme/{idActividad}")
    public String inscribirmeActividad(@PathVariable Long idActividad, HttpSession session, Model model) {
        // Recuperar socio logueado (ya deberías tenerlo guardado en sesión al iniciar sesión)
        Long idSocio = (Long) session.getAttribute("idSocio");
        Socio socio = socioRepo.findById(idSocio).orElseThrow();

        Actividad actividad = actividadRepo.findById(idActividad).orElseThrow();

        Inscripcion insc = new Inscripcion();
        insc.setSocio(socio);
        insc.setActividad(actividad);
        insc.setHorario(actividad.getHorario()); // por defecto copia el horario de la actividad

        model.addAttribute("inscripcion", insc);
        model.addAttribute("soloLectura", true); // para bloquear combos
        return "socio/inscripciones/form";
    }

    @PostMapping("/socio/inscripciones/guardar")
    public String guardarInscripcionSocio(@ModelAttribute Inscripcion inscripcion, HttpSession session) {
        Long idSocio = (Long) session.getAttribute("idSocio");
        Socio socio = socioRepo.findById(idSocio).orElseThrow();
        inscripcion.setSocio(socio);
        inscripcionRepo.save(inscripcion);
        return "redirect:/socio/inscripciones";
    }

}
