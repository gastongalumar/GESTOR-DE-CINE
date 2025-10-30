package Clases.login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CineLogin extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton, recoverButton;

    public CineLogin() {
        inicializarInterfaz();
        configurarComponentes();
    }

    private void inicializarInterfaz() {
        setTitle("CINE LOS CULIA - Inicio de Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setResizable(false);

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

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Formulario
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void configurarComponentes() {
        // Configurar acciones de los botones
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarLogin();
            }
        });

        recoverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                recuperarContrasena();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarUsuario();
            }
        });

        // Enter key listener
        emailField.addActionListener(e -> realizarLogin());
        passwordField.addActionListener(e -> realizarLogin());
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        JLabel titleLabel = new JLabel("CINE LOS CULIA", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Sistema de Gestión Cinematográfica", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 200, 200));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Email
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel emailLabel = new JLabel("E-MAIL:");
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(200, 35));
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(emailField, gbc);

        // Contraseña
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 1;
        JLabel passLabel = new JLabel("CONTRASEÑA:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(passLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 35));
        formPanel.add(passwordField, gbc);

        // Botones
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);

        loginButton = new JButton("INGRESAR");
        loginButton.setBackground(new Color(0, 150, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(120, 40));
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        buttonPanel.add(loginButton);
        formPanel.add(buttonPanel, gbc);

        // Enlaces
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.insets = new Insets(10, 5, 5, 5);

        JPanel linkPanel = new JPanel(new FlowLayout());
        linkPanel.setOpaque(false);

        recoverButton = createLinkButton("¿Olvidaste tu contraseña?");
        registerButton = createLinkButton("Registrarme");

        linkPanel.add(recoverButton);
        linkPanel.add(new JLabel("|"));
        linkPanel.add(registerButton);

        formPanel.add(linkPanel, gbc);

        return formPanel;
    }

    private JButton createLinkButton(String text) {
        JButton button = new JButton(text);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setForeground(new Color(100, 180, 255));
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        footerPanel.setLayout(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel copyrightLabel = new JLabel("© 2025 Cine Los Culia - Todos los derechos reservados", SwingConstants.CENTER);
        copyrightLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        copyrightLabel.setForeground(new Color(150, 150, 150));

        footerPanel.add(copyrightLabel, BorderLayout.CENTER);

        return footerPanel;
    }

    private void realizarLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, complete todos los campos",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Autenticación simulada
        if (autenticarUsuario(email, password)) {
            String tipoUsuario = determinarTipoUsuario(email);
            JOptionPane.showMessageDialog(this,
                    "¡Bienvenido a CINE LOS CULIA!",
                    "Login Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);

            // Abrir sistema principal
            abrirSistemaPrincipal(email, tipoUsuario);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Credenciales incorrectas",
                    "Error de Login",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean autenticarUsuario(String email, String password) {
        // Simulación de autenticación
        // Usuarios de prueba
        return (email.equals("admin@cine.com") && password.equals("admin123")) ||
                (email.equals("cliente@cine.com") && password.equals("cliente123")) ||
                (email.equals("empleado@cine.com") && password.equals("empleado123"));
    }

    private String determinarTipoUsuario(String email) {
        // Simulación de determinación de tipo de usuario
        if (email.equals("admin@cine.com")) return "Administrador";
        if (email.equals("empleado@cine.com")) return "Empleado";
        return "Cliente";
    }

    private void abrirSistemaPrincipal(String usuario, String tipoUsuario) {
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            new SistemaPrincipal(usuario, tipoUsuario).setVisible(true);
        });
    }

    private void recuperarContrasena() {
        String email = JOptionPane.showInputDialog(this,
                "Ingrese su email para recuperar contraseña:",
                "Recuperar Contraseña",
                JOptionPane.QUESTION_MESSAGE);

        if (email != null && !email.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Se ha enviado un enlace de recuperación a: " + email,
                    "Recuperación Enviada",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void registrarUsuario() {
        // Abrir ventana de registro
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RegistroUsuario().setVisible(true);
            }
        });
    }
}