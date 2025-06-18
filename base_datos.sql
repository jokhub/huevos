CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    nombre_usuario VARCHAR(50) UNIQUE NOT NULL,
    contrasena VARCHAR(50) NOT NULL,
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('gerente', 'administrador',))
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
    precio_unitario FLOAT,
);

CREATE TABLE inventario (
    id SERIAL PRIMARY KEY,
    id_producto INT REFERENCES producto(id_producto),
    cantidad FLOAT,
    fecha_actualizacion DATE
    precio_unitario FLOAT NOT NULL      

);

CREATE TABLE venta (
    id SERIAL PRIMARY KEY,
    id_empleado INTEGER NOT NULL REFERENCES empleaodos(id),
    id_cliente INTEGER NOT NULL REFERENCES clientes(id),
    fecha_emicion DATE,
    total_venta FLOAT
);


CREATE TABLE detalle_venta (
    id SERIAL PRIMARY KEY,
    id_venta INT REFERENCES venta(id),
    id_producto INT REFERENCES producto(id_producto),
    categotia VARCHAR(10) NOT NULL, -- 'grande', 'mediano', 'pequeño'
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


-- Insertar usuarios
INSERT INTO usuarios (nombre_usuario, contrasena, rol) VALUES
('gerente1', '1234', 'gerente'),
('admin1', 'abcd', 'administrador');
