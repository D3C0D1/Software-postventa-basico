# Software Postventa Básico

## Descripción
Software postventa de productos con sistema completo de gestión comercial. Incluye:
- **Login y autenticación** de usuarios
- **Dashboard** con estadísticas de ventas
- **Gestión de productos** (CRUD completo)
- **Gestión de clientes** (CRUD completo)
- **Gestión de proveedores** (CRUD completo)
- **Registro de ventas** con facturación
- **Configuración de empresa** para personalizar datos de facturación

## Tecnologías
- **Lenguaje:** Java 11+
- **Base de datos:** MySQL 8.0
- **Interfaz gráfica:** Swing con FlatLaf
- **Build tool:** Maven

## Requisitos Previos
- Java JDK 11 o superior
- MySQL Server 8.0
- Maven 3.6+

## Instalación

### 1. Clonar el repositorio
```bash
git clone https://github.com/D3C0D1/Software-postventa-basico.git
cd Software-postventa-basico
```

### 2. Configurar la base de datos
```bash
# Conectarse a MySQL y ejecutar el script de creación
mysql -u root -p < sql/schema.sql
```

### 3. Configurar conexión a la base de datos
Editar el archivo `src/main/java/com/postventa/config/DatabaseConfig.java`:
```java
private static final String URL = "jdbc:mysql://localhost:3306/postventa_db";
private static final String USER = "root";
private static final String PASSWORD = "tu_password";
```

### 4. Compilar y ejecutar
```bash
# Compilar el proyecto
mvn clean package

# Ejecutar la aplicación
java -jar target/software-postventa-basico-1.0.0.jar
```

## Credenciales por defecto
- **Usuario:** admin
- **Contraseña:** admin123

## Estructura del Proyecto
```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── postventa/
│   │           ├── Main.java              # Punto de entrada
│   │           ├── config/                # Configuración de BD
│   │           ├── controller/            # Controladores
│   │           ├── dao/                   # Data Access Objects
│   │           ├── model/                 # Modelos de datos
│   │           ├── util/                  # Utilidades
│   │           └── view/                  # Interfaces gráficas
│   └── resources/
└── test/
    └── java/

sql/
└── schema.sql                             # Script de base de datos
```

## Módulos

### 🔐 Login
Sistema de autenticación con roles de usuario (Admin, Supervisor, Vendedor).

### 📊 Dashboard
Panel principal con estadísticas de:
- Ventas del día
- Ventas del mes
- Total de productos
- Total de clientes

### 📦 Productos
- Crear, editar y eliminar productos
- Gestión de stock
- Asignación a categorías
- Precios de compra y venta

### 👥 Clientes
- Registro de clientes con datos completos
- Información de contacto y facturación
- Código de cliente automático

### 🏷️ Categorías
- Organización de productos por categorías
- Estado activo/inactivo

### 🛒 Ventas
- Crear nuevas ventas con múltiples productos
- Cálculo automático de subtotales, IVA y total
- Diferentes métodos de pago
- Generación de número de factura automático
- Cancelación de ventas con restauración de stock

### ⚙️ Configuración
- Datos de la empresa para facturación
- Configuración de porcentaje de IVA
- Mensaje personalizado para facturas
- Información de contacto

## Capturas de Pantalla

### Login
Pantalla de inicio de sesión seguro.

### Dashboard
Panel principal con estadísticas y acceso a todos los módulos.

### Productos
Gestión completa de inventario de productos.

### Ventas
Registro de ventas con cálculo automático de impuestos.

## Licencia
Este proyecto está bajo la Licencia MIT.

## Autor
D3C0D1

## Contribuciones
Las contribuciones son bienvenidas. Por favor, abre un issue primero para discutir los cambios que te gustaría realizar
