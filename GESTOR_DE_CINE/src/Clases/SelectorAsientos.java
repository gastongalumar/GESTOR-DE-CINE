package Clases;

import ManejoJSON.GestorJsonAsientos;
import javafx.application.Application;
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

public class SelectorAsientos extends Application {
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
    private Stage primaryStage;
    private VBox rootLayout;
    private Funcion funcion;

    /**
     * Constructor por defecto para uso interno
     */
    public SelectorAsientos() {
        inicializarColumnasValidas();
    }

    /**
     * Constructor para función específica
     */
    public SelectorAsientos(Funcion funcion) {
        this.funcion = funcion;
        inicializarColumnasValidas();
        inicializarConFuncion(funcion);
    }

    private void inicializarConFuncion(Funcion funcion) {
        String nombrePelicula = funcion.getPelicula() != null ? funcion.getPelicula().getNombrePelicula() : "pelicula";
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
        String horarioStr = funcion.getHorarioFuncion() != null ? funcion.getHorarioFuncion().format(fmt) : "horaDesconocida";
        String archivo = String.format("Asientos_%s_%s.json", nombrePelicula.replaceAll("\\s+", "_"), horarioStr);

        this.sala = new SalaCine(FILAS_ASIENTOS, COLUMNAS);
        this.gestorJson = new GestorJsonAsientos(sala, archivo);
    }

    private void inicializarColumnasValidas() {
        for (int c = 0; c < LEFT_BLOCK; c++) columnasValidas.add(c);
        for (int c = LEFT_BLOCK + AISLE_WIDTH; c < LEFT_BLOCK + AISLE_WIDTH + CENTER_BLOCK; c++) columnasValidas.add(c);
        for (int c = LEFT_BLOCK + AISLE_WIDTH + CENTER_BLOCK + AISLE_WIDTH; c < COLUMNAS; c++) columnasValidas.add(c);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Inicializar sala si no se hizo mediante constructor
        if (sala == null) {
            this.sala = new SalaCine(FILAS_ASIENTOS, COLUMNAS);
            this.gestorJson = new GestorJsonAsientos(sala);
        }

        configurarVentana();
        inicializarComponentes(); // ✅ ESTE MÉTODO SÍ EXISTE AHORA

        // Cargar estado guardado
        Platform.runLater(() -> {
            boolean cargaExitosa = gestorJson.cargarEstadoGuardado();
            if (!cargaExitosa) {
                System.out.println("⚠️  No se pudo cargar el estado, usando valores por defecto");
            }
            actualizarVisualizacionAsientos();
            actualizarPrecioTotal();
            System.out.println("🎉 APLICACIÓN INICIADA CORRECTAMENTE");
        });
    }

    private void configurarVentana() {
        String titulo = "🎬 Selector de Asientos - Sala de Cine";
        if (funcion != null && funcion.getPelicula() != null) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String horarioStr = funcion.getHorarioFuncion() != null ? funcion.getHorarioFuncion().format(fmt) : "";
            titulo = "🎬 " + funcion.getPelicula().getNombrePelicula() + " - " + horarioStr;
        }
        primaryStage.setTitle(titulo);

        primaryStage.setOnCloseRequest(event -> {
            System.out.println("🔚 Selector de asientos cerrado, continuando ejecución...");
        });
    }

    // ✅ MÉTODO INICIALIZAR COMPONENTES - FALTABA ESTE MÉTODO
    private void inicializarComponentes() {
        rootLayout = new VBox();
        rootLayout.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));

        rootLayout.getChildren().addAll(
                crearPanelPantalla(),
                crearPanelCentral(),
                crearPanelInferior()
        );

        Scene scene = new Scene(rootLayout, 1200, 900);
        primaryStage.setScene(scene);
        primaryStage.show();
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

        panelCentral.setTop(crearPanelLetrasColumnas());
        panelCentral.setCenter(crearPasilloDelantero());
        panelCentral.setBottom(crearPanelAsientosConNumeros());

        return panelCentral;
    }

    private GridPane crearPanelLetrasColumnas() {
        GridPane panelLetras = new GridPane();
        panelLetras.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));
        panelLetras.setPrefHeight(30);
        panelLetras.setHgap(5);

        for (int j = 0; j < COLUMNAS; j++) {
            String texto = "";
            int idx = columnasValidas.indexOf(j);
            if (idx != -1) {
                texto = String.valueOf((char) ('A' + idx));
            }
            Label letraCol = new Label(texto);
            letraCol.setTextFill(Color.WHITE);
            letraCol.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            letraCol.setAlignment(Pos.CENTER);
            letraCol.setPrefWidth(60);

            GridPane.setColumnIndex(letraCol, j);
            panelLetras.getChildren().add(letraCol);
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

        panelAsientosConNumeros.setLeft(crearPanelNumeracionFilas());
        panelAsientosConNumeros.setCenter(crearPanelPrincipalAsientos());

        return panelAsientosConNumeros;
    }

    private VBox crearPanelNumeracionFilas() {
        VBox panelFilas = new VBox();
        panelFilas.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));
        panelFilas.setPrefWidth(40);
        panelFilas.setSpacing(12);

        for (int i = 0; i < FILAS_ASIENTOS; i++) {
            Label numeroFila = new Label(String.valueOf(i + 3));
            numeroFila.setTextFill(Color.WHITE);
            numeroFila.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            numeroFila.setAlignment(Pos.CENTER);
            numeroFila.setPrefHeight(52);

            panelFilas.getChildren().add(numeroFila);
        }

        return panelFilas;
    }

    private GridPane crearPanelPrincipalAsientos() {
        GridPane panelPrincipal = new GridPane();
        panelPrincipal.setBackground(new Background(new BackgroundFill(Color.rgb(40, 40, 40), CornerRadii.EMPTY, Insets.EMPTY)));
        panelPrincipal.setPadding(new Insets(10));
        panelPrincipal.setHgap(10);

        // Crear los 5 bloques
        for (int bloque = 0; bloque < 5; bloque++) {
            Pane panelBloque = crearPanelBloque(bloque);
            GridPane.setColumnIndex(panelBloque, bloque);

            // Configurar constraints de ancho
            if (bloque == 1 || bloque == 3) {
                // Pasillos - más anchos
                GridPane.setHgrow(panelBloque, Priority.ALWAYS);
                panelBloque.setPrefWidth(100);
            } else {
                // Bloques de asientos
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

        // Texto rotado para pasillo
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
                AsientoButton asiento = new AsientoButton(i, columnaReal, sala);

                asiento.setOnAsientoCambiado(() -> {
                    actualizarContador();
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
            case 0: return 0;
            case 2: return LEFT_BLOCK + AISLE_WIDTH;
            case 4: return LEFT_BLOCK + AISLE_WIDTH + CENTER_BLOCK + AISLE_WIDTH;
            default: return 0;
        }
    }

    private int obtenerNumColumnas(int bloque) {
        switch (bloque) {
            case 0: return LEFT_BLOCK;
            case 2: return CENTER_BLOCK;
            case 4: return RIGHT_BLOCK;
            default: return 0;
        }
    }

    private BorderPane crearPanelInferior() {
        BorderPane panelInferior = new BorderPane();
        panelInferior.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));
        panelInferior.setPadding(new Insets(8, 12, 8, 12));

        // Panel de leyenda
        HBox leyenda = crearPanelLeyenda();

        // Panel de información derecha
        VBox panelDerecho = crearPanelDerecho();

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

    private VBox crearPanelDerecho() {
        VBox panelDerecho = new VBox(5);
        panelDerecho.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));
        panelDerecho.setAlignment(Pos.CENTER_RIGHT);

        // Contador
        contadorLabel.setTextFill(Color.WHITE);
        contadorLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        // Label de precio total
        precioTotalLabel.setTextFill(Color.GOLD);
        precioTotalLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        precioTotalLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gold; -fx-font-weight: bold;");

        // Botones de control
        HBox panelBotones = new HBox(10);
        panelBotones.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));
        panelBotones.setAlignment(Pos.CENTER_RIGHT);

        Button btnReporte = new Button("Estado de Sala");
        Button btnLimpiar = new Button("Anular Selecciones");
        Button btnConfirmar = new Button("Confirmar Selección");

        configurarBoton(btnReporte);
        configurarBoton(btnLimpiar);
        configurarBotonConfirmar(btnConfirmar);

        btnReporte.setOnAction(e -> generarReporte());
        btnLimpiar.setOnAction(e -> limpiarSelecciones());
        btnConfirmar.setOnAction(e -> {
            procesarPago(); // ✅ Primero procesar el pago, luego confirmar
        });

        panelBotones.getChildren().addAll(btnReporte, btnLimpiar, btnConfirmar);
        panelDerecho.getChildren().addAll(contadorLabel, precioTotalLabel, panelBotones);

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

    private void actualizarContador() {
        int count = sala.contarAsientosSeleccionados();
        contadorLabel.setText(count + (count == 1 ? " asiento seleccionado" : " asientos seleccionados"));
        actualizarPrecioTotal();
    }

    private void actualizarPrecioTotal() {
        double total = calcularPrecioTotal();
        precioTotalLabel.setText(String.format("Total: $%,.2f", total));
    }

    private void procesarPago() {
        double totalAPagar = calcularPrecioTotal();
        int asientosSeleccionados = sala.contarAsientosSeleccionados();

        if (asientosSeleccionados == 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Sin Selecciones");
            alert.setHeaderText("No hay asientos seleccionados para pagar.");
            alert.setContentText("Selecciona algunos asientos primero.");
            alert.showAndWait();
            return;
        }

        // Mostrar resumen de compra
        String resumen = String.format(
                "📋 RESUMEN DE COMPRA\n\n" +
                        "🎬 Película: %s\n" +
                        "⏰ Función: %s\n" +
                        "🎫 Asientos seleccionados: %d\n" +
                        "💰 Precio por asiento: $%,.2f\n" +
                        "💵 TOTAL A PAGAR: $%,.2f\n\n" +
                        "Seleccione el método de pago:",
                funcion != null && funcion.getPelicula() != null ? funcion.getPelicula().getNombrePelicula() : "No especificada",
                funcion != null && funcion.getHorarioFuncion() != null ?
                        funcion.getHorarioFuncion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "No especificada",
                asientosSeleccionados,
                funcion != null ? funcion.getPrecio() : 5000.0,
                totalAPagar
        );

        List<String> opciones = List.of("Tarjeta de Crédito", "Tarjeta de Débito", "Generar Cupón Pago en Efectivo", "Transferencia Bancaria");

        ChoiceDialog<String> dialog = new ChoiceDialog<>(opciones.get(0), opciones);
        dialog.setTitle("Procesar Pago");
        dialog.setHeaderText("💳 PROCESAR PAGO");
        dialog.setContentText(resumen);

        Optional<String> resultado = dialog.showAndWait();

        if (resultado.isPresent()) {
            String metodoSeleccionado = resultado.get();

            // ✅ CORREGIDO: Usar constructor correcto de MetodoDePago
            MetodoDePago metodoPago = new MetodoDePago(generarIdMetodoPago(), metodoSeleccionado);
            String descripcion = String.format("Compra de %d asientos para %s",
                    asientosSeleccionados,
                    funcion != null && funcion.getPelicula() != null ? funcion.getPelicula().getNombrePelicula() : "película"
            );

            // Usar el GestorDePagos para procesar el pago
            GestorDePagos gestorPagos = new GestorDePagos();
            boolean pagoExitoso = gestorPagos.procesarPago(metodoPago, totalAPagar, descripcion);

            if (pagoExitoso) {
                Alert exito = new Alert(Alert.AlertType.INFORMATION);
                exito.setTitle("Pago Exitoso");
                exito.setHeaderText("✅ PAGO PROCESADO EXITOSAMENTE");
                exito.setContentText(String.format(
                        "Método: %s\n" +
                                "Monto: $%,.2f\n" +
                                "Asientos: %d\n" +
                                "¡Disfrute de la función!",
                        metodoSeleccionado, totalAPagar, asientosSeleccionados
                ));
                exito.showAndWait();

                primaryStage.close();
            } else {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error en Pago");
                error.setHeaderText("❌ ERROR EN EL PAGO");
                error.setContentText("No se pudo procesar el pago. Intente nuevamente.");
                error.showAndWait();
            }
        } else {
            System.out.println("❌ Pago cancelado por el usuario.");
        }
    }

    // ✅ NUEVO: Método para generar ID único para método de pago
    private int generarIdMetodoPago() {
        return (int) (System.currentTimeMillis() % 1000000);
    }

    public double calcularPrecioTotal() {
        int asientosSeleccionados = sala.contarAsientosSeleccionados();
        double precioUnitario = funcion != null ? funcion.getPrecio() : 5000.0;
        return asientosSeleccionados * precioUnitario;
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

    private boolean confirmarSelecciones() {
        int seleccionados = sala.contarAsientosSeleccionados();

        if (seleccionados == 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Sin Selecciones");
            alert.setHeaderText("No hay asientos seleccionados para confirmar.");
            alert.setContentText("Selecciona algunos asientos primero.");
            alert.showAndWait();
            return false;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Selección");
        alert.setHeaderText("¿Confirmar " + seleccionados + " asiento(s) seleccionado(s)?");
        alert.setContentText("✅ Los asientos seleccionados (azules) pasarán a OCUPADOS (rojos)\n🔒 No podrán ser modificados después");

        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            int confirmados = gestorJson.confirmarSelecciones();
            actualizarVisualizacionAsientos();

            Alert exito = new Alert(Alert.AlertType.INFORMATION);
            exito.setTitle("Confirmación Exitosa");
            exito.setHeaderText("✅ " + confirmados + " asiento(s) confirmado(s) exitosamente!");
            exito.setContentText("🔴 Ahora aparecen en ROJO (ocupados)\n💾 Guardados correctamente!!!\n🔒 Ya no se pueden modificar");
            exito.showAndWait();
            return true;
        }
        return false;
    }

    /**
     * Método para forzar la actualización de todos los asientos
     */
    public void actualizarVisualizacionAsientos() {
        for (int i = 0; i < FILAS_ASIENTOS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                if (botonesAsientos[i][j] != null) {
                    botonesAsientos[i][j].redibujar();
                }
            }
        }
        actualizarContador();
        actualizarPrecioTotal();
        System.out.println("🔄 Visualización actualizada");
    }

    /**
     * Método estático para mostrar el selector
     */
    public static void mostrarSelectorAsientos(Funcion funcion) {
        Platform.runLater(() -> {
            SelectorAsientos selector = new SelectorAsientos(funcion);
            Stage stage = new Stage();
            selector.start(stage);
        });
    }

    /**
     * Método main para pruebas
     */
    public static void main(String[] args) {
        launch(args);
    }
}

