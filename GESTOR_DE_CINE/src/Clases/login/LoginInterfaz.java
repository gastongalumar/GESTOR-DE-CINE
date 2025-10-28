package Clases.estadisticas;

import Clases.login.GestorEstadisticasLogin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginInterfaz {
    private JFrame frame;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;
    private int intentosLogin = 0;

    public LoginInterfaz() {
        crearInterfaz();
    }

    private void crearInterfaz() {
        frame = new JFrame("CINE LOS CULIA - Inicio de Sesión");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        // Panel principal con gradiente
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(25, 25, 35);
                Color color2 = new Color(75, 0, 130);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = crearHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Formulario
        JPanel formPanel = crearFormPanel();
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = crearFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
    }

    private JPanel crearHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("INICIAR SESIÓN", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Ingresa tus credenciales", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 200, 200));

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        return headerPanel;
    }

    private JPanel crearFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = 2;

        // Email
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel emailLabel = new JLabel("E-MAIL:");
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(emailLabel, gbc);

        gbc.gridy = 1;
        emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(300, 40));
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 150), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        formPanel.add(emailField, gbc);

        // Contraseña
        gbc.gridy = 2;
        JLabel passLabel = new JLabel("CONTRASEÑA:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(passLabel, gbc);

        gbc.gridy = 3;
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(300, 40));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 150), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        formPanel.add(passwordField, gbc);

        // Botones
        gbc.gridy = 4;
        gbc.insets = new Insets(20, 10, 10, 10);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        loginButton = new JButton("INGRESAR");
        loginButton.setBackground(new Color(0, 150, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(120, 45));
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        registerButton = new JButton("REGISTRARSE");
        registerButton.setBackground(new Color(0, 150, 100));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setFocusPainted(false);
        registerButton.setPreferredSize(new Dimension(120, 45));
        registerButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        formPanel.add(buttonPanel, gbc);

        return formPanel;
    }

    private JPanel crearFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        footerPanel.setLayout(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel infoLabel = new JLabel("© 2025 Cine Los Culia - Sistema de Gestión", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        infoLabel.setForeground(new Color(150, 150, 150));

        footerPanel.add(infoLabel, BorderLayout.CENTER);
        return footerPanel;
    }

    private void configurarEventos() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarLogin();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarUsuario();
            }
        });

        // Enter para login
        emailField.addActionListener(e -> realizarLogin());
        passwordField.addActionListener(e -> realizarLogin());
    }

    private void realizarLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "Por favor, complete todos los campos",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Autenticación
        if (autenticarUsuario(email, password)) {
            String tipoUsuario = determinarTipoUsuario(email);

            // Registrar el login en estadísticas
            GestorEstadisticasLogin.getInstance().registrarLogin(email, tipoUsuario);

            JOptionPane.showMessageDialog(frame,
                    "¡Bienvenido a CINE LOS CULIA!\nUsuario: " + email + "\nTipo: " + tipoUsuario,
                    "Login Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);

            intentosLogin = 0; // Resetear intentos
            frame.dispose();

            // Aquí podrías abrir el sistema principal según el tipo de usuario
            abrirSistemaPrincipal(email, tipoUsuario);

        } else {
            intentosLogin++;
            JOptionPane.showMessageDialog(frame,
                    "Credenciales incorrectas\nIntentos fallidos: " + intentosLogin,
                    "Error de Login",
                    JOptionPane.ERROR_MESSAGE);

            if (intentosLogin >= 3) {
                JOptionPane.showMessageDialog(frame,
                        "Demasiados intentos fallidos. Cerrando aplicación.",
                        "Seguridad",
                        JOptionPane.WARNING_MESSAGE);
                System.exit(0);
            }
        }
    }

    private boolean autenticarUsuario(String email, String password) {
        // Base de datos simple de usuarios
        return (email.equals("admin@cine.com") && password.equals("admin123")) ||
                (email.equals("cliente@cine.com") && password.equals("cliente123")) ||
                (email.equals("empleado@cine.com") && password.equals("empleado123")) ||
                (email.equals("user@cine.com") && password.equals("user123"));
    }

    private String determinarTipoUsuario(String email) {
        if (email.equals("admin@cine.com")) return "Administrador";
        if (email.equals("empleado@cine.com")) return "Empleado";
        return "Cliente";
    }

    private void abrirSistemaPrincipal(String usuario, String tipoUsuario) {
        // Aquí puedes abrir tu sistema principal según el tipo de usuario
        JOptionPane.showMessageDialog(frame,
                "Abriendo sistema para: " + tipoUsuario + "\nUsuario: " + usuario,
                "Redirigiendo",
                JOptionPane.INFORMATION_MESSAGE);

        // Ejemplo: Abrir estadísticas solo si es admin
        if (tipoUsuario.equals("Administrador")) {
            GestorEstadisticasLogin.getInstance().mostrarGraficaLogins();
        }
    }

    private void registrarUsuario() {
        JOptionPane.showMessageDialog(frame,
                "Función de registro en desarrollo\nPuedes usar:\n" +
                        "admin@cine.com / admin123\n" +
                        "cliente@cine.com / cliente123\n" +
                        "empleado@cine.com / empleado123",
                "Registro",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrar() {
        configurarEventos();
        frame.setVisible(true);
    }

    // Método estático para abrir fácilmente
    public static void abrirLogin() {
        SwingUtilities.invokeLater(() -> new LoginInterfaz().mostrar());
    }
}