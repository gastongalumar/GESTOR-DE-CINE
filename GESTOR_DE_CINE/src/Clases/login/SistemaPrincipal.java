package Clases.login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SistemaPrincipal extends JFrame {
    private String usuarioActual;
    private String tipoUsuario;

    public SistemaPrincipal(String usuario, String tipoUsuario) {
        this.usuarioActual = usuario;
        this.tipoUsuario = tipoUsuario;
        inicializarInterfaz();
    }

    private void inicializarInterfaz() {
        setTitle("CINE LOS CULIA - Sistema de Gestión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header
        JPanel headerPanel = crearHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Menu lateral
        JPanel menuPanel = crearMenuPanel();
        mainPanel.add(menuPanel, BorderLayout.WEST);

        // Contenido principal
        JPanel contentPanel = crearContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel crearHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 25, 35));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 70));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Título
        JLabel titleLabel = new JLabel("CINE LOS CULIA - Sistema de Gestión Cinematográfica");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // Info usuario
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(new Color(25, 25, 35));

        JLabel userLabel = new JLabel("Usuario: " + usuarioActual + " (" + tipoUsuario + ")");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton logoutButton = new JButton("Cerrar Sesión");
        logoutButton.setBackground(new Color(150, 50, 50));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> cerrarSesion());

        userPanel.add(userLabel);
        userPanel.add(logoutButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel crearMenuPanel() {
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(new Color(40, 40, 60));
        menuPanel.setPreferredSize(new Dimension(250, getHeight()));
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Botones del menú según tipo de usuario
        String[] opcionesMenu = getOpcionesMenu();

        for (String opcion : opcionesMenu) {
            JButton menuButton = crearBotonMenu(opcion);
            menuPanel.add(menuButton);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        menuPanel.add(Box.createVerticalGlue());

        return menuPanel;
    }

    private String[] getOpcionesMenu() {
        switch (tipoUsuario.toLowerCase()) {
            case "administrador":
                return new String[]{
                        "Dashboard", "Gestión de Películas", "Gestión de Salas",
                        "Gestión de Usuarios", "Reportes y Estadísticas",
                        "Configuración del Sistema", "Ventas y Facturación"
                };
            case "empleado":
                return new String[]{
                        "Venta de Entradas", "Cartelera", "Clientes",
                        "Reportes de Ventas", "Configuración Horarios"
                };
            case "cliente":
            default:
                return new String[]{
                        "Cartelera", "Comprar Entradas", "Mis Compras",
                        "Promociones", "Perfil"
                };
        }
    }

    private JButton crearBotonMenu(String texto) {
        JButton button = new JButton(texto);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(230, 45));
        button.setBackground(new Color(60, 60, 80));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manejarOpcionMenu(texto);
            }
        });

        return button;
    }

    private JPanel crearContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);

        // Panel de bienvenida por defecto
        JPanel welcomePanel = new JPanel(new GridBagLayout());
        welcomePanel.setBackground(Color.WHITE);

        JLabel welcomeLabel = new JLabel("¡Bienvenido al Sistema CINE LOS CULIA!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(25, 25, 35));

        JLabel userInfoLabel = new JLabel("Usuario: " + usuarioActual + " | Tipo: " + tipoUsuario);
        userInfoLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        userInfoLabel.setForeground(new Color(100, 100, 120));

        JLabel instructionsLabel = new JLabel("Seleccione una opción del menú lateral para comenzar");
        instructionsLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        instructionsLabel.setForeground(new Color(150, 150, 150));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);

        welcomePanel.add(welcomeLabel, gbc);
        welcomePanel.add(userInfoLabel, gbc);
        welcomePanel.add(instructionsLabel, gbc);

        contentPanel.add(welcomePanel, BorderLayout.CENTER);

        return contentPanel;
    }

    private void manejarOpcionMenu(String opcion) {
        // Aquí implementarías la lógica para cada opción del menú
        JOptionPane.showMessageDialog(this,
                "Has seleccionado: " + opcion + "\n\n" +
                        "Esta funcionalidad está en desarrollo.",
                "Opción del Menú",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void cerrarSesion() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea cerrar sesión?",
                "Confirmar Cierre de Sesión",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new CineLogin().setVisible(true);
        }
    }
}