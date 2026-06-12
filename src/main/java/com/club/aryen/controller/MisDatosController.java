package com.club.aryen.controller;

import com.club.aryen.model.Socio;
import com.club.aryen.model.Usuario;
import com.club.aryen.repository.SocioRepository;
import com.club.aryen.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/socio/mis-datos")
public class MisDatosController {

    private final UsuarioRepository usuarioRepo;
    private final SocioRepository socioRepo;
    private final PasswordEncoder passwordEncoder;

    public MisDatosController(UsuarioRepository usuarioRepo,
                               SocioRepository socioRepo,
                               PasswordEncoder passwordEncoder) {
        this.usuarioRepo = usuarioRepo;
        this.socioRepo = socioRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String verDatos(Model model, Authentication auth) {
        Usuario u = usuarioRepo.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("usuario", u);
        model.addAttribute("socio", u.getSocio());
        return "socio/misdatos";
    }

    @PostMapping("/guardar")
    public String guardarDatos(@RequestParam(required = false) String email,
                               @RequestParam(required = false) String telefono,
                               @RequestParam(required = false) String direccion,
                               @RequestParam(required = false) String passwordActual,
                               @RequestParam(required = false) String passwordNueva,
                               Authentication auth,
                               RedirectAttributes ra) {

        Usuario u = usuarioRepo.findByUsername(auth.getName()).orElseThrow();
        Socio socio = u.getSocio();

        // Actualizar datos del socio si está vinculado
        if (socio != null) {
            if (email != null && !email.isBlank()) socio.setEmail(email);
            if (telefono != null) socio.setTelefono(telefono);
            if (direccion != null) socio.setDireccion(direccion);
            socioRepo.save(socio);
        }

        // Cambiar contraseña solo si ingresó la actual correctamente
        if (passwordNueva != null && !passwordNueva.isBlank()) {
            if (passwordActual == null || !passwordEncoder.matches(passwordActual, u.getPassword())) {
                ra.addFlashAttribute("error", "La contraseña actual es incorrecta.");
                return "redirect:/socio/mis-datos";
            }
            u.setPassword(passwordEncoder.encode(passwordNueva));
            usuarioRepo.save(u);
        }

        ra.addFlashAttribute("exito", "Datos actualizados correctamente.");
        return "redirect:/socio/mis-datos";
    }
}
