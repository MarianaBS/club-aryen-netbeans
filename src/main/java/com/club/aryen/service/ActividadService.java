package com.club.aryen.service;

import com.club.aryen.model.Actividad;
import com.club.aryen.repository.ActividadRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class ActividadService {

    private final ActividadRepository repo;
    private static final Sort SORT_ALFA = Sort.by("nombre").ascending();

    public ActividadService(ActividadRepository r) {
        this.repo = r;
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
        List<Actividad> existentes = repo.findAll();

        for (Actividad existente : existentes) {
            // No comparar consigo misma al editar
            if (existente.getId().equals(nueva.getId())) continue;
            // Solo comparar activas
            if (!existente.isActivo()) continue;

            if (nueva.seSuperponeCon(existente)) {
                throw new ActividadException(
                    "La actividad se superpone con \"" + existente.getNombre()
                    + "\" (" + existente.getDia() + " "
                    + existente.getHorario() + "-" + existente.getHorarioFin() + ")."
                );
            }
        }

        return repo.save(nueva);
    }

    @Transactional
    public void softDelete(Long id) {
        repo.findById(id).ifPresent(a -> {
            a.setActivo(false);
            repo.save(a);
        });
    }

    public static class ActividadException extends RuntimeException {
        public ActividadException(String msg) { super(msg); }
    }
}
