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
import com.club.aryen.model.Usuario;
import com.club.aryen.repository.SocioRepository;
import com.club.aryen.repository.UsuarioRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("admin/usuarios")
public class UsuarioController {

    @Autowired
    private final UsuarioRepository usuRepo;
    private final SocioRepository socioRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioRepository usuRepo, SocioRepository socioRepo, PasswordEncoder passwordEncoder) {
        this.usuRepo = usuRepo;
        this.socioRepo = socioRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/nuevo")
    public String nuevoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        List<Socio> sociosSinUsu = socioRepo.findByUsuarioIsNull();
        model.addAttribute("socios", sociosSinUsu);
        return "admin/usuarios"; // tu formulario de alta
    }

    @PostMapping({"/guardar", "/editar/{id}"})
    // public String guardarUsuario(@ModelAttribute("usuario") Usuario usuario, @RequestParam("socio.id") Long socioId, BindingResult result, Model model) {
    public String guardarUsuario(@ModelAttribute Usuario usuarioForm) {
        Usuario usuario;
        if (usuarioForm.getId() != null) {
            // Editar existente
            usuario = usuRepo.findById(usuarioForm.getId()).orElseThrow();
            usuario.setUsername(usuarioForm.getUsername());
            usuario.setRol(usuarioForm.getRol());
            if (usuario.getPassword() != null && !usuarioForm.getPassword().isBlank()) {
                usuario.setPassword(passwordEncoder.encode(usuarioForm.getPassword()));
            }
            else{
                throw new IllegalArgumentException("El nombre de usuario no puede estar vacío");
            }
        } else {
            // Nuevo usuario
            usuario = new Usuario();
            usuario.setUsername(usuarioForm.getUsername());
            usuario.setPassword(passwordEncoder.encode(usuarioForm.getPassword()));
            usuario.setRol(usuarioForm.getRol());
        }

        if (usuarioForm.getSocio() != null && usuarioForm.getSocio().getId() != null) {
            Socio socio = socioRepo.findById(usuarioForm.getSocio().getId()).orElse(null);
            usuario.setSocio(socio);
        } else {
            usuario.setSocio(null);
        }

        /*usuarioRepo.save(usuario);
        return "redirect:/admin/usuarios/listado";
        // Encriptar antes de guardar
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        Socio socio = socioRepo.findById(socioId).orElseThrow();
        usuRepo.save(usuario);
        socio.setUsuario(usuario);
        socioRepo.save(socio);
        return "redirect:/admin/usuarios/listar";*/
        usuRepo.save(usuario);
        return "redirect:/admin/usuarios/listar";
    }

    @GetMapping("/listar")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuRepo.findAll());
        return "admin/listausuarios"; // tu listado con botones editar/eliminar
    }

    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("ID inválido"));
        model.addAttribute("usuario", usuario);
        model.addAttribute("socios", socioRepo.findAll());
        return "admin/usuarios";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        Usuario usuario = usuRepo.findById(id).orElseThrow();
        if (usuario.getSocio() != null) {
        Socio socio = usuario.getSocio();
        socio.setUsuario(null);
        socioRepo.save(socio);
    }

        usuRepo.deleteById(id);
        return "redirect:/admin/usuarios/listar";
    }

}
