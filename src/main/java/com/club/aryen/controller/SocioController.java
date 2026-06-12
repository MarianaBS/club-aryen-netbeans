package com.club.aryen.controller;

import com.club.aryen.model.Socio;
import com.club.aryen.repository.ActividadRepository;
import com.club.aryen.service.SocioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SocioController {

    private final SocioService socioService;
    private final ActividadRepository actividadRepo;

    public SocioController(SocioService socioService, ActividadRepository actividadRepo) {
        this.socioService = socioService;
        this.actividadRepo = actividadRepo;
    }

    @GetMapping("/socio/menu")
    public String menuSocio() {
        return "socio/menu_socio";
    }

    @GetMapping("/admin/socios/listar")
    public String listarSocios(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("socios", socioService.buscar(q));
        model.addAttribute("q", q != null ? q : "");
        return "admin/listasocios";
    }

    @GetMapping("/admin/socios/nuevo")
    public String nuevoSocio(Model model) {
        model.addAttribute("socio", new Socio());
        return "admin/socios";
    }

    @PostMapping("/admin/socios/guardar")
    public String guardarSocio(@ModelAttribute Socio socio, RedirectAttributes ra) {
        boolean esNuevo = (socio.getId() == null);
        socioService.save(socio);
        ra.addFlashAttribute("exito", esNuevo
                ? "Socio creado correctamente."
                : "Socio actualizado correctamente.");
        return "redirect:/admin/socios/listar";
    }

    @GetMapping("/admin/socios/editar/{id}")
    public String editarSocio(@PathVariable Long id, Model model) {
        model.addAttribute("socio", socioService.findById(id).orElseThrow());
        return "admin/socios";
    }

    @GetMapping("/admin/socios/eliminar/{id}")
    public String eliminarSocio(@PathVariable Long id, RedirectAttributes ra) {
        socioService.softDelete(id);
        ra.addFlashAttribute("exito", "Socio dado de baja correctamente.");
        return "redirect:/admin/socios/listar";
    }
}
