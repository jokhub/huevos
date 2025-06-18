package eggceptional;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.sql.*;
import java.time.LocalDate;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import java.util.Vector; // Import for Vector

public class Inventario extends JFrame {
    // Solo un producto, pero puede haber varias categorías

    public Inventario() {
        setTitle("Inventario");
        setSize(950, 400); // Tamaño ajustado al quitar componentes
        setResizable(true);
        setLayout(null);

        JLabel lblTitulo = new JLabel("Inventario de Huevos");
        lblTitulo.setBounds(20, 10, 250, 25);
        add(lblTitulo);

        JLabel lblAlerta = new JLabel();
        lblAlerta.setBounds(20, 35, 900, 30);
        lblAlerta.setOpaque(true);
        lblAlerta.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblAlerta);

        // CAMBIO: "Cantidad Mapple" a "Cantidad Unitario" en el encabezado de la tabla
        String[] columnas = {"ID", "Categoría", "Precio Unidad", "Cantidad Unitario", "Fecha"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 75, 900, 120);
        add(scroll);

        JButton btnEditarProducto = new JButton("Editar Producto");
        btnEditarProducto.setBounds(20, 210, 150, 30);
        add(btnEditarProducto);

        // CAMBIO: Texto del botón para reflejar "Cantidad" en lugar de "Maples"
        JButton btnAgregarNuevo = new JButton("Agregar Cantidad al Inventario");
        btnAgregarNuevo.setBounds(190, 210, 220, 30);
        add(btnAgregarNuevo);

        JButton btnEditar = new JButton("Editar Inventario Existente");
        btnEditar.setBounds(430, 210, 200, 30);
        add(btnEditar);
        
        JButton btnActualizarPrecios = new JButton("Actualizar Precios en Inventario");
        btnActualizarPrecios.setBounds(650, 210, 250, 30); 
        add(btnActualizarPrecios);

        JButton btnVolver = new JButton("Volver");
        btnVolver.setBounds(800, 250, 90, 30); // Posición ajustada
        add(btnVolver);

        // Cargar inventario desde la base de datos al iniciar
        cargarDesdeBD(modelo, lblAlerta);

