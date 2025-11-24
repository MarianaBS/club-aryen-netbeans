package com.club.aryen.security;

import com.club.aryen.model.Usuario;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import com.club.aryen.repository.UsuarioRepository;


@Service
public class AdminUserDetailsService implements UserDetailsService {

    private final UsuarioRepository repo;

    public AdminUserDetailsService(UsuarioRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario u = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Normalizamos el rol para asegurarnos de que coincida con Spring
        String rol = "ROLE_" + u.getRol().toUpperCase();

        return User.withUsername(u.getUsername())
                .password(u.getPassword()) // debe estar encriptado con BCrypt
                .authorities(rol)
                .build();
    }
}
