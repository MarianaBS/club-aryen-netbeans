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
    private final EmailService emailService;

    public InscripcionService(InscripcionRepository r, SocioRepository s,
                               ActividadRepository a, EmailService emailService) {
        this.repo = r;
        this.socioRepo = s;
        this.actRepo = a;
        this.emailService = emailService;
    }

    @Transactional
    public Inscripcion inscribir(Long socioId, Long actividadId) {
        Socio socio = socioRepo.findById(socioId).orElseThrow();
        Actividad nueva = actRepo.findById(actividadId).orElseThrow();

        // 1. Validar que no esté inscripto ya en esta misma actividad
        if (repo.existsBySocioAndActividad(socio, nueva)) {
            throw new InscripcionException("Ya estás inscripto en \"" + nueva.getNombre() + "\".");
        }

        // 2. Validar superposición de horarios con otras inscripciones del socio
        List<Inscripcion> actuales = repo.findBySocio(socio);
        for (Inscripcion insc : actuales) {
            Actividad existente = insc.getActividad();
            if (nueva.seSuperponeCon(existente)) {
                throw new InscripcionException(
                    "\"" + nueva.getNombre() + "\" (" + nueva.getDia()
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
        Inscripcion guardada = repo.save(i);

        // 3. Enviar mail de confirmación (asíncrono, no bloquea)
        emailService.enviarConfirmacionInscripcion(guardada);

        return guardada;
    }

    @Transactional
    public void eliminar(Long inscripcionId) {
        Inscripcion i = repo.findById(inscripcionId).orElseThrow();
        // Guardar datos antes de eliminar para el mail
        repo.delete(i);
        emailService.enviarConfirmacionBaja(i);
    }

    public List<Inscripcion> findAll() {
        return repo.findAll();
    }

    public static class InscripcionException extends RuntimeException {
        public InscripcionException(String msg) { super(msg); }
    }
}
