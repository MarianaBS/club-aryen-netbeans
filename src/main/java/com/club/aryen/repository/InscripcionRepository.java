package com.club.aryen.repository;

import com.club.aryen.model.Actividad;
import com.club.aryen.model.Inscripcion;
import com.club.aryen.model.Socio;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    // Ordenadas por apellido del socio
    List<Inscripcion> findBySocioOrderBySocioApellidoAscSocioNombreAsc(Socio socio);

    // Para el listado general, ordenado por apellido del socio
    List<Inscripcion> findAllByOrderBySocioApellidoAscSocioNombreAsc();

    List<Inscripcion> findByActividad(Actividad actividad);
    List<Inscripcion> findBySocio(Socio socio);
    List<Inscripcion> findBySocioOrderByActividadNombreAsc(Socio socio);
    List<Inscripcion> findBySocioId(Long id);
    boolean existsBySocioAndActividad(Socio socio, Actividad actividad);
    long countByActividad(Actividad actividad);
}
