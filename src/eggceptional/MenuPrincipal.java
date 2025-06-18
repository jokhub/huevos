package eggceptional;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuPrincipal extends JFrame {

    public MenuPrincipal(String usuario, String rol) {
        setTitle("Menú Principal - Rol: " + rol);
        setSize(500, 350);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        // 1. Mostrar el nombre del usuario
        JLabel lblUsuario = new JLabel("Bienvenido, " + usuario + " (" + rol + ")");
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 16));
        lblUsuario.setBounds(20, 20, 400, 30);

        JPanel panel = new JPanel(null);
        panel.add(lblUsuario);

        // 5. Panel de bienvenida o información
        JLabel lblInfo = new JLabel("Seleccione una opción del menú para continuar.");
        lblInfo.setBounds(20, 60, 400, 30);
        panel.add(lblInfo);

        setContentPane(panel);

        JMenuBar menuBar = new JMenuBar();

        JMenu menuGestion = new JMenu("Gestión");
        JMenuItem itemClientes = new JMenuItem("Clientes");
        JMenuItem itemInventario = new JMenuItem("Inventario");
        JMenuItem itemVentas = new JMenuItem("Ventas");

        // 3. Iconos en los menús (simulados con texto)
        itemClientes.setIcon(UIManager.getIcon("FileView.directoryIcon"));
        itemInventario.setIcon(UIManager.getIcon("FileView.hardDriveIcon"));
        itemVentas.setIcon(UIManager.getIcon("FileView.fileIcon"));

        menuGestion.add(itemClientes);
        menuGestion.add(itemInventario);
        menuGestion.add(itemVentas);
        menuBar.add(menuGestion);

        // 4. Deshabilitar opciones según el rol
        JMenu menuAdmin = new JMenu("Administración");
        JMenuItem itemUsuarios = new JMenuItem("Gestión de Usuarios");
        JMenuItem itemReportes = new JMenuItem("Reportes");
        itemUsuarios.setIcon(UIManager.getIcon("FileView.computerIcon"));
        itemReportes.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
        menuAdmin.add(itemUsuarios);
        menuAdmin.add(itemReportes);

        if (rol.equals("gerente")) {
            menuBar.add(menuAdmin);
            itemUsuarios.setEnabled(true);
            itemReportes.setEnabled(true);
        } else {
            itemUsuarios.setEnabled(false);
            itemReportes.setEnabled(false);
        }

        // 2. Botón de "Cerrar sesión" o "Salir"
        JMenu menuSesion = new JMenu("Sesión");
        JMenuItem itemCerrarSesion = new JMenuItem("Cerrar sesión");
        itemCerrarSesion.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
        menuSesion.add(itemCerrarSesion);
        menuBar.add(menuSesion);

        // 6. Atajos de teclado
        itemClientes.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        itemInventario.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));
        itemVentas.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        itemUsuarios.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_DOWN_MASK));
        itemReportes.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
        itemCerrarSesion.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));

        setJMenuBar(menuBar);

        // Acciones de menú
        itemClientes.addActionListener(_ -> new Clientes());
        itemInventario.addActionListener(_ -> new Inventario());
        itemVentas.addActionListener(_ -> new Ventas());
        itemUsuarios.addActionListener(_ -> new GestionUsuarios());
        itemReportes.addActionListener(_ -> new Reportes());

        // Acción cerrar sesión
        itemCerrarSesion.addActionListener(_ -> {
            int confirm = JOptionPane.showConfirmDialog(this, "¿Desea cerrar sesión?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new Login();
            }
        });

        // 7. Confirmación al cerrar
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(MenuPrincipal.this, "¿Desea salir de la aplicación?", "Confirmar salida", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        setVisible(true);
    }
}