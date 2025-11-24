package com.club.aryen.service;

import com.club.aryen.model.Actividad;
import com.club.aryen.repository.ActividadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class ActividadService {

    private final ActividadRepository repo;

    public ActividadService(ActividadRepository r) {
        this.repo = r;
    }

    public List<Actividad> findAll() {
        return repo.findAll();
    }

    public Optional<Actividad> findById(Long id) {
        return repo.findById(id);
    }

    @Transactional
    public Actividad save(Actividad a) {
        return repo.save(a);
    }

    @Transactional
    public void softDelete(Long id) {
        repo.findById(id).ifPresent(a -> {
            a.setActivo(false);
            repo.save(a);
        });
    }
}
