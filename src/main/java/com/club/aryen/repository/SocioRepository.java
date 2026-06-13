package com.club.aryen.repository;

import com.club.aryen.model.Socio;
import com.club.aryen.model.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;

public interface SocioRepository extends JpaRepository<Socio, Long> {

    Optional<Socio> findByEmail(String email);
    Optional<Socio> findByDni(String dni);

    // Ordenado alfabéticamente por apellido, luego nombre
    List<Socio> findAll(Sort sort);

    List<Socio> findByActivo(boolean activo);

    // Búsqueda por nombre o apellido, ordenada alfabéticamente
    List<Socio> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(
            String nombre, String apellido, Sort sort);

    Socio findByUsuario(Usuario usuario);

    List<Socio> findByUsuarioIsNull();
}
