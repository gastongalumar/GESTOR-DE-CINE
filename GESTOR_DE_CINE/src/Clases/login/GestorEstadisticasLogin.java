package Clases.login;

import Clases.login.RegistroLogin;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GestorEstadisticasLogin {
    private static GestorEstadisticasLogin instance;
    private List<RegistroLogin> registros;
    private int loginsAdmin;
    private int loginsEmpleado;
    private int loginsCliente;
    private int[] loginsPorHora;

    private GestorEstadisticasLogin() {
        this.registros = new ArrayList<>();
        this.loginsPorHora = new int[24];
        reiniciarContadores();
    }

    public static GestorEstadisticasLogin getInstance() {
        if (instance == null) {
            instance = new GestorEstadisticasLogin();
        }
        return instance;
    }

    private void reiniciarContadores() {
        loginsAdmin = 0;
        loginsEmpleado = 0;
        loginsCliente = 0;
        for (int i = 0; i < 24; i++) {
            loginsPorHora[i] = 0;
        }
    }

    public void registrarLogin(String usuario, String tipoUsuario) {
        LocalDateTime ahora = LocalDateTime.now();
        RegistroLogin registro = new RegistroLogin(usuario, tipoUsuario, ahora);
        registros.add(registro);

        // Actualizar contadores
        if (tipoUsuario.equals("Administrador")) {
            loginsAdmin++;
        } else if (tipoUsuario.equals("Empleado")) {
            loginsEmpleado++;
        } else {
            loginsCliente++;
        }

        // Actualizar por hora
        int hora = ahora.getHour();
        loginsPorHora[hora]++;
    }

    public void mostrarGraficaLogins() {
        JFrame frame = new JFrame("Estadísticas de Logins - CINE LOS CULIA");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Gráfica por tipo de usuario
        tabbedPane.addTab("Por Tipo de Usuario", crearPanelTipoUsuario());

        // Gráfica por hora del día
        tabbedPane.addTab("Por Hora del Día", crearPanelHoraDia());

        // Tabla de registros
        tabbedPane.addTab("Registros", crearPanelRegistros());

        frame.add(tabbedPane);
        frame.setVisible(true);
    }

    private JPanel crearPanelTipoUsuario() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel graficaPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarGraficaBarras(g);
            }
        };
        graficaPanel.setPreferredSize(new Dimension(700, 400));
        graficaPanel.setBackground(Color.WHITE);

        panel.add(new JLabel("Logins por Tipo de Usuario", JLabel.CENTER), BorderLayout.NORTH);
        panel.add(graficaPanel, BorderLayout.CENTER);

        return panel;
    }

    private void dibujarGraficaBarras(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int ancho = getWidth();
        int alto = getHeight();
        int margen = 50;

        // Encontrar valor máximo para escalar
        int maxValor = Math.max(loginsAdmin, Math.max(loginsEmpleado, loginsCliente));
        if (maxValor == 0) maxValor = 1;

        // Dibujar título
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.setColor(Color.BLACK);
        String titulo = "Total de Logins: " + getTotalLogins();
        g2d.drawString(titulo, ancho / 2 - g2d.getFontMetrics().stringWidth(titulo) / 2, 30);

        // Dibujar barras
        String[] tipos = {"Administrador", "Empleado", "Cliente"};
        int[] valores = {loginsAdmin, loginsEmpleado, loginsCliente};
        Color[] colores = {new Color(70, 130, 180), new Color(220, 20, 60), new Color(34, 139, 34)};

        int anchoBarra = 100;
        int espacio = 50;

        for (int i = 0; i < 3; i++) {
            int x = margen + i * (anchoBarra + espacio);
            int alturaBarra = (int) ((double) valores[i] / maxValor * (alto - 150));
            int y = alto - alturaBarra - 80;

            // Dibujar barra
            g2d.setColor(colores[i]);
            g2d.fillRect(x, y, anchoBarra, alturaBarra);

            // Dibujar valor
            g2d.setColor(Color.BLACK);
            String valorStr = String.valueOf(valores[i]);
            g2d.drawString(valorStr, x + anchoBarra/2 - 10, y - 10);

            // Dibujar etiqueta
            g2d.drawString(tipos[i], x + anchoBarra/2 - 40, alto - 50);
        }
    }

    private int getWidth() {
        int i = 0;
        return i;
    }

    private JPanel crearPanelHoraDia() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel graficaPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarGraficaHoras(g);
            }
        };
        graficaPanel.setPreferredSize(new Dimension(700, 400));
        graficaPanel.setBackground(Color.WHITE);

        panel.add(new JLabel("Logins por Hora del Día", JLabel.CENTER), BorderLayout.NORTH);
        panel.add(graficaPanel, BorderLayout.CENTER);

        return panel;
    }

    private void dibujarGraficaHoras(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int ancho = getWidth();
        int alto = getHeight();
        int margen = 60;

        // Encontrar valor máximo
        int maxValor = 0;
        for (int i = 0; i < 24; i++) {
            if (loginsPorHora[i] > maxValor) {
                maxValor = loginsPorHora[i];
            }
        }
        if (maxValor == 0) maxValor = 1;

        // Dibujar barras por hora
        g2d.setColor(new Color(70, 130, 180));

        int anchoBarra = (ancho - 2 * margen) / 24 - 2;

        for (int i = 0; i < 24; i++) {
            int x = margen + i * (anchoBarra + 2);
            int alturaBarra = (int) ((double) loginsPorHora[i] / maxValor * (alto - 150));
            int y = alto - alturaBarra - 80;

            g2d.fillRect(x, y, anchoBarra, alturaBarra);

            // Dibujar hora cada 4 horas
            if (i % 4 == 0) {
                g2d.setColor(Color.BLACK);
                g2d.drawString(i + "h", x - 5, alto - 60);
                g2d.setColor(new Color(70, 130, 180));
            }
        }
    }

    private int getHeight() {
        int i = 0;
        return i;
    }

    private JPanel crearPanelRegistros() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"Usuario", "Tipo", "Fecha y Hora"};
        Object[][] data = new Object[registros.size()][3];

        for (int i = 0; i < registros.size(); i++) {
            RegistroLogin registro = registros.get(i);
            data[i][0] = registro.getUsuario();
            data[i][1] = registro.getTipoUsuario();
            data[i][2] = registro.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }

        JTable tabla = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(tabla);

        panel.add(new JLabel("Registros de Login (" + registros.size() + " totales)", JLabel.CENTER), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Métodos para obtener estadísticas
    public int getTotalLogins() {
        return registros.size();
    }

    public int getLoginsAdmin() {
        return loginsAdmin;
    }

    public int getLoginsEmpleado() {
        return loginsEmpleado;
    }

    public int getLoginsCliente() {
        return loginsCliente;
    }

    public int[] getLoginsPorHora() {
        return loginsPorHora.clone();
    }
}