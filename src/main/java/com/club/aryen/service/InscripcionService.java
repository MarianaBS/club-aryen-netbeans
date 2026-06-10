package com.club.aryen.service;

import com.club.aryen.model.*;
import com.club.aryen.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

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
        Socio socio = socioRepo.findById(socioId).orElseThrow();
        Actividad nueva = actRepo.findById(actividadId).orElseThrow();

        // 1. Validar que no esté inscripto ya en esta misma actividad
        if (repo.existsBySocioAndActividad(socio, nueva)) {
            throw new InscripcionException("Ya estás inscripto en " + nueva.getNombre() + ".");
        }

        // 2. Validar que ninguna actividad ya inscripta se superponga con la nueva
        List<Inscripcion> inscripcionesActuales = repo.findBySocio(socio);
        for (Inscripcion insc : inscripcionesActuales) {
            Actividad existente = insc.getActividad();
            if (nueva.seSuperponeCon(existente)) {
                throw new InscripcionException(
                    "La actividad \"" + nueva.getNombre() + "\" (" + nueva.getDia()
                    + " " + nueva.getHorario() + "-" + nueva.getHorarioFin()
                    + ") se superpone con \"" + existente.getNombre() + "\" ("
                    + existente.getDia() + " " + existente.getHorario()
                    + "-" + existente.getHorarioFin() + ")."
                );
            }
        }

        Inscripcion i = new Inscripcion();
        i.setSocio(socio);
        i.setActividad(nueva);
        return repo.save(i);
    }

    public List<Inscripcion> findAll() {
        return repo.findAll();
    }

    // Excepción propia para que el controller la pueda atrapar limpiamente
    public static class InscripcionException extends RuntimeException {
        public InscripcionException(String msg) { super(msg); }
    }
}
