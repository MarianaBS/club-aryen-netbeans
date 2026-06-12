package com.club.aryen.service;

import com.club.aryen.model.Socio;
import com.club.aryen.repository.SocioRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class SocioService {

    private final SocioRepository repo;
    private static final Sort SORT_ALFA = Sort.by("apellido").ascending().and(Sort.by("nombre").ascending());

    public SocioService(SocioRepository r) {
        this.repo = r;
    }

    // Todos los socios ordenados alfabéticamente
    public List<Socio> findAll() {
        return repo.findAll(SORT_ALFA);
    }

    // Búsqueda por nombre o apellido, ordenada
    public List<Socio> buscar(String q) {
        if (q == null || q.isBlank()) return findAll();
        return repo.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(q, q, SORT_ALFA);
    }

    public Optional<Socio> findById(Long id) {
        return repo.findById(id);
    }

    @Transactional
    public Socio save(Socio s) {
        return repo.save(s);
    }

    @Transactional
    public void softDelete(Long id) {
        repo.findById(id).ifPresent(s -> {
            s.setActivo(false);
            repo.save(s);
        });
    }

    
}
