package Clases;

import Enumeradores.EstadoAsiento;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class AsientoButton extends StackPane {
    private final int fila;
    private final int columna;
    private final SalaCine sala;
    private EstadoAsiento estado;
    private Runnable onAsientoCambiado;

    public AsientoButton(int fila, int columna, SalaCine sala) {
        this.fila = fila;
        this.columna = columna;
        this.sala = sala;
        this.estado = sala.getEstadoAsiento(fila, columna);

        configurarApariencia();
        agregarListeners();
        actualizarToolTip();
        dibujarAsiento();
    }

    private void configurarApariencia() {
        setPrefSize(60, 52);
        setStyle("-fx-background-color: transparent;");
        setPickOnBounds(true); // ✅ IMPORTANTE: Permite clicks en áreas transparentes
    }

    private void agregarListeners() {
        setOnMouseClicked(event -> {
            System.out.println("🖱️ Click en asiento " + fila + "-" + columna + " estado: " + estado);

            EstadoAsiento estadoActual = sala.getEstadoAsiento(fila, columna);
            if (estadoActual == EstadoAsiento.LIBRE ||
                    estadoActual == EstadoAsiento.SELECCIONADO) {

                sala.toggleSeleccionAsiento(fila, columna);
                this.estado = sala.getEstadoAsiento(fila, columna);
                System.out.println("🔄 Nuevo estado: " + estado);

                dibujarAsiento();
                actualizarToolTip();

                // Notificar que el asiento cambió
                if (onAsientoCambiado != null) {
                    onAsientoCambiado.run();
                }
            }
            event.consume(); // Evitar que el evento se propague
        });
    }

    private void dibujarAsiento() {
        getChildren().clear();

        // Determinar colores según estado
        Color colorBase, colorSombra, colorBorde;

        switch (estado) {
            case OCUPADO:
                colorBase = Color.rgb(180, 40, 40);
                colorSombra = Color.rgb(120, 20, 20);
                colorBorde = Color.rgb(220, 100, 100);
                break;
            case SELECCIONADO:
                colorBase = Color.rgb(40, 80, 180);
                colorSombra = Color.rgb(20, 40, 120);
                colorBorde = Color.rgb(100, 140, 220);
                break;
            case LIBRE:
            default:
                colorBase = Color.rgb(120, 120, 120);
                colorSombra = Color.rgb(80, 80, 80);
                colorBorde = Color.rgb(180, 180, 180);
                break;
        }

        // Sombra
        Rectangle sombra = new Rectangle(44, 40);
        sombra.setArcWidth(10);
        sombra.setArcHeight(10);
        sombra.setFill(Color.rgb(0, 0, 0, 0.4));

        // Asiento principal
        Rectangle asiento = new Rectangle(44, 26);
        asiento.setArcWidth(18);
        asiento.setArcHeight(18);

        LinearGradient gradienteAsiento = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, colorBase.brighter()),
                new Stop(1, colorBase)
        );
        asiento.setFill(gradienteAsiento);
        asiento.setStroke(colorBorde);
        asiento.setStrokeWidth(2);

        // Respaldo
        Rectangle respaldo = new Rectangle(44, 10);
        respaldo.setArcWidth(22);
        respaldo.setArcHeight(22);

        LinearGradient gradienteRespaldo = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, colorBase),
                new Stop(1, colorSombra)
        );
        respaldo.setFill(gradienteRespaldo);
        respaldo.setStroke(colorBorde);
        respaldo.setStrokeWidth(2);
        respaldo.setTranslateY(18);

        // ✅ APOYABRAZOS (NUEVO)
        Rectangle apoyoIzquierdo = new Rectangle(6, 20);
        apoyoIzquierdo.setArcWidth(8);
        apoyoIzquierdo.setArcHeight(8);
        apoyoIzquierdo.setFill(colorSombra.darker());
        apoyoIzquierdo.setTranslateX(-25);
        apoyoIzquierdo.setTranslateY(8);

        Rectangle apoyoDerecho = new Rectangle(6, 20);
        apoyoDerecho.setArcWidth(8);
        apoyoDerecho.setArcHeight(8);
        apoyoDerecho.setFill(colorSombra.darker());
        apoyoDerecho.setTranslateX(25);
        apoyoDerecho.setTranslateY(8);

        // Patas
        Rectangle pataIzquierda = new Rectangle(8, 12);
        pataIzquierda.setArcWidth(10);
        pataIzquierda.setArcHeight(10);
        pataIzquierda.setFill(colorSombra.darker());
        pataIzquierda.setTranslateX(-18);
        pataIzquierda.setTranslateY(6);

        Rectangle pataDerecha = new Rectangle(8, 12);
        pataDerecha.setArcWidth(10);
        pataDerecha.setArcHeight(10);
        pataDerecha.setFill(colorSombra.darker());
        pataDerecha.setTranslateX(18);
        pataDerecha.setTranslateY(6);

        // Texto de la etiqueta
        Text texto = new Text(String.valueOf((char)('A' + columna)) + (fila + 1));
        texto.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        texto.setFill(Color.WHITE);
        texto.setStroke(Color.BLACK);
        texto.setStrokeWidth(0.5);

        getChildren().addAll(sombra, apoyoIzquierdo, apoyoDerecho, pataIzquierda, pataDerecha,
                asiento, respaldo, texto);
    }

    private void actualizarToolTip() {
        String estadoStr = "";
        switch (estado) {
            case LIBRE: estadoStr = "Libre"; break;
            case SELECCIONADO: estadoStr = "Seleccionado"; break;
            case OCUPADO: estadoStr = "Ocupado"; break;
        }
        Tooltip.install(this, new Tooltip("Asiento " + (fila + 1) + "-" +
                (char)('A' + columna) + " (" + estadoStr + ")"));
    }

    /**
     * Método público para forzar el redibujado del asiento
     */
    public void redibujar() {
        this.estado = sala.getEstadoAsiento(fila, columna);
        dibujarAsiento();
        actualizarToolTip();
    }

    /**
     * Método para establecer callback cuando el asiento cambia
     */
    public void setOnAsientoCambiado(Runnable callback) {
        this.onAsientoCambiado = callback;
    }

    // Getters
    public int getFila() { return fila; }
    public int getColumna() { return columna; }
    public EstadoAsiento getEstado() { return estado; }
}


