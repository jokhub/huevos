package eggceptional;
import javax.swing.*;

public class Reportes extends JFrame {
    public Reportes() {
        setTitle("Reportes");
        setSize(500, 400);
        setResizable(true); // Permite agrandar la ventana, pero los componentes no cambian de tamaño
        setLayout(null);    // Layout absoluto

        // 1. Botón para volver atrás (al final del constructor)
        // 2. Selección de tipo de reporte
        JLabel lblTipo = new JLabel("Tipo de reporte:");
        lblTipo.setBounds(30, 20, 120, 25);
        add(lblTipo);

        JComboBox<String> comboTipo = new JComboBox<>(new String[] { "Ventas", "Usuarios", "Inventario" });
        comboTipo.setBounds(160, 20, 200, 25);
        add(comboTipo);

        // 3. Rango de fechas
        JLabel lblFechaInicio = new JLabel("Fecha inicio:");
        lblFechaInicio.setBounds(30, 60, 120, 25);
        add(lblFechaInicio);

        JTextField txtFechaInicio = new JTextField("YYYY-MM-DD");
        txtFechaInicio.setBounds(160, 60, 200, 25);
        add(txtFechaInicio);

        JLabel lblFechaFin = new JLabel("Fecha fin:");
        lblFechaFin.setBounds(30, 100, 120, 25);
        add(lblFechaFin);

        JTextField txtFechaFin = new JTextField("YYYY-MM-DD");
        txtFechaFin.setBounds(160, 100, 200, 25);
        add(txtFechaFin);

        // 7. Filtros adicionales (ejemplo: usuario)
        JLabel lblFiltroUsuario = new JLabel("Usuario:");
        lblFiltroUsuario.setBounds(30, 140, 120, 25);
        add(lblFiltroUsuario);

        JTextField txtFiltroUsuario = new JTextField();
        txtFiltroUsuario.setBounds(160, 140, 200, 25);
        add(txtFiltroUsuario);

        // 4. Botón "Generar Reporte"
        JButton btnGenerar = new JButton("Generar");
        btnGenerar.setBounds(80, 190, 120, 30);
        add(btnGenerar);

        // 5. Área de resultados
        JTextArea areaResultados = new JTextArea();
        areaResultados.setBounds(30, 240, 420, 60);
        areaResultados.setEditable(false);
        add(areaResultados);

        // 6. Botón "Exportar a PDF/Excel" (solo simulado)
        JButton btnExportar = new JButton("Exportar PDF");
        btnExportar.setBounds(220, 190, 120, 30);
        add(btnExportar);

        // 1. Botón para volver atrás
        JButton btnVolver = new JButton("Volver");
        btnVolver.setBounds(360, 190, 90, 30);
        add(btnVolver);

        // Acción para generar reporte
        btnGenerar.addActionListener(_ -> {
            String tipo = comboTipo.getSelectedItem().toString();
            String inicio = txtFechaInicio.getText();
            String fin = txtFechaFin.getText();
            String usuario = txtFiltroUsuario.getText();
            areaResultados.setText("Reporte: " + tipo +
                "\nDesde: " + inicio +
                " Hasta: " + fin +
                "\nUsuario: " + usuario +
                "\n(Datos simulados)");
        });

        // Acción para exportar (simulada)
        btnExportar.addActionListener(_ -> {
            JOptionPane.showMessageDialog(this, "Reporte exportado a PDF (simulado)");
        });

        // Acción para volver atrás
        btnVolver.addActionListener(_ -> dispose());

        setLocationRelativeTo(null);
        setVisible(true);
    }
}