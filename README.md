# Sistema de Gestión de Ventas - Eggceptional

Este proyecto es una aplicación de escritorio en Java para la gestión de ventas, inventario, usuarios y reportes, orientada a empresas distribuidoras de productos.

## Características principales
- **Gestión de usuarios:** Alta, edición y baja de usuarios y empleados con roles (gerente, administrador, preventista).
- **Gestión de clientes:** Registro, edición y eliminación de clientes.
- **Inventario:** Control de productos, stock y precios.
- **Ventas:** Registro de ventas, detalle de productos vendidos y actualización automática de inventario.
- **Reportes:** Generación de reportes de ventas, usuarios e inventario, con visualización en tabla y gráficas. Exportación a PDF.

## Requisitos
- Java 17 o superior (recomendado Java 21 para soporte de preview features)
- PostgreSQL (para la base de datos)
- Librerías externas (colocar en la carpeta `lib/`):
  - `postgresql-42.7.3.jar` (driver JDBC)
  - `jfreechart-1.0.19.jar` y `jcommon-1.0.23.jar` (gráficas)
  - `itext-2.1.7.jar` (exportación PDF)

## Instalación y ejecución

1. **Configura la base de datos:**
   - Crea una base de datos en PostgreSQL llamada `eggceptional`.
   - Ejecuta el script `base_datos.sql` para crear las tablas y datos de ejemplo.
   - Ajusta el usuario y contraseña en `src/eggceptional/ConexionDB.java` si es necesario.

2. **Compila y ejecuta la aplicación:**
   - En Linux:
     ```bash
     ./iniciar.bash
     ```
   - En Windows:
     ```bat
     iniciar.bat
     ```

3. **Accede con los usuarios de ejemplo:**
   - Usuario: `gerente1` / Contraseña: `1234`
   - Usuario: `admin1` / Contraseña: `abcd`

## Estructura del proyecto
- `src/eggceptional/` - Código fuente Java
- `lib/` - Librerías externas (.jar)
- `base_datos.sql` - Script de creación de base de datos y datos de ejemplo
- `iniciar.bash` / `iniciar.bat` - Scripts para compilar y ejecutar

## Funcionalidades destacadas
- **Reportes visuales:** Gráficas de ventas por día, tablas exportables a PDF.
- **Selector de fechas:** Manual (campo de texto) o visual (si agregas JCalendar).
- **Interfaz amigable:** Uso de JTable y paneles gráficos para una mejor experiencia.

## Créditos
- Desarrollado por el equipo de chistemas xd.
- Librerías de terceros: JFreeChart, iText, PostgreSQL JDBC.

---

¿Dudas o sugerencias? ¡Contribuciones y mejoras son bienvenidas!
