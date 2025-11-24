INSERT INTO usuario (username,password,rol) VALUES ('admin','$2a$10$ZlK7wN7eozqA1OAB8uF4UuHzFqPG2tF0iz8SxswE6c72OTF8tBtvG','ADMIN') ON DUPLICATE KEY UPDATE username=VALUES(username);
INSERT INTO socio (nombre,apellido,email,dni) VALUES ('Juan','Perez','juan@example.com','12345678') ON DUPLICATE KEY UPDATE email=email;
INSERT INTO actividad (nombre) VALUES ('Futbol') ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);
INSERT INTO inscripcion (socio_id,actividad_id) VALUES (1,1) ON DUPLICATE KEY UPDATE socio_id=socio_id;