        btnEditarProducto.addActionListener(_ -> {
            Vector<String> categorias = new Vector<>();
            try (Connection conn = ConexionDB.conectar()) {
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT DISTINCT categoria FROM producto ORDER BY categoria");
                while (rs.next()) {
                    categorias.add(rs.getString("categoria"));
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al cargar categorías: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
                return;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error inesperado al cargar categorías: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (categorias.isEmpty()) {
                categorias.add("Pequeño"); // Default categories if none found in DB
                categorias.add("Mediano");
                categorias.add("Grande");
                JOptionPane.showMessageDialog(this, "No se encontraron categorías en la base de datos. Se usarán categorías por defecto. Por favor, agregue productos con estas categorías o edite existentes.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }

            JComboBox<String> cmbCategoria = new JComboBox<>(categorias);
            JTextField txtPrecioUnidad = new JTextField();

            cmbCategoria.addActionListener(e -> {
                String selectedCategory = (String) cmbCategoria.getSelectedItem();
                if (selectedCategory != null && !selectedCategory.isEmpty()) {
                    try (Connection conn = ConexionDB.conectar()) {
                        PreparedStatement ps = conn.prepareStatement(
                            "SELECT precio_unitario FROM producto WHERE categoria=?");
                        ps.setString(1, selectedCategory);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            txtPrecioUnidad.setText(String.format("%.2f", rs.getFloat("precio_unitario")));
                        } else {
                            txtPrecioUnidad.setText("");
                        }
                    } catch (SQLException ex) {
                        System.err.println("Error pre-filling prices: " + ex.getMessage());
                    }
                }
            });

            if (!categorias.isEmpty()) {
                cmbCategoria.setSelectedIndex(0); // Select the first category by default
                String selectedCategory = (String) cmbCategoria.getSelectedItem();
                if (selectedCategory != null && !selectedCategory.isEmpty()) {
                    try (Connection conn = ConexionDB.conectar()) {
                        PreparedStatement ps = conn.prepareStatement(
                            "SELECT precio_unitario FROM producto WHERE categoria=?");
                        ps.setString(1, selectedCategory);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            txtPrecioUnidad.setText(String.format("%.2f", rs.getFloat("precio_unitario")));
                        } else {
                            txtPrecioUnidad.setText("");
                        }
                    } catch (SQLException ex) {
                        System.err.println("Error pre-filling prices: " + ex.getMessage());
                    }
                }
            }

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.add(new JLabel("Seleccione o ingrese Categoría:"));
            panel.add(cmbCategoria);
            panel.add(new JLabel("Precio por Unidad:"));
            panel.add(txtPrecioUnidad);

            int result = JOptionPane.showConfirmDialog(this, panel, "Editar o Agregar Precios de Producto", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String categoria = (String) cmbCategoria.getSelectedItem();
                float precioUnidad;
                try {
                    precioUnidad = Float.parseFloat(txtPrecioUnidad.getText().trim());
                    if (precioUnidad < 0) {
                        throw new IllegalArgumentException("Los precios no pueden ser negativos.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Por favor, ingrese valores numéricos válidos para los precios.", "Entrada Inválida", JOptionPane.WARNING_MESSAGE);
                    return;
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Entrada Inválida", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                try (Connection conn = ConexionDB.conectar()) {
                    PreparedStatement ps = conn.prepareStatement(
                        "SELECT id_producto FROM producto WHERE categoria=?");
                    ps.setString(1, categoria);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        int idProducto = rs.getInt("id_producto");
                        PreparedStatement psUp = conn.prepareStatement(
                            "UPDATE producto SET precio_unitario=? WHERE id_producto=?");
                        psUp.setFloat(1, precioUnidad);
                        psUp.setInt(2, idProducto);
                        psUp.executeUpdate();
                        JOptionPane.showMessageDialog(this, "Precios del producto actualizados correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        PreparedStatement psIns = conn.prepareStatement(
                            "INSERT INTO producto (categoria, precio_unitario) VALUES (?, ?)");
                        psIns.setString(1, categoria);
                        psIns.setFloat(2, precioUnidad);
                        psIns.executeUpdate();
                        JOptionPane.showMessageDialog(this, "Nuevo producto agregado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    }
                    cargarDesdeBD(modelo, lblAlerta);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error de base de datos al guardar producto: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error inesperado al guardar producto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnAgregarNuevo.addActionListener(_ -> {
            Vector<String> categorias = new Vector<>();
            try (Connection conn = ConexionDB.conectar()) {
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT DISTINCT categoria FROM producto ORDER BY categoria");
                while (rs.next()) {
                    categorias.add(rs.getString("categoria"));
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al cargar categorías para inventario: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (categorias.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay categorías de producto registradas. Por favor, registre un producto primero usando 'Editar Producto'.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JComboBox<String> cmbCategoria = new JComboBox<>(categorias);
            JTextField txtCantidadToAdd = new JTextField();

            cmbCategoria.addActionListener(e -> {
                String selectedCategory = (String) cmbCategoria.getSelectedItem();
                if (selectedCategory != null && !selectedCategory.isEmpty()) {
                    try (Connection conn = ConexionDB.conectar()) {
                        PreparedStatement ps = conn.prepareStatement(
                            "SELECT i.cantidad FROM inventario i JOIN producto p ON i.id_producto = p.id_producto WHERE p.categoria=?");
                        ps.setString(1, selectedCategory);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            // CAMBIO: Mensaje de tooltip a "Unidades"
                            txtCantidadToAdd.setToolTipText("Stock actual: " + String.format("%.2f", rs.getFloat("cantidad")) + " unidades");
                        } else {
                            txtCantidadToAdd.setToolTipText("Stock actual: 0 unidades"); // CAMBIO: Mensaje de tooltip
                        }
                    } catch (SQLException ex) {
                        System.err.println("Error pre-filling inventory quantity: " + ex.getMessage());
                        txtCantidadToAdd.setToolTipText("Error al cargar stock actual");
                    }
                }
            });

            if (!categorias.isEmpty()) {
                cmbCategoria.setSelectedIndex(0); // Select the first category by default
                String selectedCategory = (String) cmbCategoria.getSelectedItem();
                if (selectedCategory != null && !selectedCategory.isEmpty()) {
                    try (Connection conn = ConexionDB.conectar()) {
                        PreparedStatement ps = conn.prepareStatement(
                            "SELECT i.cantidad FROM inventario i JOIN producto p ON i.id_producto = p.id_producto WHERE p.categoria=?");
                        ps.setString(1, selectedCategory);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            // CAMBIO: Mensaje de tooltip a "Unidades"
                            txtCantidadToAdd.setToolTipText("Stock actual: " + String.format("%.2f", rs.getFloat("cantidad")) + " unidades");
                        } else {
                            txtCantidadToAdd.setToolTipText("Stock actual: 0 unidades"); // CAMBIO: Mensaje de tooltip
                        }
                    } catch (SQLException ex) {
                        System.err.println("Error pre-filling inventory quantity: " + ex.getMessage());
                        txtCantidadToAdd.setToolTipText("Error al cargar stock actual");
                    }
                }
            }

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.add(new JLabel("Categoría:"));
            panel.add(cmbCategoria);
            // CAMBIO: Texto de la etiqueta a "Unidades"
            panel.add(new JLabel("Cantidad de Unidades a Agregar:"));
            panel.add(txtCantidadToAdd);

            int result = JOptionPane.showConfirmDialog(this, panel, "Agregar Cantidad al Inventario", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String categoria = (String) cmbCategoria.getSelectedItem();
                float cantidadToAdd;
                try {
                    cantidadToAdd = Float.parseFloat(txtCantidadToAdd.getText().trim());
                    if (cantidadToAdd < 0) throw new IllegalArgumentException("La cantidad a agregar no puede ser negativa.");
                } catch (NumberFormatException ex) {
                    // CAMBIO: Mensaje a "unidades"
                    JOptionPane.showMessageDialog(this, "Por favor, ingrese un valor numérico válido para la cantidad de unidades a agregar.", "Entrada Inválida", JOptionPane.WARNING_MESSAGE);
                    return;
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Entrada Inválida", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                try (Connection conn = ConexionDB.conectar()) {
                    PreparedStatement ps = conn.prepareStatement(
                        "SELECT id_producto, precio_unitario FROM producto WHERE categoria=?");
                    ps.setString(1, categoria);
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        int idProducto = rs.getInt("id_producto");
                        float precioUnitario = rs.getFloat("precio_unitario");

                        PreparedStatement psInv = conn.prepareStatement(
                            "SELECT id, cantidad FROM inventario WHERE id_producto=?");
                        psInv.setInt(1, idProducto);
                        ResultSet rsInv = psInv.executeQuery();

                        float currentQuantity = 0;
                        int idInventario = -1;

                        if (rsInv.next()) {
                            idInventario = rsInv.getInt("id");
                            currentQuantity = rsInv.getFloat("cantidad");
                        }

                        float newTotalQuantity = currentQuantity + cantidadToAdd;

                        if (idInventario != -1) {
                            PreparedStatement psUp = conn.prepareStatement(
                                "UPDATE inventario SET cantidad=?, precio_unitario=?, fecha_actualizacion=? WHERE id=?");
                            psUp.setFloat(1, newTotalQuantity);
                            psUp.setFloat(2, precioUnitario);
                            psUp.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                            psUp.setInt(4, idInventario);
                            psUp.executeUpdate();
                            // CAMBIO: Mensaje a "unidades"
                            JOptionPane.showMessageDialog(this, "Inventario actualizado correctamente (cantidad anterior: " + String.format("%.2f", currentQuantity) + ", agregada: " + String.format("%.2f", cantidadToAdd) + ", total: " + String.format("%.2f", newTotalQuantity) + " unidades).", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            PreparedStatement psIns = conn.prepareStatement(
                                "INSERT INTO inventario (id_producto, cantidad, precio_unitario, fecha_actualizacion) VALUES (?, ?, ?, ?)");
                            psIns.setInt(1, idProducto);
                            psIns.setFloat(2, newTotalQuantity);
                            psIns.setFloat(3, precioUnitario);
                            psIns.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                            psIns.executeUpdate();
                            JOptionPane.showMessageDialog(this, "Nuevo inventario agregado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        }
                        cargarDesdeBD(modelo, lblAlerta);
                    } else {
                        JOptionPane.showMessageDialog(this, "Primero debe registrar la categoría de este producto en 'Editar Producto'.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error de base de datos al guardar inventario: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error inesperado al guardar inventario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnEditar.addActionListener(_ -> {
            int fila = tabla.getSelectedRow();
            if (fila >= 0) {
                String categoria = modelo.getValueAt(fila, 1).toString(); // Categoría (index 1)
                // CAMBIO: Índice de Cantidad Unitario es 3 (antes 4 para Cantidad Maple)
                String cantidadUnitario = modelo.getValueAt(fila, 3).toString(); 
                // CAMBIO: Índice de Fecha es 4 (antes 5 para Fecha)
                String fechaActual = "";
                if (modelo.getValueAt(fila, 4) != null) { 
                    fechaActual = modelo.getValueAt(fila, 4).toString();
                }

                JTextField txtCantidad = new JTextField(cantidadUnitario); // CAMBIO: Usar cantidadUnitario
                JTextField txtFecha = new JTextField(fechaActual);

                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                // CAMBIO: Texto de la etiqueta a "Unidades"
                panel.add(new JLabel("Nueva Cantidad Total de Unidades:"));
                panel.add(txtCantidad);
                panel.add(new JLabel("Fecha (YYYY-MM-DD HH:MM:SS):"));
                panel.add(txtFecha);

                int result = JOptionPane.showConfirmDialog(this, panel, "Editar Inventario de " + categoria, JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    float nuevaCantidad;
                    Timestamp nuevaFecha;
                    try {
                        nuevaCantidad = Float.parseFloat(txtCantidad.getText().trim());
                        if (nuevaCantidad < 0) {
                            throw new IllegalArgumentException("La cantidad no puede ser negativa.");
                        }
                    } catch (NumberFormatException ex) {
                        // CAMBIO: Mensaje a "unidades"
                        JOptionPane.showMessageDialog(this, "La cantidad de unidades debe ser un número válido (ej. 100, 50.5).", "Entrada Inválida", JOptionPane.WARNING_MESSAGE);
                        return;
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Entrada Inválida", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    try {
                        nuevaFecha = Timestamp.valueOf(txtFecha.getText().trim());
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use YYYY-MM-DD HH:MM:SS (ej. 2023-10-27 15:30:00).", "Entrada Inválida", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    try (Connection conn = ConexionDB.conectar()) {
                        PreparedStatement ps = conn.prepareStatement(
                            "SELECT id_producto FROM producto WHERE categoria=?");
                        ps.setString(1, categoria);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            int idProducto = rs.getInt("id_producto");
                            PreparedStatement psInv = conn.prepareStatement(
                                "SELECT id FROM inventario WHERE id_producto=?");
                            psInv.setInt(1, idProducto);
                            ResultSet rsInv = psInv.executeQuery();
                            if (rsInv.next()) {
                                int idInventario = rsInv.getInt("id");
                                PreparedStatement psUp = conn.prepareStatement(
                                    "UPDATE inventario SET cantidad=?, fecha_actualizacion=? WHERE id=?");
                                psUp.setFloat(1, nuevaCantidad);
                                psUp.setTimestamp(2, nuevaFecha);
                                psUp.setInt(3, idInventario);
                                psUp.executeUpdate();
                                JOptionPane.showMessageDialog(this, "Inventario editado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                                cargarDesdeBD(modelo, lblAlerta);
                            } else {
                                JOptionPane.showMessageDialog(this, "No se encontró un registro de inventario para esta categoría.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "No se encontró el producto con la categoría seleccionada.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Error de base de datos al editar inventario: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Error inesperado al editar inventario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un registro de la tabla de inventario para editar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnActualizarPrecios.addActionListener(_ -> {
            int updatedCount = 0;
            Connection conn = null; // Declared here for explicit null check in finally
            try {
                conn = ConexionDB.conectar();
                if (conn == null) {
                    JOptionPane.showMessageDialog(this, "No se pudo establecer conexión con la base de datos.", "Error de Conexión", JOptionPane.ERROR_MESSAGE);
                    return; // Exit if connection is null
                }
                conn.setAutoCommit(false); 
                
                PreparedStatement psUpdateInventory = conn.prepareStatement(
                    "UPDATE inventario SET precio_unitario=?, fecha_actualizacion=? WHERE id_producto=?"
                );
                
                PreparedStatement psGetProductInfo = conn.prepareStatement(
                    "SELECT id_producto, precio_unitario FROM producto WHERE categoria=?"
                );

                for (int i = 0; i < modelo.getRowCount(); i++) {
                    String categoria = modelo.getValueAt(i, 1).toString(); // Categoría (index 1)

                    psGetProductInfo.setString(1, categoria);
                    ResultSet rsProductInfo = psGetProductInfo.executeQuery();

                    if (rsProductInfo.next()) {
                        int idProducto = rsProductInfo.getInt("id_producto");
                        float newPrecioUnitario = rsProductInfo.getFloat("precio_unitario");

                        psUpdateInventory.setFloat(1, newPrecioUnitario);
                        psUpdateInventory.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                        psUpdateInventory.setInt(3, idProducto);
                        psUpdateInventory.addBatch();
                        updatedCount++;
                    }
                    rsProductInfo.close();
                }
                
                if (updatedCount > 0) {
                    psUpdateInventory.executeBatch();
                    conn.commit();
                    JOptionPane.showMessageDialog(this, "Se actualizaron los precios de " + updatedCount + " productos en el inventario.", "Actualización Completa", JOptionPane.INFORMATION_MESSAGE);
                    cargarDesdeBD(modelo, lblAlerta);
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontraron productos para actualizar los precios o no se realizó ningún cambio.", "Información", JOptionPane.INFORMATION_MESSAGE);
                }

            } catch (SQLException ex) {
                try { 
                    if (conn != null) { // CAMBIO: Nested check for conn != null
                        conn.rollback(); 
                    }
                } catch (SQLException rbEx) {
                    System.err.println("Error durante el rollback: " + rbEx.getMessage());
                }
                JOptionPane.showMessageDialog(this, "Error al actualizar precios en el inventario: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error inesperado al actualizar precios: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    if (conn != null) { // Check if connection is not null
                        if (!conn.isClosed()) { // Only then check if it's not closed
                            conn.setAutoCommit(true); // Reset auto-commit mode before closing
                            conn.close();
                        } else {
                            System.out.println("Connection was already closed by other means in finally block (btnActualizarPrecios).");
                        }
                    } else {
                        System.out.println("Connection was null in finally block (btnActualizarPrecios). No need to close.");
                    }
                } catch (SQLException closeEx) {
                    System.err.println("Error al cerrar la conexión en finally (btnActualizarPrecios): " + closeEx.getMessage());
                }
            }
        });


        btnVolver.addActionListener(_ -> dispose());

        // Initial stock check
        verificarStock(modelo, lblAlerta);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void verificarStock(DefaultTableModel modelo, JLabel lblAlerta) {
        StringBuilder alerta = new StringBuilder();
        boolean hayBajoStock = false;
        for (int i = 0; i < modelo.getRowCount(); i++) {
            // CAMBIO: Índice de Cantidad Unitario es 3 (antes 4 para Cantidad Maple)
            Object cantidadObj = modelo.getValueAt(i, 3); 
            float cantidadUnitario = 0.0f; // CAMBIO: Renombrar variable
            try {
                cantidadUnitario = Float.parseFloat(cantidadObj.toString());
            } catch (NumberFormatException e) {
                System.err.println("Error al parsear cantidad de unitario en la fila " + i + ": " + cantidadObj); // CAMBIO: Mensaje
            }
            
            if (cantidadUnitario < 100) { // El umbral de 100 se mantiene, asumiendo que 100 unidades es el nuevo umbral bajo
                // CAMBIO: Mensaje a "Unidades restantes"
                alerta.append("¡Atención! Bajo stock en: ").append(modelo.getValueAt(i, 1)).append(" (Unidades restantes: ").append(String.format("%.2f", cantidadUnitario)).append(").  ");
                hayBajoStock = true;
            }
        }
        lblAlerta.setText(alerta.toString());
        if (hayBajoStock) {
            lblAlerta.setForeground(Color.RED);
            lblAlerta.setBackground(new Color(255, 230, 230));
            Border redBorder = BorderFactory.createLineBorder(Color.RED, 2);
            Border emptyBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
            lblAlerta.setBorder(BorderFactory.createCompoundBorder(redBorder, emptyBorder));
        } else {
            lblAlerta.setForeground(Color.BLACK);
            lblAlerta.setBackground(getBackground());
            lblAlerta.setBorder(null);
        }
    }

    private void cargarDesdeBD(DefaultTableModel modelo, JLabel lblAlerta) {
        modelo.setRowCount(0);
        // CAMBIO: Eliminado i.precio_maple de la consulta SQL
        String sql = "SELECT i.id, p.categoria, i.precio_unitario, i.cantidad, i.fecha_actualizacion " +
                     "FROM inventario i JOIN producto p ON i.id_producto = p.id_producto ORDER BY p.categoria";
        try (Connection conn = ConexionDB.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("categoria"),
                    String.format("%.2f", rs.getFloat("precio_unitario")),
                    rs.getFloat("cantidad"), // Esta es la cantidad de unidades
                    rs.getTimestamp("fecha_actualizacion") != null ? rs.getTimestamp("fecha_actualizacion").toString() : ""
                });
            }
            verificarStock(modelo, lblAlerta);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar inventario: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inesperado al cargar inventario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}