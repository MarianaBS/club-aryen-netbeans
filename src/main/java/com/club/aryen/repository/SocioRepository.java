package com.club.aryen.repository;

import com.club.aryen.model.Socio;
import com.club.aryen.model.Usuario;
import java.util.List;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocioRepository extends JpaRepository<Socio, Long> {

    java.util.Optional<Socio> findByEmail(String email);

    java.util.List<Socio> findByActivo(boolean activo);

    Page<Socio> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrEmailContainingIgnoreCase(String q1, String q2, String q3, org.springframework.data.domain.Pageable pageable);

    public Socio findByUsuario(Usuario usuario);
    
    List<Socio> findByUsuarioIsNull();
}
