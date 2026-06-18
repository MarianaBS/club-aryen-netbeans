package com.club.aryen.controller;

import com.club.aryen.model.Socio;
import com.club.aryen.model.Usuario;
import com.club.aryen.repository.SocioRepository;
import com.club.aryen.repository.UsuarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
@RequestMapping("admin/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuRepo;
    private final SocioRepository socioRepo;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioRepository usuRepo, SocioRepository socioRepo, PasswordEncoder passwordEncoder) {
        this.usuRepo = usuRepo;
        this.socioRepo = socioRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/nuevo")
    public String nuevoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("socios", socioRepo.findByUsuarioIsNull());
        return "admin/usuarios";
    }

    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuarioForm, Model model, RedirectAttributes ra) {
        boolean esNuevo = (usuarioForm.getId() == null);

        // Validación: un usuario con rol SOCIO debe tener un socio vinculado obligatoriamente
        boolean esRolSocio = "SOCIO".equalsIgnoreCase(usuarioForm.getRol());
        boolean tieneSocio = usuarioForm.getSocio() != null && usuarioForm.getSocio().getId() != null;

        if (esRolSocio && !tieneSocio) {
            model.addAttribute("usuario", usuarioForm);
            model.addAttribute("socios", esNuevo ? socioRepo.findByUsuarioIsNull() : socioRepo.findAll());
            model.addAttribute("error", "Un usuario con rol Socio debe tener un socio vinculado. Seleccioná uno de la lista.");
            return "admin/usuarios";
        }

        Usuario usuario;

        if (!esNuevo) {
            usuario = usuRepo.findById(usuarioForm.getId()).orElseThrow();
            usuario.setUsername(usuarioForm.getUsername());
            usuario.setRol(usuarioForm.getRol());
            if (usuarioForm.getPassword() != null && !usuarioForm.getPassword().isBlank()) {
                usuario.setPassword(passwordEncoder.encode(usuarioForm.getPassword()));
            }
        } else {
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

        usuRepo.save(usuario);
        ra.addFlashAttribute("exito", esNuevo
                ? "Usuario creado correctamente."
                : "Usuario actualizado correctamente.");
        return "redirect:/admin/usuarios/listar";
    }

    @GetMapping("/listar")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuRepo.findAll());
        return "admin/listausuarios";
    }

    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuRepo.findById(id).orElseThrow());
        model.addAttribute("socios", socioRepo.findAll());
        return "admin/usuarios";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes ra) {
        Usuario usuario = usuRepo.findById(id).orElseThrow();
        if (usuario.getSocio() != null) {
            Socio socio = usuario.getSocio();
            socio.setUsuario(null);
            socioRepo.save(socio);
        }
        usuRepo.deleteById(id);
        ra.addFlashAttribute("exito", "Usuario eliminado correctamente.");
        return "redirect:/admin/usuarios/listar";
    }
}
