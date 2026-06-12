package com.club.aryen.repository;

import com.club.aryen.model.Actividad;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActividadRepository extends JpaRepository<Actividad, Long> {

    Optional<Actividad> findByNombre(String nombre);

    List<Actividad> findByActivo(boolean activo);

    // Búsqueda por nombre, ordenada alfabéticamente
    List<Actividad> findByNombreContainingIgnoreCase(String nombre, Sort sort);

    // Todas ordenadas por nombre
    List<Actividad> findAll(Sort sort);
}
