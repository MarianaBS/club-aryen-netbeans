package com.club.aryen.repository;

import com.club.aryen.model.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActividadRepository extends JpaRepository<Actividad, Long> {

    java.util.Optional<Actividad> findByNombre(String nombre);

    java.util.List<Actividad> findByActivo(boolean activo);
}
