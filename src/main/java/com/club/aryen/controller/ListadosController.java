package com.club.aryen.controller;

import com.club.aryen.model.Actividad;
import com.club.aryen.model.Inscripcion;
import com.club.aryen.model.Socio;
import com.club.aryen.repository.ActividadRepository;
import com.club.aryen.repository.InscripcionRepository;
import com.club.aryen.repository.SocioRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/listados")
public class ListadosController {

    private final InscripcionRepository inscRepo;
    private final SocioRepository socioRepo;
    private final ActividadRepository actividadRepo;

    public ListadosController(InscripcionRepository inscRepo,
                               SocioRepository socioRepo,
                               ActividadRepository actividadRepo) {
        this.inscRepo = inscRepo;
        this.socioRepo = socioRepo;
        this.actividadRepo = actividadRepo;
    }

    @GetMapping
    public String menu() {
        return "admin/listados";
    }

    // ── Por socio ─────────────────────────────────────────────
    // Devuelve mapa: Socio → List<Inscripcion>, ordenado por apellido
    @GetMapping("/por-socio")
    public String porSocio(Model model) {
        List<Socio> socios = socioRepo.findAll(Sort.by("apellido").ascending().and(Sort.by("nombre").ascending()));

        // LinkedHashMap para mantener el orden alfabético
        Map<Socio, List<Inscripcion>> mapa = new LinkedHashMap<>();
        for (Socio s : socios) {
            List<Inscripcion> inscripciones = inscRepo.findBySocio(s);
            mapa.put(s, inscripciones);
        }

        model.addAttribute("mapa", mapa);
        model.addAttribute("totalSocios", socios.size());
        model.addAttribute("totalInscripciones", inscRepo.count());
        return "admin/listado-por-socio";
    }

    // ── Por actividad ─────────────────────────────────────────
    // Devuelve mapa: Actividad → List<Inscripcion>, ordenado por nombre de actividad
    @GetMapping("/por-actividad")
    public String porActividad(Model model) {
        List<Actividad> actividades = actividadRepo.findAll(Sort.by("nombre").ascending());

        Map<Actividad, List<Inscripcion>> mapa = new LinkedHashMap<>();
        for (Actividad a : actividades) {
            List<Inscripcion> inscripciones = inscRepo.findByActividad(a);
            // Ordenar inscriptos por apellido dentro de cada actividad
            inscripciones.sort(Comparator.comparing(i -> i.getSocio().getApellido()));
            mapa.put(a, inscripciones);
        }

        model.addAttribute("mapa", mapa);
        model.addAttribute("totalActividades", actividades.size());
        model.addAttribute("totalInscripciones", inscRepo.count());
        return "admin/listado-por-actividad";
    }
}
