package com.club.aryen.service;

import com.club.aryen.model.Actividad;
import com.club.aryen.repository.ActividadRepository;
import com.club.aryen.repository.InscripcionRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class ActividadService {

    private final ActividadRepository repo;
    private final InscripcionRepository inscRepo;
    private static final Sort SORT_ALFA = Sort.by("nombre").ascending();

    public ActividadService(ActividadRepository r, InscripcionRepository inscRepo) {
        this.repo = r;
        this.inscRepo = inscRepo;
    }

    public List<Actividad> findAll() {
        return repo.findAll(SORT_ALFA);
    }

    public List<Actividad> buscar(String q) {
        if (q == null || q.isBlank()) return findAll();
        return repo.findByNombreContainingIgnoreCase(q, SORT_ALFA);
    }

    public Optional<Actividad> findById(Long id) {
        return repo.findById(id);
    }

    @Transactional
    public Actividad save(Actividad nueva) {
        // Validar que horario fin sea posterior al inicio
        if (nueva.getHorario() != null && nueva.getHorarioFin() != null) {
            if (!nueva.getHorarioFin().isAfter(nueva.getHorario())) {
                throw new ActividadException(
                    "El horario de fin (" + nueva.getHorarioFin() + ") debe ser posterior al de inicio (" + nueva.getHorario() + ")."
                );
            }
        }
        return repo.save(nueva);
    }

    @Transactional
    public void softDelete(Long id) {
        repo.findById(id).ifPresent(a -> {
            if (!a.isActivo()) {
                throw new ActividadException("La actividad ya está inactiva.");
            }
            long inscriptos = inscRepo.countByActividad(a);
            if (inscriptos > 0) {
                throw new ActividadException(
                    "La actividad \"" + a.getNombre() + "\" tiene " + inscriptos
                    + " inscripto" + (inscriptos == 1 ? "" : "s") + ". "
                    + "Eliminá las inscripciones antes de darla de baja."
                );
            }
            a.setActivo(false);
            repo.save(a);
        });
    }

    @Transactional
    public void reactivar(Long id) {
        repo.findById(id).ifPresent(a -> {
            if (a.isActivo()) {
                throw new ActividadException("La actividad ya está activa.");
            }
            a.setActivo(true);
            repo.save(a);
        });
    }

    public static class ActividadException extends RuntimeException {
        public ActividadException(String msg) { super(msg); }
    }
}
