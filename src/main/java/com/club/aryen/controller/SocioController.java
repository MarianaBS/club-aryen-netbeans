package com.club.aryen.controller;

import com.club.aryen.model.Socio;
import com.club.aryen.repository.ActividadRepository;
import com.club.aryen.repository.UsuarioRepository;
import com.club.aryen.service.SocioService;
import com.club.aryen.service.SocioService.SocioException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SocioController {

    private final SocioService socioService;
    private final ActividadRepository actividadRepo;
    private final UsuarioRepository usuarioRepo;

    public SocioController(SocioService socioService,
                           ActividadRepository actividadRepo,
                           UsuarioRepository usuarioRepo) {
        this.socioService = socioService;
        this.actividadRepo = actividadRepo;
        this.usuarioRepo = usuarioRepo;
    }

    @GetMapping("/socio/menu")
    public String menuSocio(Model model, Authentication auth) {
        usuarioRepo.findByUsername(auth.getName()).ifPresent(u -> {
            String nombre = (u.getSocio() != null)
                    ? u.getSocio().getNombre() + " " + u.getSocio().getApellido()
                    : auth.getName();
            model.addAttribute("nombreCompleto", nombre);
        });
        if (!model.containsAttribute("nombreCompleto")) {
            model.addAttribute("nombreCompleto", auth.getName());
        }
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
    public String guardarSocio(@ModelAttribute Socio socio, Model model, RedirectAttributes ra) {
        try {
            boolean esNuevo = (socio.getId() == null);
            socioService.save(socio);
            ra.addFlashAttribute("exito", esNuevo
                    ? "Socio creado correctamente."
                    : "Socio actualizado correctamente.");
            return "redirect:/admin/socios/listar";
        } catch (SocioException ex) {
            model.addAttribute("socio", socio);
            model.addAttribute("error", ex.getMessage());
            return "admin/socios";
        }
    }

    @GetMapping("/admin/socios/editar/{id}")
    public String editarSocio(@PathVariable Long id, Model model) {
        model.addAttribute("socio", socioService.findById(id).orElseThrow());
        return "admin/socios";
    }

    // Paso 1: mostrar pantalla de confirmación con inscripciones a eliminar
    @GetMapping("/admin/socios/confirmar-baja/{id}")
    public String confirmarBaja(@PathVariable Long id, Model model) {
        Socio socio = socioService.findById(id).orElseThrow();
        var inscripciones = socioService.getInscripciones(id);
        model.addAttribute("socio", socio);
        model.addAttribute("inscripciones", inscripciones);
        return "admin/confirmar-baja-socio";
    }

    // Paso 2: ejecutar la baja
    @PostMapping("/admin/socios/eliminar/{id}")
    public String eliminarSocio(@PathVariable Long id, RedirectAttributes ra) {
        Socio socio = socioService.findById(id).orElseThrow();
        String nombre = socio.getApellido() + ", " + socio.getNombre();
        socioService.softDelete(id);
        ra.addFlashAttribute("exito", "Socio " + nombre + " dado de baja correctamente.");
        return "redirect:/admin/socios/listar";
    }

    // Reactivar socio
    @GetMapping("/admin/socios/reactivar/{id}")
    public String reactivarSocio(@PathVariable Long id, RedirectAttributes ra) {
        Socio socio = socioService.findById(id).orElseThrow();
        socioService.reactivar(id);
        ra.addFlashAttribute("exito", "Socio " + socio.getApellido() + ", " + socio.getNombre() + " reactivado correctamente.");
        return "redirect:/admin/socios/listar";
    }
}
