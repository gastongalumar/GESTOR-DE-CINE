package Clases.login;

import Enumeradores.login.TipoUsuario;
import Excepciones.UsuarioException;
import ManejoJSON.GestorJsonLogin;

import javax.swing.*;
import java.awt.*;

public class RegistroUsuario extends JFrame {
    private JTextField nombreField, apellidoField, emailField, telefonoField;
    private JPasswordField passwordField, confirmPasswordField;
    private JComboBox<TipoUsuario> tipoUsuarioCombo;
    private JButton registrarButton, cancelarButton;
    private boolean esAdministrador;

    public RegistroUsuario() {
        this(false); // Por defecto, registro normal de cliente
    }

    public RegistroUsuario(boolean esAdministrador) {
        this.esAdministrador = esAdministrador;
        inicializarInterfaz();
        configurarComponentes();
    }

    private void inicializarInterfaz() {
        setTitle("CINE LOS CULIA - " + (esAdministrador ? "Registro Administrativo" : "Registro de Usuario"));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, esAdministrador ? 650 : 600);
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
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header
        JPanel headerPanel = crearHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Formulario
        JPanel formPanel = crearFormPanel();
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = crearFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel crearHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        headerPanel.setPreferredSize(new Dimension(400, 80));

        JLabel titleLabel = new JLabel(
                esAdministrador ? "REGISTRO ADMINISTRATIVO" : "CREAR CUENTA",
                SwingConstants.CENTER
        );
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel(
                esAdministrador ?
                        "Registro de nuevos usuarios del sistema" :
                        "Complete sus datos para registrarse",
                SwingConstants.CENTER
        );
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(180, 180, 180));

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        return headerPanel;
    }

    private JPanel crearFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        // Campos de Nombre y Apellido
        JPanel nombreApellidoPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        nombreApellidoPanel.setOpaque(false);
        nombreApellidoPanel.setMaximumSize(new Dimension(440, 80));

        nombreApellidoPanel.add(crearCampo("Nombre", nombreField = crearTextField()));
        nombreApellidoPanel.add(crearCampo("Apellido", apellidoField = crearTextField()));

        formPanel.add(nombreApellidoPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Email
        formPanel.add(crearCampo("E-mail", emailField = crearTextField(), 420));
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Teléfono
        formPanel.add(crearCampo("Teléfono", telefonoField = crearTextField(), 420));
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Tipo de Usuario (solo visible para administradores)
        if (esAdministrador) {
            JPanel tipoPanel = new JPanel();
            tipoPanel.setOpaque(false);
            tipoPanel.setLayout(new BoxLayout(tipoPanel, BoxLayout.Y_AXIS));
            tipoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel tipoLabel = new JLabel("Tipo de Usuario");
            tipoLabel.setForeground(Color.WHITE);
            tipoLabel.setFont(new Font("Arial", Font.BOLD, 12));
            tipoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            tipoUsuarioCombo = new JComboBox<>(TipoUsuario.values());
            tipoUsuarioCombo.setMaximumSize(new Dimension(420, 35));
            tipoUsuarioCombo.setBackground(new Color(240, 240, 245));
            tipoUsuarioCombo.setFont(new Font("Arial", Font.PLAIN, 14));

            tipoPanel.add(tipoLabel);
            tipoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            tipoPanel.add(tipoUsuarioCombo);

            formPanel.add(tipoPanel);
            formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        // Contraseña
        formPanel.add(crearCampo("Contraseña", passwordField = crearPasswordField(), 420));
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Confirmar Contraseña
        formPanel.add(crearCampo("Confirmar Contraseña", confirmPasswordField = crearPasswordField(), 420));
        formPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setMaximumSize(new Dimension(400, 60));

        registrarButton = new JButton("REGISTRARSE");
        registrarButton.setBackground(new Color(0, 150, 100));
        registrarButton.setForeground(Color.WHITE);
        registrarButton.setFont(new Font("Arial", Font.BOLD, 14));
        registrarButton.setFocusPainted(false);
        registrarButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 120, 80), 1),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        registrarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        cancelarButton = new JButton("CANCELAR");
        cancelarButton.setBackground(new Color(150, 50, 50));
        cancelarButton.setForeground(Color.WHITE);
        cancelarButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelarButton.setFocusPainted(false);
        cancelarButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 40, 40), 1),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        cancelarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonPanel.add(registrarButton);
        buttonPanel.add(cancelarButton);
        formPanel.add(buttonPanel);

        return formPanel;
    }

    private JPanel crearCampo(String label, JComponent field) {
        return crearCampo(label, field, 200);
    }

    private JPanel crearCampo(String label, JComponent field, int ancho) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel jLabel = new JLabel(label);
        jLabel.setForeground(Color.WHITE);
        jLabel.setFont(new Font("Arial", Font.BOLD, 12));
        jLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setMaximumSize(new Dimension(ancho, 35));
        field.setPreferredSize(new Dimension(ancho, 35));

        panel.add(jLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(field);

        return panel;
    }

    private JTextField crearTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 150), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setBackground(new Color(240, 240, 245));
        return field;
    }

    private JPasswordField crearPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 150), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setBackground(new Color(240, 240, 245));
        return field;
    }

    private JPanel crearFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        footerPanel.setLayout(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        footerPanel.setPreferredSize(new Dimension(400, 30));

        String mensaje = esAdministrador ?
                "Registro administrativo - Todos los campos son obligatorios" :
                "Todos los campos son obligatorios";

        JLabel infoLabel = new JLabel(mensaje, SwingConstants.CENTER);
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        infoLabel.setForeground(new Color(180, 180, 180));

        footerPanel.add(infoLabel, BorderLayout.CENTER);
        return footerPanel;
    }

    private void configurarComponentes() {
        registrarButton.addActionListener(e -> registrarUsuario());
        cancelarButton.addActionListener(e -> cancelarRegistro());

        // Enter key listener
        emailField.addActionListener(e -> registrarUsuario());
        passwordField.addActionListener(e -> registrarUsuario());
    }

    private void registrarUsuario() {
        try {
            if (!validarCampos()) {
                return;
            }

            // Determinar tipo de usuario
            TipoUsuario tipoUsuario = esAdministrador ?
                    (TipoUsuario) tipoUsuarioCombo.getSelectedItem() :
                    TipoUsuario.CLIENTE;

            Usuario nuevoUsuario = new Usuario(
                    nombreField.getText().trim(),
                    apellidoField.getText().trim(),
                    emailField.getText().trim(),
                    new String(passwordField.getPassword()),
                    telefonoField.getText().trim(),
                    tipoUsuario
            );

            // Validar y guardar
            nuevoUsuario.validarDatos();
            GestorJsonLogin.agregarUsuario(nuevoUsuario);

            mostrarExito("¡Usuario registrado exitosamente!\n" +
                    "Email: " + nuevoUsuario.getEmail() + "\n" +
                    "Tipo: " + nuevoUsuario.getTipoUsuario().getDescripcion());
            dispose();

        } catch (UsuarioException e) {
            mostrarError("Error de registro: " + e.getMessage());
        } catch (Exception e) {
            mostrarError("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validarCampos() {
        // Validaciones básicas
        if (nombreField.getText().trim().isEmpty() ||
                apellidoField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty() ||
                telefonoField.getText().trim().isEmpty() ||
                passwordField.getPassword().length == 0 ||
                confirmPasswordField.getPassword().length == 0) {

            mostrarError("Todos los campos son obligatorios");
            return false;
        }

        // Validar email
        if (!Usuario.validarEmail(emailField.getText().trim())) {
            mostrarError("Por favor ingrese un email válido");
            emailField.requestFocus();
            return false;
        }

        // Validar teléfono
        if (!Usuario.validarTelefono(telefonoField.getText().trim())) {
            mostrarError("El teléfono debe contener entre 7 y 15 dígitos");
            telefonoField.requestFocus();
            return false;
        }

        // Validar contraseña
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (!password.equals(confirmPassword)) {
            mostrarError("Las contraseñas no coinciden");
            passwordField.setText("");
            confirmPasswordField.setText("");
            passwordField.requestFocus();
            return false;
        }

        if (!Usuario.validarPassword(password)) {
            mostrarError("La contraseña debe tener al menos 6 caracteres");
            return false;
        }

        return true;
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this,
                mensaje,
                "Error de Validación",
                JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this,
                mensaje,
                "Registro Exitoso",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void cancelarRegistro() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea cancelar el registro?\nSe perderán los datos ingresados.",
                "Confirmar Cancelación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
        }
    }

    // Métodos estáticos para abrir ventanas
    public static void abrirRegistroCliente() {
        SwingUtilities.invokeLater(() -> new RegistroUsuario(false).setVisible(true));
    }

    public static void abrirRegistroAdministrativo() {
        SwingUtilities.invokeLater(() -> new RegistroUsuario(true).setVisible(true));
    }
}