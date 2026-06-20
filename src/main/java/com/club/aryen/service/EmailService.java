package com.club.aryen.service;

import com.club.aryen.model.Inscripcion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.mail.from:noreply@clubaryen.com}")
    private String from;

    @Value("${brevo.api.key:}")
    private String brevoApiKey;

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    // Logo incrustado en base64 para que funcione sin URL pública
    private static final String LOGO_B64 = "iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAABmJLR0QA/wD/AP+gvaeTAAADbUlEQVRYhdWXTWhcVRTHf+fOd/ImaWOgY9IKM9MopCTiB0JAMUpqUxNnaEUKIi6suK2LihuhILrRouBKKaK4k9CgQcSPKl2UShVam7TFtJI0bZqWVMfJfHZm8u51UU1npjOTzCQ1+N+9d847/999vHfPufB/1q7B2b5dg7N9q6khjTwUferKFox5G5EXADHwlUs79x0+smnqjgI81z9vFdyF/QZeB7xl4TzwoeTzB744GoyvKUB/v3FucF99yWDeBDYtkx5DeMfdEn9/ZGRbftUA0R1XBjDyHpie8lhhMQGAy9lSqfJ5o80bY99vHmkIILrzaje2eRfM0+UxbRZJpabJZGcRAa8ngN+/FSWuSgY/as3+sSOdp1YEENl+qQNRBwTZCzjK4zdy8ySS59G69O0qcWFZIZp8nZV8tIgcVg5eG/26Y6YqQGRwbrdo8xnQXF7BtrMsJCfJ52OVDJbkdm2gxX8vTqdVKZw2Sl4c+6ZjdAm8ZBXa9JabGzSp9DTXYyeWNQfIF+L8GfuFRPICxtjl4eZ/PJbkrFUsl/uDROoCtp1d1rgU2pDJXuZGbh6/FcbnDVTNVVUjwF8L43WbF0vrHAuJczVzar6B6hLaH+ki+qAXwZA+M83nx1LoBio1BqBaGN73MC93KwSwLzoZP3GKs4UGSjXi7+wOsfM+BdqmsAiOe4IMP9TgWup/xMED0SBbHFD49Ryf/lTAKB9PRDvwN9Da6gewOhke8KGMzcR3vzP67TUyRmh9LMzjbf8BQNuTYR5tFUz+Oj8czRA/NsPPaZCmAEM7rLoL1pevLAYiAZoETBLCu3vY+/xdeBIGxEFPJEiwToK6vhwVCjJ8vwMBpD3As6+UbjDOrhBDPWf54PTKf8g6ABTbngkRdoKOXeLgqxOc+bcfOTay52AfQ3dbbI8EOHR6jpVuXzUBNrb23tqKlRvX7BSffCTkJi/y5USCWzt9nENvuZnrdUMSWhVkNSjlocXf1TiAx9NOu6eNdHqGVGaGkyMTnKyYabh2/Dc+Pn7zShCafJvxWyFEbuvo1QG0knHRJk1RRxQUVnMQnzdAIjlJbpXtWCsZL75R90Bys0NOYtu5kvtKufFbW6t1vpUNJMWqPZLZpFJTd2Ykuw1kvYbSYq3rWF6sdTuYlGstj2ar0locTtddfwO/FHJ+Zq+QywAAAABJRU5ErkJggg==";

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm");


    @Async
    public void enviarConfirmacionInscripcion(Inscripcion inscripcion) {
        String email = inscripcion.getSocio().getEmail();
        if (email == null || email.isBlank()) return;

        String nombre   = inscripcion.getSocio().getNombre();
        String actividad = inscripcion.getActividad().getNombre();
        String dia       = inscripcion.getActividad().getDia();
        String horario   = inscripcion.getActividad().getHorario() != null
                         ? inscripcion.getActividad().getHorario().format(FMT)
                           + (inscripcion.getActividad().getHorarioFin() != null
                              ? " - " + inscripcion.getActividad().getHorarioFin().format(FMT)
                              : "")
                         : "Sin horario";

        String html = buildHtml(nombre,
            "✅ Inscripción confirmada",
            "Tu inscripción fue confirmada exitosamente.",
            new String[][]{
                {"Actividad", actividad},
                {"Día",       dia},
                {"Horario",   horario}
            },
            "#16a34a"
        );

        send(email, "Inscripción confirmada — " + actividad, html);
    }

    @Async
    public void enviarConfirmacionBaja(Inscripcion inscripcion) {
        String email = inscripcion.getSocio().getEmail();
        if (email == null || email.isBlank()) return;

        String nombre    = inscripcion.getSocio().getNombre();
        String actividad = inscripcion.getActividad().getNombre();

        String html = buildHtml(nombre,
            "📋 Baja de actividad",
            "Confirmamos que te diste de baja de la siguiente actividad:",
            new String[][]{
                {"Actividad", actividad}
            },
            "#4f46e5"
        );

        send(email, "Baja de actividad — " + actividad, html);
    }

    private void send(String to, String subject, String html) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("api-key", brevoApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

            Map<String, Object> sender = new HashMap<>();
            sender.put("name", "Club Aryen");
            sender.put("email", from);

            Map<String, Object> recipient = new HashMap<>();
            recipient.put("email", to);

            Map<String, Object> body = new HashMap<>();
            body.put("sender", sender);
            body.put("to", java.util.List.of(recipient));
            body.put("subject", subject);
            body.put("htmlContent", html);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(BREVO_API_URL, request, String.class);
        } catch (Exception e) {
            // Log sin frenar la operación
            System.err.println("Error enviando mail: " + e.getMessage());
        }
    }

    private String buildHtml(String nombre, String titulo, String intro,
                              String[][] filas, String accentColor) {
        StringBuilder rows = new StringBuilder();
        for (String[] fila : filas) {
            rows.append("""
                <tr>
                    <td style="padding:8px 12px;color:#6b7280;font-size:14px;width:120px">%s</td>
                    <td style="padding:8px 12px;font-weight:600;font-size:14px">%s</td>
                </tr>
            """.formatted(fila[0], fila[1]));
        }

        return """
            <!DOCTYPE html>
            <html lang="es">
            <head><meta charset="UTF-8"></head>
            <body style="margin:0;padding:0;background:#f3f4f6;font-family:system-ui,sans-serif">
                <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f3f4f6;padding:32px 16px">
                    <tr><td align="center">
                        <table width="100%%" cellpadding="0" cellspacing="0"
                               style="max-width:520px;background:white;border-radius:12px;overflow:hidden;box-shadow:0 2px 8px rgba(0,0,0,.08)">

                            <!-- Header con logo -->
                            <tr>
                                <td style="background:%s;padding:24px;text-align:center">
                                    <img src="data:image/png;base64,%s"
                                         alt="Club Aryen" width="48" height="48"
                                         style="display:inline-block;margin-bottom:10px"><br>
                                    <span style="color:white;font-size:20px;font-weight:800;letter-spacing:1px">ARYEN</span><br>
                                    <span style="color:rgba(255,255,255,.8);font-size:12px;letter-spacing:2px">CLUB DEPORTIVO</span>
                                </td>
                            </tr>

                            <!-- Título -->
                            <tr>
                                <td style="padding:24px 24px 8px;text-align:center">
                                    <p style="font-size:20px;font-weight:700;color:#111827;margin:0">%s</p>
                                </td>
                            </tr>

                            <!-- Saludo e intro -->
                            <tr>
                                <td style="padding:8px 24px 16px;text-align:center">
                                    <p style="color:#374151;font-size:15px;margin:0">Hola, <strong>%s</strong>.</p>
                                    <p style="color:#6b7280;font-size:14px;margin:8px 0 0">%s</p>
                                </td>
                            </tr>

                            <!-- Tabla de datos -->
                            <tr>
                                <td style="padding:0 24px 24px">
                                    <table width="100%%" style="border-collapse:collapse;background:#f9fafb;border-radius:8px;overflow:hidden">
                                        %s
                                    </table>
                                </td>
                            </tr>

                            <!-- Footer -->
                            <tr>
                                <td style="background:#f9fafb;padding:16px 24px;text-align:center;border-top:1px solid #e5e7eb">
                                    <p style="color:#9ca3af;font-size:12px;margin:0">
                                        Este es un mensaje automático del sistema de gestión de Club Aryen.<br>
                                        Por consultas escribinos por WhatsApp.
                                    </p>
                                </td>
                            </tr>

                        </table>
                    </td></tr>
                </table>
            </body>
            </html>
        """.formatted(accentColor, LOGO_B64, titulo, nombre, intro, rows.toString());
    }
}
