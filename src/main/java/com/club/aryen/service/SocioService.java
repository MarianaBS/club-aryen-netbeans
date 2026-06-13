package com.club.aryen.service;

import com.club.aryen.model.Inscripcion;
import com.club.aryen.model.Socio;
import com.club.aryen.repository.InscripcionRepository;
import com.club.aryen.repository.SocioRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.Optional;

@Service
public class SocioService {

    private final SocioRepository repo;
    private final InscripcionRepository inscRepo;
    private static final Sort SORT_ALFA = Sort.by("apellido").ascending().and(Sort.by("nombre").ascending());

    public SocioService(SocioRepository r, InscripcionRepository inscRepo) {
        this.repo = r;
        this.inscRepo = inscRepo;
    }

    public List<Socio> findAll() {
        return repo.findAll(SORT_ALFA);
    }

    public List<Socio> buscar(String q) {
        if (q == null || q.isBlank()) return findAll();
        return repo.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(q, q, SORT_ALFA);
    }

    public Optional<Socio> findById(Long id) {
        return repo.findById(id);
    }

    // Retorna las inscripciones activas del socio (para mostrar en confirmación)
    public List<Inscripcion> getInscripciones(Long id) {
        Socio s = repo.findById(id).orElseThrow();
        return inscRepo.findBySocio(s);
    }

    @Transactional
    public Socio save(Socio nuevo) {
        // Validar DNI duplicado
        if (nuevo.getDni() != null && !nuevo.getDni().isBlank()) {
            repo.findByDni(nuevo.getDni()).ifPresent(existente -> {
                if (!existente.getId().equals(nuevo.getId())) {
                    throw new SocioException("El DNI " + nuevo.getDni() + " ya está registrado.");
                }
            });
        }
        // Validar email duplicado
        if (nuevo.getEmail() != null && !nuevo.getEmail().isBlank()) {
            repo.findByEmail(nuevo.getEmail()).ifPresent(existente -> {
                if (!existente.getId().equals(nuevo.getId())) {
                    throw new SocioException("El email " + nuevo.getEmail() + " ya está registrado.");
                }
            });
        }
        return repo.save(nuevo);
    }

    public static class SocioException extends RuntimeException {
        public SocioException(String msg) { super(msg); }
    }

    // Da de baja al socio Y elimina todas sus inscripciones
    @Transactional
    public void softDelete(Long id) {
        repo.findById(id).ifPresent(s -> {
            if (!s.isActivo()) {
                throw new SocioException("El socio ya está inactivo.");
            }
            // Eliminar inscripciones primero
            List<Inscripcion> inscripciones = inscRepo.findBySocio(s);
            inscRepo.deleteAll(inscripciones);
            // Luego dar de baja
            s.setActivo(false);
            repo.save(s);
        });
    }

    @Transactional
    public void reactivar(Long id) {
        repo.findById(id).ifPresent(s -> {
            if (s.isActivo()) {
                throw new SocioException("El socio ya está activo.");
            }
            s.setActivo(true);
            repo.save(s);
        });
    }
}
