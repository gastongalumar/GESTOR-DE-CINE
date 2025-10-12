
package Clases;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;



/**
 * SelectorAsientos - Con texto de pasillos laterales bien visible
 */
public class SelectorAsientos extends JFrame {
    // Configuración de la sala
    private final int FILAS = 12; // filas totales
    private final int FILAS_ASIENTOS = 10; // filas con asientos (excluye pasillo delantero)
    private final int LEFT_BLOCK = 3;
    private final int CENTER_BLOCK = 8;
    private final int RIGHT_BLOCK = 3;
    private final int AISLE_WIDTH = 1;
    private final int COLUMNAS = LEFT_BLOCK + AISLE_WIDTH + CENTER_BLOCK + AISLE_WIDTH + RIGHT_BLOCK;

    // Matriz de componentes (null para pasillos)
    private final JButton[][] asientos = new JButton[FILAS_ASIENTOS][COLUMNAS];

    // Lista de índices de columnas que verdaderamente contienen asientos
    private final List<Integer> columnasValidas = new ArrayList<>();

    // Panel inferior con contador
    private final JLabel contadorLabel = new JLabel("0 asientos seleccionados");

    public SelectorAsientos() {
        super("🎬 Selector de Asientos - Sala con tres bloques");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 900);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(20, 20, 20));

        // Agrego las columnas válidas
        for (int c = 0; c < LEFT_BLOCK; c++) columnasValidas.add(c);
        for (int c = LEFT_BLOCK + AISLE_WIDTH; c < LEFT_BLOCK + AISLE_WIDTH + CENTER_BLOCK; c++) columnasValidas.add(c);
        for (int c = LEFT_BLOCK + AISLE_WIDTH + CENTER_BLOCK + AISLE_WIDTH; c < COLUMNAS; c++) columnasValidas.add(c);

        // Panel superior: pantalla
        JLabel pantalla = new JLabel("PANTALLA", SwingConstants.CENTER);
        pantalla.setOpaque(true);
        pantalla.setBackground(new Color(70, 130, 180));
        pantalla.setForeground(Color.WHITE);
        pantalla.setFont(new Font("Segoe UI", Font.BOLD, 30));
        pantalla.setPreferredSize(new Dimension(1000, 70));
        pantalla.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 3));
        add(pantalla, BorderLayout.NORTH);

        // Panel central principal
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(new Color(20, 20, 20));

        // PASILLO DELANTERO LISO (debajo de la pantalla)
        JPanel pasilloDelantero = new JPanel(new BorderLayout());
        pasilloDelantero.setBackground(new Color(80, 80, 80));
        pasilloDelantero.setBorder(BorderFactory.createLineBorder(new Color(120, 120, 120), 2));
        pasilloDelantero.setPreferredSize(new Dimension(0, 80));

        JLabel labelPasilloDelantero = new JLabel("PASILLO PRINCIPAL", SwingConstants.CENTER);
        labelPasilloDelantero.setForeground(new Color(200, 200, 200));
        labelPasilloDelantero.setFont(new Font("Segoe UI", Font.BOLD, 20));
        pasilloDelantero.add(labelPasilloDelantero, BorderLayout.CENTER);

        // Numeración lateral (solo para filas con asientos)
        JPanel panelFilas = new JPanel(new GridLayout(FILAS_ASIENTOS, 1, 0, 12));
        panelFilas.setBackground(new Color(20, 20, 20));
        panelFilas.setPreferredSize(new Dimension(40, 0));
        for (int i = 0; i < FILAS_ASIENTOS; i++) {
            JLabel numeroFila = new JLabel(String.valueOf(i + 3), SwingConstants.CENTER);
            numeroFila.setForeground(Color.WHITE);
            numeroFila.setFont(new Font("Segoe UI", Font.BOLD, 14));
            panelFilas.add(numeroFila);
        }

        // Letras de columnas
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

        // Panel principal que contendrá los bloques de asientos y pasillos laterales
        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        panelPrincipal.setBackground(new Color(40, 40, 40));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Crear constraints para GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(2, 2, 2, 2);

        // Color y estilo unificado para pasillos
        Color colorPasillo = new Color(80, 80, 80);
        Color colorBordePasillo = new Color(120, 120, 120);

        // Crear los 5 bloques: izquierdo, pasillo izquierdo, central, pasillo derecho, derecho
        for (int bloque = 0; bloque < 5; bloque++) {
            JPanel panelBloque = new JPanel(new BorderLayout());

            boolean esPasillo = (bloque == 1 || bloque == 3);

            if (esPasillo) {
                // Pasillo lateral - panel continuo
                panelBloque.setBackground(colorPasillo);
                panelBloque.setBorder(BorderFactory.createLineBorder(colorBordePasillo, 2));

                // Etiqueta "PASILLO" BIEN VISIBLE
                JLabel labelPasillo = new JLabel("PASILLO") {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        // Fondo semi-transparente para mejor legibilidad
                        g2.setColor(new Color(0, 0, 0, 100));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                        // Texto blanco y grande
                        g2.setColor(Color.WHITE);
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));

                        // Rotar texto 90 grados en el centro
                        g2.rotate(Math.toRadians(-90), getWidth() / 2, getHeight() / 2);
                        FontMetrics fm = g2.getFontMetrics();
                        String texto = "PASILLO";
                        int textoAncho = fm.stringWidth(texto);
                        int textoAlto = fm.getHeight();

                        // Centrar perfectamente
                        int x = (getWidth() - textoAlto) / 2 - 5;  // Ajuste fino
                        int y = (getHeight() + textoAncho) / 2 - 25; // Ajuste fino

                        g2.drawString(texto, x, y);
                        g2.dispose();
                    }

                    @Override
                    public Dimension getPreferredSize() {
                        return new Dimension(60, 200); // Tamaño fijo para mejor visualización
                    }
                };
                labelPasillo.setHorizontalAlignment(SwingConstants.CENTER);
                labelPasillo.setVerticalAlignment(SwingConstants.CENTER);
                panelBloque.add(labelPasillo, BorderLayout.CENTER);

                // Pasillos más anchos
                if (bloque == 1) {
                    gbc.gridx = 1;
                    gbc.weightx = 1.8; // Un poco más ancho
                } else {
                    gbc.gridx = 3;
                    gbc.weightx = 1.8; // Un poco más ancho
                }
            } else {
                // Bloque de asientos
                panelBloque.setBackground(new Color(20, 20, 20));
                panelBloque.setLayout(new GridLayout(FILAS_ASIENTOS, 1, 0, 12));

                int startCol = 0;
                int numColumnas = 0;
                switch (bloque) {
                    case 0: // Bloque izquierdo
                        startCol = 0;
                        numColumnas = LEFT_BLOCK;
                        gbc.gridx = 0;
                        gbc.weightx = LEFT_BLOCK * 1.0;
                        break;
                    case 2: // Bloque central
                        startCol = LEFT_BLOCK + AISLE_WIDTH;
                        numColumnas = CENTER_BLOCK;
                        gbc.gridx = 2;
                        gbc.weightx = CENTER_BLOCK * 1.0;
                        break;
                    case 4: // Bloque derecho
                        startCol = LEFT_BLOCK + AISLE_WIDTH + CENTER_BLOCK + AISLE_WIDTH;
                        numColumnas = RIGHT_BLOCK;
                        gbc.gridx = 4;
                        gbc.weightx = RIGHT_BLOCK * 1.0;
                        break;
                }

                // Crear solo asientos (sin pasillo delantero)
                for (int i = 0; i < FILAS_ASIENTOS; i++) {
                    JPanel filaPanel = new JPanel(new GridLayout(1, numColumnas, 8, 0));
                    filaPanel.setBackground(new Color(20, 20, 20));

                    for (int j = 0; j < numColumnas; j++) {
                        int columnaReal = startCol + j;
                        SeatButton asiento = new SeatButton(i, columnaReal);
                        asientos[i][columnaReal] = asiento;
                        filaPanel.add(asiento);
                    }
                    panelBloque.add(filaPanel);
                }
            }

            panelPrincipal.add(panelBloque, gbc);
        }

        // Panel que contiene la numeración y los asientos
        JPanel panelAsientosConNumeros = new JPanel(new BorderLayout());
        panelAsientosConNumeros.setBackground(new Color(20, 20, 20));
        panelAsientosConNumeros.add(panelFilas, BorderLayout.WEST);
        panelAsientosConNumeros.add(panelPrincipal, BorderLayout.CENTER);

        // Agregar todo al panel central en el orden correcto
        panelCentral.add(panelLetras, BorderLayout.NORTH);
        panelCentral.add(pasilloDelantero, BorderLayout.CENTER);
        panelCentral.add(panelAsientosConNumeros, BorderLayout.SOUTH);

        add(panelCentral, BorderLayout.CENTER);

        // Panel inferior: leyenda + contador
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(new Color(20, 20, 20));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        leyenda.setBackground(new Color(20, 20, 20));
        leyenda.add(createLeyendaItem(new Color(120, 120, 120), "Disponible"));
        leyenda.add(createLeyendaItem(new Color(0, 180, 0), "Seleccionado"));

        contadorLabel.setForeground(Color.WHITE);
        contadorLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JPanel rightInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightInfo.setBackground(new Color(20, 20, 20));
        rightInfo.add(contadorLabel);

        panelInferior.add(leyenda, BorderLayout.WEST);
        panelInferior.add(rightInfo, BorderLayout.EAST);
        add(panelInferior, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Actualiza el contador inferior
    private void actualizarContador() {
        int count = 0;
        for (int i = 0; i < FILAS_ASIENTOS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                JButton b = asientos[i][j];
                if (b instanceof SeatButton) {
                    if (((SeatButton) b).isSeleccionado()) count++;
                }
            }
        }
        contadorLabel.setText(count + (count == 1 ? " asiento seleccionado" : " asientos seleccionados"));
    }

    // Panel pequeño para leyenda
    private JPanel createLeyendaItem(Color color, String texto) {
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

    // Clase interna para los botones-asientos
    class SeatButton extends JButton {
        private final int fila;
        private final int columna;
        private boolean seleccionado = false;

        public SeatButton(int fila, int columna) {
            this.fila = fila;
            this.columna = columna;
            setPreferredSize(new Dimension(60, 52));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    seleccionado = !seleccionado;
                    repaint();
                    actualizarContador();
                }
            });

            setToolTipText(getSeatLabel());
        }

        public boolean isSeleccionado() {
            return seleccionado;
        }

        private String getSeatLabel() {
            int idx = columnasValidas.indexOf(columna);
            char letra = (idx >= 0) ? (char) ('A' + idx) : '?';
            return (fila + 3) + String.valueOf(letra);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

//            Color colorBase = seleccionado ? new Color(0, 160, 0) : new Color(200, 20, 20);
//            Color sombra = seleccionado ? new Color(0, 80, 0) : new Color(110, 10, 10);
//            Color borde = seleccionado ? new Color(0, 220, 0) : new Color(255, 100, 100);

            // Colores según selección - VERSIÓN GRIS
            Color colorBase = seleccionado ? new Color(80, 180, 80) : new Color(120, 120, 120);
            Color sombra = seleccionado ? new Color(40, 120, 40) : new Color(80, 80, 80);
            Color borde = seleccionado ? new Color(120, 220, 120) : new Color(180, 180, 180);

            g2.setColor(new Color(0, 0, 0, 100));
            g2.fillRoundRect(4, 4, w - 8, h - 8, 10, 10);

            int asientoAlto = h * 2 / 3 - 8;
            GradientPaint gradAsiento = new GradientPaint(0, 0, colorBase.brighter(), 0, asientoAlto, colorBase);
            g2.setPaint(gradAsiento);
            g2.fillRoundRect(8, 6, w - 16, asientoAlto, 18, 18);

            int respaldoAlto = h / 4;
            GradientPaint gradRespaldo = new GradientPaint(0, asientoAlto, colorBase, 0, h - 12, sombra);
            g2.setPaint(gradRespaldo);
            g2.fillRoundRect(8, asientoAlto + 4, w - 16, respaldoAlto, 22, 22);

            g2.setPaint(new GradientPaint(0, 0, sombra.darker(), 0, h, sombra));
            g2.fillRoundRect(2, asientoAlto / 2, 10, h / 3, 10, 10);
            g2.fillRoundRect(w - 12, asientoAlto / 2, 10, h / 3, 10, 10);

            g2.setStroke(new BasicStroke(2));
            g2.setColor(borde);
            g2.drawRoundRect(8, 6, w - 16, asientoAlto, 18, 18);
            g2.drawRoundRect(8, asientoAlto + 4, w - 16, respaldoAlto, 22, 22);

            String etiqueta = getSeatLabel();
            Font font = new Font("Segoe UI", Font.BOLD, 12);
            g2.setFont(font);
            FontMetrics fm = g2.getFontMetrics(font);
            int strW = fm.stringWidth(etiqueta);

            int x = (w - strW) / 2;
            int y = 6 + (asientoAlto - fm.getHeight()) / 2 + fm.getAscent();

            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(3));
            g2.drawString(etiqueta, x, y);

            g2.setColor(Color.WHITE);
            g2.setFont(font);
            g2.drawString(etiqueta, x, y);

            g2.dispose();
        }
    }

}
