package com.club.aryen.controller;

import com.club.aryen.model.Actividad;
import com.club.aryen.repository.InscripcionRepository;
import com.club.aryen.service.ActividadService;
import com.club.aryen.service.ActividadService.ActividadException;
import java.util.HashMap;
import java.util.Map;
import com.club.aryen.service.ActividadService.ActividadException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ActividadController {

    private final ActividadService actividadService;
    private final InscripcionRepository inscRepo;

    public ActividadController(ActividadService actividadService, InscripcionRepository inscRepo) {
        this.actividadService = actividadService;
        this.inscRepo = inscRepo;
    }

    @GetMapping("admin/actividades/nuevo")
    public String nuevaActividad(Model model) {
        model.addAttribute("actividad", new Actividad());
        return "admin/actividades";
    }

    @PostMapping("admin/actividades/guardar")
    public String guardarActividad(@ModelAttribute Actividad actividad, Model model, RedirectAttributes ra) {
        try {
            boolean esNueva = (actividad.getId() == null);
            actividadService.save(actividad);
            ra.addFlashAttribute("exito", esNueva
                    ? "Actividad creada correctamente."
                    : "Actividad actualizada correctamente.");
            return "redirect:/admin/actividades/listar";
        } catch (ActividadException ex) {
            model.addAttribute("actividad", actividad);
            model.addAttribute("error", ex.getMessage());
            return "admin/actividades";
        }
    }

    @GetMapping("admin/actividades/listar")
    public String listarActividades(@RequestParam(required = false) String q, Model model) {
        java.util.List<Actividad> actividades = actividadService.buscar(q);
        model.addAttribute("actividades", actividades);
        model.addAttribute("inscriptosMap", inscriptosMap(actividades));
        model.addAttribute("q", q != null ? q : "");
        return "admin/listaactividades";
    }
    private Map<Long, Long> inscriptosMap(java.util.List<Actividad> actividades) {
        Map<Long, Long> map = new HashMap<>();
        for (Actividad a : actividades) {
            map.put(a.getId(), inscRepo.countByActividad(a));
        }
        return map;
    }


    @GetMapping("admin/actividades/editar/{id}")
    public String editarActividad(@PathVariable Long id, Model model) {
        model.addAttribute("actividad", actividadService.findById(id).orElseThrow());
        return "admin/actividades";
    }

    @GetMapping("admin/actividades/eliminar/{id}")
    public String eliminarActividad(@PathVariable Long id, RedirectAttributes ra) {
        try {
            actividadService.softDelete(id);
            ra.addFlashAttribute("exito", "Actividad dada de baja correctamente.");
        } catch (ActividadException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/actividades/listar";
    }

    @GetMapping("admin/actividades/reactivar/{id}")
    public String reactivarActividad(@PathVariable Long id, RedirectAttributes ra) {
        try {
            actividadService.reactivar(id);
            ra.addFlashAttribute("exito", "Actividad reactivada correctamente.");
        } catch (ActividadException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/actividades/listar";
    }

    @GetMapping("socio/actividades/listar")
    public String listarActividadesSocio(@RequestParam(required = false) String q, Model model) {
        java.util.List<Actividad> actividades = actividadService.buscar(q);
        model.addAttribute("actividades", actividades);
        model.addAttribute("inscriptosMap", inscriptosMap(actividades));
        model.addAttribute("q", q != null ? q : "");
        return "socio/listaactividades";
    }
}
