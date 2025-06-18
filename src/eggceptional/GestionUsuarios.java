package eggceptional;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class GestionUsuarios extends JFrame {
    // Componentes para empleados
    private JTextField txtNombreEmp, txtApellidoEmp, txtCorreoEmp, txtTelefonoEmp, txtFechaIngresoEmp, txtFechaSalidaEmp, txtDniEmp; // Added txtDniEmp
    private JComboBox<String> cmbRolEmp;
    private JTable tablaEmpleados;
    private DefaultTableModel modeloTablaEmp;
    private JButton btnGuardarEmp, btnEditarEmp, btnActivarEmp, btnDesactivarEmp, btnLimpiarEmp;

    // Componentes para usuarios
    private JTextField txtNombreUsuario, txtContrasenaUsuario;
    private JComboBox<String> cmbRolUsuario;
    private JTable tablaUsuarios;
    private DefaultTableModel modeloTablaUsu;
    private JButton btnGuardarUsu, btnEditarUsu, btnLimpiarUsu, btnEliminarUsu;

    // Botón volver
    private JButton btnVolver;

    private int empleadoSeleccionado = -1;
    private int usuarioSeleccionado = -1;

    public GestionUsuarios() {
        setTitle("Gestión de Usuarios y Empleados");
        setSize(1100, 500);
        setLayout(null);

        btnVolver = new JButton("Volver");
        btnVolver.setBounds(950, 420, 100, 30);
        add(btnVolver);
        btnVolver.addActionListener(_ -> dispose());

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBounds(0, 0, 1100, 470);

        // Panel Empleados
        JPanel panelEmp = new JPanel(null);

        JLabel lblNombreEmp = new JLabel("Nombre:");
        lblNombreEmp.setBounds(30, 20, 100, 25);
        panelEmp.add(lblNombreEmp);

        txtNombreEmp = new JTextField();
        txtNombreEmp.setBounds(130, 20, 150, 25);
        panelEmp.add(txtNombreEmp);

        JLabel lblApellidoEmp = new JLabel("Apellido:");
        lblApellidoEmp.setBounds(30, 55, 100, 25);
        panelEmp.add(lblApellidoEmp);

        txtApellidoEmp = new JTextField();
        txtApellidoEmp.setBounds(130, 55, 150, 25);
        panelEmp.add(txtApellidoEmp);

        // DNI Label and Field
        JLabel lblDniEmp = new JLabel("DNI:");
        lblDniEmp.setBounds(30, 90, 100, 25); // Adjusted position for DNI
        panelEmp.add(lblDniEmp);

        txtDniEmp = new JTextField();
        txtDniEmp.setBounds(130, 90, 150, 25); // Adjusted position for DNI
        panelEmp.add(txtDniEmp);

        JLabel lblCorreoEmp = new JLabel("Correo:");
        lblCorreoEmp.setBounds(30, 125, 100, 25); // Adjusted position
        panelEmp.add(lblCorreoEmp);

        txtCorreoEmp = new JTextField();
        txtCorreoEmp.setBounds(130, 125, 150, 25); // Adjusted position
        panelEmp.add(txtCorreoEmp);

        JLabel lblTelefonoEmp = new JLabel("Teléfono:");
        lblTelefonoEmp.setBounds(30, 160, 100, 25); // Adjusted position
        panelEmp.add(lblTelefonoEmp);

        txtTelefonoEmp = new JTextField();
        txtTelefonoEmp.setBounds(130, 160, 150, 25); // Adjusted position
        panelEmp.add(txtTelefonoEmp);

        JLabel lblFechaIngresoEmp = new JLabel("Fecha Ingreso (YYYY-MM-DD):");
        lblFechaIngresoEmp.setBounds(30, 195, 180, 25); // Adjusted position
        panelEmp.add(lblFechaIngresoEmp);

        txtFechaIngresoEmp = new JTextField();
        txtFechaIngresoEmp.setBounds(210, 195, 100, 25); // Adjusted position
        panelEmp.add(txtFechaIngresoEmp);

        JLabel lblFechaSalidaEmp = new JLabel("Fecha Salida (YYYY-MM-DD):");
        lblFechaSalidaEmp.setBounds(30, 230, 180, 25); // Adjusted position
        panelEmp.add(lblFechaSalidaEmp);

        txtFechaSalidaEmp = new JTextField();
        txtFechaSalidaEmp.setBounds(210, 230, 100, 25); // Adjusted position
        panelEmp.add(txtFechaSalidaEmp);

        JLabel lblRolEmp = new JLabel("Rol:");
        lblRolEmp.setBounds(30, 265, 100, 25); // Adjusted position
        panelEmp.add(lblRolEmp);

        cmbRolEmp = new JComboBox<>(new String[] { "gerente", "administrador", "preventista" });
        cmbRolEmp.setBounds(130, 265, 150, 25); // Adjusted position
        panelEmp.add(cmbRolEmp);

        btnGuardarEmp = new JButton("Registrar");
        btnGuardarEmp.setBounds(30, 305, 110, 30); // Adjusted position
        panelEmp.add(btnGuardarEmp);

        btnEditarEmp = new JButton("Editar");
        btnEditarEmp.setBounds(170, 305, 110, 30); // Adjusted position
        panelEmp.add(btnEditarEmp);

        btnActivarEmp = new JButton("Activar");
        btnActivarEmp.setBounds(30, 345, 110, 30); // Adjusted position
        panelEmp.add(btnActivarEmp);

        btnDesactivarEmp = new JButton("Desactivar");
        btnDesactivarEmp.setBounds(170, 345, 110, 30); // Adjusted position
        panelEmp.add(btnDesactivarEmp);

        btnLimpiarEmp = new JButton("Limpiar");
        btnLimpiarEmp.setBounds(30, 385, 110, 30); // Adjusted position
        panelEmp.add(btnLimpiarEmp);

        modeloTablaEmp = new DefaultTableModel(new String[] {
            "ID_empleados", "Nombre", "Apellido", "DNI", "Correo", "Teléfono", "Fecha Ingreso", "Fecha Salida", "Rol", "Activo" // Added DNI
        }, 0);
        tablaEmpleados = new JTable(modeloTablaEmp);
        JScrollPane scrollPaneEmp = new JScrollPane(tablaEmpleados);
        scrollPaneEmp.setBounds(350, 20, 720, 400);
        panelEmp.add(scrollPaneEmp);

        // Panel Usuarios (No changes needed in this section based on the request)
        JPanel panelUsu = new JPanel(null);

        JLabel lblNombreUsu = new JLabel("Usuario:");
        lblNombreUsu.setBounds(30, 20, 100, 25);
        panelUsu.add(lblNombreUsu);

        txtNombreUsuario = new JTextField();
        txtNombreUsuario.setBounds(130, 20, 150, 25);
        panelUsu.add(txtNombreUsuario);

        JLabel lblContrasenaUsu = new JLabel("Contraseña:");
        lblContrasenaUsu.setBounds(30, 55, 100, 25);
        panelUsu.add(lblContrasenaUsu);

        txtContrasenaUsuario = new JTextField();
        txtContrasenaUsuario.setBounds(130, 55, 150, 25);
        panelUsu.add(txtContrasenaUsuario);

        JLabel lblRolUsu = new JLabel("Rol:");
        lblRolUsu.setBounds(30, 90, 100, 25);
        panelUsu.add(lblRolUsu);

        cmbRolUsuario = new JComboBox<>(new String[] { "gerente", "administrador", "preventista" });
        cmbRolUsuario.setBounds(130, 90, 150, 25);
        panelUsu.add(cmbRolUsuario);

        btnGuardarUsu = new JButton("Registrar");
        btnGuardarUsu.setBounds(30, 130, 110, 30);
        panelUsu.add(btnGuardarUsu);

        btnEditarUsu = new JButton("Editar");
        btnEditarUsu.setBounds(170, 130, 110, 30);
        panelUsu.add(btnEditarUsu);

        btnLimpiarUsu = new JButton("Limpiar");
        btnLimpiarUsu.setBounds(30, 170, 110, 30);
        panelUsu.add(btnLimpiarUsu);

        btnEliminarUsu = new JButton("Eliminar");
        btnEliminarUsu.setBounds(170, 170, 110, 30);
        panelUsu.add(btnEliminarUsu);

        modeloTablaUsu = new DefaultTableModel(new String[] {
            "ID", "Usuario", "Rol"
        }, 0);
        tablaUsuarios = new JTable(modeloTablaUsu);
        JScrollPane scrollPaneUsu = new JScrollPane(tablaUsuarios);
        scrollPaneUsu.setBounds(350, 20, 720, 400);
        panelUsu.add(scrollPaneUsu);

        // Añadir paneles al tabbedPane
        tabs.addTab("Empleados", panelEmp);
        tabs.addTab("Usuarios", panelUsu);
        add(tabs);

        // --- Lógica de empleados ---
        cargarEmpleados();

        btnGuardarEmp.addActionListener(_ -> registrarEmpleado());
        btnEditarEmp.addActionListener(_ -> editarEmpleado());
        btnActivarEmp.addActionListener(_ -> cambiarEstadoEmpleado(true));
        btnDesactivarEmp.addActionListener(_ -> cambiarEstadoEmpleado(false));
        btnLimpiarEmp.addActionListener(_ -> limpiarCamposEmpleado());

        tablaEmpleados.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int fila = tablaEmpleados.getSelectedRow();
                if (fila >= 0) {
                    empleadoSeleccionado = Integer.parseInt(modeloTablaEmp.getValueAt(fila, 0).toString());
                    txtNombreEmp.setText(modeloTablaEmp.getValueAt(fila, 1).toString());
                    txtApellidoEmp.setText(modeloTablaEmp.getValueAt(fila, 2).toString());
                    txtDniEmp.setText(modeloTablaEmp.getValueAt(fila, 3).toString()); // Get DNI from table model
                    txtCorreoEmp.setText(modeloTablaEmp.getValueAt(fila, 4).toString());
                    txtTelefonoEmp.setText(modeloTablaEmp.getValueAt(fila, 5).toString());
                    txtFechaIngresoEmp.setText(modeloTablaEmp.getValueAt(fila, 6).toString());
                    Object fechaSalida = modeloTablaEmp.getValueAt(fila, 7);
                    txtFechaSalidaEmp.setText(fechaSalida == null ? "" : fechaSalida.toString());
                    cmbRolEmp.setSelectedItem(modeloTablaEmp.getValueAt(fila, 8).toString());
                }
            }
        });

        // --- Lógica de usuarios ---
        cargarUsuarios();

        btnGuardarUsu.addActionListener(_ -> registrarUsuario());
        btnEditarUsu.addActionListener(_ -> editarUsuario());
        btnLimpiarUsu.addActionListener(_ -> limpiarCamposUsuario());
        btnEliminarUsu.addActionListener(_ -> eliminarUsuario());

        tablaUsuarios.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int fila = tablaUsuarios.getSelectedRow();
                if (fila >= 0) {
                    usuarioSeleccionado = Integer.parseInt(modeloTablaUsu.getValueAt(fila, 0).toString());
                    txtNombreUsuario.setText(modeloTablaUsu.getValueAt(fila, 1).toString());
                    cmbRolUsuario.setSelectedItem(modeloTablaUsu.getValueAt(fila, 2).toString());
                }
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Métodos para empleados
    private void cargarEmpleados() {
        modeloTablaEmp.setRowCount(0);
        try (Connection conn = ConexionDB.conectar()) {
            // Added dni to the SELECT statement
            String sql = "SELECT id_empleados, nombre, apellido, dni, correo, telefono, fecha_ingreso, fecha_salida, rol, activo FROM empleados";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                modeloTablaEmp.addRow(new Object[] {
                    rs.getInt("id_empleados"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("dni"), // Get DNI from result set
                    rs.getString("correo"),
                    rs.getString("telefono"),
                    rs.getDate("fecha_ingreso"),
                    rs.getDate("fecha_salida"),
                    rs.getString("rol"),
                    rs.getBoolean("activo") ? "Activo" : "Inactivo"
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void limpiarCamposEmpleado() {
        txtNombreEmp.setText("");
        txtApellidoEmp.setText("");
        txtDniEmp.setText(""); // Clear DNI field
        txtCorreoEmp.setText("");
        txtTelefonoEmp.setText("");
        txtFechaIngresoEmp.setText("");
        txtFechaSalidaEmp.setText("");
        cmbRolEmp.setSelectedIndex(0);
        empleadoSeleccionado = -1;
    }

    private void registrarEmpleado() {
        String nombre = txtNombreEmp.getText().trim();
        String apellido = txtApellidoEmp.getText().trim();
        String dni = txtDniEmp.getText().trim(); // Get DNI
        String correo = txtCorreoEmp.getText().trim();
        String telefono = txtTelefonoEmp.getText().trim();
        String fechaIngreso = txtFechaIngresoEmp.getText().trim();
        String fechaSalida = txtFechaSalidaEmp.getText().trim();
        String rol = cmbRolEmp.getSelectedItem().toString();

        if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || correo.isEmpty() || telefono.isEmpty() || fechaIngreso.isEmpty() || rol.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos excepto fecha de salida son obligatorios.");
            return;
        }
        if (!fechaIngreso.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "La fecha de ingreso debe tener el formato YYYY-MM-DD");
            return;
        }
        if (!fechaSalida.isEmpty() && !fechaSalida.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "La fecha de salida debe tener el formato YYYY-MM-DD o estar vacía");
            return;
        }

        try (Connection conn = ConexionDB.conectar()) {
            // Added dni to the INSERT statement
            String sql = "INSERT INTO empleados (nombre, apellido, dni, correo, telefono, fecha_ingreso, fecha_salida, rol, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setString(2, apellido);
            ps.setString(3, dni); // Set DNI
            ps.setString(4, correo);
            ps.setString(5, telefono);
            ps.setDate(6, Date.valueOf(fechaIngreso));
            if (fechaSalida.isEmpty()) {
                ps.setNull(7, java.sql.Types.DATE);
            } else {
                ps.setDate(7, Date.valueOf(fechaSalida));
            }
            ps.setString(8, rol);
            ps.setBoolean(9, true);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Empleado registrado");
            cargarEmpleados();
            limpiarCamposEmpleado();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar empleado: " + ex.getMessage());
        }
    }

    private void editarEmpleado() {
        if (empleadoSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un empleado");
            return;
        }
        String nombre = txtNombreEmp.getText().trim();
        String apellido = txtApellidoEmp.getText().trim();
        String dni = txtDniEmp.getText().trim(); // Get DNI
        String correo = txtCorreoEmp.getText().trim();
        String telefono = txtTelefonoEmp.getText().trim();
        String fechaIngreso = txtFechaIngresoEmp.getText().trim();
        String fechaSalida = txtFechaSalidaEmp.getText().trim();
        String rol = cmbRolEmp.getSelectedItem().toString();

        if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || correo.isEmpty() || telefono.isEmpty() || fechaIngreso.isEmpty() || rol.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos excepto fecha de salida son obligatorios.");
            return;
        }
        if (!fechaIngreso.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "La fecha de ingreso debe tener el formato YYYY-MM-DD");
            return;
        }
        if (!fechaSalida.isEmpty() && !fechaSalida.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "La fecha de salida debe tener el formato YYYY-MM-DD o estar vacía");
            return;
        }

        try (Connection conn = ConexionDB.conectar()) {
            // Added dni to the UPDATE statement and adjusted parameter indices
            String sql = "UPDATE empleados SET nombre=?, apellido=?, dni=?, correo=?, telefono=?, fecha_ingreso=?, fecha_salida=?, rol=? WHERE id_empleados=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setString(2, apellido);
            ps.setString(3, dni); // Set DNI
            ps.setString(4, correo);
            ps.setString(5, telefono);
            ps.setDate(6, Date.valueOf(fechaIngreso));
            if (fechaSalida.isEmpty()) {
                ps.setNull(7, java.sql.Types.DATE);
            } else {
                ps.setDate(7, Date.valueOf(fechaSalida));
            }
            ps.setString(8, rol);
            ps.setInt(9, empleadoSeleccionado);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Empleado actualizado");
            cargarEmpleados();
            limpiarCamposEmpleado();
            empleadoSeleccionado = -1;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar empleado: " + ex.getMessage());
        }
    }

    private void cambiarEstadoEmpleado(boolean activar) {
        if (empleadoSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un empleado");
            return;
        }
        try (Connection conn = ConexionDB.conectar()) {
            String sql = "UPDATE empleados SET activo=? WHERE id_empleados=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBoolean(1, activar);
            ps.setInt(2, empleadoSeleccionado);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, activar ? "Empleado activado" : "Empleado desactivado");
            cargarEmpleados();
            limpiarCamposEmpleado();
            empleadoSeleccionado = -1;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cambiar estado: " + ex.getMessage());
        }
    }

    // Métodos para usuarios
    private void cargarUsuarios() {
        modeloTablaUsu.setRowCount(0);
        try (Connection conn = ConexionDB.conectar()) {
            String sql = "SELECT id, nombre_usuario, rol FROM usuarios";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                modeloTablaUsu.addRow(new Object[] {
                    rs.getInt("id"),
                    rs.getString("nombre_usuario"),
                    rs.getString("rol")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void limpiarCamposUsuario() {
        txtNombreUsuario.setText("");
        txtContrasenaUsuario.setText("");
        cmbRolUsuario.setSelectedIndex(0);
        usuarioSeleccionado = -1;
    }

    private void registrarUsuario() {
        String nombreUsuario = txtNombreUsuario.getText().trim();
        String contrasena = txtContrasenaUsuario.getText().trim();
        String rol = cmbRolUsuario.getSelectedItem().toString();

        if (nombreUsuario.isEmpty() || contrasena.isEmpty() || rol.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
            return;
        }

        try (Connection conn = ConexionDB.conectar()) {
            String sql = "INSERT INTO usuarios (nombre_usuario, contrasena, rol) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nombreUsuario);
            ps.setString(2, contrasena);
            ps.setString(3, rol);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Usuario registrado");
            cargarUsuarios();
            limpiarCamposUsuario();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar usuario: " + ex.getMessage());
        }
    }

    private void editarUsuario() {
        if (usuarioSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario");
            return;
        }
        String nombreUsuario = txtNombreUsuario.getText().trim();
        String contrasena = txtContrasenaUsuario.getText().trim();
        String rol = cmbRolUsuario.getSelectedItem().toString();

        if (nombreUsuario.isEmpty() || contrasena.isEmpty() || rol.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
            return;
        }

        try (Connection conn = ConexionDB.conectar()) {
            String sql = "UPDATE usuarios SET nombre_usuario=?, contrasena=?, rol=? WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nombreUsuario);
            ps.setString(2, contrasena);
            ps.setString(3, rol);
            ps.setInt(4, usuarioSeleccionado);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Usuario actualizado");
            cargarUsuarios();
            limpiarCamposUsuario();
            usuarioSeleccionado = -1;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar usuario: " + ex.getMessage());
        }
    }

    private void eliminarUsuario() {
        if (usuarioSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para eliminar.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar este usuario?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = ConexionDB.conectar()) {
            String sql = "DELETE FROM usuarios WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, usuarioSeleccionado);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Usuario eliminado");
            cargarUsuarios();
            limpiarCamposUsuario();
            usuarioSeleccionado = -1;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al eliminar usuario: " + ex.getMessage());
        }
    }
}