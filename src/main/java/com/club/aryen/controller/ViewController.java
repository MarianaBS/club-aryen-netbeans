package com.club.aryen.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Redirige la raíz según el rol del usuario logueado
    @GetMapping("/")
    public String home(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return "redirect:/login";
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return esAdmin ? "redirect:/admin/menu" : "redirect:/socio/menu";
    }
}
