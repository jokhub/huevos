package eggceptional;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Clientes extends JFrame {
    private DefaultTableModel modelo;
    private JTable tabla; // Hacemos la tabla un campo de la clase para que sea accesible fácilmente
    private JButton btnVerInactivos;
    private JButton btnReactivar;

    public Clientes() {
        setTitle("Gestión de Clientes");
        setSize(1000, 480); // CAMBIO: Aumentado el ancho y alto para nuevos botones y espacio
        setResizable(true);
        setLayout(null);

        JLabel lblTitulo = new JLabel("Clientes");
        lblTitulo.setBounds(20, 10, 200, 25);
        add(lblTitulo);

        String[] columnas = {"ID", "DNI", "Nombre", "Apellido", "Teléfono", "Correo", "Dirección"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Las celdas de la tabla no son editables directamente
            }
        };
        tabla = new JTable(modelo); // Asignamos a la variable de instancia
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 50, 950, 180); // Ajustado el ancho del scroll
        add(scroll);

        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.setBounds(20, 250, 100, 30);
        add(btnAgregar);

        JButton btnEditar = new JButton("Editar");
        btnEditar.setBounds(140, 250, 100, 30);
        add(btnEditar);

        JButton btnDesactivar = new JButton("Desactivar"); // CAMBIO: Botón renombrado
        btnDesactivar.setBounds(260, 250, 100, 30);
        add(btnDesactivar);

        btnVerInactivos = new JButton("Ver Inactivos"); // Nuevo botón
        btnVerInactivos.setBounds(380, 250, 120, 30);
        add(btnVerInactivos);

        btnReactivar = new JButton("Reactivar"); // Nuevo botón
        btnReactivar.setBounds(520, 250, 100, 30);
        btnReactivar.setEnabled(false); // Deshabilitado por defecto, solo se activa si se ve la lista de inactivos
        add(btnReactivar);

        JButton btnVolver = new JButton("Volver");
        btnVolver.setBounds(870, 400, 100, 30); // Ajustado posición del botón volver
        add(btnVolver);

        // Carga inicial de los clientes activos al iniciar la ventana
        cargarClientes(true); // Carga solo clientes activos por defecto

        // Asignación de ActionListeners a los botones
        btnAgregar.addActionListener(_ -> agregarCliente());
        btnEditar.addActionListener(_ -> editarCliente()); // Ya no necesita pasar la tabla
        btnDesactivar.addActionListener(_ -> DesactivarCliente()); // CAMBIO: Llamada al nuevo método
        btnReactivar.addActionListener(_ -> reactivarCliente());
        btnVolver.addActionListener(_ -> dispose()); // Cierra la ventana actual

        btnVerInactivos.addActionListener(new ActionListener() {
            private boolean showingInactive = false;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (showingInactive) {
                    cargarClientes(true); // Cargar activos
                    btnVerInactivos.setText("Ver Inactivos");
                    btnDesactivar.setEnabled(true);
                    btnReactivar.setEnabled(false);
                    showingInactive = false;
                } else {
                    cargarClientes(false); // Cargar inactivos
                    btnVerInactivos.setText("Ver Activos");
                    btnDesactivar.setEnabled(false); // No se puede Desactivar si estás viendo inactivos
                    btnReactivar.setEnabled(true);
                    showingInactive = true;
                }
            }
        });


        // Centra la ventana en la pantalla y la hace visible
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Carga los datos de los clientes desde la base de datos a la tabla.
     * @param activos Si es true, carga solo clientes activos. Si es false, carga solo inactivos.
     */
    private void cargarClientes(boolean activos) {
        modelo.setRowCount(0); // Limpia todas las filas existentes en la tabla
        String sql = "SELECT id, dni, nombre, apellido, telefono, email, direccion FROM clientes ";
        if (activos) {
            sql += "WHERE activo = TRUE ";
        } else {
            sql += "WHERE activo = FALSE ";
        }
        sql += "ORDER BY nombre";

        try (Connection conn = ConexionDB.conectar(); // Establece conexión con la DB
             Statement st = conn.createStatement(); // Crea un statement
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("dni"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("telefono"),
                    rs.getString("email"),
                    rs.getString("direccion")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar clientes: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Abre un diálogo para agregar un nuevo cliente a la base de datos.
     */
    private void agregarCliente() {
        JTextField txtDNI = new JTextField();
        JTextField txtNombre = new JTextField();
        JTextField txtApellido = new JTextField();
        JTextField txtTelefono = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtDireccion = new JTextField();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("DNI:"));
        panel.add(txtDNI);
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

        int result = JOptionPane.showConfirmDialog(this, panel, "Agregar Cliente", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String dni = txtDNI.getText().trim();
            String nombre = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String email = txtEmail.getText().trim();
            String direccion = txtDireccion.getText().trim();

            if (dni.isEmpty() || nombre.isEmpty() || apellido.isEmpty()) {
                JOptionPane.showMessageDialog(this, "DNI, Nombre y Apellido son obligatorios.");
                return;
            }
            if (!dni.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "El DNI debe contener solo números.", "Entrada Inválida", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection conn = ConexionDB.conectar()) {
                // Verificar si el DNI ya existe
                if (dniExiste(conn, dni, -1)) { // -1 porque no estamos editando un cliente existente
                    JOptionPane.showMessageDialog(this, "Error: El DNI '" + dni + "' ya existe para otro cliente.", "DNI Duplicado", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO clientes (dni, nombre, apellido, telefono, email, direccion, activo) VALUES (?, ?, ?, ?, ?, ?, TRUE)"); // CAMBIO: Añadido 'activo'
                ps.setString(1, dni);
                ps.setString(2, nombre);
                ps.setString(3, apellido);
                ps.setString(4, telefono);
                ps.setString(5, email);
                ps.setString(6, direccion);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Cliente agregado correctamente.");
                cargarClientes(true); // Recarga los clientes activos
            } catch (SQLException ex) {
                 if (ex.getMessage().contains("duplicate key value violates unique constraint") || ex.getSQLState().startsWith("23")) {
                    // Esto es un fallback, ya que deberíamos atrapar el DNI duplicado con dniExiste()
                    JOptionPane.showMessageDialog(this, "Error: El DNI '" + dni + "' ya existe para otro cliente.", "DNI Duplicado", JOptionPane.ERROR_MESSAGE);
                 } else {
                    JOptionPane.showMessageDialog(this, "Error al agregar cliente: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
                 }
                ex.printStackTrace();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error inesperado al agregar cliente: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    /**
     * Abre un diálogo para editar un cliente existente en la base de datos.
     */
    private void editarCliente() { // Ya no necesita recibir la tabla como parámetro
        int fila = tabla.getSelectedRow();
        if (fila >= 0) {
            int id = Integer.parseInt(modelo.getValueAt(fila, 0).toString());
            String dniActual = modelo.getValueAt(fila, 1).toString();
            String nombreActual = modelo.getValueAt(fila, 2).toString();
            String apellidoActual = modelo.getValueAt(fila, 3).toString();
            String telefonoActual = modelo.getValueAt(fila, 4).toString();
            String emailActual = modelo.getValueAt(fila, 5).toString();
            String direccionActual = modelo.getValueAt(fila, 6).toString();

            JTextField txtDNI = new JTextField(dniActual);
            JTextField txtNombre = new JTextField(nombreActual);
            JTextField txtApellido = new JTextField(apellidoActual);
            JTextField txtTelefono = new JTextField(telefonoActual);
            JTextField txtEmail = new JTextField(emailActual);
            JTextField txtDireccion = new JTextField(direccionActual);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.add(new JLabel("DNI:"));
            panel.add(txtDNI);
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

            int result = JOptionPane.showConfirmDialog(this, panel, "Editar Cliente", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String dni = txtDNI.getText().trim();
                String nombre = txtNombre.getText().trim();
                String apellido = txtApellido.getText().trim();
                String telefono = txtTelefono.getText().trim();
                String email = txtEmail.getText().trim();
                String direccion = txtDireccion.getText().trim();

                if (dni.isEmpty() || nombre.isEmpty() || apellido.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "DNI, Nombre y Apellido son obligatorios.");
                    return;
                }
                if (!dni.matches("\\d+")) {
                    JOptionPane.showMessageDialog(this, "El DNI debe contener solo números.", "Entrada Inválida", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try (Connection conn = ConexionDB.conectar()) {
                    // Verificar si el DNI ya existe para otro cliente (excluyendo el actual)
                    if (dniExiste(conn, dni, id)) {
                        JOptionPane.showMessageDialog(this, "Error: El DNI '" + dni + "' ya existe para otro cliente.", "DNI Duplicado", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    PreparedStatement ps = conn.prepareStatement(
                        "UPDATE clientes SET dni=?, nombre=?, apellido=?, telefono=?, email=?, direccion=? WHERE id=?");
                    ps.setString(1, dni);
                    ps.setString(2, nombre);
                    ps.setString(3, apellido);
                    ps.setString(4, telefono);
                    ps.setString(5, email);
                    ps.setString(6, direccion);
                    ps.setInt(7, id);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Cliente actualizado correctamente.");
                    cargarClientes(true); // Recarga los clientes activos
                } catch (SQLException ex) {
                    if (ex.getMessage().contains("duplicate key value violates unique constraint") || ex.getSQLState().startsWith("23")) {
                         // Esto es un fallback, ya que deberíamos atrapar el DNI duplicado con dniExiste()
                        JOptionPane.showMessageDialog(this, "Error: El DNI '" + dni + "' ya existe para otro cliente.", "DNI Duplicado", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Error al actualizar cliente: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
                    }
                    ex.printStackTrace();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error inesperado al actualizar cliente: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para editar.");
        }
    }
    
    /**
     * Inactiva (soft-delete) un cliente seleccionado de la base de datos.
     */
    private void DesactivarCliente() { // Ya no necesita recibir la tabla como parámetro
        int fila = tabla.getSelectedRow();
        if (fila >= 0) {
            int id = Integer.parseInt(modelo.getValueAt(fila, 0).toString());
            String nombreCliente = modelo.getValueAt(fila, 2).toString() + " " + modelo.getValueAt(fila, 3).toString(); // Para el mensaje

            int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de Desactivar al cliente " + nombreCliente + "? (Las ventas asociadas permanecerán)", "Confirmar Inactivación", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = ConexionDB.conectar()) {
                    // CAMBIO: Actualiza el estado 'activo' a FALSE en lugar de eliminar la fila
                    PreparedStatement ps = conn.prepareStatement("UPDATE clientes SET activo = FALSE WHERE id=?");
                    ps.setInt(1, id);
                    int rowsAffected = ps.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Cliente inactivado correctamente. Las ventas asociadas no fueron eliminadas.");
                        cargarClientes(true); // Recarga los clientes activos para que el cliente inactivado desaparezca
                    } else {
                        JOptionPane.showMessageDialog(this, "No se pudo Desactivar al cliente. Es posible que no exista.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error al Desactivar cliente: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error inesperado al Desactivar cliente: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para Desactivar.");
        }
    }

    /**
     * Reactiva un cliente previamente inactivado, cambiando su estado 'activo' a TRUE.
     */
    private void reactivarCliente() {
        int fila = tabla.getSelectedRow();
        if (fila >= 0) {
            int id = Integer.parseInt(modelo.getValueAt(fila, 0).toString());
            String nombreCliente = modelo.getValueAt(fila, 2).toString() + " " + modelo.getValueAt(fila, 3).toString();

            int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de REACTIVAR al cliente " + nombreCliente + "?", "Confirmar Reactivación", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = ConexionDB.conectar()) {
                    PreparedStatement ps = conn.prepareStatement("UPDATE clientes SET activo = TRUE WHERE id=?");
                    ps.setInt(1, id);
                    int rowsAffected = ps.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Cliente reactivado correctamente.");
                        cargarClientes(false); // Recarga los inactivos (el cliente reactivado desaparecerá de esta vista)
                    } else {
                        JOptionPane.showMessageDialog(this, "No se pudo reactivar al cliente. Es posible que no exista.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error al reactivar cliente: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error inesperado al reactivar cliente: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para reactivar.");
        }
    }

    /**
     * Verifica si un DNI ya existe en la base de datos para otro cliente.
     * @param conn La conexión a la base de datos.
     * @param dni El DNI a verificar.
     * @param idExcluir El ID del cliente actual (para ediciones), o -1 si es una nueva adición.
     * @return true si el DNI ya existe, false en caso contrario.
     */
    private boolean dniExiste(Connection conn, String dni, int idExcluir) throws SQLException {
        String sql = "SELECT COUNT(*) FROM clientes WHERE dni = ?";
        if (idExcluir != -1) {
            sql += " AND id != ?";
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dni);
            if (idExcluir != -1) {
                ps.setInt(2, idExcluir);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}