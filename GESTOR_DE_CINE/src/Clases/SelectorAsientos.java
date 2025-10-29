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
            procesarPago();
            // confirmarSelecciones();
            // ✅ Primero procesar el pago, luego confirmar
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
        int asientosSeleccionados = confirmarSelecciones();

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

                // ✅ CORREGIDO: Usar ButtonType en lugar de Button
                ButtonType btnImprimirTicket = new ButtonType("🎫 Imprimir Ticket");
                ButtonType btnCerrar = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);

                // Reemplazar los botones por defecto
                exito.getButtonTypes().setAll(btnImprimirTicket, btnCerrar);

                // Mostrar y esperar la respuesta
                Optional<ButtonType> resultado2 = exito.showAndWait();

                // Verificar qué botón presionó el usuario
                if (resultado2.isPresent() && resultado2.get() == btnImprimirTicket) {
                    imprimirTicketCompra(metodoSeleccionado, totalAPagar, asientosSeleccionados);
                }

                primaryStage.close();
            }
        }
    }
    private void imprimirTicketCompra(String metodoPago, double monto, int asientos) {
        System.out.println("----- TICKET DE COMPRA -----");
        System.out.println("Método de Pago: " + metodoPago);
        System.out.printf("Monto Pagado: $%,.2f%n", monto);
        System.out.println("Asientos Comprados: " + asientos);
        System.out.println("Gracias por su compra. ¡Disfrute la función!");
        System.out.println("----------------------------");
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

    private int confirmarSelecciones() {
        int seleccionados = sala.contarAsientosSeleccionados();

        if (seleccionados == 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Sin Selecciones");
            alert.setHeaderText("No hay asientos seleccionados para confirmar.");
            alert.setContentText("Selecciona algunos asientos primero.");
            alert.showAndWait();
            return seleccionados;
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
            return seleccionados;
        }
        return seleccionados;
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
