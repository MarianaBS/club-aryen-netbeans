package com.club.aryen.security;

import org.springframework.context.annotation.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AdminUserDetailsService uds;

    public SecurityConfig(AdminUserDetailsService uds) {
        this.uds = uds;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authProvider(passwordEncoder()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/css/**", "/js/**", "/img/**", "/favicon.ico").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/socio/**").hasAnyRole("SOCIO", "ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(successHandler())
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (req, res, auth) -> {
            boolean esAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            res.sendRedirect(esAdmin ? "/admin/menu" : "/socio/menu");
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider(PasswordEncoder encoder) {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(uds);
        p.setPasswordEncoder(encoder);
        return p;
    }
}
