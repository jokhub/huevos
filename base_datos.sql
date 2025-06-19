CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    nombre_usuario VARCHAR(50) UNIQUE NOT NULL,
    contrasena VARCHAR(50) NOT NULL,
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('gerente', 'administrador'))
);

CREATE TABLE empleados (
    id_empleados SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    fecha_ingreso DATE NOT NULL,
    fecha_salida DATE,
    dni VARCHAR(20) UNIQUE NOT NULL,
    correo VARCHAR(100) UNIQUE NOT NULL,
    telefono VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('gerente', 'administrador', 'preventista')),
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE clientes (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100),
    dni VARCHAR(20) UNIQUE,
    apellido VARCHAR(100),
    telefono VARCHAR(50),
    email VARCHAR(100),
    activo BOOLEAN DEFAULT TRUE,
    direccion VARCHAR(200)
);

CREATE TABLE producto (
    id_producto SERIAL PRIMARY KEY,
    categoria VARCHAR(30),
    precio_unitario FLOAT
);

CREATE TABLE inventario (
    id SERIAL PRIMARY KEY,
    id_producto INT REFERENCES producto(id_producto),
    cantidad FLOAT,
    fecha_actualizacion DATE,
    precio_unitario FLOAT NOT NULL
);

CREATE TABLE venta (
    id SERIAL PRIMARY KEY,
    id_empleados INTEGER NOT NULL REFERENCES empleados(id_empleados),
    id_cliente INTEGER NOT NULL REFERENCES clientes(id),
    fecha_emicion DATE,
    total_venta FLOAT
);

CREATE TABLE detalle_venta (
    id SERIAL PRIMARY KEY,
    id_venta INT REFERENCES venta(id),
    id_producto INT REFERENCES producto(id_producto),
    categoria VARCHAR(10) NOT NULL,
    cantidad INT NOT NULL,
    precio FLOAT NOT NULL,
    direccion VARCHAR(200) NOT NULL
);

CREATE TABLE reporte (
    id SERIAL PRIMARY KEY,
    tipo VARCHAR(20),
    fecha_gen DATE,
    descripcion VARCHAR(1000),
    id_usuario INT REFERENCES usuarios(id)
);

-- Insertar usuarios
INSERT INTO usuarios (nombre_usuario, contrasena, rol) VALUES
('gerente1', '1234', 'gerente'),
('admin1', 'abcd', 'administrador');

-- Insertar datos de empleados
INSERT INTO empleados (nombre, fecha_ingreso, fecha_salida, dni, correo, telefono, apellido, rol, activo)
VALUES
('Juan', '2020-01-15', NULL, '12345678', 'juan@example.com', '555-1234', 'Perez', 'preventista', true),
('Maria', '2018-05-10', NULL, '87654321', 'maria@example.com', '555-5678', 'Lopez', 'gerente', true);

-- Insertar datos de clientes
INSERT INTO clientes (nombre, dni, apellido, telefono, email, activo, direccion)
VALUES
('Carlos', '11223344', 'Gomez', '555-1122', 'carlos@example.com', true, 'Calle Falsa 123'),
('Ana', '44332211', 'Martinez', '555-3344', 'ana@example.com', true, 'Avenida Siempre Viva 742');

-- Insertar datos de productos
INSERT INTO producto (categoria, precio_unitario)
VALUES
('Electronica', 299.99),
('Ropa', 49.99),
('Alimento', 5.99);

-- Insertar datos en inventario
INSERT INTO inventario (id_producto, cantidad, fecha_actualizacion, precio_unitario)
VALUES
(1, 100, '2023-10-05', 299.99),
(2, 200, '2023-10-05', 49.99),
(3, 500, '2023-10-05', 5.99);

-- Insertar ventas
INSERT INTO venta (id_empleados, id_cliente, fecha_emicion, total_venta)
VALUES
(1, 1, '2023-10-10', 349.98),
(2, 2, '2023-10-11', 49.99);

-- Insertar detalles de venta
INSERT INTO detalle_venta (id_venta, id_producto, categoria, cantidad, precio, direccion)
VALUES
(1, 1, 'A', 1, 299.99, 'Calle Falsa 123'),
(1, 2, 'B', 1, 49.99, 'Calle Falsa 123'),
(2, 2, 'B', 1, 49.99, 'Avenida Siempre Viva 742');

-- Insertar reportes
INSERT INTO reporte (tipo, fecha_gen, descripcion, id_usuario)
VALUES
('diario', '2023-10-12', 'Reporte de ventas del día', 1),
('mensual', '2023-10-12', 'Reporte de ventas del mes', 2);