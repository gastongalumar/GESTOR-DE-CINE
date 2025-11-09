
package Clases.GestionSelectorAsientos;

import Clases.Funcion;
import Clases.GestionDePagos.GestorDePagos;
import Clases.SalaCine;
import Clases.login.usuario.Administrador;
import Clases.login.usuario.Cliente;
import Clases.login.usuario.Usuario;
import ManejoJSON.GestorJsonAsientos;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class SelectorAsientos {

   // Configuración de la sala
    private final int FILAS = 12;
    private final int FILAS_ASIENTOS = 10;
    private final int LEFT_BLOCK = 3;
    private final int CENTER_BLOCK = 8;
    private final int RIGHT_BLOCK = 3;
    private final int AISLE_WIDTH = 1;
    private final int COLUMNAS = LEFT_BLOCK + AISLE_WIDTH + CENTER_BLOCK + AISLE_WIDTH + RIGHT_BLOCK;
    private SalaCine sala;
    private GestorJsonAsientos gestorJson;
    private AsientoButton[][] botonesAsientos = new AsientoButton[FILAS_ASIENTOS][COLUMNAS];
    private final List<Integer> columnasValidas = new ArrayList<>();
    private Label contadorLabel = new Label("0 asientos seleccionados");
    private Label precioTotalLabel = new Label("Total: $0.00");
    private VBox rootLayout;
    private Funcion funcion;
    private GestorDePagos gestorDePagos;

    /**
     * Constructor por defecto para uso interno
     */
    public SelectorAsientos() {
        inicializarColumnasValidas();
        this.gestorDePagos = new GestorDePagos(this);
    }

    /**
     * Constructor para función específica
     */
    public SelectorAsientos(Funcion funcion) {
        this.funcion = funcion;
        inicializarColumnasValidas();
        this.gestorDePagos = new GestorDePagos(this);
        inicializarConFuncion(funcion);
    }


//GETTER Y SETTER
    public SalaCine getSala() {
        return sala;
    }

    public Funcion getFuncion() {
        return funcion;
    }

    public Label getPrecioTotalLabel() {
        return precioTotalLabel;
    }

    public Label getContadorLabel() {
        return contadorLabel;
    }

    public AsientoButton[][] getBotonesAsientos() {
        return botonesAsientos;
    }

    public List<Integer> getColumnasValidas() {
        return columnasValidas;
    }

    public int getFilasAsientos() {
        return FILAS_ASIENTOS;
    }

    public int getColumnas() {
        return COLUMNAS;
    }

    public GestorJsonAsientos getGestorJson() {
        return gestorJson;
    }


    //METODOS
    public void actualizarVisualizacionAsientos() {
        for (int i = 0; i < FILAS_ASIENTOS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                if (botonesAsientos[i][j] != null) {
                    botonesAsientos[i][j].redibujar();
                }
            }
        }
        gestorDePagos.actualizarContador();
        gestorDePagos.actualizarPrecioTotal();
        System.out.println("🔄 Visualización actualizada");
    }

    /**
     * Método estático para mostrar el selector
     */
    public static void mostrarSelectorAsientos(Funcion funcion,Usuario usuario) {
        Platform.runLater(() -> {
            SelectorAsientos selector = new SelectorAsientos(funcion);
            Stage stage = new Stage();
            selector.inicializarSelectorAsientos(stage,usuario);
        });
    }

    private void inicializarConFuncion(Funcion funcion) {
        String nombrePelicula = funcion.getPelicula() != null ? funcion.getPelicula().getNombrePelicula() : "pelicula";
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
        String horarioStr = funcion.getHorarioFuncion() != null ? funcion.getHorarioFuncion().format(fmt) : "horaDesconocida";
        String archivo = String.format("Asientos_%s_%s.json", nombrePelicula.replaceAll("\\s+", "_"), horarioStr);

        this.sala = new SalaCine(funcion.getSala().getNombreSala(),FILAS_ASIENTOS, COLUMNAS);
        this.gestorJson = new GestorJsonAsientos(sala, archivo);
    }

    private void inicializarColumnasValidas() {
        columnasValidas.clear();

        // Columnas del bloque izquierdo (0, 1, 2)
        for (int c = 0; c < LEFT_BLOCK; c++) columnasValidas.add(c);

        // Columnas del bloque central (4, 5, 6, 7, 8, 9, 10, 11)
        for (int c = LEFT_BLOCK + AISLE_WIDTH; c < LEFT_BLOCK + AISLE_WIDTH + CENTER_BLOCK; c++) columnasValidas.add(c);

        // Columnas del bloque derecho (13, 14, 15)
        for (int c = LEFT_BLOCK + AISLE_WIDTH + CENTER_BLOCK + AISLE_WIDTH; c < COLUMNAS; c++) columnasValidas.add(c);

        System.out.println("📍 Columnas válidas (donde hay asientos): " + columnasValidas);
    }

    private void inicializarSelectorAsientos(Stage stage,Usuario usuario) {

        // Inicializar sala si no se hizo mediante constructor
        if (sala == null) {
            this.sala = new SalaCine(funcion.getSala().getNombreSala(),FILAS_ASIENTOS, COLUMNAS);
            this.gestorJson = new GestorJsonAsientos(sala);
        }

        configurarVentana(stage);
        inicializarComponentes(stage,usuario);

        // Cargar estado guardado
        Platform.runLater(() -> {
            boolean cargaExitosa = gestorJson.cargarEstadoGuardado();
            if (!cargaExitosa) {
                System.out.println("⚠️  No se pudo cargar el estado, usando valores por defecto");
            }
            actualizarVisualizacionAsientos();
            gestorDePagos.actualizarPrecioTotal();
            System.out.println("🎉 APLICACIÓN INICIADA CORRECTAMENTE");
        });
    }

    private void configurarVentana(Stage stage) {
        String titulo = "🎬 Selector de Asientos - Sala de Cine";
        if (funcion != null && funcion.getPelicula() != null) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String horarioStr = funcion.getHorarioFuncion() != null ? funcion.getHorarioFuncion().format(fmt) : "";
            titulo = "🎬 " + funcion.getPelicula().getNombrePelicula() + " - " + horarioStr;
        }
        stage.setTitle(titulo);

        stage.setOnCloseRequest(event -> {
            System.out.println("🔚 Selector de asientos cerrado, continuando ejecución...");
        });
    }

    private void inicializarComponentes(Stage stage,Usuario usuario) {
        rootLayout = new VBox();
        rootLayout.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));

        rootLayout.getChildren().addAll(
                crearPanelPantalla(),
                crearPanelCentral(),
                crearPanelInferior(usuario)
        );

        Scene scene = new Scene(rootLayout, 1200, 900);
        stage.setScene(scene);
        stage.show();
    }

    private BorderPane crearPanelPantalla() {
        BorderPane pantalla = new BorderPane();
        pantalla.setBackground(new Background(new BackgroundFill(Color.rgb(70, 130, 180), CornerRadii.EMPTY, Insets.EMPTY)));
        pantalla.setBorder(new Border(new BorderStroke(Color.GOLD, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(3))));
        pantalla.setPrefHeight(70);

        Label labelPantalla = new Label("PANTALLA");
        labelPantalla.setTextFill(Color.WHITE);
        labelPantalla.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        labelPantalla.setAlignment(Pos.CENTER);

        BorderPane.setAlignment(labelPantalla, Pos.CENTER);
        pantalla.setCenter(labelPantalla);

        return pantalla;
    }

    private BorderPane crearPanelCentral() {
        BorderPane panelCentral = new BorderPane();
        panelCentral.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));

        panelCentral.setTop(crearPanelColumnas());
        panelCentral.setCenter(crearPasilloDelantero());
        panelCentral.setBottom(crearPanelAsientosConNumeros());

        return panelCentral;
    }

    private VBox crearPanelFilas() {
        VBox panelFilas = new VBox();
        panelFilas.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));
        panelFilas.setPrefWidth(40);
        panelFilas.setSpacing(12);
        panelFilas.setPadding(new Insets(20, 0, 0, 0));

        for (int i = 0; i < FILAS_ASIENTOS; i++) {
            String letraFila = String.valueOf((char) ('A' + i));
            Label labelFila = new Label(letraFila);
            labelFila.setTextFill(Color.WHITE);
            labelFila.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            labelFila.setAlignment(Pos.CENTER);
            labelFila.setPrefHeight(52);

            panelFilas.getChildren().add(labelFila);
        }

        return panelFilas;
    }

    private GridPane crearPanelColumnas() {
        GridPane panelLetras = new GridPane();
        panelLetras.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));
        panelLetras.setPrefHeight(60);
        panelLetras.setHgap(20);
        panelLetras.setPadding(new Insets(0, 0, 0, 50));

        for (int j = 0; j < COLUMNAS; j++) {
            String texto = " ";
            int idx = columnasValidas.indexOf(j);
            if (idx != -1) {
                texto = String.valueOf(idx + 1);
            }
            Label numeroCol = new Label(texto);
            numeroCol.setTextFill(Color.WHITE);
            numeroCol.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            numeroCol.setAlignment(Pos.CENTER);
            numeroCol.setPrefWidth(60);

            GridPane.setColumnIndex(numeroCol, j);
            panelLetras.getChildren().add(numeroCol);
        }

        return panelLetras;
    }

    private BorderPane crearPasilloDelantero() {
        BorderPane pasilloDelantero = new BorderPane();
        pasilloDelantero.setBackground(new Background(new BackgroundFill(Color.rgb(80, 80, 80), CornerRadii.EMPTY, Insets.EMPTY)));
        pasilloDelantero.setBorder(new Border(new BorderStroke(Color.rgb(120, 120, 120), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        pasilloDelantero.setPrefHeight(80);

        Label labelPasilloDelantero = new Label("PASILLO PRINCIPAL");
        labelPasilloDelantero.setTextFill(Color.rgb(200, 200, 200));
        labelPasilloDelantero.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        labelPasilloDelantero.setAlignment(Pos.CENTER);

        pasilloDelantero.setCenter(labelPasilloDelantero);

        return pasilloDelantero;
    }

    private BorderPane crearPanelAsientosConNumeros() {
        BorderPane panelAsientosConNumeros = new BorderPane();
        panelAsientosConNumeros.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));

        panelAsientosConNumeros.setLeft(crearPanelFilas());
        panelAsientosConNumeros.setCenter(crearPanelPrincipalAsientos());

        return panelAsientosConNumeros;
    }

    private GridPane crearPanelPrincipalAsientos() {
        GridPane panelPrincipal = new GridPane();
        panelPrincipal.setBackground(new Background(new BackgroundFill(Color.rgb(40, 40, 40), CornerRadii.EMPTY, Insets.EMPTY)));
        panelPrincipal.setPadding(new Insets(10));
        panelPrincipal.setHgap(10);

        for (int bloque = 0; bloque < 5; bloque++) {
            Pane panelBloque = crearPanelBloque(bloque);
            GridPane.setColumnIndex(panelBloque, bloque);

            if (bloque == 1 || bloque == 3) {
                GridPane.setHgrow(panelBloque, Priority.ALWAYS);
                panelBloque.setPrefWidth(100);
            } else {
                GridPane.setHgrow(panelBloque, Priority.ALWAYS);
                int numColumnas = obtenerNumColumnas(bloque);
                panelBloque.setPrefWidth(numColumnas * 70);
            }

            panelPrincipal.getChildren().add(panelBloque);
        }

        return panelPrincipal;
    }

    private Pane crearPanelBloque(int bloque) {
        if (bloque == 1 || bloque == 3) {
            return crearPanelPasilloLateral(bloque);
        } else {
            return crearPanelBloqueAsientos(bloque);
        }
    }

    private VBox crearPanelPasilloLateral(int bloque) {
        VBox panelPasillo = new VBox();
        panelPasillo.setBackground(new Background(new BackgroundFill(Color.rgb(80, 80, 80), CornerRadii.EMPTY, Insets.EMPTY)));
        panelPasillo.setBorder(new Border(new BorderStroke(Color.rgb(120, 120, 120), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        panelPasillo.setAlignment(Pos.CENTER);
        panelPasillo.setPrefWidth(60);

        Text textoPasillo = new Text("PASILLO");
        textoPasillo.setFill(Color.WHITE);
        textoPasillo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        textoPasillo.setRotate(-90);

        panelPasillo.getChildren().add(textoPasillo);

        return panelPasillo;
    }

    private VBox crearPanelBloqueAsientos(int bloque) {
        VBox panelBloque = new VBox();
        panelBloque.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));
        panelBloque.setSpacing(12);

        int startCol = obtenerStartColumna(bloque);
        int numColumnas = obtenerNumColumnas(bloque);

        for (int i = 0; i < FILAS_ASIENTOS; i++) {
            HBox filaPanel = new HBox();
            filaPanel.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));
            filaPanel.setSpacing(8);
            filaPanel.setAlignment(Pos.CENTER);

            for (int j = 0; j < numColumnas; j++) {
                int columnaReal = startCol + j;

                // ✅ PASAR columnasValidas al constructor
                AsientoButton asiento = new AsientoButton(i, columnaReal, sala, columnasValidas);

                asiento.setOnAsientoCambiado(() -> {
                    gestorDePagos.actualizarContador();
                    gestorDePagos.actualizarPrecioTotal();
                    System.out.println("🔄 Asiento cambiado, contador actualizado");
                });

                botonesAsientos[i][columnaReal] = asiento;
                filaPanel.getChildren().add(asiento);
            }
            panelBloque.getChildren().add(filaPanel);
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

    private BorderPane crearPanelInferior(Usuario usuario) {
        BorderPane panelInferior = new BorderPane();
        panelInferior.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));
        panelInferior.setPadding(new Insets(8, 12, 8, 12));

        HBox leyenda = crearPanelLeyenda();
        VBox panelDerecho = crearPanelDerecho(usuario);

        panelInferior.setLeft(leyenda);
        panelInferior.setRight(panelDerecho);

        return panelInferior;
    }

    private HBox crearPanelLeyenda() {
        HBox leyenda = new HBox(10);
        leyenda.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));
        leyenda.setAlignment(Pos.CENTER_LEFT);

        leyenda.getChildren().addAll(
                crearItemLeyenda(Color.GRAY, "Disponible"),
                crearItemLeyenda(Color.BLUE, "Seleccionado"),
                crearItemLeyenda(Color.RED, "Ocupado")
        );

        return leyenda;
    }

    private HBox crearItemLeyenda(Color color, String texto) {
        HBox item = new HBox(6);
        item.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));
        item.setAlignment(Pos.CENTER_LEFT);

        Rectangle ejemplo = new Rectangle(24, 18);
        ejemplo.setFill(color);
        ejemplo.setStroke(color.brighter());
        ejemplo.setArcWidth(8);
        ejemplo.setArcHeight(8);

        Label label = new Label(texto);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));

        item.getChildren().addAll(ejemplo, label);

        return item;
    }

    private VBox crearPanelDerecho(Usuario usuario) {
        VBox panelDerecho = new VBox(5);
        panelDerecho.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));
        panelDerecho.setAlignment(Pos.CENTER_RIGHT);

        contadorLabel.setTextFill(Color.WHITE);
        contadorLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        precioTotalLabel.setTextFill(Color.GOLD);
        precioTotalLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        precioTotalLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gold; -fx-font-weight: bold;");

        HBox panelBotones = new HBox(10);
        panelBotones.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));
        panelBotones.setAlignment(Pos.CENTER_RIGHT);

        System.out.println("------------------------------------------------------------------------------");
        System.out.println(usuario);

        if(usuario instanceof Cliente) {
            // PANEL PARA CLIENTE
            Button btnLimpiar = new Button("Anular Selecciones");
            Button btnConfirmar = new Button("Confirmar Selección");

            configurarBoton(btnLimpiar);
            configurarBotonConfirmar(btnConfirmar);

            btnLimpiar.setOnAction(e -> limpiarSelecciones());
            btnConfirmar.setOnAction(e -> gestorDePagos.procesarPago((Cliente) usuario));

            panelBotones.getChildren().addAll(btnLimpiar, btnConfirmar);

            // AGREGAR TODOS LOS ELEMENTOS AL PANEL DERECHO
            panelDerecho.getChildren().addAll(contadorLabel, precioTotalLabel, panelBotones);

        } else {


            // PANEL PARA ADMIN
            Button btnReporte = new Button("Estado de Sala");
            configurarBoton(btnReporte);
            btnReporte.setOnAction(e -> generarReporte());
            panelBotones.getChildren().addAll(btnReporte);

            // AGREGAR TODOS LOS ELEMENTOS AL PANEL DERECHO
            panelDerecho.getChildren().addAll(contadorLabel, precioTotalLabel, panelBotones);

        }

        return panelDerecho;
    }

    private void configurarBoton(Button boton) {
        boton.setStyle("-fx-background-color: #4682B4; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 8 15 8 15;");
        boton.setOnMouseEntered(e -> boton.setStyle("-fx-background-color: #64A0D2; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 8 15 8 15;"));
        boton.setOnMouseExited(e -> boton.setStyle("-fx-background-color: #4682B4; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 8 15 8 15;"));
    }

    private void configurarBotonConfirmar(Button boton) {
        boton.setStyle("-fx-background-color: #28A03C; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 8 15 8 15;");
        boton.setOnMouseEntered(e -> boton.setStyle("-fx-background-color: #3CB43C; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 8 15 8 15;"));
        boton.setOnMouseExited(e -> boton.setStyle("-fx-background-color: #28A03C; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 8 15 8 15;"));
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

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Estado de Sala");
            alert.setHeaderText("📊 REPORTE DE SALA");
            alert.setContentText(mensaje);
            alert.showAndWait();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error al generar el reporte");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void limpiarSelecciones() {
        int seleccionados = sala.contarAsientosSeleccionados();

        if (seleccionados == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sin Selecciones");
            alert.setHeaderText("No hay asientos seleccionados para Anular.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Anular Selecciones");
        alert.setHeaderText("¿Estás seguro de que quieres Anular el/los " + seleccionados + " asiento(s) seleccionado(s)?");
        alert.setContentText("Esto convertirá todos los asientos seleccionados a libres.");

        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            gestorJson.limpiarSelecciones();
            actualizarVisualizacionAsientos();

            Alert exito = new Alert(Alert.AlertType.INFORMATION);
            exito.setTitle("Éxito");
            exito.setHeaderText(seleccionados + " selección(es) Anulada(s) correctamente.");
            exito.showAndWait();
        }
    }

    public Cliente getCliente() {
        return gestorDePagos.getCliente();

    }
}