//////////////////////////////v2///////////////////////////////
//
//package Clases;
//
//import Enumeradores.EstadoAsiento;
//import javafx.scene.control.Tooltip;
//import javafx.scene.layout.StackPane;
//import javafx.scene.paint.*;
//import javafx.scene.shape.Rectangle;
//import javafx.scene.text.Font;
//import javafx.scene.text.FontWeight;
//import javafx.scene.text.Text;
//
//public class AsientoButton extends StackPane {
//    private final int fila;
//    private final int columna;
//    private final SalaCine sala;
//    private EstadoAsiento estado;
//
//    public AsientoButton(int fila, int columna, SalaCine sala) {
//        this.fila = fila;
//        this.columna = columna;
//        this.sala = sala;
//        this.estado = sala.getEstadoAsiento(fila, columna);
//
//        configurarApariencia();
//        agregarListeners();
//        actualizarToolTip();
//        dibujarAsiento();
//    }
//
//    private void configurarApariencia() {
//        setPrefSize(60, 52);
//        setStyle("-fx-background-color: transparent;");
//    }
//
//    private void agregarListeners() {
//        setOnMouseClicked(event -> {
//            EstadoAsiento estadoActual = sala.getEstadoAsiento(fila, columna);
//            if (estadoActual == EstadoAsiento.LIBRE ||
//                    estadoActual == EstadoAsiento.SELECCIONADO) {
//
//                sala.toggleSeleccionAsiento(fila, columna);
//                this.estado = sala.getEstadoAsiento(fila, columna);
//                dibujarAsiento();
//                actualizarToolTip();
//            }
//        });
//    }
//
//    private void dibujarAsiento() {
//        getChildren().clear();
//
//        // Determinar colores según estado
//        Color colorBase, colorSombra, colorBorde;
//
//        switch (estado) {
//            case OCUPADO:
//                colorBase = Color.rgb(180, 40, 40);
//                colorSombra = Color.rgb(120, 20, 20);
//                colorBorde = Color.rgb(220, 100, 100);
//                break;
//            case SELECCIONADO:
//                colorBase = Color.rgb(40, 80, 180);
//                colorSombra = Color.rgb(20, 40, 120);
//                colorBorde = Color.rgb(100, 140, 220);
//                break;
//            case LIBRE:
//            default:
//                colorBase = Color.rgb(120, 120, 120);
//                colorSombra = Color.rgb(80, 80, 80);
//                colorBorde = Color.rgb(180, 180, 180);
//                break;
//        }
//
//        // Sombra
//        Rectangle sombra = new Rectangle(44, 40);
//        sombra.setArcWidth(10);
//        sombra.setArcHeight(10);
//        sombra.setFill(Color.rgb(0, 0, 0, 0.4));
//
//        // Asiento principal
//        Rectangle asiento = new Rectangle(44, 26);
//        asiento.setArcWidth(18);
//        asiento.setArcHeight(18);
//
//        LinearGradient gradienteAsiento = new LinearGradient(
//                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
//                new Stop(0, colorBase.brighter()),
//                new Stop(1, colorBase)
//        );
//        asiento.setFill(gradienteAsiento);
//        asiento.setStroke(colorBorde);
//        asiento.setStrokeWidth(2);
//
//        // Respaldo
//        Rectangle respaldo = new Rectangle(44, 10);
//        respaldo.setArcWidth(22);
//        respaldo.setArcHeight(22);
//
//        LinearGradient gradienteRespaldo = new LinearGradient(
//                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
//                new Stop(0, colorBase),
//                new Stop(1, colorSombra)
//        );
//        respaldo.setFill(gradienteRespaldo);
//        respaldo.setStroke(colorBorde);
//        respaldo.setStrokeWidth(2);
//        respaldo.setTranslateY(18);
//
//        // Patas
//        Rectangle pataIzquierda = new Rectangle(8, 12);
//        pataIzquierda.setArcWidth(10);
//        pataIzquierda.setArcHeight(10);
//        pataIzquierda.setFill(colorSombra.darker());
//        pataIzquierda.setTranslateX(-18);
//        pataIzquierda.setTranslateY(6);
//
//        Rectangle pataDerecha = new Rectangle(8, 12);
//        pataDerecha.setArcWidth(10);
//        pataDerecha.setArcHeight(10);
//        pataDerecha.setFill(colorSombra.darker());
//        pataDerecha.setTranslateX(18);
//        pataDerecha.setTranslateY(6);
//
//        // Texto de la etiqueta
//        Text texto = new Text(String.valueOf((char)('A' + columna)) + (fila + 1));
//        texto.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
//        texto.setFill(Color.WHITE);
//        texto.setStroke(Color.BLACK);
//        texto.setStrokeWidth(0.5);
//
//        getChildren().addAll(sombra, pataIzquierda, pataDerecha,
//                asiento, respaldo, texto);
//    }
//
//    private void actualizarToolTip() {
//        String estadoStr = "";
//        switch (estado) {
//            case LIBRE: estadoStr = "Libre"; break;
//            case SELECCIONADO: estadoStr = "Seleccionado"; break;
//            case OCUPADO: estadoStr = "Ocupado"; break;
//        }
//        Tooltip.install(this, new Tooltip("Asiento " + (fila + 1) + "-" +
//                (char)('A' + columna) + " (" + estadoStr + ")"));
//    }
//
//    /**
//     * Método público para forzar el redibujado del asiento
//     */
//    public void redibujar() {
//        this.estado = sala.getEstadoAsiento(fila, columna);
//        dibujarAsiento();
//        actualizarToolTip();
//    }
//
//    // Getters
//    public int getFila() { return fila; }
//    public int getColumna() { return columna; }
//    public EstadoAsiento getEstado() { return estado; }
//}
//
///////////////////////////////////////V1
////package Clases;
////
////import Enumeradores.EstadoAsiento;
////
////import javax.swing.*;
////import java.awt.*;
////import java.awt.event.MouseAdapter;
////import java.awt.event.MouseEvent;
////import java.beans.PropertyChangeSupport;
////
////public class AsientoButton extends JButton {
////    private final int fila;
////    private final int columna;
////    private final SalaCine sala;
////    private final PropertyChangeSupport propertyChangeSupport;
////
////
////    //CONSTRUCTOR
////
////    public AsientoButton(int fila, int columna, SalaCine sala) {
////        this.fila = fila;
////        this.columna = columna;
////        this.sala = sala;
////        this.propertyChangeSupport = new PropertyChangeSupport(this);
////
////        configurarApariencia();
////        agregarListeners();
////        actualizarToolTip();
////    }
////
////    //GETTER Y SETTER
////
////    public int getFila() {
////        return fila;
////    }
////
////    public int getColumna() {
////        return columna;
////    }
////
////
////
////    //METODOS
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
////                EstadoAsiento estadoActual =sala.getEstadoAsiento(fila, columna);
////                if (estadoActual == EstadoAsiento.LIBRE ||
////                        estadoActual == EstadoAsiento.SELECCIONADO) {
////
////                    sala.toggleSeleccionAsiento(fila, columna);
////                    repaint();
////
////                    propertyChangeSupport.firePropertyChange("asientoCambiado", false, true);
////                }
////            }
////        });
////    }
////
////    public void addPropertyChangeListener(String propertyName, java.beans.PropertyChangeListener listener) {
////        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
////    }
////
////    private void actualizarToolTip() {
////        EstadoAsiento estado = sala.getEstadoAsiento(fila, columna);
////        String estadoStr = "";
////        switch (estado) {
////            case LIBRE: estadoStr = "Libre"; break;
////            case SELECCIONADO: estadoStr = "Seleccionado"; break;
////            case OCUPADO: estadoStr = "Ocupado"; break;
////        }
////        setToolTipText("Asiento " + (fila + 1) + "-" + (char)('A' + columna) + " (" + estadoStr + ")");
////    }
////
////
////    @Override
////    protected void paintComponent(Graphics g) {
////        Graphics2D g2 = (Graphics2D) g.create();
////        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
////
////        EstadoAsiento estado = sala.getEstadoAsiento(fila, columna);
////        pintarAsiento(g2, estado);
////
////        g2.dispose();
////    }
////
////    private void pintarAsiento(Graphics2D g2, EstadoAsiento estado) {
////        int w = getWidth();
////        int h = getHeight();
////
////        Color colorBase, sombra, borde;
////
////        switch (estado) {
////            case OCUPADO:
////                colorBase = new Color(180, 40, 40);
////                sombra = new Color(120, 20, 20);
////                borde = new Color(220, 100, 100);
////                break;
////            case SELECCIONADO:
////                colorBase = new Color(40, 80, 180);
////                sombra = new Color(20, 40, 120);
////                borde = new Color(100, 140, 220);
////                break;
////            case LIBRE:
////            default:
////                colorBase = new Color(120, 120, 120);
////                sombra = new Color(80, 80, 80);
////                borde = new Color(180, 180, 180);
////                break;
////        }
////
////        g2.setColor(new Color(0, 0, 0, 100));
////        g2.fillRoundRect(4, 4, w - 8, h - 8, 10, 10);
////
////        int asientoAlto = h * 2 / 3 - 8;
////        GradientPaint gradAsiento = new GradientPaint(0, 0, colorBase.brighter(), 0, asientoAlto, colorBase);
////        g2.setPaint(gradAsiento);
////        g2.fillRoundRect(8, 6, w - 16, asientoAlto, 18, 18);
////
////        int respaldoAlto = h / 4;
////        GradientPaint gradRespaldo = new GradientPaint(0, asientoAlto, colorBase, 0, h - 12, sombra);
////        g2.setPaint(gradRespaldo);
////        g2.fillRoundRect(8, asientoAlto + 4, w - 16, respaldoAlto, 22, 22);
////
////        g2.setPaint(new GradientPaint(0, 0, sombra.darker(), 0, h, sombra));
////        g2.fillRoundRect(2, asientoAlto / 2, 10, h / 3, 10, 10);
////        g2.fillRoundRect(w - 12, asientoAlto / 2, 10, h / 3, 10, 10);
////
////        g2.setStroke(new BasicStroke(2));
////        g2.setColor(borde);
////        g2.drawRoundRect(8, 6, w - 16, asientoAlto, 18, 18);
////        g2.drawRoundRect(8, asientoAlto + 4, w - 16, respaldoAlto, 22, 22);
////
////        pintarEtiqueta(g2, w, h, asientoAlto);
////
////        actualizarToolTip();
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
////        g2.setColor(Color.BLACK);
////        g2.setStroke(new BasicStroke(3));
////        g2.drawString(etiqueta, x, y);
////
////        g2.setColor(Color.WHITE);
////        g2.setFont(font);
////        g2.drawString(etiqueta, x, y);
////    }
////}