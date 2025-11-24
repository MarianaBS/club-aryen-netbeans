/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.club.aryen.controller;

import com.club.aryen.model.Socio;
import com.club.aryen.repository.ActividadRepository;
import com.club.aryen.repository.SocioRepository;
import com.club.aryen.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Marian
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ActividadRepository actividadRepo;
    private final UsuarioRepository usuarioRepo;
    private final SocioRepository socioRepo;

    public AdminController(UsuarioRepository usuarioRepo, SocioRepository socioRepo, ActividadRepository actividadRepo) {
        this.usuarioRepo = usuarioRepo;
        this.socioRepo = socioRepo;
        this.actividadRepo = actividadRepo;
    }

    @GetMapping("/menu")
    public String menuAdmin(Model model, Authentication auth) {
        model.addAttribute("usuario", auth.getName());
        return "admin/menu"; // templates/admin/menu.html
    }

    /*@GetMapping("/actividades/listar")
    public String listarActividades(Model model) {
        model.addAttribute("actividades", actividadRepo.findAll());
        return "admin/listaactividades"; // listado con botones
    }
// === SOCIOS ===

    @GetMapping("/socios/listar")
    public String listarSocios(Model model) {
        model.addAttribute("socios", socioRepo.findAll());
        return "admin/listasocios";
    }
     @GetMapping("/socios/nuevo")
    public String nuevoSocioForm(Model model) {
        model.addAttribute("socio", new Socio());
        return "admin/socios"; // tu formulario de alta
    }

    @PostMapping("/socios/guardar")
    public String guardarSocio(@ModelAttribute Socio socio) {
        socioRepo.save(socio);
        return "redirect:/admin/socios/listar";
    }

    /*@GetMapping("/listar")
    public String listarSocios(Model model) {
        model.addAttribute("socios", socioRepo.findAll());
        return "listasocios"; // tu listado con botones editar/eliminar
    }

    @GetMapping("/editar/{id}")
    public String editarSocio(@PathVariable Long id, Model model) {
        model.addAttribute("socio", socioRepo.findById(id).orElse(null));
        return "socios"; // reutiliza el mismo form para editar
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarSocio(@PathVariable Long id) {
        socioRepo.deleteById(id);
        return "redirect:/socios/listar";
    }*/
}
