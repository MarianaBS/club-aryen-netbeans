/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.club.aryen.controller;



import com.club.aryen.model.*;
import com.club.aryen.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class InscripcionController {

    private final InscripcionRepository inscRepo;
    private final UsuarioRepository usuarioRepo;
    private final SocioRepository socioRepo;
    private final ActividadRepository actividadRepo;

    public InscripcionController(InscripcionRepository inscRepo,
                                 UsuarioRepository usuarioRepo,
                                 SocioRepository socioRepo,
                                 ActividadRepository actividadRepo) {
        this.inscRepo = inscRepo;
        this.usuarioRepo = usuarioRepo;
        this.socioRepo = socioRepo;
        this.actividadRepo = actividadRepo;
    }

    /* -------------------------
       RUTAS SOCIO
       ------------------------- */

    // 1) Inscribirme desde listado de actividades (botón)
    @GetMapping("/socio/inscripciones/inscribirse/{actividadId}")
    public String inscribirseDesdeActividad(@PathVariable Long actividadId, Model model, Authentication auth) {
        Usuario u = usuarioRepo.findByUsername(auth.getName()).orElseThrow();
        Socio socio = u.getSocio();
        Actividad actividad = actividadRepo.findById(actividadId).orElseThrow();

        Inscripcion i = new Inscripcion();
        i.setActividad(actividad);
        i.setSocio(socio);
        // por defecto horario toma el de la actividad si querés:
        i.setHorario(actividad.getHorario());

        model.addAttribute("inscripcion", i);
        model.addAttribute("soloLectura", true); // socio y actividad fijos en la forma
        return "socio/inscripcionform";
    }

    // 2) Alta desde menú socio (elige actividad)
    @GetMapping("/socio/inscripciones/alta")
    public String altaSocio(Model model, Authentication auth) {
        Usuario u = usuarioRepo.findByUsername(auth.getName()).orElseThrow();
        Socio socio = u.getSocio();

        Inscripcion i = new Inscripcion();
        i.setSocio(socio);

        model.addAttribute("inscripcion", i);
        model.addAttribute("actividades", actividadRepo.findAll());
        model.addAttribute("soloLectura", false);
        return "socio/inscripcionform";
    }

    // 3) Guardar inscripción (tanto socio como admin usan esta POST)
    @PostMapping("/inscripciones/guardar")
    public String guardarInscripcion(@ModelAttribute Inscripcion inscripcion, Model model, Authentication auth) {
        // Validaciones
        if (inscripcion.getSocio() == null || inscripcion.getActividad() == null) {
            model.addAttribute("error", "Faltan datos obligatorios.");
            model.addAttribute("socios", socioRepo.findAll());
            model.addAttribute("actividades", actividadRepo.findAll());
            // elegir cuál view dependiendo si ruta invocante era admin o socio:
            // si el socio está logueado y coincide con inscripcion.socio -> view socio
            return "socio/inscripcionform";
        }

        // Evitar duplicados
        if (inscRepo.existsBySocioAndActividad(inscripcion.getSocio(), inscripcion.getActividad())) {
            model.addAttribute("error", "Ya estás inscripto en esa actividad.");
            model.addAttribute("inscripcion", inscripcion);
            model.addAttribute("soloLectura", true);
            return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                    ? "admin/inscripcionform"
                    : "socio/inscripcionform";
        }

        inscRepo.save(inscripcion);

        // Redirigir según rol
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) return "redirect:/admin/inscripciones/listar";
        else return "redirect:/socio/inscripciones/listar";
    }

    // 4) Listar inscripciones del socio logueado
    @GetMapping("/socio/inscripciones/listar")
    public String listarInscripcionesSocio(Model model, Authentication auth) {
        Usuario u = usuarioRepo.findByUsername(auth.getName()).orElseThrow();
        Socio socio = u.getSocio();
        List<Inscripcion> insc = inscRepo.findBySocio(socio);
        model.addAttribute("inscripciones", insc);
        return "socio/listainscripciones";
    }

    // 5) Baja de inscripción por parte del socio (o admin) - botón eliminar
    @GetMapping({"/socio/inscripciones/eliminar/{id}", "/admin/inscripciones/eliminar/{id}"})
    public String eliminarInscripcion(@PathVariable Long id, Authentication auth) {
        Inscripcion i = inscRepo.findById(id).orElseThrow();
        // permiso: socio solo puede borrar la suya
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            Usuario u = usuarioRepo.findByUsername(auth.getName()).orElseThrow();
            if (!i.getSocio().getId().equals(u.getSocio().getId())) {
                throw new RuntimeException("No autorizado");
            }
        }
        inscRepo.delete(i);
        return isAdmin ? "redirect:/admin/inscripciones/listar" : "redirect:/socio/inscripciones/listar";
    }

    /* -------------------------
       RUTAS ADMIN
       ------------------------- */

    // 6) Alta admin (elige socio y actividad)
    @GetMapping("/admin/inscripciones/nuevo")
    public String altaAdmin(Model model) {
        model.addAttribute("inscripcion", new Inscripcion());
        model.addAttribute("socios", socioRepo.findAll());
        model.addAttribute("actividades", actividadRepo.findAll());
        return "admin/inscripciones";
    }

    // 7) Listado admin (general)
    @GetMapping("/admin/inscripciones/listar")
    public String listarAdmin(Model model) {
        model.addAttribute("inscripciones", inscRepo.findAll());
        return "admin/listainscripciones";
    }

    // 8) Listado agrupado por socio
    @GetMapping("/admin/inscripciones/listar/socios")
    public String listarPorSocio(Model model) {
        model.addAttribute("agrupado", "socio");
        model.addAttribute("inscripciones", inscRepo.findAll());
        return "admin/listainscripciones_agrupado";
    }

    // 9) Listado agrupado por actividad
    @GetMapping("/admin/inscripciones/listar/actividades")
    public String listarPorActividad(Model model) {
        model.addAttribute("agrupado", "actividad");
        model.addAttribute("inscripciones", inscRepo.findAll());
        return "admin/listainscripciones_agrupado";
    }
}
