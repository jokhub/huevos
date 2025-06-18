package eggceptional;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class Clientes extends JFrame {
    private DefaultTableModel modelo;

    public Clientes() {
        setTitle("Gestión de Clientes");
        setSize(950, 420); // CAMBIO: Aumentado el ancho para acomodar la nueva columna 'DNI'
        setResizable(true);
        setLayout(null);

        JLabel lblTitulo = new JLabel("Clientes");
        lblTitulo.setBounds(20, 10, 200, 25);
        add(lblTitulo);

        // CAMBIO: Añadido "DNI" a las columnas de la tabla
        String[] columnas = {"ID", "DNI", "Nombre", "Apellido", "Teléfono", "Correo", "Dirección"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Las celdas de la tabla no son editables directamente
            }
        };
        JTable tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 50, 900, 180); // CAMBIO: Ajustado el ancho del scroll
        add(scroll);

        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.setBounds(20, 250, 100, 30);
        add(btnAgregar);

        JButton btnEditar = new JButton("Editar");
        btnEditar.setBounds(140, 250, 100, 30);
        add(btnEditar);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(260, 250, 100, 30);
        add(btnEliminar);

        JButton btnVolver = new JButton("Volver");
        btnVolver.setBounds(800, 340, 100, 30); // CAMBIO: Ajustado posición del botón volver
        add(btnVolver);

        // Carga inicial de los clientes al iniciar la ventana
        cargarClientes();

        // Asignación de ActionListeners a los botones
        btnAgregar.addActionListener(_ -> agregarCliente());
        btnEditar.addActionListener(_ -> editarCliente(tabla));
        btnEliminar.addActionListener(_ -> eliminarCliente(tabla));
        btnVolver.addActionListener(_ -> dispose()); // Cierra la ventana actual

        // Centra la ventana en la pantalla y la hace visible
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Carga los datos de los clientes desde la base de datos a la tabla.
     */
    private void cargarClientes() {
        modelo.setRowCount(0); // Limpia todas las filas existentes en la tabla
        try (Connection conn = ConexionDB.conectar(); // Establece conexión con la DB
             Statement st = conn.createStatement(); // Crea un statement
             // CAMBIO: Incluido 'dni' en la consulta SELECT
             // Se ordena por nombre para una mejor visualización de los datos
             ResultSet rs = st.executeQuery("SELECT id, dni, nombre, apellido, telefono, email, direccion FROM clientes ORDER BY nombre")) {
            while (rs.next()) {
                // Añade una nueva fila al modelo de la tabla con los datos del cliente
                modelo.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("dni"), // CAMBIO: Añadido DNI
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("telefono"),
                    rs.getString("email"),
                    rs.getString("direccion")
                });
            }
        } catch (Exception ex) {
            // Muestra un mensaje de error si falla la carga de clientes
            JOptionPane.showMessageDialog(this, "Error al cargar clientes: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Imprime la traza de la excepción para depuración
        }
    }

    /**
     * Abre un diálogo para agregar un nuevo cliente a la base de datos.
     */
    private void agregarCliente() {
        // CAMBIO: Nuevo campo DNI y los demás campos existentes
        JTextField txtDNI = new JTextField();
        JTextField txtNombre = new JTextField();
        JTextField txtApellido = new JTextField();
        JTextField txtTelefono = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtDireccion = new JTextField();

        // Panel para organizar los campos de entrada
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Layout vertical
        panel.add(new JLabel("DNI:")); // CAMBIO: Etiqueta para DNI
        panel.add(txtDNI); // CAMBIO: Campo para DNI
        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Apellido:"));
        panel.add(txtApellido);
        panel.add(new JLabel("Teléfono:"));
        panel.add(txtTelefono);
        panel.add(new JLabel("Correo electrónico:"));
        panel.add(txtEmail);
        panel.add(new JLabel("Dirección:"));
        panel.add(txtDireccion);

        // Muestra el diálogo de confirmación para que el usuario ingrese los datos
        int result = JOptionPane.showConfirmDialog(this, panel, "Agregar Cliente", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) { // Si el usuario presiona OK
            // Obtiene los valores ingresados por el usuario, eliminando espacios en blanco al inicio/final
            String dni = txtDNI.getText().trim(); // CAMBIO: Obtener DNI
            String nombre = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String email = txtEmail.getText().trim();
            String direccion = txtDireccion.getText().trim();

            // CAMBIO: Validación de campos obligatorios (incluyendo DNI)
            if (dni.isEmpty() || nombre.isEmpty() || apellido.isEmpty()) {
                JOptionPane.showMessageDialog(this, "DNI, Nombre y Apellido son obligatorios.");
                return; // Sale del método si los campos obligatorios están vacíos
            }
            // Validación para que DNI contenga solo números
            if (!dni.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "El DNI debe contener solo números.", "Entrada Inválida", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try (Connection conn = ConexionDB.conectar()) { // Establece conexión con la DB
                // CAMBIO: Incluido 'dni' en la sentencia INSERT
                // Se usa PreparedStatement para evitar inyección SQL y manejar parámetros
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO clientes (dni, nombre, apellido, telefono, email, direccion) VALUES (?, ?, ?, ?, ?, ?)");
                ps.setString(1, dni); // CAMBIO: Establecer DNI
                ps.setString(2, nombre); // CAMBIO: Índices ajustados debido a la nueva columna DNI
                ps.setString(3, apellido);
                ps.setString(4, telefono);
                ps.setString(5, email);
                ps.setString(6, direccion);
                ps.executeUpdate(); // Ejecuta la inserción
                JOptionPane.showMessageDialog(this, "Cliente agregado correctamente.");
                cargarClientes(); // Recarga los clientes en la tabla para mostrar el nuevo
            } catch (SQLException ex) { // Captura excepciones específicas de SQL
                // CAMBIO: Manejo más específico de errores de BD, útil para UNIQUE constraint en DNI
                // Comprueba si el error se debe a una restricción de clave única (DNI duplicado)
                if (ex.getMessage().contains("duplicate key value violates unique constraint") || ex.getSQLState().startsWith("23")) {
                     JOptionPane.showMessageDialog(this, "Error: El DNI '" + dni + "' ya existe para otro cliente.", "DNI Duplicado", JOptionPane.ERROR_MESSAGE);
                } else {
                     JOptionPane.showMessageDialog(this, "Error al agregar cliente: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
                }
                ex.printStackTrace();
            } catch (Exception ex) { // Captura cualquier otra excepción inesperada
                JOptionPane.showMessageDialog(this, "Error inesperado al agregar cliente: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    /**
     * Abre un diálogo para editar un cliente existente en la base de datos.
     * @param tabla La JTable de donde se seleccionó el cliente.
     */
    private void editarCliente(JTable tabla) {
        int fila = tabla.getSelectedRow(); // Obtiene la fila seleccionada
        if (fila >= 0) { // Si hay una fila seleccionada
            // Obtiene los datos actuales de la fila seleccionada
            int id = Integer.parseInt(modelo.getValueAt(fila, 0).toString());
            String dniActual = modelo.getValueAt(fila, 1).toString(); // CAMBIO: Obtener DNI actual (índice 1)
            String nombreActual = modelo.getValueAt(fila, 2).toString(); // CAMBIO: Nombre ahora en índice 2
            String apellidoActual = modelo.getValueAt(fila, 3).toString(); // CAMBIO: Apellido ahora en índice 3
            String telefonoActual = modelo.getValueAt(fila, 4).toString(); // CAMBIO: Teléfono ahora en índice 4
            String emailActual = modelo.getValueAt(fila, 5).toString(); // CAMBIO: Email ahora en índice 5
            String direccionActual = modelo.getValueAt(fila, 6).toString(); // CAMBIO: Dirección ahora en índice 6

            // Crea campos de texto pre-rellenados con los datos actuales
            JTextField txtDNI = new JTextField(dniActual); // CAMBIO: Campo para DNI
            JTextField txtNombre = new JTextField(nombreActual);
            JTextField txtApellido = new JTextField(apellidoActual);
            JTextField txtTelefono = new JTextField(telefonoActual);
            JTextField txtEmail = new JTextField(emailActual);
            JTextField txtDireccion = new JTextField(direccionActual);

            // Panel para organizar los campos de entrada
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.add(new JLabel("DNI:")); // CAMBIO: Etiqueta para DNI
            panel.add(txtDNI); // CAMBIO: Campo para DNI
            panel.add(new JLabel("Nombre:"));
            panel.add(txtNombre);
            panel.add(new JLabel("Apellido:"));
            panel.add(txtApellido);
            panel.add(new JLabel("Teléfono:"));
            panel.add(txtTelefono);
            panel.add(new JLabel("Correo electrónico:"));
            panel.add(txtEmail);
            panel.add(new JLabel("Dirección:"));
            panel.add(txtDireccion);

            // Muestra el diálogo de confirmación para que el usuario edite los datos
            int result = JOptionPane.showConfirmDialog(this, panel, "Editar Cliente", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) { // Si el usuario presiona OK
                // Obtiene los valores modificados por el usuario
                String dni = txtDNI.getText().trim(); // CAMBIO: Obtener DNI
                String nombre = txtNombre.getText().trim();
                String apellido = txtApellido.getText().trim();
                String telefono = txtTelefono.getText().trim();
                String email = txtEmail.getText().trim();
                String direccion = txtDireccion.getText().trim();

                // CAMBIO: Validación de campos obligatorios (incluyendo DNI)
                if (dni.isEmpty() || nombre.isEmpty() || apellido.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "DNI, Nombre y Apellido son obligatorios.");
                    return;
                }
                // Validación para que DNI contenga solo números
                if (!dni.matches("\\d+")) {
                    JOptionPane.showMessageDialog(this, "El DNI debe contener solo números.", "Entrada Inválida", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try (Connection conn = ConexionDB.conectar()) { // Establece conexión con la DB
                    // CAMBIO: Incluido 'dni' en la sentencia UPDATE
                    // Actualiza el cliente en la base de datos usando su ID
                    PreparedStatement ps = conn.prepareStatement(
                        "UPDATE clientes SET dni=?, nombre=?, apellido=?, telefono=?, email=?, direccion=? WHERE id=?");
                    ps.setString(1, dni); // CAMBIO: Establecer DNI
                    ps.setString(2, nombre); // CAMBIO: Índices ajustados
                    ps.setString(3, apellido);
                    ps.setString(4, telefono);
                    ps.setString(5, email);
                    ps.setString(6, direccion);
                    ps.setInt(7, id); // CAMBIO: Índice ajustado
                    ps.executeUpdate(); // Ejecuta la actualización
                    JOptionPane.showMessageDialog(this, "Cliente actualizado correctamente.");
                    cargarClientes(); // Recarga los clientes en la tabla
                } catch (SQLException ex) { // Captura excepciones específicas de SQL
                    // CAMBIO: Manejo más específico de errores de BD, útil para UNIQUE constraint en DNI
                    if (ex.getMessage().contains("duplicate key value violates unique constraint") || ex.getSQLState().startsWith("23")) {
                         JOptionPane.showMessageDialog(this, "Error: El DNI '" + dni + "' ya existe para otro cliente.", "DNI Duplicado", JOptionPane.ERROR_MESSAGE);
                    } else {
                         JOptionPane.showMessageDialog(this, "Error al actualizar cliente: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
                    }
                    ex.printStackTrace();
                } catch (Exception ex) { // Captura cualquier otra excepción inesperada
                    JOptionPane.showMessageDialog(this, "Error inesperado al actualizar cliente: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        } else {
            // Muestra un mensaje si no se ha seleccionado ninguna fila
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para editar.");
        }
    }
    
    /**
     * Elimina un cliente seleccionado de la base de datos.
     * @param tabla La JTable de donde se seleccionó el cliente.
     */
    private void eliminarCliente(JTable tabla) {
        int fila = tabla.getSelectedRow(); // Obtiene la fila seleccionada
        if (fila >= 0) { // Si hay una fila seleccionada
            int id = Integer.parseInt(modelo.getValueAt(fila, 0).toString()); // Obtiene el ID del cliente
            // Pide confirmación al usuario antes de eliminar
            int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar este cliente?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) { // Si el usuario confirma
                try (Connection conn = ConexionDB.conectar()) { // Establece conexión con la DB
                    // PreparedStatement para eliminar el cliente por su ID
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM clientes WHERE id=?");
                    ps.setInt(1, id);
                    ps.executeUpdate(); // Ejecuta la eliminación
                    JOptionPane.showMessageDialog(this, "Cliente eliminado correctamente.");
                    cargarClientes(); // Recarga los clientes en la tabla
                } catch (SQLException ex) { // Captura excepciones específicas de SQL
                    // CAMBIO: Mensaje de error más específico para FK (Foreign Key), si aplica
                    // Esto es útil si hay ventas asociadas a un cliente y la DB tiene restricciones de FK
                    if (ex.getMessage().contains("violates foreign key constraint") || ex.getSQLState().startsWith("23")) {
                         JOptionPane.showMessageDialog(this, "No se puede eliminar el cliente porque tiene ventas asociadas. Elimine las ventas primero.", "Error de Integridad", JOptionPane.ERROR_MESSAGE);
                    } else {
                         JOptionPane.showMessageDialog(this, "Error al eliminar cliente: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
                    }
                    ex.printStackTrace();
                } catch (Exception ex) { // Captura cualquier otra excepción inesperada
                    JOptionPane.showMessageDialog(this, "Error inesperado al eliminar cliente: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        } else {
            // Muestra un mensaje si no se ha seleccionado ninguna fila
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para eliminar.");
        }
    }
}
