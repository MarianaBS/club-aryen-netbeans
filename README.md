# ARYEN - club-aryen-netbeans

Proyecto Spring Boot listo para Apache NetBeans 15.

## Contenido
- Código en `src/main/java`.
- Templates en `src/main/resources/templates`.
- `application.properties` configurado para MySQL local.
- `schema.sql` y `data.sql` con admin (admin/1234) y datos de ejemplo.
- `Dockerfile` y `docker-compose.yml` para levantar la app + MySQL.

## Requisitos
- JDK 17
- Apache NetBeans 15
- (Opcional) Docker & Docker Compose

## Abrir en NetBeans
1. File -> Open Project... -> seleccionar la carpeta del proyecto (donde está pom.xml).
2. Run -> Run Project (NetBeans usará Maven para levantar Spring Boot).

## Ejecutar con Docker
1. mvn -DskipTests package
2. docker-compose up --build

## Acceso
- http://localhost:8080/login  (user: admin / pass: 1234)

