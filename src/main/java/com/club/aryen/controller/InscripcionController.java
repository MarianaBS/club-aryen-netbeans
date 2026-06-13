package com.club.aryen.controller;

import com.club.aryen.model.*;
import com.club.aryen.repository.*;
import com.club.aryen.service.InscripcionService;
import com.club.aryen.service.InscripcionService.InscripcionException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class InscripcionController {

    private final InscripcionService inscripcionService;
    private final InscripcionRepository inscRepo;
    private final UsuarioRepository usuarioRepo;
    private final SocioRepository socioRepo;
    private final ActividadRepository actividadRepo;

    public InscripcionController(InscripcionService inscripcionService,
                                 InscripcionRepository inscRepo,
                                 UsuarioRepository usuarioRepo,
                                 SocioRepository socioRepo,
                                 ActividadRepository actividadRepo) {
        this.inscripcionService = inscripcionService;
        this.inscRepo = inscRepo;
        this.usuarioRepo = usuarioRepo;
        this.socioRepo = socioRepo;
        this.actividadRepo = actividadRepo;
    }

    /* ── SOCIO ─────────────────────────────────────────────── */

    @GetMapping("/socio/inscripciones/inscribirse/{actividadId}")
    public String inscribirseDesdeActividad(@PathVariable Long actividadId, Model model, Authentication auth) {
        Usuario u = usuarioRepo.findByUsername(auth.getName()).orElseThrow();
        Actividad actividad = actividadRepo.findById(actividadId).orElseThrow();

        Inscripcion i = new Inscripcion();
        i.setActividad(actividad);
        i.setSocio(u.getSocio());

        // Pasar info de cupo para mostrar en la confirmación
        long inscriptos = inscRepo.countByActividad(actividad);
        model.addAttribute("inscripcion", i);
        model.addAttribute("inscriptos", inscriptos);
        return "socio/inscripcionform";
    }

    @PostMapping("/inscripciones/guardar")
    public String guardarInscripcion(@RequestParam Long socioId,
                                     @RequestParam Long actividadId,
                                     Model model,
                                     Authentication auth,
                                     RedirectAttributes ra) {
        try {
            inscripcionService.inscribir(socioId, actividadId);
            ra.addFlashAttribute("exito", "Inscripción realizada correctamente.");
            return isAdmin(auth)
                    ? "redirect:/admin/inscripciones/listar"
                    : "redirect:/socio/inscripciones/listar";

        } catch (InscripcionException ex) {
            Actividad actividad = actividadRepo.findById(actividadId).orElseThrow();
            Socio socio = socioRepo.findById(socioId).orElseThrow();

            Inscripcion i = new Inscripcion();
            i.setActividad(actividad);
            i.setSocio(socio);

            model.addAttribute("inscripcion", i);
            model.addAttribute("error", ex.getMessage());
            return isAdmin(auth) ? "admin/inscripciones" : "socio/inscripcionform";
        }
    }

    // ← SEGURIDAD: el socio solo puede ver SUS inscripciones
    @GetMapping("/socio/inscripciones/listar")
    public String listarInscripcionesSocio(Model model, Authentication auth) {
        Usuario u = usuarioRepo.findByUsername(auth.getName()).orElseThrow();
        if (u.getSocio() == null) {
            model.addAttribute("inscripciones", List.of());
            return "socio/listainscripciones";
        }
        List<Inscripcion> insc = inscRepo.findBySocioOrderByActividadNombreAsc(u.getSocio());
        model.addAttribute("inscripciones", insc);
        return "socio/listainscripciones";
    }

    // ← SEGURIDAD: verifica que la inscripción le pertenezca al socio
    @GetMapping({"/socio/inscripciones/eliminar/{id}", "/admin/inscripciones/eliminar/{id}"})
    public String eliminarInscripcion(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        Inscripcion i = inscRepo.findById(id).orElseThrow();
        if (!isAdmin(auth)) {
            Usuario u = usuarioRepo.findByUsername(auth.getName()).orElseThrow();
            if (u.getSocio() == null || !i.getSocio().getId().equals(u.getSocio().getId())) {
                throw new RuntimeException("No autorizado");
            }
        }
        inscripcionService.eliminar(id);
        ra.addFlashAttribute("exito", "Inscripción eliminada correctamente.");
        return isAdmin(auth)
                ? "redirect:/admin/inscripciones/listar"
                : "redirect:/socio/inscripciones/listar";
    }

    /* ── ADMIN ─────────────────────────────────────────────── */

    @GetMapping("/admin/inscripciones/nuevo")
    public String altaAdmin(Model model) {
        model.addAttribute("inscripcion", new Inscripcion());
        model.addAttribute("socios", socioRepo.findByActivo(true));
        model.addAttribute("actividades", actividadRepo.findByActivo(true));
        return "admin/inscripciones";
    }

    @GetMapping("/admin/inscripciones/listar")
    public String listarAdmin(Model model) {
        model.addAttribute("inscripciones", inscRepo.findAllByOrderBySocioApellidoAscSocioNombreAsc());
        return "admin/listainscripciones";
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
