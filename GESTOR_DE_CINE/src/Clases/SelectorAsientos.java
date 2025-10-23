package Clases;

import ManejoJSON.GestorJsonAsientos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class SelectorAsientos extends JFrame implements PropertyChangeListener {
    // Configuración de la sala
    private final int FILAS = 12;
    private final int FILAS_ASIENTOS = 10;
    private final int LEFT_BLOCK = 3;
    private final int CENTER_BLOCK = 8;
    private final int RIGHT_BLOCK = 3;
    private final int AISLE_WIDTH = 1;
    private final int COLUMNAS = LEFT_BLOCK + AISLE_WIDTH + CENTER_BLOCK + AISLE_WIDTH + RIGHT_BLOCK;

    private final SalaCine sala;
    private final GestorJsonAsientos gestorJson;
    private final AsientoButton[][] botonesAsientos = new AsientoButton[FILAS_ASIENTOS][COLUMNAS];
    private final List<Integer> columnasValidas = new ArrayList<>();
    private final JLabel contadorLabel = new JLabel("0 asientos seleccionados");

    public SelectorAsientos() {
        super("🎬 Selector de Asientos - Sala con tres bloques");
        this.sala = new SalaCine(FILAS_ASIENTOS, COLUMNAS);
        this.gestorJson = new GestorJsonAsientos(sala);

        inicializarColumnasValidas();
        configurarVentana();

        System.out.println("🚀 ===== INICIANDO APLICACIÓN =====");
        boolean cargaExitosa = gestorJson.cargarEstadoGuardado();

        if (!cargaExitosa) {
            System.out.println("⚠️  No se pudo cargar el estado, usando valores por defecto");
        }

        inicializarComponentes();

        SwingUtilities.invokeLater(() -> {
            actualizarVisualizacionAsientos();
            System.out.println("🎉 APLICACIÓN INICIADA CORRECTAMENTE");
        });
    }

    private void inicializarColumnasValidas() {
        for (int c = 0; c < LEFT_BLOCK; c++) columnasValidas.add(c);
        for (int c = LEFT_BLOCK + AISLE_WIDTH; c < LEFT_BLOCK + AISLE_WIDTH + CENTER_BLOCK; c++) columnasValidas.add(c);
        for (int c = LEFT_BLOCK + AISLE_WIDTH + CENTER_BLOCK + AISLE_WIDTH; c < COLUMNAS; c++) columnasValidas.add(c);
    }

    private void configurarVentana() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 900);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(20, 20, 20));
    }

    private void inicializarComponentes() {
        add(crearPanelPantalla(), BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);
        add(crearPanelInferior(), BorderLayout.SOUTH);

        actualizarContador();
    }

    private JLabel crearPanelPantalla() {
        JLabel pantalla = new JLabel("PANTALLA", SwingConstants.CENTER);
        pantalla.setOpaque(true);
        pantalla.setBackground(new Color(70, 130, 180));
        pantalla.setForeground(Color.WHITE);
        pantalla.setFont(new Font("Segoe UI", Font.BOLD, 30));
        pantalla.setPreferredSize(new Dimension(1000, 70));
        pantalla.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 3));
        return pantalla;
    }

    private JPanel crearPanelCentral() {
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(new Color(20, 20, 20));

        panelCentral.add(crearPanelLetrasColumnas(), BorderLayout.NORTH);
        panelCentral.add(crearPasilloDelantero(), BorderLayout.CENTER);
        panelCentral.add(crearPanelAsientosConNumeros(), BorderLayout.SOUTH);

        return panelCentral;
    }

    private JPanel crearPanelLetrasColumnas() {
        JPanel panelLetras = new JPanel(new GridLayout(1, COLUMNAS, 5, 0));
        panelLetras.setBackground(new Color(20, 20, 20));
        panelLetras.setPreferredSize(new Dimension(0, 30));

        for (int j = 0; j < COLUMNAS; j++) {
            String texto = "";
            int idx = columnasValidas.indexOf(j);
            if (idx != -1) {
                texto = String.valueOf((char) ('A' + idx));
            }
            JLabel letraCol = new JLabel(texto, SwingConstants.CENTER);
            letraCol.setForeground(Color.WHITE);
            letraCol.setFont(new Font("Segoe UI", Font.BOLD, 14));
            panelLetras.add(letraCol);
        }

        return panelLetras;
    }

    private JPanel crearPasilloDelantero() {
        JPanel pasilloDelantero = new JPanel(new BorderLayout());
        pasilloDelantero.setBackground(new Color(80, 80, 80));
        pasilloDelantero.setBorder(BorderFactory.createLineBorder(new Color(120, 120, 120), 2));
        pasilloDelantero.setPreferredSize(new Dimension(0, 80));

        JLabel labelPasilloDelantero = new JLabel("PASILLO PRINCIPAL", SwingConstants.CENTER);
        labelPasilloDelantero.setForeground(new Color(200, 200, 200));
        labelPasilloDelantero.setFont(new Font("Segoe UI", Font.BOLD, 20));
        pasilloDelantero.add(labelPasilloDelantero, BorderLayout.CENTER);

        return pasilloDelantero;
    }

    private JPanel crearPanelAsientosConNumeros() {
        JPanel panelAsientosConNumeros = new JPanel(new BorderLayout());
        panelAsientosConNumeros.setBackground(new Color(20, 20, 20));
        panelAsientosConNumeros.add(crearPanelNumeracionFilas(), BorderLayout.WEST);
        panelAsientosConNumeros.add(crearPanelPrincipalAsientos(), BorderLayout.CENTER);

        return panelAsientosConNumeros;
    }

    private JPanel crearPanelNumeracionFilas() {
        JPanel panelFilas = new JPanel(new GridLayout(FILAS_ASIENTOS, 1, 0, 12));
        panelFilas.setBackground(new Color(20, 20, 20));
        panelFilas.setPreferredSize(new Dimension(40, 0));

        for (int i = 0; i < FILAS_ASIENTOS; i++) {
            JLabel numeroFila = new JLabel(String.valueOf(i + 3), SwingConstants.CENTER);
            numeroFila.setForeground(Color.WHITE);
            numeroFila.setFont(new Font("Segoe UI", Font.BOLD, 14));
            panelFilas.add(numeroFila);
        }

        return panelFilas;
    }

    private JPanel crearPanelPrincipalAsientos() {
        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        panelPrincipal.setBackground(new Color(40, 40, 40));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(2, 2, 2, 2);

        // Crear los 5 bloques
        for (int bloque = 0; bloque < 5; bloque++) {
            JPanel panelBloque = crearPanelBloque(bloque, gbc);
            panelPrincipal.add(panelBloque, gbc);
        }

        return panelPrincipal;
    }

    private JPanel crearPanelBloque(int bloque, GridBagConstraints gbc) {
        JPanel panelBloque = new JPanel(new BorderLayout());

        boolean esPasillo = (bloque == 1 || bloque == 3);

        if (esPasillo) {
            panelBloque = crearPanelPasilloLateral(bloque);
            configurarConstraintsPasillo(gbc, bloque);
        } else {
            panelBloque = crearPanelBloqueAsientos(bloque);
            configurarConstraintsBloque(gbc, bloque);
        }

        return panelBloque;
    }

    private JPanel crearPanelPasilloLateral(int bloque) {
        JPanel panelPasillo = new JPanel(new BorderLayout());
        panelPasillo.setBackground(new Color(80, 80, 80));
        panelPasillo.setBorder(BorderFactory.createLineBorder(new Color(120, 120, 120), 2));

        JLabel labelPasillo = new JLabel("PASILLO") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(0, 0, 0, 100));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));

                g2.rotate(Math.toRadians(-90), getWidth() / 2, getHeight() / 2);
                FontMetrics fm = g2.getFontMetrics();
                String texto = "PASILLO";
                int textoAncho = fm.stringWidth(texto);

                int x = (getWidth() - fm.getHeight()) / 2 - 5;
                int y = (getHeight() + textoAncho) / 2 - 25;

                g2.drawString(texto, x, y);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(60, 200);
            }
        };

        labelPasillo.setHorizontalAlignment(SwingConstants.CENTER);
        labelPasillo.setVerticalAlignment(SwingConstants.CENTER);
        panelPasillo.add(labelPasillo, BorderLayout.CENTER);

        return panelPasillo;
    }

    private JPanel crearPanelBloqueAsientos(int bloque) {
        JPanel panelBloque = new JPanel(new GridLayout(FILAS_ASIENTOS, 1, 0, 12));
        panelBloque.setBackground(new Color(20, 20, 20));

        int startCol = obtenerStartColumna(bloque);
        int numColumnas = obtenerNumColumnas(bloque);

        for (int i = 0; i < FILAS_ASIENTOS; i++) {
            JPanel filaPanel = new JPanel(new GridLayout(1, numColumnas, 8, 0));
            filaPanel.setBackground(new Color(20, 20, 20));

            for (int j = 0; j < numColumnas; j++) {
                int columnaReal = startCol + j;
                AsientoButton asiento = new AsientoButton(i, columnaReal, sala);
                asiento.addPropertyChangeListener("asientoCambiado", this);
                botonesAsientos[i][columnaReal] = asiento;
                filaPanel.add(asiento);
            }
            panelBloque.add(filaPanel);
        }

        return panelBloque;
    }

    private int obtenerStartColumna(int bloque) {
        switch (bloque) {
            case 0:
                return 0;
            case 2:
                return LEFT_BLOCK + AISLE_WIDTH;
            case 4:
                return LEFT_BLOCK + AISLE_WIDTH + CENTER_BLOCK + AISLE_WIDTH;
            default:
                return 0;
        }
    }

    private int obtenerNumColumnas(int bloque) {
        switch (bloque) {
            case 0:
                return LEFT_BLOCK;
            case 2:
                return CENTER_BLOCK;
            case 4:
                return RIGHT_BLOCK;
            default:
                return 0;
        }
    }

    private void configurarConstraintsPasillo(GridBagConstraints gbc, int bloque) {
        if (bloque == 1) {
            gbc.gridx = 1;
            gbc.weightx = 1.8;
        } else {
            gbc.gridx = 3;
            gbc.weightx = 1.8;
        }
    }

    private void configurarConstraintsBloque(GridBagConstraints gbc, int bloque) {
        switch (bloque) {
            case 0:
                gbc.gridx = 0;
                gbc.weightx = LEFT_BLOCK * 1.0;
                break;
            case 2:
                gbc.gridx = 2;
                gbc.weightx = CENTER_BLOCK * 1.0;
                break;
            case 4:
                gbc.gridx = 4;
                gbc.weightx = RIGHT_BLOCK * 1.0;
                break;
        }
    }

    private JPanel crearPanelInferior() {
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(new Color(20, 20, 20));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        // Panel de leyenda
        JPanel leyenda = crearPanelLeyenda();

        // Panel de información derecha
        JPanel panelDerecho = crearPanelDerecho();

        panelInferior.add(leyenda, BorderLayout.WEST);
        panelInferior.add(panelDerecho, BorderLayout.EAST);

        return panelInferior;
    }

    private JPanel crearPanelLeyenda() {
        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        leyenda.setBackground(new Color(20, 20, 20));

        leyenda.add(crearItemLeyenda(new Color(120, 120, 120), "Disponible"));
        leyenda.add(crearItemLeyenda(new Color(40, 80, 180), "Seleccionado"));
        leyenda.add(crearItemLeyenda(new Color(180, 40, 40), "Ocupado"));

        return leyenda;
    }

    private JPanel crearItemLeyenda(Color color, String texto) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        p.setBackground(new Color(20, 20, 20));

        JButton ejemplo = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setColor(color);
                g2.fillRoundRect(2, 2, w - 4, h - 4, 8, 8);
                g2.setColor(color.brighter());
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(2, 2, w - 4, h - 4, 8, 8);
            }
        };
        ejemplo.setPreferredSize(new Dimension(24, 18));
        ejemplo.setContentAreaFilled(false);
        ejemplo.setBorderPainted(false);
        ejemplo.setOpaque(false);

        JLabel l = new JLabel(texto);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        p.add(ejemplo);
        p.add(l);
        return p;
    }

    private JPanel crearPanelDerecho() {
        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.setBackground(new Color(20, 20, 20));

        // Contador
        contadorLabel.setForeground(Color.WHITE);
        contadorLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Botones de control
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setBackground(new Color(20, 20, 20));

        JButton btnReporte = new JButton("Generar Reporte");
        JButton btnLimpiar = new JButton("Limpiar Selecciones");
        JButton btnConfirmar = new JButton("Confirmar Selección");

        configurarBoton(btnReporte);
        configurarBoton(btnLimpiar);
        configurarBotonConfirmar(btnConfirmar);

        btnReporte.addActionListener(e -> generarReporte());
        btnLimpiar.addActionListener(e -> limpiarSelecciones());
        btnConfirmar.addActionListener(e -> confirmarSelecciones());

        panelBotones.add(btnReporte);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnConfirmar);

        panelDerecho.add(contadorLabel, BorderLayout.NORTH);
        panelDerecho.add(panelBotones, BorderLayout.SOUTH);

        return panelDerecho;
    }

    private void configurarBoton(JButton boton) {
        boton.setBackground(new Color(70, 130, 180));
        boton.setForeground(Color.BLACK);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(new Color(100, 160, 210));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(new Color(70, 130, 180));
            }
        });
    }

    private void configurarBotonConfirmar(JButton boton) {
        boton.setBackground(new Color(40, 160, 60));
        boton.setForeground(Color.BLACK);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(new Color(60, 180, 80));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(new Color(40, 160, 60));
            }
        });
    }

    private void actualizarContador() {
        int count = sala.contarAsientosSeleccionados();
        contadorLabel.setText(count + (count == 1 ? " asiento seleccionado" : " asientos seleccionados"));
    }

    private void generarReporte() {
        try {
            org.json.JSONObject reporte = gestorJson.generarReporte();
            String mensaje = String.format(
                    "📊 REPORTE DE SALA\n\n" +
                            "📅 Fecha del reporte: %s\n" +
                            "🎫 Total de asientos: %d\n" +
                            "🟢 Asientos libres: %d\n" +
                            "🔵 Asientos seleccionados: %d\n" +
                            "🔴 Asientos ocupados: %d\n" +
                            "⏰ Última actualización: %s",
                    reporte.getString("fechaReporte"),
                    reporte.getInt("totalAsientos"),
                    reporte.getInt("asientosLibres"),
                    reporte.getInt("asientosSeleccionados"),
                    reporte.getInt("asientosOcupados"),
                    reporte.getString("ultimaActualizacion")
            );

            JOptionPane.showMessageDialog(this, mensaje, "Reporte de Sala", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al generar el reporte: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarSelecciones() {
        int seleccionados = sala.contarAsientosSeleccionados();

        if (seleccionados == 0) {
            JOptionPane.showMessageDialog(this,
                    "No hay asientos seleccionados para limpiar.",
                    "Sin Selecciones",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de que quieres limpiar " + seleccionados + " asiento(s) seleccionado(s)?\n" +
                        "Esto convertirá todos los asientos seleccionados a libres.",
                "Limpiar Selecciones",
                JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            gestorJson.limpiarSelecciones();
            actualizarVisualizacionAsientos();
            JOptionPane.showMessageDialog(this,
                    seleccionados + " selección(es) limpiada(s) correctamente.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void confirmarSelecciones() {
        int seleccionados = sala.contarAsientosSeleccionados();

        if (seleccionados == 0) {
            JOptionPane.showMessageDialog(this,
                    "No hay asientos seleccionados para confirmar.\n" +
                            "Selecciona algunos asientos primero.",
                    "Sin Selecciones",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Confirmar " + seleccionados + " asiento(s) seleccionado(s)?\n\n" +
                        "✅ Los asientos seleccionados (azules) pasarán a OCUPADOS (rojos)\n" +
                        "💾 Se guardarán permanentemente en el archivo JSON\n" +
                        "🔒 No podrán ser modificados después",
                "Confirmar Selección",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (respuesta == JOptionPane.YES_OPTION) {
            int confirmados = gestorJson.confirmarSelecciones();
            actualizarVisualizacionAsientos();

            JOptionPane.showMessageDialog(this,
                    "✅ " + confirmados + " asiento(s) confirmado(s) exitosamente!\n\n" +
                            "🔴 Ahora aparecen en ROJO (ocupados)\n" +
                            "💾 Guardados permanentemente en JSON\n" +
                            "🔒 Ya no se pueden modificar",
                    "Confirmación Exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Método para forzar la actualización de todos los asientos
     */
    public void actualizarVisualizacionAsientos() {
        for (int i = 0; i < FILAS_ASIENTOS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                if (botonesAsientos[i][j] != null) {
                    botonesAsientos[i][j].repaint();
                }
            }
        }
        actualizarContador();
        System.out.println("🔄 Visualización actualizada");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("asientoCambiado".equals(evt.getPropertyName())) {
            actualizarContador();
        }
    }

    /**
     * Método para obtener el gestor JSON desde otras clases
     */
    public GestorJsonAsientos getGestorJson() {
        return gestorJson;
    }
}
