package com.club.aryen.controller;

import com.club.aryen.model.Actividad;
import com.club.aryen.service.ActividadService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ActividadController {

    private final ActividadService actividadService;

    public ActividadController(ActividadService actividadService) {
        this.actividadService = actividadService;
    }

    @GetMapping("admin/actividades/nuevo")
    public String nuevaActividad(Model model) {
        model.addAttribute("actividad", new Actividad());
        return "admin/actividades";
    }

    @PostMapping("admin/actividades/guardar")
    public String guardarActividad(@ModelAttribute Actividad actividad, RedirectAttributes ra) {
        boolean esNueva = (actividad.getId() == null);
        actividadService.save(actividad);
        ra.addFlashAttribute("exito", esNueva
                ? "Actividad creada correctamente."
                : "Actividad actualizada correctamente.");
        return "redirect:/admin/actividades/listar";
    }

    @GetMapping("admin/actividades/listar")
    public String listarActividades(Model model) {
        model.addAttribute("actividades", actividadService.findAll());
        return "admin/listaactividades";
    }

    @GetMapping("admin/actividades/editar/{id}")
    public String editarActividad(@PathVariable Long id, Model model) {
        model.addAttribute("actividad", actividadService.findById(id).orElseThrow());
        return "admin/actividades";
    }

    @GetMapping("admin/actividades/eliminar/{id}")
    public String eliminarActividad(@PathVariable Long id, RedirectAttributes ra) {
        actividadService.softDelete(id);
        ra.addFlashAttribute("exito", "Actividad dada de baja correctamente.");
        return "redirect:/admin/actividades/listar";
    }

    @GetMapping("socio/actividades/listar")
    public String listarActividadesSocio(Model model) {
        model.addAttribute("actividades", actividadService.findAll());
        return "socio/listaactividades";
    }
}
