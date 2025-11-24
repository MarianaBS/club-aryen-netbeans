package com.club.aryen.service;

import com.club.aryen.model.Socio;
import com.club.aryen.repository.SocioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class SocioService {

    private final SocioRepository repo;

    public SocioService(SocioRepository r) {
        this.repo = r;
    }

    public List<Socio> findAll() {
        return repo.findAll();
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
