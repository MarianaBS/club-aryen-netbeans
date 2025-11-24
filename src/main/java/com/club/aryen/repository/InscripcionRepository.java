package com.club.aryen.repository;

import com.club.aryen.model.Actividad;
import com.club.aryen.model.Inscripcion;
import com.club.aryen.model.Socio;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    List<Inscripcion> findBySocio(Socio socio);
    List<Inscripcion> findByActividad(Actividad actividad);

    public Object findBySocioId(Long id);

    public boolean existsBySocioAndActividad(Socio socio, Actividad actividad);
}
