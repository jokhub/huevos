package eggceptional;
import javax.swing.*;
import java.sql.*;
import java.util.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import java.awt.BorderLayout;
import java.io.FileOutputStream;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;
import org.jfree.chart.ChartUtilities;

// Si tienes iText, descomenta:
// import com.itextpdf.text.*;
// import com.itextpdf.text.pdf.*;

public class Reportes extends JFrame {
    private JPanel panelGrafica;
    private JFreeChart lastChart = null;
    private JTable tablaResultados;
    private JScrollPane scrollTabla;
    // Para fechas
    private JTextField txtFechaInicio, txtFechaFin;
    // Si tienes JCalendar, puedes usar:
    // private JDateChooser dateChooserInicio, dateChooserFin;

    public Reportes() {
        setTitle("Reportes");
        setSize(600, 600);
        setResizable(true);
        setLayout(null);

        JLabel lblTipo = new JLabel("Tipo de reporte:");
        lblTipo.setBounds(30, 20, 120, 25);
        add(lblTipo);

        JComboBox<String> comboTipo = new JComboBox<>(new String[] { "Ventas", "Usuarios", "Inventario" });
        comboTipo.setBounds(160, 20, 200, 25);
        add(comboTipo);

        JLabel lblFechaInicio = new JLabel("Fecha inicio:");
        lblFechaInicio.setBounds(30, 60, 120, 25);
        add(lblFechaInicio);

        txtFechaInicio = new JTextField("YYYY-MM-DD");
        txtFechaInicio.setBounds(160, 60, 120, 25);
        add(txtFechaInicio);
        // Si tienes JCalendar, usa esto en vez del JTextField:
        // dateChooserInicio = new JDateChooser();
        // dateChooserInicio.setBounds(160, 60, 120, 25);
        // add(dateChooserInicio);

        JLabel lblFechaFin = new JLabel("Fecha fin:");
        lblFechaFin.setBounds(300, 60, 120, 25);
        add(lblFechaFin);

        txtFechaFin = new JTextField("YYYY-MM-DD");
        txtFechaFin.setBounds(400, 60, 120, 25);
        add(txtFechaFin);
        // Si tienes JCalendar, usa esto en vez del JTextField:
        // dateChooserFin = new JDateChooser();
        // dateChooserFin.setBounds(400, 60, 120, 25);
        // add(dateChooserFin);

        JLabel lblFiltroUsuario = new JLabel("Usuario:");
        lblFiltroUsuario.setBounds(30, 100, 120, 25);
        add(lblFiltroUsuario);

        JTextField txtFiltroUsuario = new JTextField();
        txtFiltroUsuario.setBounds(160, 100, 200, 25);
        add(txtFiltroUsuario);

        JButton btnGenerar = new JButton("Generar");
        btnGenerar.setBounds(80, 150, 120, 30);
        add(btnGenerar);

        JButton btnExportar = new JButton("Exportar PDF");
        btnExportar.setBounds(220, 150, 120, 30);
        add(btnExportar);

        JButton btnVolver = new JButton("Volver");
        btnVolver.setBounds(360, 150, 90, 30);
        add(btnVolver);

        // Tabla de resultados
        tablaResultados = new JTable();
        scrollTabla = new JScrollPane(tablaResultados);
        scrollTabla.setBounds(30, 200, 520, 120);
        add(scrollTabla);

        // Panel para la gráfica
        panelGrafica = new JPanel(new BorderLayout());
        panelGrafica.setBounds(30, 340, 520, 200);
        add(panelGrafica);

        btnGenerar.addActionListener(_ -> {
            String tipo = comboTipo.getSelectedItem().toString();
            String inicio = txtFechaInicio.getText();
            String fin = txtFechaFin.getText();
            // Si tienes JCalendar, usa:
            // String inicio = ((JTextField)dateChooserInicio.getDateEditor().getUiComponent()).getText();
            // String fin = ((JTextField)dateChooserFin.getDateEditor().getUiComponent()).getText();
            String usuario = txtFiltroUsuario.getText();
            panelGrafica.removeAll();
            if (tipo.equals("Ventas")) {
                generarReporteVentas(inicio, fin, usuario);
            } else if (tipo.equals("Usuarios")) {
                generarReporteUsuarios();
            } else if (tipo.equals("Inventario")) {
                generarReporteInventario();
            }
        });

        btnExportar.addActionListener(_ -> {
            try {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Guardar reporte PDF");
                if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    String path = fileChooser.getSelectedFile().getAbsolutePath();
                    if (!path.toLowerCase().endsWith(".pdf")) path += ".pdf";
                    exportarPDF(path, tablaResultados, lastChart);
                    JOptionPane.showMessageDialog(this, "Reporte exportado a PDF: " + path);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar PDF: " + ex.getMessage());
            }
        });

        btnVolver.addActionListener(_ -> dispose());
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void generarReporteVentas(String inicio, String fin, String usuario) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] columnas = {"Fecha", "Total Venta"};
        java.util.List<Object[]> filas = new java.util.ArrayList<>();
        try (Connection conn = ConexionDB.conectar()) {
            String sql = "SELECT fecha_emicion, SUM(total_venta) as total FROM venta v ";
            java.util.List<String> filtros = new java.util.ArrayList<>();
            if (inicio != null && !inicio.isEmpty() && !inicio.equals("YYYY-MM-DD"))
                filtros.add("fecha_emicion >= '" + inicio + "'");
            if (fin != null && !fin.isEmpty() && !fin.equals("YYYY-MM-DD"))
                filtros.add("fecha_emicion <= '" + fin + "'");
            if (usuario != null && !usuario.isEmpty())
                filtros.add("id_empleados IN (SELECT id_empleados FROM empleados WHERE nombre ILIKE '%" + usuario + "%' OR apellido ILIKE '%" + usuario + "%')");
            if (!filtros.isEmpty())
                sql += "WHERE " + String.join(" AND ", filtros) + " ";
            sql += "GROUP BY fecha_emicion ORDER BY fecha_emicion";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String fecha = rs.getString("fecha_emicion");
                double total = rs.getDouble("total");
                filas.add(new Object[]{fecha, total});
                dataset.addValue(total, "Ventas", fecha);
            }
            tablaResultados.setModel(new javax.swing.table.DefaultTableModel(filas.toArray(new Object[0][]), columnas));
            mostrarGrafica(dataset, "Ventas por Día", "Fecha", "Total");
        } catch (Exception ex) {
            tablaResultados.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{{"Error", ex.getMessage()}}, new String[]{"Error", "Mensaje"}));
        }
    }

    private void generarReporteUsuarios() {
        String[] columnas = {"ID", "Usuario", "Rol"};
        java.util.List<Object[]> filas = new java.util.ArrayList<>();
        try (Connection conn = ConexionDB.conectar()) {
            String sql = "SELECT id, nombre_usuario, rol FROM usuarios ORDER BY id";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                filas.add(new Object[]{rs.getInt("id"), rs.getString("nombre_usuario"), rs.getString("rol")});
            }
            tablaResultados.setModel(new javax.swing.table.DefaultTableModel(filas.toArray(new Object[0][]), columnas));
            panelGrafica.removeAll();
            panelGrafica.repaint();
        } catch (Exception ex) {
            tablaResultados.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{{"Error", ex.getMessage()}}, new String[]{"Error", "Mensaje"}));
        }
    }

    private void generarReporteInventario() {
        String[] columnas = {"ID", "Categoría", "Precio Unitario", "Cantidad", "Fecha Actualización"};
        java.util.List<Object[]> filas = new java.util.ArrayList<>();
        try (Connection conn = ConexionDB.conectar()) {
            String sql = "SELECT i.id, p.categoria, i.precio_unitario, i.cantidad, i.fecha_actualizacion FROM inventario i JOIN producto p ON i.id_producto = p.id_producto ORDER BY p.categoria";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                filas.add(new Object[]{rs.getInt("id"), rs.getString("categoria"), rs.getFloat("precio_unitario"), rs.getFloat("cantidad"), rs.getDate("fecha_actualizacion")});
            }
            tablaResultados.setModel(new javax.swing.table.DefaultTableModel(filas.toArray(new Object[0][]), columnas));
            panelGrafica.removeAll();
            panelGrafica.repaint();
        } catch (Exception ex) {
            tablaResultados.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{{"Error", ex.getMessage()}}, new String[]{"Error", "Mensaje"}));
        }
    }

    private void mostrarGrafica(DefaultCategoryDataset dataset, String titulo, String ejeX, String ejeY) {
        JFreeChart chart = ChartFactory.createLineChart(titulo, ejeX, ejeY, dataset);
        lastChart = chart;
        ChartPanel chartPanel = new ChartPanel(chart);
        panelGrafica.removeAll();
        panelGrafica.add(chartPanel, BorderLayout.CENTER);
        panelGrafica.revalidate();
        panelGrafica.repaint();
    }

    private void exportarPDF(String path, JTable tabla, JFreeChart chart) throws Exception {
        Document doc = new Document();
        PdfWriter.getInstance(doc, new FileOutputStream(path));
        doc.open();
        doc.add(new Paragraph("Reporte generado: " + new java.util.Date()));
        // Exportar tabla
        PdfPTable pdfTable = new PdfPTable(tabla.getColumnCount());
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            pdfTable.addCell(tabla.getColumnName(i));
        }
        for (int row = 0; row < tabla.getRowCount(); row++) {
            for (int col = 0; col < tabla.getColumnCount(); col++) {
                Object val = tabla.getValueAt(row, col);
                pdfTable.addCell(val == null ? "" : val.toString());
            }
        }
        doc.add(pdfTable);
        if (chart != null) {
            java.awt.image.BufferedImage img = chart.createBufferedImage(400, 200);
            com.lowagie.text.Image pdfImg = com.lowagie.text.Image.getInstance(
                org.jfree.chart.ChartUtilities.encodeAsPNG(img));
            doc.add(pdfImg);
        }
        doc.close();
    }
}