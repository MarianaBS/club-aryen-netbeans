package com.club.aryen.controller;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    // ID que no existe (ej: /admin/socios/editar/9999)
    @ExceptionHandler(java.util.NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(Exception ex, Model model) {
        model.addAttribute("codigo", "404");
        model.addAttribute("titulo", "No encontrado");
        model.addAttribute("mensaje", "El registro que buscás no existe o fue eliminado.");
        return "error/error";
    }

    // Acceso no autorizado (ej: socio intentando ver inscripción de otro)
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntime(RuntimeException ex, Model model) {
        if ("No autorizado".equals(ex.getMessage())) {
            model.addAttribute("codigo", "403");
            model.addAttribute("titulo", "Acceso denegado");
            model.addAttribute("mensaje", "No tenés permiso para realizar esta acción.");
            return "error/error";
        }
        model.addAttribute("codigo", "500");
        model.addAttribute("titulo", "Error inesperado");
        model.addAttribute("mensaje", "Ocurrió un error. Por favor intentá de nuevo.");
        return "error/error";
    }

    // Cualquier otra excepción no controlada
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneric(Exception ex, Model model) {
        model.addAttribute("codigo", "500");
        model.addAttribute("titulo", "Error inesperado");
        model.addAttribute("mensaje", "Ocurrió un error. Por favor intentá de nuevo.");
        return "error/error";
    }
}
