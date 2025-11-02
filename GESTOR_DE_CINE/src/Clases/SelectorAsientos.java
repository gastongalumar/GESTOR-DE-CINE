package Clases;

import Enumeradores.EstadoAsiento;
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
import java.util.Random;

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
        columnasValidas.clear(); // Limpiar por si acaso

        // Columnas del bloque izquierdo (0, 1, 2)
        for (int c = 0; c < LEFT_BLOCK; c++) columnasValidas.add(c);

        // Columnas del bloque central (4, 5, 6, 7, 8, 9, 10, 11)
        for (int c = LEFT_BLOCK + AISLE_WIDTH; c < LEFT_BLOCK + AISLE_WIDTH + CENTER_BLOCK; c++) columnasValidas.add(c);

        // Columnas del bloque derecho (13, 14, 15)
        for (int c = LEFT_BLOCK + AISLE_WIDTH + CENTER_BLOCK + AISLE_WIDTH; c < COLUMNAS; c++) columnasValidas.add(c);

        System.out.println("📍 Columnas válidas (donde hay asientos): " + columnasValidas);
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
        inicializarComponentes();

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
        panelFilas.setPadding(new Insets(20, 0, 0, 0)); // 🔧 AGREGAR: 20px de margen superior

        for (int i = 0; i < FILAS_ASIENTOS; i++) {
            // ✅ CORREGIDO: Mostrar LETRAS para las filas (A, B, C, D, E, F, G, H, I, J)
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
        panelLetras.setPadding(new Insets(0, 0, 0, 50)); // 🔧 AGREGAR: 30px de margen izquierdo

        for (int j = 0; j < COLUMNAS; j++) {
            String texto = " ";
            int idx = columnasValidas.indexOf(j);
            if (idx != -1) {
                // ✅ CORREGIDO: Mostrar números para las columnas (1, 2, 3, 4, ...)
                // Solo mostrar números en las columnas donde hay asientos
                texto = String.valueOf(idx + 1); // +1 porque las columnas empiezan en 1
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

                // ✅ PASAR columnasValidas al constructor
                AsientoButton asiento = new AsientoButton(i, columnaReal, sala, columnasValidas);

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

        // ✅ PRIMERO obtener los asientos seleccionados ANTES de confirmarlos
        List<String> asientosSeleccionadosParaTicket = obtenerAsientosSeleccionados();
        int cantidadAsientos = asientosSeleccionadosParaTicket.size();

        if (cantidadAsientos == 0) {
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
                cantidadAsientos,
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

            MetodoDePago metodoPago = new MetodoDePago(generarIdMetodoPago(), metodoSeleccionado);
            String descripcion = String.format("Compra de %d asientos para %s",
                    cantidadAsientos,
                    funcion != null && funcion.getPelicula() != null ? funcion.getPelicula().getNombrePelicula() : "película"
            );

            // Usar el GestorDePagos para procesar el pago
            GestorDePagos gestorPagos = new GestorDePagos();
            boolean pagoExitoso = gestorPagos.procesarPago(metodoPago, totalAPagar, descripcion);

            if (pagoExitoso) {
                // ✅ AHORA confirmar los asientos después del pago exitoso
                int asientosConfirmados = confirmarSelecciones();

                Alert exito = new Alert(Alert.AlertType.INFORMATION);
                exito.setTitle("Pago Exitoso");
                exito.setHeaderText("✅ PAGO PROCESADO EXITOSAMENTE");
                exito.setContentText(String.format(
                        "Método: %s\n" +
                                "Monto: $%,.2f\n" +
                                "Asientos: %d\n" +
                                "¡Disfrute de la función!",
                        metodoSeleccionado, totalAPagar, cantidadAsientos
                ));

                ButtonType btnImprimirTicket = new ButtonType("🎫 Imprimir Ticket");
                ButtonType btnCerrar = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);

                exito.getButtonTypes().setAll(btnImprimirTicket, btnCerrar);

                Optional<ButtonType> resultado2 = exito.showAndWait();

                if (resultado2.isPresent() && resultado2.get() == btnImprimirTicket) {
                    // ✅ Pasar la lista de asientos que guardamos ANTES de confirmar
                    imprimirTicketCompra(metodoSeleccionado, totalAPagar, asientosSeleccionadosParaTicket);
                }

                primaryStage.close();
            }
        }
    }

    private void imprimirTicketCompra(String metodoPago, double monto, List<String> asientosSeleccionados) {
        try {
            // Obtener información
            String nombrePelicula = funcion.getPelicula().getNombrePelicula();
            String horario = funcion.getHorarioFuncion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            String salaNombre = funcion.getSala() != null ? funcion.getSala().getNombreSala() : "Sala Principal";
            String timestamp = java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

            // ✅ USAR la lista de asientos que nos pasan como parámetro
            String asientosStr = asientosSeleccionados.isEmpty() ? "Por asignar" : String.join(", ", asientosSeleccionados);

            // DEBUG: Mostrar en consola qué asientos se encontraron
            System.out.println("🔍 Asientos seleccionados para ticket: " + asientosSeleccionados);
            System.out.println("🔍 Total de asientos: " + asientosSeleccionados.size());

            // Generar número de ticket aleatorio
            String numeroTicket = generarNumeroTicket();
            String codigoOR = "OR-CMX-" + numeroTicket.replace("TK", "");

            // Crear archivo HTML
            String fileName = "ticket_cine_" + System.currentTimeMillis() + ".html";
            java.io.FileWriter writer = new java.io.FileWriter(fileName);

            // Obtener rutas absolutas de las imágenes
            String qrImagePath = obtenerRutaAbsolutaImagen("qr.jpg");
            String barcodeImagePath = obtenerRutaAbsolutaImagen("bar.jpg");

            // Escribir HTML mejorado
            writer.write("<!DOCTYPE html>\n");
            writer.write("<html>\n");
            writer.write("<head>\n");
            writer.write("    <title>Ticket de Cine - CINEMAX</title>\n");
            writer.write("    <meta charset=\"UTF-8\">\n");
            writer.write("    <style>\n");
            writer.write("        body { \n");
            writer.write("            font-family: Arial, sans-serif; \n");
            writer.write("            margin: 0; \n");
            writer.write("            padding: 20px;\n");
            writer.write("            background: #f0f0f0;\n");
            writer.write("        }\n");
            writer.write("        .ticket-container {\n");
            writer.write("            max-width: 480px;\n");
            writer.write("            margin: 0 auto;\n");
            writer.write("            background: white;\n");
            writer.write("            border: 2px solid #333;\n");
            writer.write("            border-radius: 8px;\n");
            writer.write("            box-shadow: 0 4px 12px rgba(0,0,0,0.3);\n");
            writer.write("        }\n");
            writer.write("        .header {\n");
            writer.write("            text-align: center;\n");
            writer.write("            background: linear-gradient(135deg, #1a237e, #283593);\n");
            writer.write("            color: white;\n");
            writer.write("            padding: 12px;\n");
            writer.write("            border-radius: 6px 6px 0 0;\n");
            writer.write("        }\n");
            writer.write("        .header h1 {\n");
            writer.write("            margin: 0;\n");
            writer.write("            font-size: 22px;\n");
            writer.write("            font-weight: bold;\n");
            writer.write("        }\n");
            writer.write("        .ticket-info {\n");
            writer.write("            padding: 15px;\n");
            writer.write("            background: white;\n");
            writer.write("        }\n");
            writer.write("        .info-section {\n");
            writer.write("            margin-bottom: 12px;\n");
            writer.write("            padding: 10px;\n");
            writer.write("            background: #f8f9fa;\n");
            writer.write("            border-radius: 5px;\n");
            writer.write("            border-left: 4px solid #1a237e;\n");
            writer.write("        }\n");
            writer.write("        .info-row {\n");
            writer.write("            display: flex;\n");
            writer.write("            justify-content: space-between;\n");
            writer.write("            margin-bottom: 5px;\n");
            writer.write("            padding-bottom: 3px;\n");
            writer.write("            border-bottom: 1px dashed #ddd;\n");
            writer.write("        }\n");
            writer.write("        .info-label {\n");
            writer.write("            font-weight: bold;\n");
            writer.write("            color: #333;\n");
            writer.write("            min-width: 120px;\n");
            writer.write("        }\n");
            writer.write("        .info-value {\n");
            writer.write("            color: #555;\n");
            writer.write("            text-align: right;\n");
            writer.write("            flex: 1;\n");
            writer.write("        }\n");
            writer.write("        .total-section {\n");
            writer.write("            background: #e8f5e8;\n");
            writer.write("            padding: 12px;\n");
            writer.write("            margin: 12px 0;\n");
            writer.write("            border: 2px solid #4caf50;\n");
            writer.write("            border-radius: 5px;\n");
            writer.write("            text-align: center;\n");
            writer.write("        }\n");
            writer.write("        .total-amount {\n");
            writer.write("            font-size: 20px;\n");
            writer.write("            font-weight: bold;\n");
            writer.write("            color: #2e7d32;\n");
            writer.write("        }\n");
            writer.write("        .codes-section {\n");
            writer.write("            display: flex;\n");
            writer.write("            justify-content: space-between;\n");
            writer.write("            background: #f5f5f5;\n");
            writer.write("            padding: 12px;\n");
            writer.write("            margin: 12px 0;\n");
            writer.write("            border-radius: 5px;\n");
            writer.write("            gap: 10px;\n");
            writer.write("        }\n");
            writer.write("        .code-block {\n");
            writer.write("            flex: 1;\n");
            writer.write("            text-align: center;\n");
            writer.write("            padding: 8px;\n");
            writer.write("        }\n");
            writer.write("        .barcode-simple {\n");
            writer.write("            font-family: 'Courier New', monospace;\n");
            writer.write("            font-size: 12px;\n");
            writer.write("            letter-spacing: 3px;\n");
            writer.write("            font-weight: bold;\n");
            writer.write("            margin: 5px 0;\n");
            writer.write("            padding: 6px;\n");
            writer.write("            background: white;\n");
            writer.write("            border: 1px solid #ccc;\n");
            writer.write("            border-radius: 3px;\n");
            writer.write("            color: #333;\n");
            writer.write("        }\n");
            writer.write("        .numeric-code {\n");
            writer.write("            font-family: 'Courier New', monospace;\n");
            writer.write("            font-size: 11px;\n");
            writer.write("            font-weight: bold;\n");
            writer.write("            margin: 4px 0;\n");
            writer.write("            padding: 5px;\n");
            writer.write("            background: white;\n");
            writer.write("            border: 1px solid #ccc;\n");
            writer.write("            border-radius: 3px;\n");
            writer.write("            color: #333;\n");
            writer.write("        }\n");
            writer.write("        .footer {\n");
            writer.write("            text-align: center;\n");
            writer.write("            padding: 10px;\n");
            writer.write("            background: #333;\n");
            writer.write("            color: white;\n");
            writer.write("            border-radius: 0 0 6px 6px;\n");
            writer.write("            font-size: 10px;\n");
            writer.write("        }\n");
            writer.write("        .important-info {\n");
            writer.write("            background: #fff3cd;\n");
            writer.write("            padding: 8px;\n");
            writer.write("            margin: 8px 0;\n");
            writer.write("            border: 1px solid #ffeaa7;\n");
            writer.write("            border-radius: 4px;\n");
            writer.write("            font-size: 10px;\n");
            writer.write("        }\n");
            writer.write("        .promo-section {\n");
            writer.write("            background: #e3f2fd;\n");
            writer.write("            padding: 6px;\n");
            writer.write("            margin: 8px 0;\n");
            writer.write("            border: 1px solid #90caf9;\n");
            writer.write("            border-radius: 4px;\n");
            writer.write("            text-align: center;\n");
            writer.write("            font-size: 10px;\n");
            writer.write("        }\n");
            writer.write("        .img-container {\n");
            writer.write("            width: 80px;\n");
            writer.write("            height: 80px;\n");
            writer.write("            margin: 5px auto;\n");
            writer.write("            border: 2px solid #333;\n");
            writer.write("            border-radius: 4px;\n");
            writer.write("            overflow: hidden;\n");
            writer.write("            display: flex;\n");
            writer.write("            align-items: center;\n");
            writer.write("            justify-content: center;\n");
            writer.write("            background: white;\n");
            writer.write("        }\n");
            writer.write("        .img-container img {\n");
            writer.write("            max-width: 100%;\n");
            writer.write("            max-height: 100%;\n");
            writer.write("            object-fit: contain;\n");
            writer.write("        }\n");
            writer.write("    </style>\n");
            writer.write("</head>\n");
            writer.write("<body>\n");
            writer.write("    <div class=\"ticket-container\">\n");
            writer.write("        <div class=\"header\">\n");
            writer.write("            <h1>🎬 CINEMAX THEATRES </h1>\n");
            writer.write("            <div style=\"font-size: 11px; margin-top: 3px;\">¡Gracias por su compra!</div>\n");
            writer.write("        </div>\n");
            writer.write("        \n");
            writer.write("        <div class=\"ticket-info\">\n");
            writer.write("            <div class=\"info-section\">\n");
            writer.write("                <div class=\"info-row\">\n");
            writer.write("                    <span class=\"info-label\">Ticket #:</span>\n");
            writer.write("                    <span class=\"info-value\">" + numeroTicket + "</span>\n");
            writer.write("                </div>\n");
            writer.write("                <div class=\"info-row\">\n");
            writer.write("                    <span class=\"info-label\">Emisión:</span>\n");
            writer.write("                    <span class=\"info-value\">" + timestamp + "</span>\n");
            writer.write("                </div>\n");
            writer.write("            </div>\n");
            writer.write("            \n");
            writer.write("            <div class=\"info-section\">\n");
            writer.write("                <div class=\"info-row\">\n");
            writer.write("                    <span class=\"info-label\">Película:</span>\n");
            writer.write("                    <span class=\"info-value\">" + nombrePelicula + "</span>\n");
            writer.write("                </div>\n");
            writer.write("                <div class=\"info-row\">\n");
            writer.write("                    <span class=\"info-label\">Función:</span>\n");
            writer.write("                    <span class=\"info-value\">" + horario + "</span>\n");
            writer.write("                </div>\n");
            writer.write("                <div class=\"info-row\">\n");
            writer.write("                    <span class=\"info-label\">Sala:</span>\n");
            writer.write("                    <span class=\"info-value\">" + salaNombre + "</span>\n");
            writer.write("                </div>\n");
            writer.write("                <div class=\"info-row\">\n");
            writer.write("                    <span class=\"info-label\">Asientos:</span>\n");
            writer.write("                    <span class=\"info-value\">" + asientosStr + "</span>\n");
            writer.write("                </div>\n");
            writer.write("                <div class=\"info-row\">\n");
            writer.write("                    <span class=\"info-label\">Método Pago:</span>\n");
            writer.write("                    <span class=\"info-value\">" + metodoPago + "</span>\n");
            writer.write("                </div>\n");
            writer.write("            </div>\n");
            writer.write("            \n");
            writer.write("            <div class=\"total-section\">\n");
            writer.write("                <div class=\"total-amount\">TOTAL: $" + String.format("%,.2f", monto) + "</div>\n");
            writer.write("            </div>\n");
            writer.write("            \n");
            writer.write("            <div class=\"codes-section\">\n");
            writer.write("                <div class=\"code-block\">\n");
            writer.write("                    <strong style=\"font-size: 11px;\">📋 CÓDIGO OR</strong>\n");
            writer.write("                    <div class=\"numeric-code\" style=\"font-size: 10px; padding: 4px;\">" + codigoOR + "</div>\n");
            writer.write("                    <small style=\"font-size: 8px; color: #666;\">Orden de compra</small>\n");
            writer.write("                </div>\n");
            writer.write("                \n");
            writer.write("                <div class=\"code-block\">\n");
            writer.write("                    <strong style=\"font-size: 11px;\">📊 CÓDIGO BARRAS</strong>\n");
            writer.write("                    <div class=\"img-container\" style=\"width: 150px; height: 100px;\">\n");
            writer.write("                        <img src=\"" + barcodeImagePath + "\" alt=\"Código de Barras\" onerror=\"this.style.display='none'; this.parentNode.innerHTML='<div style=&quot;padding:10px;text-align:center;color:#660;&quot;>Imagen no disponible</div>';\">\n");
            writer.write("                    </div>\n");
            writer.write("                    <div class=\"numeric-code\" style=\"font-size: 9px; margin-top: 3px;\">" + numeroTicket.replace("TK", "") + "</div>\n");
            writer.write("                    <small style=\"font-size: 8px; color: #666;\">Escaneo rápido</small>\n");
            writer.write("                </div>\n");
            writer.write("                \n");
            writer.write("                <div class=\"code-block\">\n");
            writer.write("                    <strong style=\"font-size: 11px;\">🔳 CÓDIGO QR</strong>\n");
            writer.write("                    <div class=\"img-container\" style=\"width: 100px; height: 100px;\">\n");
            writer.write("                        <img src=\"" + qrImagePath + "\" alt=\"Código QR\" onerror=\"this.style.display='none'; this.parentNode.innerHTML='<div style=&quot;padding:10px;text-align:center;color:#666;&quot;>Imagen no disponible</div>';\">\n");
            writer.write("                    </div>\n");
            writer.write("                    <div class=\"numeric-code\" style=\"font-size: 9px; margin-top: 3px;\">" + numeroTicket.replace("TK", "").substring(0, 6) + "</div>\n");
            writer.write("                    <small style=\"font-size: 8px; color: #666;\">Escaneo móvil</small>\n");
            writer.write("                </div>\n");
            writer.write("            </div>\n");
            writer.write("            \n");
            writer.write("            <div class=\"important-info\">\n");
            writer.write("                <strong>📋 INFORMACIÓN IMPORTANTE</strong><br>\n");
            writer.write("                • Presente este ticket en la entrada<br>\n");
            writer.write("                • Llegue con 20 min de anticipación<br>\n");
            writer.write("                • No se permiten reembolsos\n");
            writer.write("            </div>\n");
            writer.write("            \n");
            writer.write("            <div class=\"promo-section\">\n");
            writer.write("                <strong>📱 ¡Descargue nuestra App!</strong><br>\n");
            writer.write("                Obtenga un <strong>combo GRATIS</strong>\n");
            writer.write("            </div>\n");
            writer.write("        </div>\n");
            writer.write("        \n");
            writer.write("        <div class=\"footer\">\n");
            writer.write("            ¡Disfrute de la función!<br>\n");
            writer.write("            @CinemaxTheaters #CinemaxExperience\n");
            writer.write("        </div>\n");
            writer.write("    </div>\n");
            writer.write("</body>\n");
            writer.write("</html>\n");

            writer.close();

            // Abrir automáticamente en el navegador
            abrirEnNavegador(fileName);

        } catch (Exception e) {
            mostrarAlertaError(e);
        }
    }

    // Método auxiliar para obtener la ruta absoluta de las imágenes
    private String obtenerRutaAbsolutaImagen(String rutaRelativa) {
        try {
            java.io.File archivoImagen = new java.io.File(rutaRelativa);
            if (archivoImagen.exists()) {
                return archivoImagen.toURI().toString();
            } else {
                System.err.println("⚠️ No se encontró la imagen: " + rutaRelativa);
                // Intentar buscar en diferentes ubicaciones comunes
                String[] rutasAlternativas = {
                        "src/" + rutaRelativa,
                        "resources/" + rutaRelativa,
                        "./" + rutaRelativa,
                        "../" + rutaRelativa
                };

                for (String rutaAlt : rutasAlternativas) {
                    archivoImagen = new java.io.File(rutaAlt);
                    if (archivoImagen.exists()) {
                        System.out.println("✅ Imagen encontrada en: " + rutaAlt);
                        return archivoImagen.toURI().toString();
                    }
                }

                System.err.println("❌ No se pudo encontrar la imagen en ninguna ubicación alternativa");
                return ""; // Retorna cadena vacía si no se encuentra
            }
        } catch (Exception e) {
            System.err.println("❌ Error al obtener ruta de imagen: " + e.getMessage());
            return "";
        }
    }

    private List<String> obtenerAsientosSeleccionados() {
        List<String> asientos = new ArrayList<>();

        System.out.println("🔍 BUSCANDO ASIENTOS SELECCIONADOS...");

        // Recorrer todos los botones de asientos
        for (int filaIndex = 0; filaIndex < FILAS_ASIENTOS; filaIndex++) {
            for (int colIndex = 0; colIndex < COLUMNAS; colIndex++) {
                if (botonesAsientos[filaIndex][colIndex] != null) {
                    AsientoButton boton = botonesAsientos[filaIndex][colIndex];

                    // Verificar si el asiento está seleccionado
                    if (boton.getEstado() == EstadoAsiento.SELECCIONADO) {
                        // ✅ CORREGIDO: Filas son letras, columnas son números
                        String letraFila = String.valueOf((char) ('A' + filaIndex));

                        // Para la columna, necesitamos encontrar el número correcto
                        int numeroColumna = -1;
                        for (int i = 0; i < columnasValidas.size(); i++) {
                            if (columnasValidas.get(i) == colIndex) {
                                numeroColumna = i + 1; // +1 porque las columnas empiezan en 1
                                break;
                            }
                        }

                        if (numeroColumna != -1) {
                            String asiento = letraFila + numeroColumna;
                            asientos.add(asiento);
                            System.out.println("✅ Asiento seleccionado: " + asiento + " (fila=" + filaIndex + ", col=" + colIndex + ")");
                        }
                    }
                }
            }
        }

        System.out.println("🔍 Total de asientos encontrados: " + asientos.size());
        System.out.println("🔍 Asientos: " + asientos);

        return asientos;
    }

    // 🔢 GENERAR NÚMERO DE TICKET ALEATORIO
    private String generarNumeroTicket() {
        Random random = new Random();
        int numero = random.nextInt(900000) + 100000; // Número entre 100000 y 999999
        return "TK" + numero;
    }

    // ✅ MÉTODO PARA ABRIR AUTOMÁTICAMENTE EN EL NAVEGADOR
    private void abrirEnNavegador(String fileName) {
        try {
            java.io.File file = new java.io.File(fileName);
            if (file.exists()) {
                java.awt.Desktop.getDesktop().open(file);
                System.out.println("🌐 Navegador abierto con el ticket: " + fileName);
            }
        } catch (Exception e) {
            System.err.println("❌ No se pudo abrir automáticamente: " + e.getMessage());

            // Mostrar instrucciones alternativas
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Ticket Listo");
            info.setHeaderText("📄 TICKET GENERADO");
            info.setContentText("El ticket se guardó como: " + fileName +
                    "\n\nPuedes abrirlo manualmente desde:\n" +
                    new java.io.File(".").getAbsolutePath() +
                    "\n\nBusca el archivo y ábrelo con tu navegador.");
            info.showAndWait();
        }
    }

    private void mostrarAlertaError(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error al Generar Ticket");
        alert.setHeaderText("❌ ERROR AL GENERAR EL TICKET");
        alert.setContentText("Ocurrió un error al generar el ticket:\n" + e.getMessage());
        alert.showAndWait();
    }

    // ✅ MÉTODO PARA GENERAR ID ÚNICO PARA MÉTODO DE PAGO
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
