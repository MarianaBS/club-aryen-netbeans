package com.club.aryen.service;

import com.club.aryen.model.*;
import com.club.aryen.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class InscripcionService {

    private final InscripcionRepository repo;
    private final SocioRepository socioRepo;
    private final ActividadRepository actRepo;

    public InscripcionService(InscripcionRepository r, SocioRepository s, ActividadRepository a) {
        this.repo = r;
        this.socioRepo = s;
        this.actRepo = a;
    }

    @Transactional
    public Inscripcion inscribir(Long socioId, Long actividadId) {
        Socio s = socioRepo.findById(socioId).orElseThrow();
        Actividad a = actRepo.findById(actividadId).orElseThrow();
        Inscripcion i = new Inscripcion();
        i.setSocio(s);
        i.setActividad(a);
        return repo.save(i);
    }

    public List<Inscripcion> findAll() {
        return repo.findAll();
    }
}
