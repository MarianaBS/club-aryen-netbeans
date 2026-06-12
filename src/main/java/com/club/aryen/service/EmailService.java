package com.club.aryen.service;

import com.club.aryen.model.Inscripcion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:noreply@clubaryen.com}")
    private String from;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void enviarConfirmacionInscripcion(Inscripcion inscripcion) {
        String email = inscripcion.getSocio().getEmail();
        if (email == null || email.isBlank()) return;

        String actividad = inscripcion.getActividad().getNombre();
        String dia       = inscripcion.getActividad().getDia();
        String horario   = inscripcion.getActividad().getHorario()
                              .format(DateTimeFormatter.ofPattern("HH:mm"))
                         + " - "
                         + inscripcion.getActividad().getHorarioFin()
                              .format(DateTimeFormatter.ofPattern("HH:mm"));

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(email);
        msg.setSubject("Inscripción confirmada — " + actividad);
        msg.setText(
            "Hola " + inscripcion.getSocio().getNombre() + ",\n\n"
            + "Tu inscripción fue confirmada:\n\n"
            + "  Actividad : " + actividad + "\n"
            + "  Día       : " + dia + "\n"
            + "  Horario   : " + horario + "\n\n"
            + "¡Nos vemos en el club!\n\n"
            + "Club Aryen"
        );
        mailSender.send(msg);
    }

    @Async
    public void enviarConfirmacionBaja(Inscripcion inscripcion) {
        String email = inscripcion.getSocio().getEmail();
        if (email == null || email.isBlank()) return;

        String actividad = inscripcion.getActividad().getNombre();

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(email);
        msg.setSubject("Baja de actividad — " + actividad);
        msg.setText(
            "Hola " + inscripcion.getSocio().getNombre() + ",\n\n"
            + "Confirmamos que te diste de baja de la actividad:\n\n"
            + "  Actividad : " + actividad + "\n\n"
            + "Si fue un error, podés volver a inscribirte desde la app.\n\n"
            + "Club Aryen"
        );
        mailSender.send(msg);
    }
}
