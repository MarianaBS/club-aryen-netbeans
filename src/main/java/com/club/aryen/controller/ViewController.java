package com.club.aryen.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@Controller
public class ViewController {

    //@GetMapping("/")
    //public String home() {
     //   return "redirect:/menu";
   // }

    @GetMapping("/login")
    public String login() {
        return "login"; // tu página login.html
    }

   // @GetMapping("/menu")
    //public String menu(Model model, Authentication authentication) {
     //   // authentication.getName() devuelve el username del usuario logueado
     //   model.addAttribute("username", authentication.getName());
     //   return "menu";
    //}

}
