package com.club.aryen.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Habilita @Async para que los mails se envíen en segundo plano
 * sin bloquear la respuesta al usuario.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
