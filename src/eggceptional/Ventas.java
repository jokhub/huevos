package eggceptional;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Ventas extends JFrame {

    // --- Componentes de la Interfaz de Ventas ---
    private DefaultTableModel modeloDetalleVenta;
    private JComboBox<String> cbCategoria;
    private JTextField txtCantidad, txtPrecioUnitario, txtTotalVenta;
    private JLabel lblClienteInfoDisplay;
    private JLabel lblPreventistaInfoDisplay; // NUEVO: Para mostrar el preventista seleccionado
    private JButton btnAgregar, btnFinalizar;

    // --- Componentes para la Gestión de Clientes ---
    private JTextField txtDniBusquedaCliente;
    private JButton btnBuscarCliente, btnRegistrarClienteNuevo, btnSeleccionarCliente;
    private JTable tablaClientesBusqueda;
    private DefaultTableModel modeloTablaClientesBusqueda;

    // --- Componentes para la Gestión de Preventistas (Nuevos) ---
    private JTextField txtNombreApellidoPreventistaBusqueda;
    private JButton btnBuscarPreventista, btnSeleccionarPreventista;
    private JTable tablaPreventistasBusqueda;
    private DefaultTableModel modeloTablaPreventistasBusqueda;

    // --- Datos de la Venta y Cliente/Preventista ---
    private int idClienteSeleccionado = -1;
    private String nombreClienteSeleccionado = "";
    private String direccionClienteSeleccionado = "";

    private int idPreventistaSeleccionado = -1; // NUEVO: Para almacenar el ID del preventista
    private String nombrePreventistaSeleccionado = ""; // NUEVO: Para almacenar el nombre del preventista

    private Map<String, Integer> mapaProductos;

    public Ventas() {
        setTitle("Registro de Ventas");
        setSize(1000, 750); // CAMBIO: Aumentado el tamaño nuevamente
        setResizable(true);
        setLayout(null);
        setLocationRelativeTo(null);

        mapaProductos = new HashMap<>();
        cargarMapaProductos();

        // --- Panel de Gestión de Preventistas (NUEVO PANEL) ---
        JPanel panelGestionPreventista = new JPanel();
        panelGestionPreventista.setBorder(BorderFactory.createTitledBorder("Selección de Preventista"));
        panelGestionPreventista.setLayout(null);
        panelGestionPreventista.setBounds(20, 20, 950, 150); // Posición y tamaño
        add(panelGestionPreventista);

        JLabel lblNombrePreventista = new JLabel("Preventista (Nombre/Apellido):");
        lblNombrePreventista.setBounds(10, 25, 180, 25);
        panelGestionPreventista.add(lblNombrePreventista);

        txtNombreApellidoPreventistaBusqueda = new JTextField();
        txtNombreApellidoPreventistaBusqueda.setBounds(190, 25, 200, 25);
        panelGestionPreventista.add(txtNombreApellidoPreventistaBusqueda);

        btnBuscarPreventista = new JButton("Buscar Preventista");
        btnBuscarPreventista.setBounds(400, 25, 150, 25);
        panelGestionPreventista.add(btnBuscarPreventista);

        lblPreventistaInfoDisplay = new JLabel("Preventista: Ninguno seleccionado");
        lblPreventistaInfoDisplay.setBounds(560, 25, 350, 25);
        panelGestionPreventista.add(lblPreventistaInfoDisplay);

        String[] columnasPreventistas = {"ID", "Nombre", "Apellido", "DNI", "Teléfono"};
        modeloTablaPreventistasBusqueda = new DefaultTableModel(columnasPreventistas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaPreventistasBusqueda = new JTable(modeloTablaPreventistasBusqueda);
        tablaPreventistasBusqueda.removeColumn(tablaPreventistasBusqueda.getColumnModel().getColumn(0)); // Ocultar ID
        JScrollPane scrollPreventistas = new JScrollPane(tablaPreventistasBusqueda);
        scrollPreventistas.setBounds(10, 60, 930, 50); // Tabla más pequeña
        panelGestionPreventista.add(scrollPreventistas);

        btnSeleccionarPreventista = new JButton("Seleccionar Preventista de la Tabla");
        btnSeleccionarPreventista.setBounds(350, 115, 250, 25);
        btnSeleccionarPreventista.setEnabled(false);
        panelGestionPreventista.add(btnSeleccionarPreventista);


        // --- Panel de Gestión de Cliente (Reajustado su posición) ---
        JPanel panelGestionCliente = new JPanel();
        panelGestionCliente.setBorder(BorderFactory.createTitledBorder("Gestión y Selección de Cliente"));
        panelGestionCliente.setLayout(null);
        panelGestionCliente.setBounds(20, 180, 950, 200); // CAMBIO: Nueva posición
        add(panelGestionCliente);

        JLabel lblDniBusqueda = new JLabel("DNI Cliente:");
        lblDniBusqueda.setBounds(10, 25, 80, 25);
        panelGestionCliente.add(lblDniBusqueda);

        txtDniBusquedaCliente = new JTextField();
        txtDniBusquedaCliente.setBounds(90, 25, 150, 25);
        panelGestionCliente.add(txtDniBusquedaCliente);

        btnBuscarCliente = new JButton("Buscar Cliente");
        btnBuscarCliente.setBounds(250, 25, 130, 25);
        panelGestionCliente.add(btnBuscarCliente);

        btnRegistrarClienteNuevo = new JButton("Registrar Nuevo");
        btnRegistrarClienteNuevo.setBounds(390, 25, 150, 25);
        panelGestionCliente.add(btnRegistrarClienteNuevo);

        lblClienteInfoDisplay = new JLabel("Cliente Actual: Ninguno seleccionado");
        lblClienteInfoDisplay.setBounds(550, 25, 350, 25);
        panelGestionCliente.add(lblClienteInfoDisplay);

        String[] columnasClientes = {"ID", "DNI", "Nombre", "Apellido", "Teléfono", "Correo", "Dirección"};
        modeloTablaClientesBusqueda = new DefaultTableModel(columnasClientes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaClientesBusqueda = new JTable(modeloTablaClientesBusqueda);
        tablaClientesBusqueda.removeColumn(tablaClientesBusqueda.getColumnModel().getColumn(0));
        JScrollPane scrollClientes = new JScrollPane(tablaClientesBusqueda);
        scrollClientes.setBounds(10, 60, 930, 90); // Ancho ajustado
        panelGestionCliente.add(scrollClientes);

        btnSeleccionarCliente = new JButton("Seleccionar Cliente de la Tabla");
        btnSeleccionarCliente.setBounds(350, 160, 250, 25); // Posición ajustada
        btnSeleccionarCliente.setEnabled(false);
        panelGestionCliente.add(btnSeleccionarCliente);


        // --- Panel para Agregar Productos (Reajustado su posición) ---
        JPanel panelAgregar = new JPanel();
        panelAgregar.setBorder(BorderFactory.createTitledBorder("Agregar Producto a la Venta"));
        panelAgregar.setLayout(null);
        panelAgregar.setBounds(20, 390, 950, 70); // CAMBIO: Nueva posición
        add(panelAgregar);

        JLabel lblCategoria = new JLabel("Categoría:");
        lblCategoria.setBounds(10, 30, 80, 25);
        panelAgregar.add(lblCategoria);

        cbCategoria = new JComboBox<>(new Vector<>(mapaProductos.keySet()));
        cbCategoria.setBounds(80, 30, 120, 25);
        panelAgregar.add(cbCategoria);

        JLabel lblCantidad = new JLabel("Cantidad Unitaria:");
        lblCantidad.setBounds(220, 30, 120, 25);
        panelAgregar.add(lblCantidad);

        txtCantidad = new JTextField();
        txtCantidad.setBounds(340, 30, 60, 25);
        panelAgregar.add(txtCantidad);

        JLabel lblPrecio = new JLabel("Precio Unitario:");
        lblPrecio.setBounds(420, 30, 100, 25);
        panelAgregar.add(lblPrecio);

        txtPrecioUnitario = new JTextField();
        txtPrecioUnitario.setBounds(520, 30, 80, 25);
        txtPrecioUnitario.setEditable(false);
        panelAgregar.add(txtPrecioUnitario);

        btnAgregar = new JButton("Agregar Producto");
        btnAgregar.setBounds(650, 30, 180, 25);
        panelAgregar.add(btnAgregar);

        // --- Tabla de Detalles de la Venta (Reajustada su posición) ---
        String[] columnasDetalleVenta = {"ID Producto", "Categoría", "Cantidad", "Precio Unitario", "Subtotal"};
        modeloDetalleVenta = new DefaultTableModel(columnasDetalleVenta, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tablaVentas = new JTable(modeloDetalleVenta);
        tablaVentas.removeColumn(tablaVentas.getColumnModel().getColumn(0));
        
        JScrollPane scrollTablaVentas = new JScrollPane(tablaVentas);
        scrollTablaVentas.setBounds(20, 470, 950, 150); // CAMBIO: Nueva posición y ancho
        add(scrollTablaVentas);

        // --- Total y Botones Finales (Reajustados sus posiciones) ---
        JLabel lblTotal = new JLabel("Total Venta:");
        lblTotal.setBounds(700, 630, 80, 25); // CAMBIO: Nueva posición
        add(lblTotal);

        txtTotalVenta = new JTextField("0.00");
        txtTotalVenta.setBounds(780, 630, 190, 25); // CAMBIO: Nueva posición
        txtTotalVenta.setEditable(false);
        add(txtTotalVenta);

        btnFinalizar = new JButton("Finalizar Venta");
        btnFinalizar.setBounds(780, 670, 190, 30); // CAMBIO: Nueva posición
        add(btnFinalizar);
        
        JButton btnCancelar = new JButton("Cancelar Venta");
        btnCancelar.setBounds(590, 670, 160, 30); // CAMBIO: Nueva posición
        add(btnCancelar);


        // --- Lógica de Eventos ---

        // Autocompletar precio al cambiar categoría de producto
        cbCategoria.addActionListener(e -> actualizarPrecioUnitario());
        actualizarPrecioUnitario();

        // Acciones de botones de gestión de preventista
        btnBuscarPreventista.addActionListener(e -> buscarPreventistaInterno());
        btnSeleccionarPreventista.addActionListener(e -> seleccionarPreventistaDesdeTabla());

        // Habilitar/Deshabilitar botón de selección de preventista
        tablaPreventistasBusqueda.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaPreventistasBusqueda.getSelectedRow() != -1) {
                btnSeleccionarPreventista.setEnabled(true);
            } else {
                btnSeleccionarPreventista.setEnabled(false);
            }
        });

        // Doble clic en la tabla de preventistas para seleccionar
        tablaPreventistasBusqueda.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tablaPreventistasBusqueda.getSelectedRow() != -1) {
                    seleccionarPreventistaDesdeTabla();
                }
            }
        });

        // Acciones de botones de gestión de cliente
        btnBuscarCliente.addActionListener(e -> buscarClienteInterno());
        btnRegistrarClienteNuevo.addActionListener(e -> registrarNuevoClienteInterno(""));
        btnSeleccionarCliente.addActionListener(e -> seleccionarClienteDesdeTabla());

        // Habilitar/Deshabilitar botón de selección de cliente
        tablaClientesBusqueda.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaClientesBusqueda.getSelectedRow() != -1) {
                btnSeleccionarCliente.setEnabled(true);
            } else {
                btnSeleccionarCliente.setEnabled(false);
            }
        });

        // Doble clic en la tabla de clientes para seleccionar
        tablaClientesBusqueda.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tablaClientesBusqueda.getSelectedRow() != -1) {
                    seleccionarClienteDesdeTabla();
                }
            }
        });


        // Acciones de botones de venta
        btnAgregar.addActionListener(e -> agregarProductoATabla());
        btnFinalizar.addActionListener(e -> finalizarVenta());
        btnCancelar.addActionListener(e -> limpiarFormularioCompleto());

        // Inicializar con preventista y cliente limpios
        limpiarDatosPreventista();
        limpiarDatosCliente();

        setVisible(true);
    }

    // --- Métodos de Gestión de Preventistas (Nuevos) ---
    private void buscarPreventistaInterno() {
        String busqueda = txtNombreApellidoPreventistaBusqueda.getText().trim();
        if (busqueda.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un nombre o apellido para buscar preventistas.", "Campo Vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        modeloTablaPreventistasBusqueda.setRowCount(0); // Limpia la tabla de preventistas

        // Consulta para buscar preventistas por nombre o apellido
        // Asume que tienes una columna 'rol' en tu tabla 'empleados' y buscas 'Preventista'
        String sql = "SELECT id_empleados, nombre, apellido, dni, telefono FROM empleados WHERE (LOWER(nombre) LIKE ? OR LOWER(apellido) LIKE ?) AND LOWER(rol) = 'preventista'";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + busqueda.toLowerCase() + "%");
            ps.setString(2, "%" + busqueda.toLowerCase() + "%");
            ResultSet rs = ps.executeQuery();

            boolean found = false;
            while (rs.next()) {
                found = true;
                modeloTablaPreventistasBusqueda.addRow(new Object[]{
                    rs.getInt("id_empleados"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("dni"),
                    rs.getString("telefono")
                });
            }

            if (!found) {
                JOptionPane.showMessageDialog(this, "No se encontraron preventistas con ese nombre/apellido. Asegúrese de que el rol sea 'Preventista'.", "No Encontrado", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Preventistas encontrados. Selecciónelos de la tabla.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                if (modeloTablaPreventistasBusqueda.getRowCount() == 1) {
                    tablaPreventistasBusqueda.setRowSelectionInterval(0, 0); // Selecciona si solo hay uno
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar preventista: " + ex.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void seleccionarPreventistaDesdeTabla() {
        int selectedRow = tablaPreventistasBusqueda.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) modeloTablaPreventistasBusqueda.getValueAt(selectedRow, 0);
            String nombre = (String) modeloTablaPreventistasBusqueda.getValueAt(selectedRow, 1);
            String apellido = (String) modeloTablaPreventistasBusqueda.getValueAt(selectedRow, 2);

            this.idPreventistaSeleccionado = id;
            this.nombrePreventistaSeleccionado = nombre + " " + apellido;
            lblPreventistaInfoDisplay.setText("Preventista: ID " + idPreventistaSeleccionado + " - " + nombrePreventistaSeleccionado);
            JOptionPane.showMessageDialog(this, "Preventista '" + nombrePreventistaSeleccionado + "' seleccionado para la venta.", "Preventista Seleccionado", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un preventista de la tabla de búsqueda.", "Ningún Preventista Seleccionado", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void limpiarDatosPreventista() {
        this.idPreventistaSeleccionado = -1;
        this.nombrePreventistaSeleccionado = "";
        lblPreventistaInfoDisplay.setText("Preventista: Ninguno seleccionado");
        txtNombreApellidoPreventistaBusqueda.setText("");
        modeloTablaPreventistasBusqueda.setRowCount(0);
    }


    // --- Métodos de Gestión de Cliente (Originales, sin cambios de lógica) ---

    private void buscarClienteInterno() {
        String dni = txtDniBusquedaCliente.getText().trim();
        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un DNI para buscar.", "Campo Vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!dni.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "El DNI debe contener solo números.", "Entrada Inválida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        modeloTablaClientesBusqueda.setRowCount(0);

        String sql = "SELECT id, dni, nombre, apellido, telefono, email, direccion FROM clientes WHERE dni = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dni);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                modeloTablaClientesBusqueda.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("dni"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("telefono"),
                    rs.getString("email"),
                    rs.getString("direccion")
                });
                JOptionPane.showMessageDialog(this, "Cliente encontrado. Selecciónelo en la tabla para la venta.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                if (modeloTablaClientesBusqueda.getRowCount() == 1) {
                    tablaClientesBusqueda.setRowSelectionInterval(0, 0);
                }
            } else {
                int option = JOptionPane.showConfirmDialog(this,
                        "Cliente con DNI '" + dni + "' no encontrado.\n¿Desea registrar un nuevo cliente?",
                        "Cliente No Encontrado", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    registrarNuevoClienteInterno(dni);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar cliente: " + ex.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void registrarNuevoClienteInterno(String dniPrellenado) {
        JTextField txtDNI = new JTextField(dniPrellenado);
        JTextField txtNombre = new JTextField();
        JTextField txtApellido = new JTextField();
        JTextField txtTelefono = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtDireccion = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
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

        int result = JOptionPane.showConfirmDialog(this, panel, "Registrar Nuevo Cliente", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String nuevoDni = txtDNI.getText().trim();
            String nuevoNombre = txtNombre.getText().trim();
            String nuevoApellido = txtApellido.getText().trim();
            String nuevoTelefono = txtTelefono.getText().trim();
            String nuevoEmail = txtEmail.getText().trim();
            String nuevoDireccion = txtDireccion.getText().trim();

            if (nuevoDni.isEmpty() || nuevoNombre.isEmpty() || nuevoApellido.isEmpty()) {
                JOptionPane.showMessageDialog(this, "DNI, Nombre y Apellido son obligatorios.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!nuevoDni.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "El DNI debe contener solo números.", "Entrada Inválida", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (clienteExisteEnDB(nuevoDni)) {
                JOptionPane.showMessageDialog(this, "Error: El DNI '" + nuevoDni + "' ya existe para otro cliente.", "DNI Duplicado", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sqlInsert = "INSERT INTO clientes (dni, nombre, apellido, telefono, email, direccion) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
            try (Connection conn = ConexionDB.conectar();
                 PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                ps.setString(1, nuevoDni);
                ps.setString(2, nuevoNombre);
                ps.setString(3, nuevoApellido);
                ps.setString(4, nuevoTelefono);
                ps.setString(5, nuevoEmail);
                ps.setString(6, nuevoDireccion);

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int idRecienCreado = rs.getInt(1);
                    JOptionPane.showMessageDialog(this, "Cliente registrado exitosamente. Seleccionando para la venta...", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    actualizarClienteSeleccionado(idRecienCreado, nuevoNombre + " " + nuevoApellido, nuevoDireccion);
                    
                    modeloTablaClientesBusqueda.setRowCount(0);
                    modeloTablaClientesBusqueda.addRow(new Object[]{
                        idRecienCreado, nuevoDni, nuevoNombre, nuevoApellido, nuevoTelefono, nuevoEmail, nuevoDireccion
                    });
                    tablaClientesBusqueda.setRowSelectionInterval(0, 0);
                    txtDniBusquedaCliente.setText(nuevoDni);
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo obtener el ID del cliente registrado.", "Error de Registro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                if (ex.getMessage().contains("duplicate key value violates unique constraint") || ex.getSQLState().startsWith("23")) {
                    JOptionPane.showMessageDialog(this, "Error: El DNI '" + nuevoDni + "' ya existe para otro cliente.", "DNI Duplicado", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Error al registrar cliente: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
                }
                ex.printStackTrace();
            }
        }
    }

    private boolean clienteExisteEnDB(String dni) {
        String sql = "SELECT COUNT(*) FROM clientes WHERE dni = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dni);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            System.err.println("Error al verificar existencia de DNI: " + ex.getMessage());
            ex.printStackTrace();
        }
        return false;
    }

    private void seleccionarClienteDesdeTabla() {
        int selectedRow = tablaClientesBusqueda.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) modeloTablaClientesBusqueda.getValueAt(selectedRow, 0);
            String nombre = (String) modeloTablaClientesBusqueda.getValueAt(selectedRow, 2);
            String apellido = (String) modeloTablaClientesBusqueda.getValueAt(selectedRow, 3);
            String direccion = (String) modeloTablaClientesBusqueda.getValueAt(selectedRow, 6);

            actualizarClienteSeleccionado(id, nombre + " " + apellido, direccion);
            JOptionPane.showMessageDialog(this, "Cliente '" + nombre + " " + apellido + "' seleccionado para la venta.", "Cliente Seleccionado", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un cliente de la tabla de búsqueda.", "Ningún Cliente Seleccionado", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void actualizarClienteSeleccionado(int id, String nombreCompleto, String direccion) {
        this.idClienteSeleccionado = id;
        this.nombreClienteSeleccionado = nombreCompleto;
        this.direccionClienteSeleccionado = direccion;
        lblClienteInfoDisplay.setText("Cliente Actual: ID " + idClienteSeleccionado + " - " + nombreClienteSeleccionado);
    }

    // --- Métodos de Lógica de Ventas (Originales, con un ajuste para preventista) ---

    private void cargarMapaProductos() {
        String sql = "SELECT id_producto, categoria FROM producto ORDER BY categoria";
        try (Connection conn = ConexionDB.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                mapaProductos.put(rs.getString("categoria"), rs.getInt("id_producto"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error fatal al cargar categorías de productos.", "Error de Conexión", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void actualizarPrecioUnitario() {
        String categoria = (String) cbCategoria.getSelectedItem();
        if (categoria == null) return;

        String sql = "SELECT precio_unitario FROM producto WHERE categoria = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categoria);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtPrecioUnitario.setText(String.format("%.2f", rs.getFloat("precio_unitario")));
            } else {
                txtPrecioUnitario.setText("0.00");
            }
        } catch (Exception ex) {
            txtPrecioUnitario.setText("Error");
            ex.printStackTrace();
        }
    }
    
    private void agregarProductoATabla() {
        if (idClienteSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un cliente primero.", "Cliente no seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (txtCantidad.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar una cantidad.", "Campo Vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String categoria = (String) cbCategoria.getSelectedItem();
        int idProducto = mapaProductos.get(categoria);
        int cantidad;
        float precioUnitario;

        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
            precioUnitario = Float.parseFloat(txtPrecioUnitario.getText().replace(",","."));
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a cero.", "Dato Inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un número entero válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String sqlCheckStock = "SELECT cantidad FROM inventario WHERE id_producto = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sqlCheckStock)) {
            ps.setInt(1, idProducto);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                float stockActual = rs.getFloat("cantidad");
                if (cantidad > stockActual) {
                    JOptionPane.showMessageDialog(this, "Stock insuficiente para '" + categoria + "'.\nDisponible: " + stockActual + ", Solicitado: " + cantidad, "Stock Insuficiente", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } else {
                JOptionPane.showMessageDialog(this, "El producto '" + categoria + "' no tiene registro en el inventario.", "Producto sin Stock", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al verificar stock: " + ex.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
            return;
        }

        float subtotal = cantidad * precioUnitario;
        modeloDetalleVenta.addRow(new Object[]{
            idProducto,
            categoria,
            cantidad,
            String.format("%.2f", precioUnitario),
            String.format("%.2f", subtotal)
        });

        actualizarTotalVenta();
        txtCantidad.setText("");
    }
    
    private void finalizarVenta() {
        if (idPreventistaSeleccionado == -1) { // NUEVA VALIDACIÓN
            JOptionPane.showMessageDialog(this, "Por favor, seleccione el preventista que realiza la venta.", "Preventista No Seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (idClienteSeleccionado == -1) {
             JOptionPane.showMessageDialog(this, "Por favor, seleccione un cliente antes de finalizar la venta.", "Cliente No Seleccionado", JOptionPane.WARNING_MESSAGE);
             return;
        }
        if (modeloDetalleVenta.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay productos en la venta.", "Venta Vacía", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Connection conn = null;
        try {
            conn = ConexionDB.conectar();
            conn.setAutoCommit(false); // Iniciar transacción

            String sqlVenta = "INSERT INTO venta (id_empleados, id_cliente, fecha_emicion, total_venta) VALUES (?, ?, ?, ?) RETURNING id";
            int idVentaGenerado;
            try (PreparedStatement psVenta = conn.prepareStatement(sqlVenta)) {
                psVenta.setInt(1, this.idPreventistaSeleccionado); // CAMBIO: Usar el ID del preventista seleccionado
                psVenta.setInt(2, this.idClienteSeleccionado);
                psVenta.setDate(3, Date.valueOf(LocalDate.now()));
                psVenta.setFloat(4, Float.parseFloat(txtTotalVenta.getText().replace(",",".")));
                
                ResultSet rs = psVenta.executeQuery();
                if (rs.next()) {
                    idVentaGenerado = rs.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID de la venta generada.");
                }
            }

            String sqlDetalle = "INSERT INTO detalle_venta (id_venta, id_producto, categoria, cantidad, precio, direccion) VALUES (?, ?, ?, ?, ?, ?)";
            String sqlUpdateInventario = "UPDATE inventario SET cantidad = cantidad - ? WHERE id_producto = ?";
            
            try (PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle);
                 PreparedStatement psUpdate = conn.prepareStatement(sqlUpdateInventario)) {

                for (int i = 0; i < modeloDetalleVenta.getRowCount(); i++) {
                    int idProducto = (int) modeloDetalleVenta.getValueAt(i, 0);
                    String categoria = (String) modeloDetalleVenta.getValueAt(i, 1);
                    int cantidad = (int) modeloDetalleVenta.getValueAt(i, 2);
                    float precio = Float.parseFloat(((String) modeloDetalleVenta.getValueAt(i, 3)).replace(",","."));

                    psDetalle.setInt(1, idVentaGenerado);
                    psDetalle.setInt(2, idProducto);
                    psDetalle.setString(3, categoria);
                    psDetalle.setInt(4, cantidad);
                    psDetalle.setFloat(5, precio);
                    psDetalle.setString(6, this.direccionClienteSeleccionado);
                    psDetalle.addBatch();

                    psUpdate.setInt(1, cantidad);
                    psUpdate.setInt(2, idProducto);
                    psUpdate.addBatch();
                }
                
                psDetalle.executeBatch();
                psUpdate.executeBatch();
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "Venta registrada y stock actualizado exitosamente.", "Venta Exitosa", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormularioCompleto();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error de base de datos. Se revertirán los cambios.\nError: " + ex.getMessage(), "Error en Transacción", JOptionPane.ERROR_MESSAGE);
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rbEx) {
                System.err.println("Error crítico al intentar hacer rollback: " + rbEx.getMessage());
            }
            ex.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException clEx) {
                clEx.printStackTrace();
            }
        }
    }

    private void actualizarTotalVenta() {
        double total = 0.0;
        for (int i = 0; i < modeloDetalleVenta.getRowCount(); i++) {
            total += Float.parseFloat(((String) modeloDetalleVenta.getValueAt(i, 4)).replace(",", "."));
        }
        txtTotalVenta.setText(String.format("%.2f", total));
    }
    
    private void limpiarDatosCliente() {
        this.idClienteSeleccionado = -1;
        this.nombreClienteSeleccionado = "";
        this.direccionClienteSeleccionado = "";
        lblClienteInfoDisplay.setText("Cliente Actual: Ninguno seleccionado");
        txtDniBusquedaCliente.setText("");
        modeloTablaClientesBusqueda.setRowCount(0);
    }

    private void limpiarFormularioCompleto() {
        limpiarDatosPreventista(); // Limpiar también el preventista
        limpiarDatosCliente();
        modeloDetalleVenta.setRowCount(0);
        txtCantidad.setText("");
        actualizarTotalVenta();
    }
}