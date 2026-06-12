package com.club.aryen.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registra los intentos fallidos de login por usuario.
 * Si supera el máximo, bloquea por X minutos.
 * Se guarda en memoria (se resetea al reiniciar el servidor).
 */
@Service
public class LoginAttemptService {

    @Value("${app.login.max-intentos:3}")
    private int maxIntentos;

    @Value("${app.login.bloqueo-minutos:30}")
    private int bloqueoMinutos;

    // username → datos del intento
    private final ConcurrentHashMap<String, AttemptData> intentos = new ConcurrentHashMap<>();

    public void loginFallido(String username) {
        AttemptData data = intentos.getOrDefault(username, new AttemptData());
        data.incrementar();
        intentos.put(username, data);
    }

    public void loginExitoso(String username) {
        intentos.remove(username);
    }

    public boolean estaBloqueado(String username) {
        AttemptData data = intentos.get(username);
        if (data == null) return false;
        if (data.intentos < maxIntentos) return false;

        // Verificar si ya pasó el tiempo de bloqueo
        if (LocalDateTime.now().isAfter(data.bloqueadoHasta)) {
            intentos.remove(username);
            return false;
        }
        return true;
    }

    public int intentosRestantes(String username) {
        AttemptData data = intentos.get(username);
        if (data == null) return maxIntentos;
        return Math.max(0, maxIntentos - data.intentos);
    }

    public LocalDateTime bloqueadoHasta(String username) {
        AttemptData data = intentos.get(username);
        return data != null ? data.bloqueadoHasta : null;
    }

    private class AttemptData {
        int intentos = 0;
        LocalDateTime bloqueadoHasta = LocalDateTime.now();

        void incrementar() {
            intentos++;
            if (intentos >= maxIntentos) {
                bloqueadoHasta = LocalDateTime.now().plusMinutes(bloqueoMinutos);
            }
        }
    }
}
