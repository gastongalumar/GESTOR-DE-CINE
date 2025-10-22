package Clases;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeSupport;

public class AsientoButton extends JButton {
    private final int fila;
    private final int columna;
    private final SalaCine sala;
    private final PropertyChangeSupport propertyChangeSupport;

    public AsientoButton(int fila, int columna, SalaCine sala) {
        this.fila = fila;
        this.columna = columna;
        this.sala = sala;
        this.propertyChangeSupport = new PropertyChangeSupport(this);

        configurarApariencia();
        agregarListeners();
        actualizarToolTip();
    }

    private void configurarApariencia() {
        setPreferredSize(new Dimension(60, 52));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
    }

    private void agregarListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SalaCine.EstadoAsiento estadoActual = sala.getEstadoAsiento(fila, columna);
                if (estadoActual == SalaCine.EstadoAsiento.LIBRE ||
                        estadoActual == SalaCine.EstadoAsiento.SELECCIONADO) {

                    sala.toggleSeleccionAsiento(fila, columna);
                    repaint();

                    // Notificar del cambio
                    propertyChangeSupport.firePropertyChange("asientoCambiado", false, true);
                }
            }
        });
    }

    public void addPropertyChangeListener(String propertyName, java.beans.PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    private void actualizarToolTip() {
        SalaCine.EstadoAsiento estado = sala.getEstadoAsiento(fila, columna);
        String estadoStr = "";
        switch (estado) {
            case LIBRE: estadoStr = "Libre"; break;
            case SELECCIONADO: estadoStr = "Seleccionado"; break;
            case OCUPADO: estadoStr = "Ocupado"; break;
        }
        setToolTipText("Asiento " + (fila + 1) + "-" + (char)('A' + columna) + " (" + estadoStr + ")");
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        SalaCine.EstadoAsiento estado = sala.getEstadoAsiento(fila, columna);
        pintarAsiento(g2, estado);

        g2.dispose();
    }

    private void pintarAsiento(Graphics2D g2, SalaCine.EstadoAsiento estado) {
        int w = getWidth();
        int h = getHeight();

        Color colorBase, sombra, borde;

        switch (estado) {
            case OCUPADO:
                colorBase = new Color(180, 40, 40);    // Rojo
                sombra = new Color(120, 20, 20);
                borde = new Color(220, 100, 100);
                break;
            case SELECCIONADO:
                colorBase = new Color(40, 80, 180);    // Azul
                sombra = new Color(20, 40, 120);
                borde = new Color(100, 140, 220);
                break;
            case LIBRE:
            default:
                colorBase = new Color(120, 120, 120);  // Gris
                sombra = new Color(80, 80, 80);
                borde = new Color(180, 180, 180);
                break;
        }

        // Sombra
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRoundRect(4, 4, w - 8, h - 8, 10, 10);

        // Asiento
        int asientoAlto = h * 2 / 3 - 8;
        GradientPaint gradAsiento = new GradientPaint(0, 0, colorBase.brighter(), 0, asientoAlto, colorBase);
        g2.setPaint(gradAsiento);
        g2.fillRoundRect(8, 6, w - 16, asientoAlto, 18, 18);

        // Respaldo
        int respaldoAlto = h / 4;
        GradientPaint gradRespaldo = new GradientPaint(0, asientoAlto, colorBase, 0, h - 12, sombra);
        g2.setPaint(gradRespaldo);
        g2.fillRoundRect(8, asientoAlto + 4, w - 16, respaldoAlto, 22, 22);

        // Patas
        g2.setPaint(new GradientPaint(0, 0, sombra.darker(), 0, h, sombra));
        g2.fillRoundRect(2, asientoAlto / 2, 10, h / 3, 10, 10);
        g2.fillRoundRect(w - 12, asientoAlto / 2, 10, h / 3, 10, 10);

        // Bordes
        g2.setStroke(new BasicStroke(2));
        g2.setColor(borde);
        g2.drawRoundRect(8, 6, w - 16, asientoAlto, 18, 18);
        g2.drawRoundRect(8, asientoAlto + 4, w - 16, respaldoAlto, 22, 22);

        // Etiqueta
        pintarEtiqueta(g2, w, h, asientoAlto);

        // Actualizar tooltip
        actualizarToolTip();
    }

    private void pintarEtiqueta(Graphics2D g2, int w, int h, int asientoAlto) {
        String etiqueta = String.valueOf((char)('A' + columna)) + (fila + 1);
        Font font = new Font("Segoe UI", Font.BOLD, 12);
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics(font);
        int strW = fm.stringWidth(etiqueta);

        int x = (w - strW) / 2;
        int y = 6 + (asientoAlto - fm.getHeight()) / 2 + fm.getAscent();

        // Sombra del texto
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3));
        g2.drawString(etiqueta, x, y);

        // Texto principal
        g2.setColor(Color.WHITE);
        g2.setFont(font);
        g2.drawString(etiqueta, x, y);
    }
}
//package Clases;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.beans.PropertyChangeSupport;
//
//public class AsientoButton extends JButton {
//    private final int fila;
//    private final int columna;
//    private final SalaCine sala;
//    private final PropertyChangeSupport propertyChangeSupport;
//
//    public AsientoButton(int fila, int columna, SalaCine sala) {
//        this.fila = fila;
//        this.columna = columna;
//        this.sala = sala;
//        this.propertyChangeSupport = new PropertyChangeSupport(this);
//
//        configurarApariencia();
//        agregarListeners();
//        actualizarToolTip();
//    }
//
//    private void configurarApariencia() {
//        setPreferredSize(new Dimension(60, 52));
//        setContentAreaFilled(false);
//        setBorderPainted(false);
//        setFocusPainted(false);
//        setOpaque(false);
//    }
//
//    private void agregarListeners() {
//        addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                SalaCine.EstadoAsiento estadoActual = sala.getEstadoAsiento(fila, columna);
//                if (estadoActual == SalaCine.EstadoAsiento.LIBRE ||
//                        estadoActual == SalaCine.EstadoAsiento.SELECCIONADO) {
//                    sala.toggleSeleccionAsiento(fila, columna);
//                    repaint();
//
//                    // Notificar del cambio
//                    propertyChangeSupport.firePropertyChange("asientoCambiado", false, true);
//                }
//            }
//        });
//    }
//
//    public void addPropertyChangeListener(String propertyName, java.beans.PropertyChangeListener listener) {
//        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
//    }
//
//    private void actualizarToolTip() {
//        setToolTipText("Asiento " + (fila + 1) + "-" + (char)('A' + columna));
//    }
//
//    public int getFila() {
//        return fila;
//    }
//
//    public int getColumna() {
//        return columna;
//    }
//
//    @Override
//    protected void paintComponent(Graphics g) {
//        Graphics2D g2 = (Graphics2D) g.create();
//        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//        SalaCine.EstadoAsiento estado = sala.getEstadoAsiento(fila, columna);
//        pintarAsiento(g2, estado);
//
//        g2.dispose();
//    }
//
//    private void pintarAsiento(Graphics2D g2, SalaCine.EstadoAsiento estado) {
//        int w = getWidth();
//        int h = getHeight();
//
//        Color colorBase, sombra, borde;
//
//        switch (estado) {
//            case OCUPADO:
//                colorBase = new Color(180, 40, 40);    // Rojo
//                sombra = new Color(120, 20, 20);
//                borde = new Color(220, 100, 100);
//                break;
//            case SELECCIONADO:
//                colorBase = new Color(40, 80, 180);    // Azul
//                sombra = new Color(20, 40, 120);
//                borde = new Color(100, 140, 220);
//                break;
//            case LIBRE:
//            default:
//                colorBase = new Color(120, 120, 120);  // Gris
//                sombra = new Color(80, 80, 80);
//                borde = new Color(180, 180, 180);
//                break;
//        }
//
//        // ... (resto del código de pintado igual que antes)
//        // Sombra
//        g2.setColor(new Color(0, 0, 0, 100));
//        g2.fillRoundRect(4, 4, w - 8, h - 8, 10, 10);
//
//        // Asiento
//        int asientoAlto = h * 2 / 3 - 8;
//        GradientPaint gradAsiento = new GradientPaint(0, 0, colorBase.brighter(), 0, asientoAlto, colorBase);
//        g2.setPaint(gradAsiento);
//        g2.fillRoundRect(8, 6, w - 16, asientoAlto, 18, 18);
//
//        // Respaldo
//        int respaldoAlto = h / 4;
//        GradientPaint gradRespaldo = new GradientPaint(0, asientoAlto, colorBase, 0, h - 12, sombra);
//        g2.setPaint(gradRespaldo);
//        g2.fillRoundRect(8, asientoAlto + 4, w - 16, respaldoAlto, 22, 22);
//
//        // Patas
//        g2.setPaint(new GradientPaint(0, 0, sombra.darker(), 0, h, sombra));
//        g2.fillRoundRect(2, asientoAlto / 2, 10, h / 3, 10, 10);
//        g2.fillRoundRect(w - 12, asientoAlto / 2, 10, h / 3, 10, 10);
//
//        // Bordes
//        g2.setStroke(new BasicStroke(2));
//        g2.setColor(borde);
//        g2.drawRoundRect(8, 6, w - 16, asientoAlto, 18, 18);
//        g2.drawRoundRect(8, asientoAlto + 4, w - 16, respaldoAlto, 22, 22);
//
//        // Etiqueta
//        pintarEtiqueta(g2, w, h, asientoAlto);
//    }
//
//    private void pintarEtiqueta(Graphics2D g2, int w, int h, int asientoAlto) {
//        String etiqueta = String.valueOf((char)('A' + columna)) + (fila + 1);
//        Font font = new Font("Segoe UI", Font.BOLD, 12);
//        g2.setFont(font);
//        FontMetrics fm = g2.getFontMetrics(font);
//        int strW = fm.stringWidth(etiqueta);
//
//        int x = (w - strW) / 2;
//        int y = 6 + (asientoAlto - fm.getHeight()) / 2 + fm.getAscent();
//
//        // Sombra del texto
//        g2.setColor(Color.BLACK);
//        g2.setStroke(new BasicStroke(3));
//        g2.drawString(etiqueta, x, y);
//
//        // Texto principal
//        g2.setColor(Color.WHITE);
//        g2.setFont(font);
//        g2.drawString(etiqueta, x, y);
//    }
//}
//
//
////package Clases;
////
////import javax.swing.*;
////import java.awt.*;
////import java.awt.event.MouseAdapter;
////import java.awt.event.MouseEvent;
////
////public class AsientoButton extends JButton {
////    private final int fila;
////    private final int columna;
////    private final SalaCine sala;
////
////    public AsientoButton(int fila, int columna, SalaCine sala) {
////        this.fila = fila;
////        this.columna = columna;
////        this.sala = sala;
////
////        configurarApariencia();
////        agregarListeners();
////        actualizarToolTip();
////    }
////
////    private void configurarApariencia() {
////        setPreferredSize(new Dimension(60, 52));
////        setContentAreaFilled(false);
////        setBorderPainted(false);
////        setFocusPainted(false);
////        setOpaque(false);
////    }
////
////    private void agregarListeners() {
////        addMouseListener(new MouseAdapter() {
////            @Override
////            public void mouseClicked(MouseEvent e) {
////                SalaCine.EstadoAsiento estadoActual = sala.getEstadoAsiento(fila, columna);
////                if (estadoActual == SalaCine.EstadoAsiento.LIBRE ||
////                        estadoActual == SalaCine.EstadoAsiento.SELECCIONADO) {
////                    sala.toggleSeleccionAsiento(fila, columna);
////                    repaint();
////
////                    // Notificar al contenedor principal para actualizar el contador
////                    firePropertyChange("asientoCambiado", false, true);
////                }
////            }
////        });
////    }
////
////    private void actualizarToolTip() {
////        setToolTipText("Asiento " + (fila + 1) + "-" + (char)('A' + columna));
////    }
////
////    @Override
////    protected void paintComponent(Graphics g) {
////        Graphics2D g2 = (Graphics2D) g.create();
////        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
////
////        SalaCine.EstadoAsiento estado = sala.getEstadoAsiento(fila, columna);
////        pintarAsiento(g2, estado);
////
////        g2.dispose();
////    }
////
////    private void pintarAsiento(Graphics2D g2, SalaCine.EstadoAsiento estado) {
////        int w = getWidth();
////        int h = getHeight();
////
////        Color colorBase, sombra, borde;
////
////        switch (estado) {
////            case OCUPADO:
////                colorBase = new Color(180, 40, 40);    // Rojo
////                sombra = new Color(120, 20, 20);
////                borde = new Color(220, 100, 100);
////                break;
////            case SELECCIONADO:
////                colorBase = new Color(40, 80, 180);    // Azul
////                sombra = new Color(20, 40, 120);
////                borde = new Color(100, 140, 220);
////                break;
////            case LIBRE:
////            default:
////                colorBase = new Color(120, 120, 120);  // Gris
////                sombra = new Color(80, 80, 80);
////                borde = new Color(180, 180, 180);
////                break;
////        }
////
////        // Sombra
////        g2.setColor(new Color(0, 0, 0, 100));
////        g2.fillRoundRect(4, 4, w - 8, h - 8, 10, 10);
////
////        // Asiento
////        int asientoAlto = h * 2 / 3 - 8;
////        GradientPaint gradAsiento = new GradientPaint(0, 0, colorBase.brighter(), 0, asientoAlto, colorBase);
////        g2.setPaint(gradAsiento);
////        g2.fillRoundRect(8, 6, w - 16, asientoAlto, 18, 18);
////
////        // Respaldo
////        int respaldoAlto = h / 4;
////        GradientPaint gradRespaldo = new GradientPaint(0, asientoAlto, colorBase, 0, h - 12, sombra);
////        g2.setPaint(gradRespaldo);
////        g2.fillRoundRect(8, asientoAlto + 4, w - 16, respaldoAlto, 22, 22);
////
////        // Patas
////        g2.setPaint(new GradientPaint(0, 0, sombra.darker(), 0, h, sombra));
////        g2.fillRoundRect(2, asientoAlto / 2, 10, h / 3, 10, 10);
////        g2.fillRoundRect(w - 12, asientoAlto / 2, 10, h / 3, 10, 10);
////
////        // Bordes
////        g2.setStroke(new BasicStroke(2));
////        g2.setColor(borde);
////        g2.drawRoundRect(8, 6, w - 16, asientoAlto, 18, 18);
////        g2.drawRoundRect(8, asientoAlto + 4, w - 16, respaldoAlto, 22, 22);
////
////        // Etiqueta
////        pintarEtiqueta(g2, w, h, asientoAlto);
////    }
////
////    private void pintarEtiqueta(Graphics2D g2, int w, int h, int asientoAlto) {
////        String etiqueta = String.valueOf((char)('A' + columna)) + (fila + 1);
////        Font font = new Font("Segoe UI", Font.BOLD, 12);
////        g2.setFont(font);
////        FontMetrics fm = g2.getFontMetrics(font);
////        int strW = fm.stringWidth(etiqueta);
////
////        int x = (w - strW) / 2;
////        int y = 6 + (asientoAlto - fm.getHeight()) / 2 + fm.getAscent();
////
////        // Sombra del texto
////        g2.setColor(Color.BLACK);
////        g2.setStroke(new BasicStroke(3));
////        g2.drawString(etiqueta, x, y);
////
////        // Texto principal
////        g2.setColor(Color.WHITE);
////        g2.setFont(font);
////        g2.drawString(etiqueta, x, y);
////    }
////}