//package Clases;
//
//import ManejoJSON.GestorJsonAsientos;
//import javafx.application.Application;
//import javafx.application.Platform;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.layout.*;
//import javafx.scene.paint.Color;
//import javafx.scene.shape.Rectangle;
//import javafx.scene.text.Font;
//import javafx.scene.text.FontWeight;
//import javafx.scene.text.Text;
//import javafx.stage.Stage;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//public class SelectorAsientos extends Application {
//    // Configuración de la sala
//    private final int FILAS = 12;
//    private final int FILAS_ASIENTOS = 10;
//    private final int LEFT_BLOCK = 3;
//    private final int CENTER_BLOCK = 8;
//    private final int RIGHT_BLOCK = 3;
//    private final int AISLE_WIDTH = 1;
//    private final int COLUMNAS = LEFT_BLOCK + AISLE_WIDTH + CENTER_BLOCK + AISLE_WIDTH + RIGHT_BLOCK;
//
//    private SalaCine sala;
//    private GestorJsonAsientos gestorJson;
//    private AsientoButton[][] botonesAsientos = new AsientoButton[FILAS_ASIENTOS][COLUMNAS];
//    private final List<Integer> columnasValidas = new ArrayList<>();
//    private Label contadorLabel = new Label("0 asientos seleccionados");
//    private Label precioTotalLabel = new Label("Total: $0.00"); // ✅ NUEVO: Label para precio total
//    private Stage primaryStage;
//    private VBox rootLayout;
//    private Funcion funcion; // ✅ NUEVO: Referencia a la función
//
//    /**
//     * Constructor por defecto para uso interno
//     */
//    public SelectorAsientos() {
//        inicializarColumnasValidas();
//    }
//
//    /**
//     * Constructor para función específica
//     */
//    public SelectorAsientos(Funcion funcion) {
//        this.funcion = funcion; // ✅ GUARDAR LA FUNCIÓN
//        inicializarColumnasValidas();
//        inicializarConFuncion(funcion);
//    }
//
//    private void inicializarConFuncion(Funcion funcion) {
//        String nombrePelicula = funcion.getPelicula() != null ? funcion.getPelicula().getNombrePelicula() : "pelicula";
//        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
//        String horarioStr = funcion.getHorarioFuncion() != null ? funcion.getHorarioFuncion().format(fmt) : "horaDesconocida";
//        String archivo = String.format("Asientos_%s_%s.json", nombrePelicula.replaceAll("\\s+", "_"), horarioStr);
//
//        this.sala = new SalaCine(FILAS_ASIENTOS, COLUMNAS);
//        this.gestorJson = new GestorJsonAsientos(sala, archivo);
//    }
//
//    private void inicializarColumnasValidas() {
//        for (int c = 0; c < LEFT_BLOCK; c++) columnasValidas.add(c);
//        for (int c = LEFT_BLOCK + AISLE_WIDTH; c < LEFT_BLOCK + AISLE_WIDTH + CENTER_BLOCK; c++) columnasValidas.add(c);
//        for (int c = LEFT_BLOCK + AISLE_WIDTH + CENTER_BLOCK + AISLE_WIDTH; c < COLUMNAS; c++) columnasValidas.add(c);
//    }
//
//    @Override
//    public void start(Stage primaryStage) {
//        this.primaryStage = primaryStage;
//
//        // Inicializar sala si no se hizo mediante constructor
//        if (sala == null) {
//            this.sala = new SalaCine(FILAS_ASIENTOS, COLUMNAS);
//            this.gestorJson = new GestorJsonAsientos(sala);
//        }
//
//        configurarVentana();
//        inicializarComponentes();
//
//        // Cargar estado guardado
//        Platform.runLater(() -> {
//            boolean cargaExitosa = gestorJson.cargarEstadoGuardado();
//            if (!cargaExitosa) {
//                System.out.println("⚠️  No se pudo cargar el estado, usando valores por defecto");
//            }
//            actualizarVisualizacionAsientos();
//            actualizarPrecioTotal(); // ✅ ACTUALIZAR PRECIO INICIAL
//            System.out.println("🎉 APLICACIÓN INICIADA CORRECTAMENTE");
//        });
//    }
//
//    private void inicializarComponentes() {
//        rootLayout = new VBox();
//        rootLayout.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));
//
//        rootLayout.getChildren().addAll(
//                crearPanelPantalla(),
//                crearPanelCentral(),
//                crearPanelInferior()
//        );
//
//        Scene scene = new Scene(rootLayout, 1200, 900);
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
//
//    private void configurarVentana() {
//        String titulo = "🎬 Selector de Asientos - Sala de Cine";
//        if (funcion != null && funcion.getPelicula() != null) {
//            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
//            String horarioStr = funcion.getHorarioFuncion() != null ? funcion.getHorarioFuncion().format(fmt) : "";
//            titulo = "🎬 " + funcion.getPelicula().getNombrePelicula() + " - " + horarioStr;
//        }
//        primaryStage.setTitle(titulo);
//
//        primaryStage.setOnCloseRequest(event -> {
//            System.out.println("🔚 Selector de asientos cerrado, continuando ejecución...");
//        });
//    }
//
//    // ... (los métodos de creación de paneles se mantienen igual hasta crearPanelDerecho)
//
//    private VBox crearPanelDerecho() {
//        VBox panelDerecho = new VBox(5);
//        panelDerecho.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));
//        panelDerecho.setAlignment(Pos.CENTER_RIGHT);
//
//        // Contador
//        contadorLabel.setTextFill(Color.WHITE);
//        contadorLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
//
//        // ✅ NUEVO: Label de precio total
//        precioTotalLabel.setTextFill(Color.GOLD);
//        precioTotalLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
//        precioTotalLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gold; -fx-font-weight: bold;");
//
//        // Botones de control
//        HBox panelBotones = new HBox(10);
//        panelBotones.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));
//        panelBotones.setAlignment(Pos.CENTER_RIGHT);
//
//        Button btnReporte = new Button("Estado de Sala");
//        Button btnLimpiar = new Button("Anular Selecciones");
//        Button btnConfirmar = new Button("Confirmar Selección");
//
//        configurarBoton(btnReporte);
//        configurarBoton(btnLimpiar);
//        configurarBotonConfirmar(btnConfirmar);
//
//        btnReporte.setOnAction(e -> generarReporte());
//        btnLimpiar.setOnAction(e -> limpiarSelecciones());
//        btnConfirmar.setOnAction(e -> {
//            if (confirmarSelecciones()) {
//                procesarPago(); // ✅ CAMBIADO: Llamar a procesarPago en lugar de elegirMetodoPago
//            }
//        });
//
//        panelBotones.getChildren().addAll(btnReporte, btnLimpiar, btnConfirmar);
//
//        // ✅ CAMBIADO: Agregar precioTotalLabel al panel
//        panelDerecho.getChildren().addAll(contadorLabel, precioTotalLabel, panelBotones);
//
//        return panelDerecho;
//    }
//
//    // ... (los demás métodos se mantienen igual hasta actualizarContador)
//
//    private void actualizarContador() {
//        int count = sala.contarAsientosSeleccionados();
//        contadorLabel.setText(count + (count == 1 ? " asiento seleccionado" : " asientos seleccionados"));
//        actualizarPrecioTotal(); // ✅ ACTUALIZAR PRECIO CUANDO CAMBIA EL CONTADOR
//    }
//
//    // ✅ NUEVO MÉTODO: Actualizar precio total
//    private void actualizarPrecioTotal() {
//        double total = calcularPrecioTotal();
//        precioTotalLabel.setText(String.format("Total: $%,.2f", total));
//    }
//
//    // ✅ MEJORADO: Método para procesar pago completo
//    private void procesarPago() {
//        double totalAPagar = calcularPrecioTotal();
//        int asientosSeleccionados = sala.contarAsientosSeleccionados();
//
//        if (asientosSeleccionados == 0) {
//            Alert alert = new Alert(Alert.AlertType.WARNING);
//            alert.setTitle("Sin Selecciones");
//            alert.setHeaderText("No hay asientos seleccionados para pagar.");
//            alert.setContentText("Selecciona algunos asientos primero.");
//            alert.showAndWait();
//            return;
//        }
//
//        // Mostrar resumen de compra
//        String resumen = String.format(
//                "📋 RESUMEN DE COMPRA\n\n" +
//                        "🎬 Película: %s\n" +
//                        "⏰ Función: %s\n" +
//                        "🎫 Asientos seleccionados: %d\n" +
//                        "💰 Precio por asiento: $%,.2f\n" +
//                        "💵 TOTAL A PAGAR: $%,.2f\n\n" +
//                        "Seleccione el método de pago:",
//                funcion != null && funcion.getPelicula() != null ? funcion.getPelicula().getNombrePelicula() : "No especificada",
//                funcion != null && funcion.getHorarioFuncion() != null ?
//                        funcion.getHorarioFuncion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "No especificada",
//                asientosSeleccionados,
//                funcion != null ? funcion.getPrecio() : 5000.0,
//                totalAPagar
//        );
//
//        List<String> opciones = List.of("Tarjeta de Crédito", "Tarjeta de Débito", "Generar Cupón Pago en Efectivo", "Transferencia Bancaria");
//
//        ChoiceDialog<String> dialog = new ChoiceDialog<>(opciones.get(0), opciones);
//        dialog.setTitle("Procesar Pago");
//        dialog.setHeaderText("💳 PROCESAR PAGO");
//        dialog.setContentText(resumen);
//
//        Optional<String> resultado = dialog.showAndWait();
//
//        if (resultado.isPresent()) {
//            String metodoSeleccionado = resultado.get();
//
//            // Crear y procesar el pago
//            MetodoDePago metodoPago = new MetodoDePago(metodoSeleccionado, metodoSeleccionado);
//            String descripcion = String.format("Compra de %d asientos para %s",
//                    asientosSeleccionados,
//                    funcion != null && funcion.getPelicula() != null ? funcion.getPelicula().getNombrePelicula() : "película"
//            );
//
//            // Usar el GestorDePagos para procesar el pago
//            GestorDePagos gestorPagos = new GestorDePagos();
//            boolean pagoExitoso = gestorPagos.procesarPago(metodoPago, totalAPagar, descripcion);
//
//            if (pagoExitoso) {
//                // Mostrar confirmación exitosa
//                Alert exito = new Alert(Alert.AlertType.INFORMATION);
//                exito.setTitle("Pago Exitoso");
//                exito.setHeaderText("✅ PAGO PROCESADO EXITOSAMENTE");
//                exito.setContentText(String.format(
//                        "Método: %s\n" +
//                                "Monto: $%,.2f\n" +
//                                "Asientos: %d\n" +
//                                "¡Disfrute de la función!",
//                        metodoSeleccionado, totalAPagar, asientosSeleccionados
//                ));
//                exito.showAndWait();
//
//                // Cerrar ventana después de pago exitoso
//                primaryStage.close();
//            } else {
//                Alert error = new Alert(Alert.AlertType.ERROR);
//                error.setTitle("Error en Pago");
//                error.setHeaderText("❌ ERROR EN EL PAGO");
//                error.setContentText("No se pudo procesar el pago. Intente nuevamente.");
//                error.showAndWait();
//            }
//        } else {
//            System.out.println("❌ Pago cancelado por el usuario.");
//        }
//    }
//
//    // ✅ MÉTODO MEJORADO: Calcular precio total usando el precio de la función
//    public double calcularPrecioTotal() {
//        int asientosSeleccionados = sala.contarAsientosSeleccionados();
//        double precioUnitario = funcion != null ? funcion.getPrecio() : 5000.0;
//        return asientosSeleccionados * precioUnitario;
//    }
//
//    // ... (el resto de los métodos se mantienen igual)
//
//    /**
//     * Método para forzar la actualización de todos los asientos
//     */
//    public void actualizarVisualizacionAsientos() {
//        for (int i = 0; i < FILAS_ASIENTOS; i++) {
//            for (int j = 0; j < COLUMNAS; j++) {
//                if (botonesAsientos[i][j] != null) {
//                    botonesAsientos[i][j].redibujar();
//                }
//            }
//        }
//        actualizarContador();
//        actualizarPrecioTotal(); // ✅ ACTUALIZAR PRECIO AL ACTUALIZAR VISUALIZACIÓN
//        System.out.println("🔄 Visualización actualizada");
//    }
//
//    /**
//     * Método estático para mostrar el selector
//     */
//    public static void mostrarSelectorAsientos(Funcion funcion) {
//        Platform.runLater(() -> {
//            SelectorAsientos selector = new SelectorAsientos(funcion);
//            Stage stage = new Stage();
//            selector.start(stage);
//        });
//    }
//
//    /**
//     * Método main para pruebas
//     */
//    public static void main(String[] args) {
//        launch(args);
//    }
//}
//
////package Clases;
////
////import ManejoJSON.GestorJsonAsientos;
////import javafx.application.Application;
////import javafx.application.Platform;
////import javafx.geometry.Insets;
////import javafx.geometry.Pos;
////import javafx.scene.Scene;
////import javafx.scene.control.*;
////import javafx.scene.layout.*;
////import javafx.scene.paint.Color;
////import javafx.scene.shape.Rectangle;
////import javafx.scene.text.Font;
////import javafx.scene.text.FontWeight;
////import javafx.scene.text.Text;
////import javafx.stage.Stage;
////import java.time.format.DateTimeFormatter;
////import java.util.ArrayList;
////import java.util.List;
////import java.util.Optional;
////
////public class SelectorAsientos extends Application {
////    // Configuración de la sala
////    private final int FILAS = 12;
////    private final int FILAS_ASIENTOS = 10;
////    private final int LEFT_BLOCK = 3;
////    private final int CENTER_BLOCK = 8;
////    private final int RIGHT_BLOCK = 3;
////    private final int AISLE_WIDTH = 1;
////    private final int COLUMNAS = LEFT_BLOCK + AISLE_WIDTH + CENTER_BLOCK + AISLE_WIDTH + RIGHT_BLOCK;
////
////    private SalaCine sala;
////    private GestorJsonAsientos gestorJson;
////    private AsientoButton[][] botonesAsientos = new AsientoButton[FILAS_ASIENTOS][COLUMNAS];
////    private final List<Integer> columnasValidas = new ArrayList<>();
////    private Label contadorLabel = new Label("0 asientos seleccionados");
////    private Stage primaryStage;
////    private VBox rootLayout;
////
////    /**
////     * Constructor por defecto para uso interno
////     */
////    public SelectorAsientos() {
////        inicializarColumnasValidas();
////    }
////
////    /**
////     * Constructor para función específica
////     */
////    public SelectorAsientos(Funcion funcion) {
////        inicializarColumnasValidas();
////        inicializarConFuncion(funcion);
////    }
////
////    private void inicializarConFuncion(Funcion funcion) {
////        String nombrePelicula = funcion.getPelicula() != null ? funcion.getPelicula().getNombrePelicula() : "pelicula";
////        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
////        String horarioStr = funcion.getHorarioFuncion() != null ? funcion.getHorarioFuncion().format(fmt) : "horaDesconocida";
////        String archivo = String.format("Asientos_%s_%s.json", nombrePelicula.replaceAll("\\s+", "_"), horarioStr);
////
////        this.sala = new SalaCine(FILAS_ASIENTOS, COLUMNAS);
////        this.gestorJson = new GestorJsonAsientos(sala, archivo);
////    }
////
////    private void inicializarColumnasValidas() {
////        for (int c = 0; c < LEFT_BLOCK; c++) columnasValidas.add(c);
////        for (int c = LEFT_BLOCK + AISLE_WIDTH; c < LEFT_BLOCK + AISLE_WIDTH + CENTER_BLOCK; c++) columnasValidas.add(c);
////        for (int c = LEFT_BLOCK + AISLE_WIDTH + CENTER_BLOCK + AISLE_WIDTH; c < COLUMNAS; c++) columnasValidas.add(c);
////    }
////
////    @Override
////    public void start(Stage primaryStage) {
////        this.primaryStage = primaryStage;
////
////        // Inicializar sala si no se hizo mediante constructor
////        if (sala == null) {
////            this.sala = new SalaCine(FILAS_ASIENTOS, COLUMNAS);
////            this.gestorJson = new GestorJsonAsientos(sala);
////        }
////
////        configurarVentana();
////        inicializarComponentes();
////
////        // Cargar estado guardado
////        Platform.runLater(() -> {
////            boolean cargaExitosa = gestorJson.cargarEstadoGuardado();
////            if (!cargaExitosa) {
////                System.out.println("⚠️  No se pudo cargar el estado, usando valores por defecto");
////            }
////            actualizarVisualizacionAsientos();
////            System.out.println("🎉 APLICACIÓN INICIADA CORRECTAMENTE");
////        });
////    }
////
////    private void configurarVentana() {
////        primaryStage.setTitle("🎬 Selector de Asientos - Sala de Cine");
////        primaryStage.setOnCloseRequest(event -> {
////            System.out.println("🔚 Selector de asientos cerrado, continuando ejecución...");
////        });
////    }
////
////    private void inicializarComponentes() {
////        rootLayout = new VBox();
////        rootLayout.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));
////
////        rootLayout.getChildren().addAll(
////                crearPanelPantalla(),
////                crearPanelCentral(),
////                crearPanelInferior()
////        );
////
////        Scene scene = new Scene(rootLayout, 1200, 900);
////        primaryStage.setScene(scene);
////        primaryStage.show();
////    }
////
////    private BorderPane crearPanelPantalla() {
////        BorderPane pantalla = new BorderPane();
////        pantalla.setBackground(new Background(new BackgroundFill(Color.rgb(70, 130, 180), CornerRadii.EMPTY, Insets.EMPTY)));
////        pantalla.setBorder(new Border(new BorderStroke(Color.GOLD, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(3))));
////        pantalla.setPrefHeight(70);
////
////        Label labelPantalla = new Label("PANTALLA");
////        labelPantalla.setTextFill(Color.WHITE);
////        labelPantalla.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
////        labelPantalla.setAlignment(Pos.CENTER);
////
////        BorderPane.setAlignment(labelPantalla, Pos.CENTER);
////        pantalla.setCenter(labelPantalla);
////
////        return pantalla;
////    }
////
////    private BorderPane crearPanelCentral() {
////        BorderPane panelCentral = new BorderPane();
////        panelCentral.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));
////
////        panelCentral.setTop(crearPanelLetrasColumnas());
////        panelCentral.setCenter(crearPasilloDelantero());
////        panelCentral.setBottom(crearPanelAsientosConNumeros());
////
////        return panelCentral;
////    }
////
////    private GridPane crearPanelLetrasColumnas() {
////        GridPane panelLetras = new GridPane();
////        panelLetras.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));
////        panelLetras.setPrefHeight(30);
////        panelLetras.setHgap(5);
////
////        for (int j = 0; j < COLUMNAS; j++) {
////            String texto = "";
////            int idx = columnasValidas.indexOf(j);
////            if (idx != -1) {
////                texto = String.valueOf((char) ('A' + idx));
////            }
////            Label letraCol = new Label(texto);
////            letraCol.setTextFill(Color.WHITE);
////            letraCol.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
////            letraCol.setAlignment(Pos.CENTER);
////            letraCol.setPrefWidth(60);
////
////            GridPane.setColumnIndex(letraCol, j);
////            panelLetras.getChildren().add(letraCol);
////        }
////
////        return panelLetras;
////    }
////
////    private BorderPane crearPasilloDelantero() {
////        BorderPane pasilloDelantero = new BorderPane();
////        pasilloDelantero.setBackground(new Background(new BackgroundFill(Color.rgb(80, 80, 80), CornerRadii.EMPTY, Insets.EMPTY)));
////        pasilloDelantero.setBorder(new Border(new BorderStroke(Color.rgb(120, 120, 120), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
////        pasilloDelantero.setPrefHeight(80);
////
////        Label labelPasilloDelantero = new Label("PASILLO PRINCIPAL");
////        labelPasilloDelantero.setTextFill(Color.rgb(200, 200, 200));
////        labelPasilloDelantero.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
////        labelPasilloDelantero.setAlignment(Pos.CENTER);
////
////        pasilloDelantero.setCenter(labelPasilloDelantero);
////
////        return pasilloDelantero;
////    }
////
////    private BorderPane crearPanelAsientosConNumeros() {
////        BorderPane panelAsientosConNumeros = new BorderPane();
////        panelAsientosConNumeros.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));
////
////        panelAsientosConNumeros.setLeft(crearPanelNumeracionFilas());
////        panelAsientosConNumeros.setCenter(crearPanelPrincipalAsientos());
////
////        return panelAsientosConNumeros;
////    }
////
////    private VBox crearPanelNumeracionFilas() {
////        VBox panelFilas = new VBox();
////        panelFilas.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));
////        panelFilas.setPrefWidth(40);
////        panelFilas.setSpacing(12);
////
////        for (int i = 0; i < FILAS_ASIENTOS; i++) {
////            Label numeroFila = new Label(String.valueOf(i + 3));
////            numeroFila.setTextFill(Color.WHITE);
////            numeroFila.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
////            numeroFila.setAlignment(Pos.CENTER);
////            numeroFila.setPrefHeight(52);
////
////            panelFilas.getChildren().add(numeroFila);
////        }
////
////        return panelFilas;
////    }
////
////    private GridPane crearPanelPrincipalAsientos() {
////        GridPane panelPrincipal = new GridPane();
////        panelPrincipal.setBackground(new Background(new BackgroundFill(Color.rgb(40, 40, 40), CornerRadii.EMPTY, Insets.EMPTY)));
////        panelPrincipal.setPadding(new Insets(10));
////        panelPrincipal.setHgap(10);
////
////        // Crear los 5 bloques
////        for (int bloque = 0; bloque < 5; bloque++) {
////            Pane panelBloque = crearPanelBloque(bloque);
////            GridPane.setColumnIndex(panelBloque, bloque);
////
////            // Configurar constraints de ancho
////            if (bloque == 1 || bloque == 3) {
////                // Pasillos - más anchos
////                GridPane.setHgrow(panelBloque, Priority.ALWAYS);
////                panelBloque.setPrefWidth(100);
////            } else {
////                // Bloques de asientos
////                GridPane.setHgrow(panelBloque, Priority.ALWAYS);
////                int numColumnas = obtenerNumColumnas(bloque);
////                panelBloque.setPrefWidth(numColumnas * 70);
////            }
////
////            panelPrincipal.getChildren().add(panelBloque);
////        }
////
////        return panelPrincipal;
////    }
////
////    private Pane crearPanelBloque(int bloque) {
////        if (bloque == 1 || bloque == 3) {
////            return crearPanelPasilloLateral(bloque);
////        } else {
////            return crearPanelBloqueAsientos(bloque);
////        }
////    }
////
////    private VBox crearPanelPasilloLateral(int bloque) {
////        VBox panelPasillo = new VBox();
////        panelPasillo.setBackground(new Background(new BackgroundFill(Color.rgb(80, 80, 80), CornerRadii.EMPTY, Insets.EMPTY)));
////        panelPasillo.setBorder(new Border(new BorderStroke(Color.rgb(120, 120, 120), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
////        panelPasillo.setAlignment(Pos.CENTER);
////        panelPasillo.setPrefWidth(60);
////
////        // Texto rotado para pasillo
////        Text textoPasillo = new Text("PASILLO");
////        textoPasillo.setFill(Color.WHITE);
////        textoPasillo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
////        textoPasillo.setRotate(-90);
////
////        panelPasillo.getChildren().add(textoPasillo);
////
////        return panelPasillo;
////    }
////
////    private VBox crearPanelBloqueAsientos(int bloque) {
////        VBox panelBloque = new VBox();
////        panelBloque.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));
////        panelBloque.setSpacing(12);
////
////        int startCol = obtenerStartColumna(bloque);
////        int numColumnas = obtenerNumColumnas(bloque);
////
////        for (int i = 0; i < FILAS_ASIENTOS; i++) {
////            HBox filaPanel = new HBox();
////            filaPanel.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));
////            filaPanel.setSpacing(8);
////            filaPanel.setAlignment(Pos.CENTER);
////
////            for (int j = 0; j < numColumnas; j++) {
////                int columnaReal = startCol + j;
////                AsientoButton asiento = new AsientoButton(i, columnaReal, sala);
////
////                // ✅ CORREGIDO - Usar el callback en lugar de setOnMouseClicked
////                asiento.setOnAsientoCambiado(() -> {
////                    actualizarContador();
////                    System.out.println("🔄 Asiento cambiado, contador actualizado");
////                });
////
////                botonesAsientos[i][columnaReal] = asiento;
////                filaPanel.getChildren().add(asiento);
////            }
////            panelBloque.getChildren().add(filaPanel);
////        }
////
////        return panelBloque;
////    }
////
////    private int obtenerStartColumna(int bloque) {
////        switch (bloque) {
////            case 0: return 0;
////            case 2: return LEFT_BLOCK + AISLE_WIDTH;
////            case 4: return LEFT_BLOCK + AISLE_WIDTH + CENTER_BLOCK + AISLE_WIDTH;
////            default: return 0;
////        }
////    }
////
////    private int obtenerNumColumnas(int bloque) {
////        switch (bloque) {
////            case 0: return LEFT_BLOCK;
////            case 2: return CENTER_BLOCK;
////            case 4: return RIGHT_BLOCK;
////            default: return 0;
////        }
////    }
////
////    private BorderPane crearPanelInferior() {
////        BorderPane panelInferior = new BorderPane();
////        panelInferior.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));
////        panelInferior.setPadding(new Insets(8, 12, 8, 12));
////
////        // Panel de leyenda
////        HBox leyenda = crearPanelLeyenda();
////
////        // Panel de información derecha
////        VBox panelDerecho = crearPanelDerecho();
////
////        panelInferior.setLeft(leyenda);
////        panelInferior.setRight(panelDerecho);
////
////        return panelInferior;
////    }
////
////    private HBox crearPanelLeyenda() {
////        HBox leyenda = new HBox(10);
////        leyenda.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));
////        leyenda.setAlignment(Pos.CENTER_LEFT);
////
////        leyenda.getChildren().addAll(
////                crearItemLeyenda(Color.GRAY, "Disponible"),
////                crearItemLeyenda(Color.BLUE, "Seleccionado"),
////                crearItemLeyenda(Color.RED, "Ocupado")
////        );
////
////        return leyenda;
////    }
////
////    private HBox crearItemLeyenda(Color color, String texto) {
////        HBox item = new HBox(6);
////        item.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));
////        item.setAlignment(Pos.CENTER_LEFT);
////
////        Rectangle ejemplo = new Rectangle(24, 18);
////        ejemplo.setFill(color);
////        ejemplo.setStroke(color.brighter());
////        ejemplo.setArcWidth(8);
////        ejemplo.setArcHeight(8);
////
////        Label label = new Label(texto);
////        label.setTextFill(Color.WHITE);
////        label.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
////
////        item.getChildren().addAll(ejemplo, label);
////
////        return item;
////    }
////
////    private VBox crearPanelDerecho() {
////        VBox panelDerecho = new VBox(5);
////        panelDerecho.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));
////        panelDerecho.setAlignment(Pos.CENTER_RIGHT);
////
////        // Contador
////        contadorLabel.setTextFill(Color.WHITE);
////        contadorLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
////
////        // Botones de control
////        HBox panelBotones = new HBox(10);
////        panelBotones.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));
////        panelBotones.setAlignment(Pos.CENTER_RIGHT);
////
////        Button btnReporte = new Button("Estado de Sala");
////        Button btnLimpiar = new Button("Anular Selecciones");
////        Button btnConfirmar = new Button("Confirmar Selección");
////
////        configurarBoton(btnReporte);
////        configurarBoton(btnLimpiar);
////        configurarBotonConfirmar(btnConfirmar);
////
////        btnReporte.setOnAction(e -> generarReporte());
////        btnLimpiar.setOnAction(e -> limpiarSelecciones());
////        btnConfirmar.setOnAction(e -> {
////            if (confirmarSelecciones()) {
////                System.out.println(elegirMetodoPago());
////            }
////        });
////
////        panelBotones.getChildren().addAll(btnReporte, btnLimpiar, btnConfirmar);
////        panelDerecho.getChildren().addAll(contadorLabel, panelBotones);
////
////        return panelDerecho;
////    }
////
////    private void configurarBoton(Button boton) {
////        boton.setStyle("-fx-background-color: #4682B4; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 8 15 8 15;");
////
////        boton.setOnMouseEntered(e -> boton.setStyle("-fx-background-color: #64A0D2; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 8 15 8 15;"));
////        boton.setOnMouseExited(e -> boton.setStyle("-fx-background-color: #4682B4; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 8 15 8 15;"));
////    }
////
////    private void configurarBotonConfirmar(Button boton) {
////        boton.setStyle("-fx-background-color: #28A03C; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 8 15 8 15;");
////
////        boton.setOnMouseEntered(e -> boton.setStyle("-fx-background-color: #3CB43C; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 8 15 8 15;"));
////        boton.setOnMouseExited(e -> boton.setStyle("-fx-background-color: #28A03C; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 8 15 8 15;"));
////    }
////
////    private boolean elegirMetodoPago() {
////        List<String> opciones = List.of("Tarjeta de Crédito", "Tarjeta de Debito", "Generar Cupon Pago en Efectivo", "Transferencia Bancaria");
////
////        System.out.println(calcularPrecioTotal());
////        ChoiceDialog<String> dialog = new ChoiceDialog<>(opciones.get(0), opciones);
////        dialog.setTitle("Método de Pago");
////        dialog.setHeaderText("Seleccione el método de pago:");
////        dialog.setContentText("Método:");
////
////        Optional<String> resultado = dialog.showAndWait();
////
////        if (resultado.isPresent()) {
////            System.out.println("✅ Método de pago seleccionado: " + resultado.get());
////            return true;
////        } else {
////            System.out.println("❌ Pago cancelado por el usuario.");
////            return false;
////        }
////    }
////
////    private void actualizarContador() {
////        int count = sala.contarAsientosSeleccionados();
////        contadorLabel.setText(count + (count == 1 ? " asiento seleccionado" : " asientos seleccionados"));
////    }
////
////    private void generarReporte() {
////        try {
////            org.json.JSONObject reporte = gestorJson.generarReporte();
////            String mensaje = String.format(
////                    "📊 REPORTE DE SALA\n\n" +
////                            "📅 Fecha del reporte: %s\n" +
////                            "🎫 Total de asientos: %d\n" +
////                            "🟢 Asientos libres: %d\n" +
////                            "🔵 Asientos seleccionados: %d\n" +
////                            "🔴 Asientos ocupados: %d\n" +
////                            "⏰ Última actualización: %s",
////                    reporte.getString("fechaReporte"),
////                    reporte.getInt("totalAsientos"),
////                    reporte.getInt("asientosLibres"),
////                    reporte.getInt("asientosSeleccionados"),
////                    reporte.getInt("asientosOcupados"),
////                    reporte.getString("ultimaActualizacion")
////            );
////
////            Alert alert = new Alert(Alert.AlertType.INFORMATION);
////            alert.setTitle("Estado de Sala");
////            alert.setHeaderText("📊 REPORTE DE SALA");
////            alert.setContentText(mensaje);
////            alert.showAndWait();
////
////        } catch (Exception e) {
////            Alert alert = new Alert(Alert.AlertType.ERROR);
////            alert.setTitle("Error");
////            alert.setHeaderText("Error al generar el reporte");
////            alert.setContentText(e.getMessage());
////            alert.showAndWait();
////        }
////    }
////
////    private void limpiarSelecciones() {
////        int seleccionados = sala.contarAsientosSeleccionados();
////
////        if (seleccionados == 0) {
////            Alert alert = new Alert(Alert.AlertType.INFORMATION);
////            alert.setTitle("Sin Selecciones");
////            alert.setHeaderText("No hay asientos seleccionados para Anular.");
////            alert.showAndWait();
////            return;
////        }
////
////        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
////        alert.setTitle("Anular Selecciones");
////        alert.setHeaderText("¿Estás seguro de que quieres Anular el/los " + seleccionados + " asiento(s) seleccionado(s)?");
////        alert.setContentText("Esto convertirá todos los asientos seleccionados a libres.");
////
////        Optional<ButtonType> resultado = alert.showAndWait();
////        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
////            gestorJson.limpiarSelecciones();
////            actualizarVisualizacionAsientos();
////
////            Alert exito = new Alert(Alert.AlertType.INFORMATION);
////            exito.setTitle("Éxito");
////            exito.setHeaderText(seleccionados + " selección(es) Anulada(s) correctamente.");
////            exito.showAndWait();
////        }
////    }
////
////    private boolean confirmarSelecciones() {
////        int seleccionados = sala.contarAsientosSeleccionados();
////
////        if (seleccionados == 0) {
////            Alert alert = new Alert(Alert.AlertType.WARNING);
////            alert.setTitle("Sin Selecciones");
////            alert.setHeaderText("No hay asientos seleccionados para confirmar.");
////            alert.setContentText("Selecciona algunos asientos primero.");
////            alert.showAndWait();
////            return false;
////        }
////
////        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
////        alert.setTitle("Confirmar Selección");
////        alert.setHeaderText("¿Confirmar " + seleccionados + " asiento(s) seleccionado(s)?");
////        alert.setContentText("✅ Los asientos seleccionados (azules) pasarán a OCUPADOS (rojos)\n🔒 No podrán ser modificados después");
////
////        Optional<ButtonType> resultado = alert.showAndWait();
////        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
////            int confirmados = gestorJson.confirmarSelecciones();
////            actualizarVisualizacionAsientos();
////
////            Alert exito = new Alert(Alert.AlertType.INFORMATION);
////            exito.setTitle("Confirmación Exitosa");
////            exito.setHeaderText("✅ " + confirmados + " asiento(s) confirmado(s) exitosamente!");
////            exito.setContentText("🔴 Ahora aparecen en ROJO (ocupados)\n💾 Guardados correctamente!!!\n🔒 Ya no se pueden modificar");
////            exito.showAndWait();
////            return true;
////        }
////        return false;
////    }
////
////    /**
////     * Método para forzar la actualización de todos los asientos
////     */
////    public void actualizarVisualizacionAsientos() {
////        for (int i = 0; i < FILAS_ASIENTOS; i++) {
////            for (int j = 0; j < COLUMNAS; j++) {
////                if (botonesAsientos[i][j] != null) {
////                    botonesAsientos[i][j].redibujar();
////                }
////            }
////        }
////        actualizarContador();
////        System.out.println("🔄 Visualización actualizada");
////    }
////
////    /**
////     * Método estático para mostrar el selector
////     */
////    public static void mostrarSelectorAsientos(Funcion funcion) {
////        Platform.runLater(() -> {
////            SelectorAsientos selector = new SelectorAsientos(funcion);
////            Stage stage = new Stage();
////            selector.start(stage);
////        });
////    }
////
////    /**
////     * Método main para pruebas
////     */
////    public static void main(String[] args) {
////        launch(args);
////    }
////
//////calcular precio a abonar
////    public double calcularPrecioTotal() {
////        int asientosSeleccionados = sala.contarAsientosSeleccionados();
////        double precioUnitario = 5000.0; // Precio fijo por asiento
////        return asientosSeleccionados * precioUnitario;
////    }
////
////}
//////package Clases;
//////
//////import ManejoJSON.GestorJsonAsientos;
//////
//////import javax.swing.*;
//////import java.awt.*;
//////import java.awt.event.*;
//////import java.beans.PropertyChangeEvent;
//////import java.beans.PropertyChangeListener;
//////import java.util.ArrayList;
//////import java.util.List;
//////import java.time.format.DateTimeFormatter;
//////
//////public class SelectorAsientos extends JFrame implements PropertyChangeListener {
//////    // Configuración de la sala
//////    private final int FILAS = 12;
//////    private final int FILAS_ASIENTOS = 10;
//////    private final int LEFT_BLOCK = 3;
//////    private final int CENTER_BLOCK = 8;
//////    private final int RIGHT_BLOCK = 3;
//////    private final int AISLE_WIDTH = 1;
//////    private final int COLUMNAS = LEFT_BLOCK + AISLE_WIDTH + CENTER_BLOCK + AISLE_WIDTH + RIGHT_BLOCK;
//////    private SalaCine sala; // ya no final, se inicializa en init
//////    private GestorJsonAsientos gestorJson; // ya no final
//////    private final AsientoButton[][] botonesAsientos = new AsientoButton[FILAS_ASIENTOS][COLUMNAS];
//////    private final List<Integer> columnasValidas = new ArrayList<>();
//////    private final JLabel contadorLabel = new JLabel("0 asientos seleccionados");
//////
//////    /**
//////     * Constructor por defecto: crea el selector con configuración estándar.
//////     * Usa el archivo JSON por defecto dentro del gestor.
//////     */
//////    public SelectorAsientos() {
//////        super("🎬 Selector de Asientos - Sala de Cine");
//////        SalaCine salaDefault = new SalaCine(FILAS_ASIENTOS, COLUMNAS);
//////        init(salaDefault, null);
//////    }
//////
//////    /**
//////     * Nuevo constructor: crea el selector para una Funcion concreta.
//////     * Genera un nombre de archivo JSON por función para separar estados.
//////     */
//////    public SelectorAsientos(Funcion funcion) {
//////        String nombrePelicula = funcion.getPelicula() != null ? funcion.getPelicula().getNombrePelicula() : "pelicula";
//////        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
//////        String horarioStr = funcion.getHorarioFuncion() != null ? funcion.getHorarioFuncion().format(fmt) : "horaDesconocida";
//////        String archivo = String.format("Asientos_%s_%s.json", nombrePelicula.replaceAll("\\s+", "_"), horarioStr);
//////
//////        this.setTitle("🎬 Selector - " + nombrePelicula + " (" + horarioStr + ")");
//////
//////        SalaCine salaDefault = new SalaCine(FILAS_ASIENTOS, COLUMNAS);
//////        init(salaDefault, archivo);
//////    }
//////
//////    //GETTER Y SETTER
//////
//////    /**
//////     * Método para obtener el gestor JSON desde otras clases
//////     */
//////    public GestorJsonAsientos getGestorJson() {
//////        return gestorJson;
//////    }
//////
//////
////////METODOS
//////
//////    /**
//////     * Método central de inicialización que antes estaba en el constructor.
//////     * Si archivoJson es null se usa el por defecto dentro del gestor.
//////     */
//////    private void init(SalaCine salaCine, String archivoJson) {
//////        this.sala = salaCine;
//////        if (archivoJson == null) {
//////            this.gestorJson = new GestorJsonAsientos(sala);
//////        } else {
//////            this.gestorJson = new GestorJsonAsientos(sala, archivoJson);
//////        }
//////
//////        inicializarColumnasValidas();
//////        configurarVentana();
//////
//////        System.out.println("🚀 ===== INICIANDO APLICACIÓN =====");
//////        boolean cargaExitosa = gestorJson.cargarEstadoGuardado();
//////
//////        if (!cargaExitosa) {
//////            System.out.println("⚠️  No se pudo cargar el estado, usando valores por defecto");
//////        }
//////
//////        inicializarComponentes();
//////
//////        SwingUtilities.invokeLater(() -> {
//////            actualizarVisualizacionAsientos();
//////            System.out.println("🎉 APLICACIÓN INICIADA CORRECTAMENTE");
//////        });
//////    }
//////
//////
//////    private void inicializarColumnasValidas() {
//////        for (int c = 0; c < LEFT_BLOCK; c++) columnasValidas.add(c);
//////        for (int c = LEFT_BLOCK + AISLE_WIDTH; c < LEFT_BLOCK + AISLE_WIDTH + CENTER_BLOCK; c++) columnasValidas.add(c);
//////        for (int c = LEFT_BLOCK + AISLE_WIDTH + CENTER_BLOCK + AISLE_WIDTH; c < COLUMNAS; c++) columnasValidas.add(c);
//////    }
//////
//////    private void configurarVentana() {
//////        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//////        setSize(1200, 900);
//////        setLocationRelativeTo(null);
//////        setLayout(new BorderLayout());
//////        getContentPane().setBackground(new Color(20, 20, 20));
//////    }
//////
//////    private void inicializarComponentes() {
//////        add(crearPanelPantalla(), BorderLayout.NORTH);
//////        add(crearPanelCentral(), BorderLayout.CENTER);
//////        add(crearPanelInferior(), BorderLayout.SOUTH);
//////
//////        actualizarContador();
//////    }
//////
//////    private JLabel crearPanelPantalla() {
//////        JLabel pantalla = new JLabel("PANTALLA", SwingConstants.CENTER);
//////        pantalla.setOpaque(true);
//////        pantalla.setBackground(new Color(70, 130, 180));
//////        pantalla.setForeground(Color.WHITE);
//////        pantalla.setFont(new Font("Segoe UI", Font.BOLD, 30));
//////        pantalla.setPreferredSize(new Dimension(1000, 70));
//////        pantalla.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 3));
//////        return pantalla;
//////    }
//////
//////    private JPanel crearPanelCentral() {
//////        JPanel panelCentral = new JPanel(new BorderLayout());
//////        panelCentral.setBackground(new Color(20, 20, 20));
//////
//////        panelCentral.add(crearPanelLetrasColumnas(), BorderLayout.NORTH);
//////        panelCentral.add(crearPasilloDelantero(), BorderLayout.CENTER);
//////        panelCentral.add(crearPanelAsientosConNumeros(), BorderLayout.SOUTH);
//////
//////        return panelCentral;
//////    }
//////
//////    private JPanel crearPanelLetrasColumnas() {
//////        JPanel panelLetras = new JPanel(new GridLayout(1, COLUMNAS, 5, 0));
//////        panelLetras.setBackground(new Color(20, 20, 20));
//////        panelLetras.setPreferredSize(new Dimension(0, 30));
//////
//////        for (int j = 0; j < COLUMNAS; j++) {
//////            String texto = "";
//////            int idx = columnasValidas.indexOf(j);
//////            if (idx != -1) {
//////                texto = String.valueOf((char) ('A' + idx));
//////            }
//////            JLabel letraCol = new JLabel(texto, SwingConstants.CENTER);
//////            letraCol.setForeground(Color.WHITE);
//////            letraCol.setFont(new Font("Segoe UI", Font.BOLD, 14));
//////            panelLetras.add(letraCol);
//////        }
//////
//////        return panelLetras;
//////    }
//////
//////    private JPanel crearPasilloDelantero() {
//////        JPanel pasilloDelantero = new JPanel(new BorderLayout());
//////        pasilloDelantero.setBackground(new Color(80, 80, 80));
//////        pasilloDelantero.setBorder(BorderFactory.createLineBorder(new Color(120, 120, 120), 2));
//////        pasilloDelantero.setPreferredSize(new Dimension(0, 80));
//////
//////        JLabel labelPasilloDelantero = new JLabel("PASILLO PRINCIPAL", SwingConstants.CENTER);
//////        labelPasilloDelantero.setForeground(new Color(200, 200, 200));
//////        labelPasilloDelantero.setFont(new Font("Segoe UI", Font.BOLD, 20));
//////        pasilloDelantero.add(labelPasilloDelantero, BorderLayout.CENTER);
//////
//////        return pasilloDelantero;
//////    }
//////
//////    private JPanel crearPanelAsientosConNumeros() {
//////        JPanel panelAsientosConNumeros = new JPanel(new BorderLayout());
//////        panelAsientosConNumeros.setBackground(new Color(20, 20, 20));
//////        panelAsientosConNumeros.add(crearPanelNumeracionFilas(), BorderLayout.WEST);
//////        panelAsientosConNumeros.add(crearPanelPrincipalAsientos(), BorderLayout.CENTER);
//////
//////        return panelAsientosConNumeros;
//////    }
//////
//////    private JPanel crearPanelNumeracionFilas() {
//////        JPanel panelFilas = new JPanel(new GridLayout(FILAS_ASIENTOS, 1, 0, 12));
//////        panelFilas.setBackground(new Color(20, 20, 20));
//////        panelFilas.setPreferredSize(new Dimension(40, 0));
//////
//////        for (int i = 0; i < FILAS_ASIENTOS; i++) {
//////            JLabel numeroFila = new JLabel(String.valueOf(i + 3), SwingConstants.CENTER);
//////            numeroFila.setForeground(Color.WHITE);
//////            numeroFila.setFont(new Font("Segoe UI", Font.BOLD, 14));
//////            panelFilas.add(numeroFila);
//////        }
//////
//////        return panelFilas;
//////    }
//////
//////    private JPanel crearPanelPrincipalAsientos() {
//////        JPanel panelPrincipal = new JPanel(new GridBagLayout());
//////        panelPrincipal.setBackground(new Color(40, 40, 40));
//////        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//////
//////        GridBagConstraints gbc = new GridBagConstraints();
//////        gbc.fill = GridBagConstraints.BOTH;
//////        gbc.weighty = 1.0;
//////        gbc.insets = new Insets(2, 2, 2, 2);
//////
//////        // Crear los 5 bloques
//////        for (int bloque = 0; bloque < 5; bloque++) {
//////            JPanel panelBloque = crearPanelBloque(bloque, gbc);
//////            panelPrincipal.add(panelBloque, gbc);
//////        }
//////
//////        return panelPrincipal;
//////    }
//////
//////    private JPanel crearPanelBloque(int bloque, GridBagConstraints gbc) {
//////        JPanel panelBloque = new JPanel(new BorderLayout());
//////
//////        boolean esPasillo = (bloque == 1 || bloque == 3);
//////
//////        if (esPasillo) {
//////            panelBloque = crearPanelPasilloLateral(bloque);
//////            configurarConstraintsPasillo(gbc, bloque);
//////        } else {
//////            panelBloque = crearPanelBloqueAsientos(bloque);
//////            configurarConstraintsBloque(gbc, bloque);
//////        }
//////
//////        return panelBloque;
//////    }
//////
//////    private JPanel crearPanelPasilloLateral(int bloque) {
//////        JPanel panelPasillo = new JPanel(new BorderLayout());
//////        panelPasillo.setBackground(new Color(80, 80, 80));
//////        panelPasillo.setBorder(BorderFactory.createLineBorder(new Color(120, 120, 120), 2));
//////
//////        JLabel labelPasillo = new JLabel("PASILLO") {
//////            @Override
//////            protected void paintComponent(Graphics g) {
//////                Graphics2D g2 = (Graphics2D) g.create();
//////                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//////
//////                g2.setColor(new Color(0, 0, 0, 100));
//////                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
//////
//////                g2.setColor(Color.WHITE);
//////                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
//////
//////                g2.rotate(Math.toRadians(-90), getWidth() / 2, getHeight() / 2);
//////                FontMetrics fm = g2.getFontMetrics();
//////                String texto = "PASILLO";
//////                int textoAncho = fm.stringWidth(texto);
//////
//////                int x = (getWidth() - fm.getHeight()) / 2 - 5;
//////                int y = (getHeight() + textoAncho) / 2 - 25;
//////
//////                g2.drawString(texto, x, y);
//////                g2.dispose();
//////            }
//////
//////            @Override
//////            public Dimension getPreferredSize() {
//////                return new Dimension(60, 200);
//////            }
//////        };
//////
//////        labelPasillo.setHorizontalAlignment(SwingConstants.CENTER);
//////        labelPasillo.setVerticalAlignment(SwingConstants.CENTER);
//////        panelPasillo.add(labelPasillo, BorderLayout.CENTER);
//////
//////        return panelPasillo;
//////    }
//////
//////    private JPanel crearPanelBloqueAsientos(int bloque) {
//////        JPanel panelBloque = new JPanel(new GridLayout(FILAS_ASIENTOS, 1, 0, 12));
//////        panelBloque.setBackground(new Color(20, 20, 20));
//////
//////        int startCol = obtenerStartColumna(bloque);
//////        int numColumnas = obtenerNumColumnas(bloque);
//////
//////        for (int i = 0; i < FILAS_ASIENTOS; i++) {
//////            JPanel filaPanel = new JPanel(new GridLayout(1, numColumnas, 8, 0));
//////            filaPanel.setBackground(new Color(20, 20, 20));
//////
//////            for (int j = 0; j < numColumnas; j++) {
//////                int columnaReal = startCol + j;
//////                AsientoButton asiento = new AsientoButton(i, columnaReal, sala);
//////                asiento.addPropertyChangeListener("asientoCambiado", this);
//////                botonesAsientos[i][columnaReal] = asiento;
//////                filaPanel.add(asiento);
//////            }
//////            panelBloque.add(filaPanel);
//////        }
//////
//////        return panelBloque;
//////    }
//////
//////    private int obtenerStartColumna(int bloque) {
//////        switch (bloque) {
//////            case 0:
//////                return 0;
//////            case 2:
//////                return LEFT_BLOCK + AISLE_WIDTH;
//////            case 4:
//////                return LEFT_BLOCK + AISLE_WIDTH + CENTER_BLOCK + AISLE_WIDTH;
//////            default:
//////                return 0;
//////        }
//////    }
//////
//////    private int obtenerNumColumnas(int bloque) {
//////        switch (bloque) {
//////            case 0:
//////                return LEFT_BLOCK;
//////            case 2:
//////                return CENTER_BLOCK;
//////            case 4:
//////                return RIGHT_BLOCK;
//////            default:
//////                return 0;
//////        }
//////    }
//////
//////    private void configurarConstraintsPasillo(GridBagConstraints gbc, int bloque) {
//////        if (bloque == 1) {
//////            gbc.gridx = 1;
//////            gbc.weightx = 1.8;
//////        } else {
//////            gbc.gridx = 3;
//////            gbc.weightx = 1.8;
//////        }
//////    }
//////
//////    private void configurarConstraintsBloque(GridBagConstraints gbc, int bloque) {
//////        switch (bloque) {
//////            case 0:
//////                gbc.gridx = 0;
//////                gbc.weightx = LEFT_BLOCK * 1.0;
//////                break;
//////            case 2:
//////                gbc.gridx = 2;
//////                gbc.weightx = CENTER_BLOCK * 1.0;
//////                break;
//////            case 4:
//////                gbc.gridx = 4;
//////                gbc.weightx = RIGHT_BLOCK * 1.0;
//////                break;
//////        }
//////    }
//////
//////    private JPanel crearPanelInferior() {
//////        JPanel panelInferior = new JPanel(new BorderLayout());
//////        panelInferior.setBackground(new Color(20, 20, 20));
//////        panelInferior.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
//////
//////        // Panel de leyenda
//////        JPanel leyenda = crearPanelLeyenda();
//////
//////        // Panel de información derecha
//////        JPanel panelDerecho = crearPanelDerecho();
//////
//////        panelInferior.add(leyenda, BorderLayout.WEST);
//////        panelInferior.add(panelDerecho, BorderLayout.EAST);
//////
//////        return panelInferior;
//////    }
//////
//////    private JPanel crearPanelLeyenda() {
//////        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
//////        leyenda.setBackground(new Color(20, 20, 20));
//////
//////        leyenda.add(crearItemLeyenda(new Color(120, 120, 120), "Disponible"));
//////        leyenda.add(crearItemLeyenda(new Color(40, 80, 180), "Seleccionado"));
//////        leyenda.add(crearItemLeyenda(new Color(180, 40, 40), "Ocupado"));
//////
//////        return leyenda;
//////    }
//////
//////    private JPanel crearItemLeyenda(Color color, String texto) {
//////        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
//////        p.setBackground(new Color(20, 20, 20));
//////
//////        JButton ejemplo = new JButton() {
//////            @Override
//////            protected void paintComponent(Graphics g) {
//////                Graphics2D g2 = (Graphics2D) g;
//////                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//////                int w = getWidth(), h = getHeight();
//////                g2.setColor(color);
//////                g2.fillRoundRect(2, 2, w - 4, h - 4, 8, 8);
//////                g2.setColor(color.brighter());
//////                g2.setStroke(new BasicStroke(1));
//////                g2.drawRoundRect(2, 2, w - 4, h - 4, 8, 8);
//////            }
//////        };
//////        ejemplo.setPreferredSize(new Dimension(24, 18));
//////        ejemplo.setContentAreaFilled(false);
//////        ejemplo.setBorderPainted(false);
//////        ejemplo.setOpaque(false);
//////
//////        JLabel l = new JLabel(texto);
//////        l.setForeground(Color.WHITE);
//////        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
//////
//////        p.add(ejemplo);
//////        p.add(l);
//////        return p;
//////    }
//////
//////    private JPanel crearPanelDerecho() {
//////        JPanel panelDerecho = new JPanel(new BorderLayout());
//////        panelDerecho.setBackground(new Color(20, 20, 20));
//////
//////        // Contador
//////        contadorLabel.setForeground(Color.WHITE);
//////        contadorLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
//////
//////        // Botones de control
//////        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
//////        panelBotones.setBackground(new Color(20, 20, 20));
//////
//////        JButton btnReporte = new JButton("Estado de Sala");
//////        JButton btnLimpiar = new JButton("Anular Selecciones");
//////        JButton btnConfirmar = new JButton("Confirmar Selección");
//////
//////        configurarBoton(btnReporte);
//////        configurarBoton(btnLimpiar);
//////        configurarBotonConfirmar(btnConfirmar);
//////
//////        btnReporte.addActionListener(e -> generarReporte());
//////        btnLimpiar.addActionListener(e -> limpiarSelecciones());
//////        btnConfirmar.addActionListener(e -> confirmarSelecciones());
//////        btnConfirmar.addActionListener(e -> System.out.println(elegirMetodoPago()));
//////
//////        panelBotones.add(btnReporte);
//////        panelBotones.add(btnLimpiar);
//////        panelBotones.add(btnConfirmar);
//////
//////        panelDerecho.add(contadorLabel, BorderLayout.NORTH);
//////        panelDerecho.add(panelBotones, BorderLayout.SOUTH);
//////
//////        return panelDerecho;
//////    }
//////
//////    private boolean elegirMetodoPago() {
//////        String[] opciones = {"Tarjeta de Crédito", "PayPal", "Criptomonedas", "Transferencia Bancaria"};
//////        int seleccion = JOptionPane.showOptionDialog(this,
//////                "Seleccione el método de pago:",
//////                "Método de Pago",
//////                JOptionPane.DEFAULT_OPTION,
//////                JOptionPane.QUESTION_MESSAGE,
//////                null,
//////                opciones,
//////                opciones[0]);
//////
//////        if (seleccion == -1) {
//////            System.out.println("❌ Pago cancelado por el usuario.");
//////            return false;
//////        } else {
//////            System.out.println("✅ Método de pago seleccionado: " + opciones[seleccion]);
//////            return true;
//////        }
//////    }
//////
//////    private void configurarBoton(JButton boton) {
//////        boton.setBackground(new Color(70, 130, 180));
//////        boton.setForeground(Color.BLACK);
//////        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
//////        boton.setFocusPainted(false);
//////        boton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
//////
//////        boton.addMouseListener(new MouseAdapter() {
//////            @Override
//////            public void mouseEntered(MouseEvent e) {
//////                boton.setBackground(new Color(100, 160, 210));
//////            }
//////
//////            @Override
//////            public void mouseExited(MouseEvent e) {
//////                boton.setBackground(new Color(70, 130, 180));
//////            }
//////        });
//////    }
//////
//////    private void configurarBotonConfirmar(JButton boton) {
//////        boton.setBackground(new Color(40, 160, 60));
//////        boton.setForeground(Color.BLACK);
//////        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
//////        boton.setFocusPainted(false);
//////        boton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
//////
//////        boton.addMouseListener(new MouseAdapter() {
//////            @Override
//////            public void mouseEntered(MouseEvent e) {
//////                boton.setBackground(new Color(60, 180, 80));
//////            }
//////
//////            @Override
//////            public void mouseExited(MouseEvent e) {
//////                boton.setBackground(new Color(40, 160, 60));
//////            }
//////        });
//////    }
//////
//////    private void actualizarContador() {
//////        int count = sala.contarAsientosSeleccionados();
//////        contadorLabel.setText(count + (count == 1 ? " asiento seleccionado" : " asientos seleccionados"));
//////    }
//////
//////    private void generarReporte() {
//////        try {
//////            org.json.JSONObject reporte = gestorJson.generarReporte();
//////            String mensaje = String.format(
//////                    "📊 REPORTE DE SALA\n\n" +
//////                            "📅 Fecha del reporte: %s\n" +
//////                            "🎫 Total de asientos: %d\n" +
//////                            "🟢 Asientos libres: %d\n" +
//////                            "🔵 Asientos seleccionados: %d\n" +
//////                            "🔴 Asientos ocupados: %d\n" +
//////                            "⏰ Última actualización: %s",
//////                    reporte.getString("fechaReporte"),
//////                    reporte.getInt("totalAsientos"),
//////                    reporte.getInt("asientosLibres"),
//////                    reporte.getInt("asientosSeleccionados"),
//////                    reporte.getInt("asientosOcupados"),
//////                    reporte.getString("ultimaActualizacion")
//////            );
//////
//////            JOptionPane.showMessageDialog(this, mensaje, "Estado de Sala", JOptionPane.INFORMATION_MESSAGE);
//////
//////        } catch (Exception e) {
//////            JOptionPane.showMessageDialog(this, "Error al generar el reporte: " + e.getMessage(),
//////                    "Error", JOptionPane.ERROR_MESSAGE);
//////        }
//////    }
//////
//////    private void limpiarSelecciones() {
//////        int seleccionados = sala.contarAsientosSeleccionados();
//////
//////        if (seleccionados == 0) {
//////            JOptionPane.showMessageDialog(this,
//////                    "No hay asientos seleccionados para Anular.",
//////                    "Sin Selecciones",
//////                    JOptionPane.INFORMATION_MESSAGE);
//////            return;
//////        }
//////
//////        int respuesta = JOptionPane.showConfirmDialog(this,
//////                "¿Estás seguro de que quieres Anular el/los " + seleccionados + " asiento(s) seleccionado(s)?\n" +
//////                        "Esto convertirá todos los asientos seleccionados a libres.",
//////                "Anular Selecciones",
//////                JOptionPane.YES_NO_OPTION);
//////
//////        if (respuesta == JOptionPane.YES_OPTION) {
//////            gestorJson.limpiarSelecciones();
//////            actualizarVisualizacionAsientos();
//////            JOptionPane.showMessageDialog(this,
//////                    seleccionados + " selección(es) Anulada(s) correctamente.",
//////                    "Éxito",
//////                    JOptionPane.INFORMATION_MESSAGE);
//////        }
//////    }
//////
//////    private void confirmarSelecciones() {
//////        int seleccionados = sala.contarAsientosSeleccionados();
//////
//////        if (seleccionados == 0) {
//////            JOptionPane.showMessageDialog(this,
//////                    "No hay asientos seleccionados para confirmar.\n" +
//////                            "Selecciona algunos asientos primero.",
//////                    "Sin Selecciones",
//////                    JOptionPane.WARNING_MESSAGE);
//////            return;
//////        }
//////
//////        int respuesta = JOptionPane.showConfirmDialog(this,
//////                "¿Confirmar " + seleccionados + " asiento(s) seleccionado(s)?\n\n" +
//////                        "✅ Los asientos seleccionados (azules) pasarán a OCUPADOS (rojos)\n" +
//////                        // "💾 Se guardarán permanentemente en el archivo JSON\n" +
//////                        "🔒 No podrán ser modificados después",
//////                "Confirmar Selección",
//////                JOptionPane.YES_NO_OPTION,
//////                JOptionPane.QUESTION_MESSAGE);
//////
//////        if (respuesta == JOptionPane.YES_OPTION) {
//////            int confirmados = gestorJson.confirmarSelecciones();
//////            actualizarVisualizacionAsientos();
//////
//////            JOptionPane.showMessageDialog(this,
//////                    "✅ " + confirmados + " asiento(s) confirmado(s) exitosamente!\n\n" +
//////                            "🔴 Ahora aparecen en ROJO (ocupados)\n" +
//////                            "💾 Guardados correctamente!!!\n" +
//////                            "🔒 Ya no se pueden modificar",
//////                    "Confirmación Exitosa",
//////                    JOptionPane.INFORMATION_MESSAGE);
//////        }
//////    }
//////
//////    /**
//////     * Método para forzar la actualización de todos los asientos
//////     */
//////    public void actualizarVisualizacionAsientos() {
//////        for (int i = 0; i < FILAS_ASIENTOS; i++) {
//////            for (int j = 0; j < COLUMNAS; j++) {
//////                if (botonesAsientos[i][j] != null) {
//////                    botonesAsientos[i][j].repaint();
//////                }
//////            }
//////        }
//////        actualizarContador();
//////        System.out.println("🔄 Visualización actualizada");
//////    }
//////
//////    @Override
//////    public void propertyChange(PropertyChangeEvent evt) {
//////        if ("asientoCambiado".equals(evt.getPropertyName())) {
//////            actualizarContador();
//////        }
//////    }
//////
//////    //funcion llamar selector asientos y cuando deje de estar en uso continue con la ejecucion de lo que lo llamo
//////    public static void mostrarSelectorAsientos(Funcion funcion) {
//////        SelectorAsientos selector = new SelectorAsientos(funcion);
//////
//////        // Agregar el listener antes de mostrar la ventana para garantizar que recibimos el evento
//////        selector.addWindowListener(new WindowAdapter() {
//////            @Override
//////            public void windowClosed(WindowEvent e) {
//////                System.out.println("🔚 Selector de asientos cerrado, continuando ejecución...");
//////            }
//////        });
//////
//////        selector.setVisible(true);
//////    }
//////
//////}