package com.club.aryen.controller;

import com.club.aryen.repository.ActividadRepository;
import com.club.aryen.repository.InscripcionRepository;
import com.club.aryen.repository.SocioRepository;
import com.club.aryen.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final SocioRepository socioRepo;
    private final ActividadRepository actividadRepo;
    private final InscripcionRepository inscripcionRepo;
    private final UsuarioRepository usuarioRepo;

    public AdminController(SocioRepository socioRepo,
                           ActividadRepository actividadRepo,
                           InscripcionRepository inscripcionRepo,
                           UsuarioRepository usuarioRepo) {
        this.socioRepo = socioRepo;
        this.actividadRepo = actividadRepo;
        this.inscripcionRepo = inscripcionRepo;
        this.usuarioRepo = usuarioRepo;
    }

    @GetMapping("/menu")
    public String menuAdmin(Model model, Authentication auth) {
        // Stats dashboard
        model.addAttribute("totalSocios",        socioRepo.findByActivo(true).size());
        model.addAttribute("totalActividades",   actividadRepo.findByActivo(true).size());
        model.addAttribute("totalInscripciones", inscripcionRepo.count());
        model.addAttribute("totalUsuarios",      usuarioRepo.count());

        // Nombre: si tiene socio → nombre + apellido, si no → username
        usuarioRepo.findByUsername(auth.getName()).ifPresent(u -> {
            String nombre = (u.getSocio() != null)
                    ? u.getSocio().getNombre() + " " + u.getSocio().getApellido()
                    : auth.getName();
            model.addAttribute("nombreCompleto", nombre);
        });

        // Fallback por si no encuentra el usuario (no debería pasar)
        if (!model.containsAttribute("nombreCompleto")) {
            model.addAttribute("nombreCompleto", auth.getName());
        }

        return "admin/menu";
    }
}
