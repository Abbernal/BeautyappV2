-- BeautyApp - Datos de Prueba (Seed Data)
-- Este script contiene los datos iniciales que se cargan automáticamente

-- Insertar Roles
INSERT INTO ROLES (nombre, descripcion) VALUES 
('Administrador', 'Administrador del sistema'),
('Empleado', 'Empleado del centro'),
('Cliente', 'Cliente del centro');

-- Insertar Usuarios
-- Nota: Las contraseñas están hasheadas con SHA-256
-- admin123 -> hash SHA-256
-- empleado123 -> hash SHA-256
-- cliente123 -> hash SHA-256

INSERT INTO USUARIOS (nombre, email, password, telefono, rolId) VALUES
('Admin Principal', 'admin@beautyapp.com', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', '600123456', 1),
('María García', 'maria@beautyapp.com', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', '600234567', 2),
('Juan López', 'juan@beautyapp.com', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', '600345678', 2),
('Ana Martínez', 'ana@beautyapp.com', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', '600456789', 3),
('Carlos Ruiz', 'carlos@beautyapp.com', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', '600567890', 3),
('Laura Sánchez', 'laura@beautyapp.com', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', '600678901', 3),
('Pedro Torres', 'pedro@beautyapp.com', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', '600789012', 3);

-- Insertar Estados de Cita
INSERT INTO ESTADOS_CITA (nombre, descripcion) VALUES
('Pendiente', 'Cita pendiente de confirmación'),
('Confirmada', 'Cita confirmada'),
('Realizada', 'Cita completada'),
('Cancelada', 'Cita cancelada');

-- Insertar Servicios
INSERT INTO SERVICIOS (nombre, descripcion, precio, duracionMinutos) VALUES
('Corte de Pelo', 'Corte de pelo profesional', 25.00, 30),
('Tinte', 'Tinte completo de cabello', 60.00, 120),
('Manicura', 'Manicura completa con esmaltado', 20.00, 45),
('Pedicura', 'Pedicura completa con esmaltado', 25.00, 60),
('Tratamiento Facial', 'Tratamiento facial rejuvenecedor', 50.00, 90),
('Depilación', 'Depilación con cera', 30.00, 45),
('Maquillaje', 'Maquillaje profesional', 40.00, 60),
('Peinado', 'Peinado para eventos', 35.00, 60);

-- Insertar Citas
INSERT INTO CITAS (clienteId, servicioId, estadoId, fechaHora, notas) VALUES
(4, 1, 2, '2024-12-20 10:00', 'Cliente regular'),
(5, 2, 1, '2024-12-20 14:00', 'Primera vez'),
(6, 3, 2, '2024-12-21 09:00', ''),
(4, 4, 3, '2024-12-19 16:00', 'Cita completada'),
(5, 1, 4, '2024-12-18 11:00', 'Cliente canceló'),
(6, 2, 2, '2024-12-22 15:00', ''),
(4, 3, 1, '2024-12-23 10:30', ''),
(5, 4, 2, '2024-12-21 13:00', '');

-- Insertar Asignaciones Cita-Empleado
INSERT INTO CITA_EMPLEADO (citaId, empleadoId) VALUES
(1, 2), -- Cita 1 asignada a María
(2, 3), -- Cita 2 asignada a Juan
(3, 2), -- Cita 3 asignada a María
(4, 2), -- Cita 4 asignada a María
(5, 3), -- Cita 5 asignada a Juan
(6, 3), -- Cita 6 asignada a Juan
(7, 2), -- Cita 7 asignada a María
(8, 3); -- Cita 8 asignada a Juan

