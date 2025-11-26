-- =====================================================
-- SOFTWARE POSTVENTA BÁSICO - Database Schema
-- Sistema de gestión postventa con facturación
-- =====================================================

-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS postventa_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE postventa_db;

-- =====================================================
-- Tabla de Usuarios (para login)
-- =====================================================
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    rol ENUM('ADMIN', 'VENDEDOR', 'SUPERVISOR') DEFAULT 'VENDEDOR',
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ultimo_acceso TIMESTAMP NULL,
    INDEX idx_username (username),
    INDEX idx_activo (activo)
) ENGINE=InnoDB;

-- =====================================================
-- Tabla de Configuración de Empresa
-- =====================================================
CREATE TABLE IF NOT EXISTS empresa (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    rfc VARCHAR(20),
    direccion VARCHAR(255),
    ciudad VARCHAR(100),
    estado VARCHAR(100),
    codigo_postal VARCHAR(10),
    telefono VARCHAR(20),
    email VARCHAR(100),
    sitio_web VARCHAR(100),
    logo_path VARCHAR(255),
    moneda VARCHAR(10) DEFAULT 'MXN',
    iva_porcentaje DECIMAL(5,2) DEFAULT 16.00,
    mensaje_factura TEXT,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =====================================================
-- Tabla de Categorías
-- =====================================================
CREATE TABLE IF NOT EXISTS categorias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    activa BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_nombre (nombre),
    INDEX idx_activa (activa)
) ENGINE=InnoDB;

-- =====================================================
-- Tabla de Productos
-- =====================================================
CREATE TABLE IF NOT EXISTS productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    categoria_id INT,
    precio_compra DECIMAL(10,2) DEFAULT 0.00,
    precio_venta DECIMAL(10,2) NOT NULL,
    stock INT DEFAULT 0,
    stock_minimo INT DEFAULT 5,
    unidad VARCHAR(20) DEFAULT 'PZA',
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE SET NULL,
    INDEX idx_codigo (codigo),
    INDEX idx_nombre (nombre),
    INDEX idx_categoria (categoria_id),
    INDEX idx_activo (activo)
) ENGINE=InnoDB;

-- =====================================================
-- Tabla de Clientes
-- =====================================================
CREATE TABLE IF NOT EXISTS clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100),
    rfc VARCHAR(20),
    email VARCHAR(100),
    telefono VARCHAR(20),
    direccion VARCHAR(255),
    ciudad VARCHAR(100),
    estado VARCHAR(100),
    codigo_postal VARCHAR(10),
    notas TEXT,
    activo BOOLEAN DEFAULT TRUE,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_codigo (codigo),
    INDEX idx_nombre (nombre),
    INDEX idx_activo (activo)
) ENGINE=InnoDB;

-- =====================================================
-- Tabla de Ventas
-- =====================================================
CREATE TABLE IF NOT EXISTS ventas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero_factura VARCHAR(50) NOT NULL UNIQUE,
    cliente_id INT,
    usuario_id INT NOT NULL,
    fecha_venta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    subtotal DECIMAL(12,2) NOT NULL,
    iva DECIMAL(12,2) DEFAULT 0.00,
    descuento DECIMAL(12,2) DEFAULT 0.00,
    total DECIMAL(12,2) NOT NULL,
    metodo_pago ENUM('EFECTIVO', 'TARJETA', 'TRANSFERENCIA', 'CREDITO') DEFAULT 'EFECTIVO',
    estado ENUM('PENDIENTE', 'PAGADA', 'CANCELADA') DEFAULT 'PAGADA',
    notas TEXT,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE SET NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    INDEX idx_numero_factura (numero_factura),
    INDEX idx_cliente (cliente_id),
    INDEX idx_fecha (fecha_venta),
    INDEX idx_estado (estado)
) ENGINE=InnoDB;

-- =====================================================
-- Tabla de Detalle de Ventas
-- =====================================================
CREATE TABLE IF NOT EXISTS detalle_ventas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    venta_id INT NOT NULL,
    producto_id INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    descuento DECIMAL(10,2) DEFAULT 0.00,
    subtotal DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (venta_id) REFERENCES ventas(id) ON DELETE CASCADE,
    FOREIGN KEY (producto_id) REFERENCES productos(id),
    INDEX idx_venta (venta_id),
    INDEX idx_producto (producto_id)
) ENGINE=InnoDB;

-- =====================================================
-- Datos iniciales
-- =====================================================

-- IMPORTANTE: La contraseña por defecto será hasheada automáticamente
-- en el primer inicio de sesión. Cambie la contraseña después de la instalación.
-- Usuario administrador por defecto (password: admin123)
INSERT INTO usuarios (username, password, nombre_completo, email, rol) 
VALUES ('admin', 'admin123', 'Administrador del Sistema', 'admin@empresa.com', 'ADMIN');

-- Configuración inicial de empresa
INSERT INTO empresa (nombre, rfc, direccion, ciudad, estado, codigo_postal, telefono, email, moneda, iva_porcentaje, mensaje_factura)
VALUES ('Mi Empresa S.A. de C.V.', 'XAXX010101000', 'Calle Principal #123', 'Ciudad de México', 'CDMX', '01000', '55-1234-5678', 'contacto@miempresa.com', 'MXN', 16.00, 'Gracias por su compra. Este documento es un comprobante de venta.');

-- Categorías iniciales
INSERT INTO categorias (nombre, descripcion) VALUES 
('General', 'Productos de categoría general'),
('Electrónicos', 'Equipos y accesorios electrónicos'),
('Oficina', 'Artículos de oficina'),
('Hogar', 'Productos para el hogar');

-- Cliente público general
INSERT INTO clientes (codigo, nombre, apellido, rfc, email, notas)
VALUES ('CLI001', 'Público', 'General', 'XAXX010101000', 'publico@general.com', 'Cliente para ventas sin registro');
