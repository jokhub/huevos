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
import org.jfree.chart.ChartUtilities;

// Si tienes iText, descomenta:
// import com.itextpdf.text.*;
// import com.itextpdf.text.pdf.*;

public class Reportes extends JFrame {
    private JPanel panelGrafica; // Panel para la gráfica
    private JFreeChart lastChart = null; // Guarda la última gráfica generada
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

        // Panel para la gráfica
        panelGrafica = new JPanel(new BorderLayout());
        panelGrafica.setBounds(30, 310, 420, 120);
        add(panelGrafica);

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
            areaResultados.setText("");
            panelGrafica.removeAll();
            if (tipo.equals("Ventas")) {
                generarReporteVentas(inicio, fin, usuario, areaResultados);
            } else {
                areaResultados.setText("Funcionalidad solo implementada para Ventas");
            }
        });

        // Acción para exportar (real)
        btnExportar.addActionListener(_ -> {
            try {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Guardar reporte PDF");
                if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    String path = fileChooser.getSelectedFile().getAbsolutePath();
                    if (!path.toLowerCase().endsWith(".pdf")) path += ".pdf";
                    exportarPDF(path, areaResultados.getText(), lastChart);
                    JOptionPane.showMessageDialog(this, "Reporte exportado a PDF: " + path);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar PDF: " + ex.getMessage());
            }
        });

        // Acción para volver atrás
        btnVolver.addActionListener(_ -> dispose());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void generarReporteVentas(String inicio, String fin, String usuario, JTextArea areaResultados) {
        StringBuilder sb = new StringBuilder();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try (Connection conn = ConexionDB.conectar()) {
            String sql = "SELECT fecha_emicion, SUM(total_venta) as total FROM venta v ";
            java.util.List<String> filtros = new ArrayList<>();
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
            sb.append("Fecha\tTotal Venta\n");
            while (rs.next()) {
                String fecha = rs.getString("fecha_emicion");
                double total = rs.getDouble("total");
                sb.append(fecha + "\t" + total + "\n");
                dataset.addValue(total, "Ventas", fecha);
            }
            areaResultados.setText(sb.toString());
            mostrarGrafica(dataset, "Ventas por Día", "Fecha", "Total");
        } catch (Exception ex) {
            areaResultados.setText("Error al consultar ventas: " + ex.getMessage());
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

    private void exportarPDF(String path, String texto, JFreeChart chart) throws Exception {
        Document doc = new Document();
        PdfWriter.getInstance(doc, new FileOutputStream(path));
        doc.open();
        doc.add(new Paragraph("Reporte generado: " + new java.util.Date()));
        doc.add(new Paragraph(texto));
        if (chart != null) {
            java.awt.image.BufferedImage img = chart.createBufferedImage(400, 200);
            com.lowagie.text.Image pdfImg = com.lowagie.text.Image.getInstance(
                org.jfree.chart.ChartUtilities.encodeAsPNG(img));
            doc.add(pdfImg);
        }
        doc.close();
    }
}