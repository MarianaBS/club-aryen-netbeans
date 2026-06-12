package com.club.aryen.config;

import com.club.aryen.model.*;
import com.club.aryen.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalTime;
import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner init(UsuarioRepository usuRepo,
                           PasswordEncoder encoder,
                           SocioRepository socioRepo,
                           ActividadRepository actRepo) {
        return args -> {

            // Usuarios iniciales
            usuRepo.findByUsername("admin").orElseGet(() -> {
                Usuario a = new Usuario();
                a.setUsername("admin");
                a.setPassword(encoder.encode("1234"));
                a.setRol("ADMIN");
                return usuRepo.save(a);
            });

            usuRepo.findByUsername("marian").orElseGet(() -> {
                Usuario s = new Usuario();
                s.setUsername("marian");
                s.setPassword(encoder.encode("1234"));
                s.setRol("SOCIO");
                return usuRepo.save(s);
            });

            // Socios de prueba
            if (socioRepo.count() == 0) {
                Socio s1 = new Socio();
                s1.setNombre("Juan");
                s1.setApellido("Perez");
                s1.setEmail("juan@example.com");
                s1.setDni("12345678");

                Socio s2 = new Socio();
                s2.setNombre("Ana");
                s2.setApellido("Gomez");
                s2.setEmail("ana@example.com");
                s2.setDni("23456789");

                socioRepo.saveAll(List.of(s1, s2));
            }

            // Actividades de prueba — ahora con día, horario inicio y fin
            if (actRepo.count() == 0) {
                Actividad a1 = new Actividad();
                a1.setNombre("Fútbol");
                a1.setDia("Lunes");
                a1.setHorario(LocalTime.of(8, 0));
                a1.setHorarioFin(LocalTime.of(9, 0));
                a1.setProfesor("Carlos Ruiz");

                Actividad a2 = new Actividad();
                a2.setNombre("Natación");
                a2.setDia("Miercoles");
                a2.setHorario(LocalTime.of(10, 0));
                a2.setHorarioFin(LocalTime.of(11, 0));
                a2.setProfesor("Laura Díaz");

                Actividad a3 = new Actividad();
                a3.setNombre("Yoga");
                a3.setDia("Lunes");
                a3.setHorario(LocalTime.of(18, 0));
                a3.setHorarioFin(LocalTime.of(19, 0));
                a3.setProfesor("Ana Gómez");

                actRepo.saveAll(List.of(a1, a2, a3));
            }
        };
    }
}
