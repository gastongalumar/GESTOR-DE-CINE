package Clases.GestionDePagos;

import Clases.Funcion;
import Clases.login.usuario.Cliente;
import Clases.login.usuario.Usuario;
import ManejoJSON.JSONReservas;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class HistorialCompras {

    public static void agregarReservaAlHistorial(Cliente cliente, Reserva reserva) {
        JSONReservas.guardarReserva(reserva);
        System.out.println("✅ Reserva registrada para: " + cliente.getEmail());
    }

    public static void mostrarHistorialReservas(Usuario cliente) {
        Stage stage = new Stage();
        stage.setTitle("Mi Historial de Reservas - CINE LOS CULIA");

        BorderPane panel = new BorderPane();
        panel.setPadding(new Insets(20));

        Label titulo = new Label("Mis Reservas");
        titulo.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        panel.setTop(titulo);

        // Obtener reservas del cliente
        var reservasCliente = JSONReservas.obtenerReservasPorCliente(cliente.getEmail());

        // Tabla de reservas con manejo seguro
        TableView<JSONObject> tabla = new TableView<>();

        // Columna Película
        TableColumn<JSONObject, String> colPelicula = new TableColumn<>("Película");
        colPelicula.setCellValueFactory(data -> {
            JSONObject reserva = data.getValue();
            return javafx.beans.binding.Bindings.createStringBinding(() ->
                    reserva.has("nombrePelicula") ? reserva.getString("nombrePelicula") : "N/A"
            );
        });

        // Columna Función
        TableColumn<JSONObject, String> colFechaFuncion = new TableColumn<>("Función");
        colFechaFuncion.setCellValueFactory(data -> {
            JSONObject reserva = data.getValue();
            return javafx.beans.binding.Bindings.createStringBinding(() ->
                    reserva.has("horarioFuncion") ? reserva.getString("horarioFuncion") : "N/A"
            );
        });

        // Columna Asientos
        TableColumn<JSONObject, String> colAsientos = new TableColumn<>("Asientos");
        colAsientos.setCellValueFactory(data -> {
            JSONObject reserva = data.getValue();
            return javafx.beans.binding.Bindings.createStringBinding(() -> {
                if (reserva.has("asientosSeleccionados")) {
                    try {
                        return String.valueOf(reserva.getJSONArray("asientosSeleccionados").length());
                    } catch (JSONException e) {
                        return "0";
                    }
                }
                return "0";
            });
        });

        // Columna Total
        TableColumn<JSONObject, String> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(data -> {
            JSONObject reserva = data.getValue();
            return javafx.beans.binding.Bindings.createStringBinding(() ->
                    reserva.has("monto") ? String.format("$%.2f", reserva.getDouble("monto")) : "$0.00"
            );
        });

        // Columna Ticket
        TableColumn<JSONObject, String> colTicket = new TableColumn<>("N° Ticket");
        colTicket.setCellValueFactory(data -> {
            JSONObject reserva = data.getValue();
            return javafx.beans.binding.Bindings.createStringBinding(() ->
                    reserva.has("numeroTicket") ? reserva.getString("numeroTicket") : "N/A"
            );
        });

        // ✅ NUEVA COLUMNA: Acciones (Cancelar)
        TableColumn<JSONObject, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(param -> new TableCell<JSONObject, Void>() {
            private final Button btnCancelar = new Button("Cancelar");

            {
                btnCancelar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
                btnCancelar.setOnAction(event -> {
                    JSONObject reserva = getTableView().getItems().get(getIndex());
                    if (reserva != null) {
                        String numeroTicket = reserva.optString("numeroTicket", "");
                        if (!numeroTicket.isEmpty()) {
                            cancelarReserva(numeroTicket, cliente, stage);
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnCancelar);
                }
            }
        });

        tabla.getColumns().addAll(colPelicula, colFechaFuncion, colAsientos, colTotal, colTicket, colAcciones);

        ObservableList<JSONObject> data = FXCollections.observableArrayList(reservasCliente.obtenerTodos());
        tabla.setItems(data);

//        // Mostrar estadísticas
//        Label puntosLabel = new Label("Tus puntos de fidelidad: " + cliente.getPuntosFidelidad());
//        puntosLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #e67e22;");

        double totalGastado = reservasCliente.obtenerTodos().stream()
                .mapToDouble(reserva -> {
                    try {
                        return reserva.has("monto") ? reserva.getDouble("monto") : 0.0;
                    } catch (JSONException e) {
                        return 0.0;
                    }
                })
                .sum();
        Label totalLabel = new Label("Total gastado: $" + String.format("%.2f", totalGastado));
        totalLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label reservasLabel = new Label("Total de reservas: " + reservasCliente.tamaño());
        reservasLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        // ✅ BOTÓN PARA ACTUALIZAR
        Button btnActualizar = new Button("Actualizar Historial");
        btnActualizar.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnActualizar.setOnAction(e -> {
            stage.close();
            mostrarHistorialReservas(cliente);
        });

        VBox contenido = new VBox(15 , totalLabel, reservasLabel, btnActualizar, tabla);
        contenido.setPadding(new Insets(20));
        panel.setCenter(contenido);

        Scene scene = new Scene(panel, 1000, 600);
        stage.setScene(scene);
        stage.show();
    }

    // ✅ MÉTODO MEJORADO PARA CANCELAR RESERVA
    private static void cancelarReserva(String numeroTicket, Usuario cliente, Stage stageActual) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Cancelación");
        confirmacion.setHeaderText("¿Estás seguro de que quieres cancelar esta reserva?");
        confirmacion.setContentText("Ticket: " + numeroTicket + "\nEsta acción no se puede deshacer.");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean eliminada = JSONReservas.eliminarReserva(numeroTicket);

                if (eliminada) {
                    Alert exito = new Alert(Alert.AlertType.INFORMATION);
                    exito.setTitle("Reserva Cancelada");
                    exito.setHeaderText("✅ Reserva cancelada exitosamente");
                    exito.setContentText("El ticket " + numeroTicket + " ha sido cancelado.\nSe han reembolsado tus puntos si correspondía.");
                    exito.showAndWait();

                    // Recargar el historial
                    stageActual.close();
                    mostrarHistorialReservas(cliente);
                } else {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Error");
                    error.setHeaderText("❌ No se pudo cancelar la reserva");
                    error.setContentText("El ticket " + numeroTicket + " no existe o ya fue cancelado.");
                    error.showAndWait();
                }
            }
        });
    }

    // ✅ NUEVO MÉTODO PARA CANCELAR RESERVA DESDE EL PANEL LATERAL
    public static void mostrarCancelarReserva(Cliente cliente) {
        Stage stage = new Stage();
        stage.setTitle("Cancelar Reserva - CINEMAX");

        VBox panel = new VBox(20);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: #f8f9fa;");

        Label titulo = new Label("Cancelar Reserva");
        titulo.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Obtener reservas activas del cliente
        var reservasCliente = JSONReservas.obtenerReservasPorCliente(cliente.getEmail());

        if (reservasCliente.tamaño() == 0) {
            Label sinReservas = new Label("No tienes reservas activas para cancelar.");
            sinReservas.setStyle("-fx-font-size: 16; -fx-text-fill: #7f8c8d;");
            panel.getChildren().addAll(titulo, sinReservas);
        } else {
            Label instrucciones = new Label("Selecciona una reserva para cancelar:");
            instrucciones.setStyle("-fx-font-size: 14; -fx-text-fill: #34495e;");

            ListView<String> listaReservas = new ListView<>();
            ObservableList<String> items = FXCollections.observableArrayList();

            for (JSONObject reserva : reservasCliente.obtenerTodos()) {
                String pelicula = reserva.optString("nombrePelicula", "N/A");
                String ticket = reserva.optString("numeroTicket", "N/A");
                String funcion = reserva.optString("horarioFuncion", "N/A");
                String item = String.format("🎬 %s - Ticket: %s - %s", pelicula, ticket, funcion);
                items.add(item);
            }

            listaReservas.setItems(items);

            Button btnCancelar = new Button("Cancelar Reserva Seleccionada");
            btnCancelar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
            btnCancelar.setOnAction(e -> {
                String seleccionado = listaReservas.getSelectionModel().getSelectedItem();
                if (seleccionado != null) {
                    // Extraer el número de ticket del texto
                    String[] partes = seleccionado.split("Ticket: ");
                    if (partes.length > 1) {
                        String numeroTicket = partes[1].split(" -")[0];
                        cancelarReserva(numeroTicket, cliente, stage);
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Selección Requerida");
                    alert.setHeaderText("Por favor selecciona una reserva");
                    alert.setContentText("Debes seleccionar una reserva de la lista para cancelar.");
                    alert.showAndWait();
                }
            });

            panel.getChildren().addAll(titulo, instrucciones, listaReservas, btnCancelar);
        }

        Scene scene = new Scene(panel, 500, 400);
        stage.setScene(scene);
        stage.show();
    }

    public static void mostrarHistorial(Usuario cliente) {
        mostrarHistorialReservas(cliente);
    }

    public static void agregarCompra(Cliente cliente, Funcion funcionSeleccionada, int asientosSeleccionados, double total) {
        Reserva reserva = new Reserva(
                cliente,
                funcionSeleccionada,
                "Método de Pago Desconocido",
                total,
                List.of()
        );
        agregarReservaAlHistorial(cliente, reserva);
    }

    public static void agregarCompraConReserva(Cliente cliente, Reserva reserva) {
        agregarReservaAlHistorial(cliente, reserva);
    }

    // ✅ MÉTODO PARA VERIFICAR SI UN CLIENTE TIENE RESERVAS
    public static boolean tieneReservasActivas(Cliente cliente) {
        return JSONReservas.obtenerReservasPorCliente(cliente.getEmail()).tamaño() > 0;
    }
}