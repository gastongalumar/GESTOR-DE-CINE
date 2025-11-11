package Clases.GestionSelectorAsientos;

import Clases.SalaCine;
import Enumeradores.EstadoAsiento;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.List;

public class AsientoButton extends StackPane {
    private final int fila;
    private final int columna;
    private final SalaCine sala;
    private EstadoAsiento estado;
    private Runnable onAsientoCambiado;
    private final List<Integer> columnasValidas;

    public AsientoButton(int fila, int columna, SalaCine sala, List<Integer> columnasValidas) {
        this.fila = fila;
        this.columna = columna;
        this.sala = sala;
        this.columnasValidas = columnasValidas; // ✅ Guardar la referencia
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
        apoyoIzquierdo.setFill(colorBase.darker());
        apoyoIzquierdo.setTranslateX(-25);
        apoyoIzquierdo.setTranslateY(8);

        Rectangle apoyoDerecho = new Rectangle(6, 20);
        apoyoDerecho.setArcWidth(8);
        apoyoDerecho.setArcHeight(8);
        apoyoDerecho.setFill(colorBase.darker());
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

        String letraFila = String.valueOf((char)('A' + fila));

        // Encontrar el número de columna visible
        int numeroColumnaVisible = -1;
        for (int i = 0; i < columnasValidas.size(); i++) {
            if (columnasValidas.get(i) == columna) {
                numeroColumnaVisible = i + 1;
                break;
            }
        }

        String textoAsiento;
        if (numeroColumnaVisible != -1) {
            textoAsiento = letraFila + numeroColumnaVisible;
        } else {
            textoAsiento = "??"; // No debería pasar
        }

        Text texto = new Text(textoAsiento);
        texto.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        texto.setFill(Color.WHITE);
        texto.setStroke(Color.BLACK);
        texto.setStrokeWidth(0.3);

        getChildren().addAll(sombra, apoyoIzquierdo, apoyoDerecho, pataIzquierda, pataDerecha,
                asiento, respaldo, texto);
    }


    private void actualizarToolTip() {
        String estadoStr = "";
        switch (estado) {
            case LIBRE:
                estadoStr = "Libre";
                break;
            case SELECCIONADO:
                estadoStr = "Seleccionado";
                break;
            case OCUPADO:
                estadoStr = "Ocupado";
                break;
        }

        String letraFila = String.valueOf((char) ('A' + fila));
        int numeroColumnaVisible = -1;
        for (int i = 0; i < columnasValidas.size(); i++) {
            if (columnasValidas.get(i) == columna) {
                numeroColumnaVisible = i + 1;
                break;
            }
        }

        String asientoStr = letraFila + numeroColumnaVisible;
        Tooltip.install(this, new Tooltip("Asiento " + asientoStr + " (" + estadoStr + ")"));
    }
    /**
     * Metodo público para forzar el redibujado del asiento
     */
    public void redibujar() {
        this.estado = sala.getEstadoAsiento(fila, columna);
        dibujarAsiento();
        actualizarToolTip();
    }

    /**
     * Metodo para establecer callback cuando el asiento cambia
     */
    public void setOnAsientoCambiado(Runnable callback) {
        this.onAsientoCambiado = callback;
    }

    // Getters
    public int getFila() { return fila; }
    public int getColumna() { return columna; }
    public EstadoAsiento getEstado() { return estado; }
}