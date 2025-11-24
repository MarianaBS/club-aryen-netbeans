/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.club.aryen.controller;

/**
 *
 * @author Marian
 */
import com.club.aryen.model.Socio;
import com.club.aryen.repository.SocioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.club.aryen.model.Actividad;
import com.club.aryen.repository.ActividadRepository;

@Controller
//@RequestMapping("/socio")
public class SocioController {

    @Autowired
    private SocioRepository socioRepo;
    private final ActividadRepository actividadRepo;

    public SocioController(ActividadRepository actividadRepo) {
        this.actividadRepo = actividadRepo;
    }

    // -------- MENU SOCIO --------
    @GetMapping("/socio/menu")
    public String menuSocio() {
        return "socio/menu_socio"; // tu página de menú para socios
    }

    @GetMapping("/admin/socios/listar")
    public String listarSocios(Model model) {
        model.addAttribute("socios", socioRepo.findAll());
        return "admin/listasocios";
    }

    @GetMapping("/admin/socios/nuevo")
    public String nuevoSocio(Model model) {
        model.addAttribute("socio", new Socio());
        return "admin/socios"; // tu formulario de alta
    }

    @PostMapping("/admin/socios/guardar")
    public String guardarSocio(@ModelAttribute Socio socio) {
        socioRepo.save(socio);
        return "redirect:/admin/socios/listar";
    }

    @GetMapping("/admin/socios/editar/{id}")
    public String editarSocio(@PathVariable Long id, Model model) {
        model.addAttribute("socio", socioRepo.findById(id).orElse(null));
        return "admin/socios"; // reutiliza el mismo form para editar
    }

    @GetMapping("/admin/socios/eliminar/{id}")
    public String eliminarSocio(@PathVariable Long id) {
        socioRepo.deleteById(id);
        return "redirect:/admin/socios/listar";
    }

}
