CREATE TABLE IF NOT EXISTS membresias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_plan VARCHAR(100) UNIQUE NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10, 2) NOT NULL,
    duracion_dias INT NOT NULL,
    activo BOOLEAN NOT NULL
);