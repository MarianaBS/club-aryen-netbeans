package com.club.aryen.repository;

import com.club.aryen.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    java.util.Optional<Usuario> findByUsername(String username);
}
