package com.club.aryen.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/menu")
    public String menuAdmin(Model model, Authentication auth) {
        model.addAttribute("usuario", auth.getName());
        return "admin/menu";
    }
}
