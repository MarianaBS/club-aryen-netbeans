package com.club.aryen.service;

import com.club.aryen.model.*;
import com.club.aryen.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
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

        // 1. Validar duplicado
        if (repo.existsBySocioAndActividad(socio, nueva)) {
            throw new InscripcionException("Ya estás inscripto en \"" + nueva.getNombre() + "\".");
        }

        // 2. Validar cupo
        if (nueva.getCupoMaximo() > 0) {
            long inscriptos = repo.countByActividad(nueva);
            if (inscriptos >= nueva.getCupoMaximo()) {
                throw new InscripcionException(
                    "La actividad \"" + nueva.getNombre() + "\" no tiene lugares disponibles "
                    + "(" + inscriptos + "/" + nueva.getCupoMaximo() + ")."
                );
            }
        }

        // 3. Validar superposición
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
        final Inscripcion saved = repo.save(i);

        // Enviar mail DESPUÉS de que la transacción commitee
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                emailService.enviarConfirmacionInscripcion(saved);
            }
        });

        return saved;
    }

    @Transactional
    public void eliminar(Long inscripcionId) {
        final Inscripcion i = repo.findById(inscripcionId).orElseThrow();
        repo.delete(i);

        // Enviar mail DESPUÉS de que la transacción commitee
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                emailService.enviarConfirmacionBaja(i);
            }
        });
    }

    public List<Inscripcion> findAll() {
        return repo.findAll();
    }

    public static class InscripcionException extends RuntimeException {
        public InscripcionException(String msg) { super(msg); }
    }
}
