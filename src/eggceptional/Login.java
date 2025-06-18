package eggceptional;

import javax.swing.*;
import java.sql.*;

public class Login extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;

    public Login() {
        setTitle("Login - Sistema Eggceptional");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setResizable(true); // Permite cambiar el tamaño de la ventana
        setLayout(null);    // Layout absoluto

        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setBounds(30, 20, 80, 25);
        add(lblUsuario);

        txtUsuario = new JTextField();
        txtUsuario.setBounds(120, 20, 140, 25);
        add(txtUsuario);

        JLabel lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setBounds(30, 60, 80, 25);
        add(lblContrasena);

        txtContrasena = new JPasswordField();
        txtContrasena.setBounds(120, 60, 140, 25);
        add(txtContrasena);

        JButton btnLogin = new JButton("Iniciar sesión");
        btnLogin.setBounds(90, 110, 120, 30);
        add(btnLogin);

        btnLogin.addActionListener(_ -> autenticar());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void autenticar() {
        String usuario = txtUsuario.getText();
        String contrasena = new String(txtContrasena.getPassword());

        try (Connection conn = ConexionDB.conectar()) {
            String query = "SELECT rol FROM usuarios WHERE nombre_usuario = ? AND contrasena = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, usuario);
            stmt.setString(2, contrasena);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String rol = rs.getString("rol");
                dispose();
                new MenuPrincipal(usuario, rol);
            } else {
                JOptionPane.showMessageDialog(this, "Credenciales incorrectas");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error de conexión");
        }
    }
}