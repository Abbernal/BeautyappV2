-- BeautyApp - Script de Creación de Base de Datos (SQLite)
-- Este script representa el esquema de la base de datos implementado con Room

-- Tabla de Roles
CREATE TABLE IF NOT EXISTS ROLES (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    descripcion TEXT
);

-- Tabla de Usuarios
CREATE TABLE IF NOT EXISTS USUARIOS (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    telefono TEXT,
    rolId INTEGER NOT NULL,
    FOREIGN KEY (rolId) REFERENCES ROLES(id) ON DELETE RESTRICT
);

-- Índices para USUARIOS
CREATE INDEX IF NOT EXISTS idx_usuarios_rolId ON USUARIOS(rolId);
CREATE INDEX IF NOT EXISTS idx_usuarios_email ON USUARIOS(email);

-- Tabla de Servicios
CREATE TABLE IF NOT EXISTS SERVICIOS (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    descripcion TEXT,
    precio REAL NOT NULL,
    duracionMinutos INTEGER NOT NULL
);

-- Tabla de Estados de Cita
CREATE TABLE IF NOT EXISTS ESTADOS_CITA (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    descripcion TEXT
);

-- Tabla de Citas
CREATE TABLE IF NOT EXISTS CITAS (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    clienteId INTEGER NOT NULL,
    servicioId INTEGER NOT NULL,
    estadoId INTEGER NOT NULL,
    fechaHora TEXT NOT NULL,
    notas TEXT,
    FOREIGN KEY (clienteId) REFERENCES USUARIOS(id) ON DELETE RESTRICT,
    FOREIGN KEY (servicioId) REFERENCES SERVICIOS(id) ON DELETE RESTRICT,
    FOREIGN KEY (estadoId) REFERENCES ESTADOS_CITA(id) ON DELETE RESTRICT
);

-- Índices para CITAS
CREATE INDEX IF NOT EXISTS idx_citas_clienteId ON CITAS(clienteId);
CREATE INDEX IF NOT EXISTS idx_citas_servicioId ON CITAS(servicioId);
CREATE INDEX IF NOT EXISTS idx_citas_estadoId ON CITAS(estadoId);

-- Tabla de Relación Cita-Empleado
CREATE TABLE IF NOT EXISTS CITA_EMPLEADO (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    citaId INTEGER NOT NULL,
    empleadoId INTEGER NOT NULL,
    FOREIGN KEY (citaId) REFERENCES CITAS(id) ON DELETE CASCADE,
    FOREIGN KEY (empleadoId) REFERENCES USUARIOS(id) ON DELETE RESTRICT
);

-- Índices para CITA_EMPLEADO
CREATE INDEX IF NOT EXISTS idx_cita_empleado_citaId ON CITA_EMPLEADO(citaId);
CREATE INDEX IF NOT EXISTS idx_cita_empleado_empleadoId ON CITA_EMPLEADO(empleadoId);

-- Activar foreign keys (si se usa SQLite directamente)
PRAGMA foreign_keys = ON;

