package Clases.GestionDePagos;

import Clases.Funcion;
import Clases.login.usuario.Cliente;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

/*public class GestorReservas {
    private Cliente cliente;
    private Funcion funcionSeleccionada;
    private int asientosSeleccionados;

    public GestorReservas(Cliente cliente) {
        this.cliente = cliente;
    }


    private void mostrarResumenCompra() {
        Stage stage = new Stage();
        stage.setTitle("Confirmar Compra - CINEMAX");

        BorderPane panel = new BorderPane();
        panel.setPadding(new Insets(20));

        // Header
        Label titulo = new Label("Confirmar Compra");
        titulo.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        panel.setTop(titulo);

        // Detalles de la compra
        VBox detalles = new VBox(15);
        detalles.setPadding(new Insets(20));

        DateTimeFormatter fechaFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter horaFormatter = DateTimeFormatter.ofPattern("HH:mm");

        double total = funcionSeleccionada.getPrecio() * asientosSeleccionados;
        int puntosGanados = (int) (total / 1000);

        detalles.getChildren().addAll(
                crearFilaDetalle("Película:", funcionSeleccionada.getPelicula().getNombrePelicula()),
                crearFilaDetalle("Fecha:", funcionSeleccionada.getHorarioFuncion().format(fechaFormatter)),
                crearFilaDetalle("Hora:", funcionSeleccionada.getHorarioFuncion().format(horaFormatter)),
                crearFilaDetalle("Sala:", funcionSeleccionada.getSala().getNombreSala()),
                crearFilaDetalle("Asientos:", asientosSeleccionados + " asientos"),
                crearFilaDetalle("Precio Unitario:", String.format("$%.2f", funcionSeleccionada.getPrecio())),
                crearFilaDetalle("Total:", String.format("$%.2f", total)),
                crearFilaDetalle("Puntos a ganar:", puntosGanados + " puntos")
        );

        panel.setCenter(detalles);

        // Botones
        HBox botones = new HBox(15);
        botones.setPadding(new Insets(20));

        Button btnConfirmar = new Button("Confirmar Compra");
        btnConfirmar.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        btnConfirmar.setOnAction(e -> confirmarCompra(stage, total, puntosGanados));

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setOnAction(e -> stage.close());

        botones.getChildren().addAll(btnConfirmar, btnCancelar);
        panel.setBottom(botones);

        Scene scene = new Scene(panel, 450, 450);
        stage.setScene(scene);
        stage.show();
    }

    private HBox crearFilaDetalle(String etiqueta, String valor) {
        HBox fila = new HBox(10);

        Label lblEtiqueta = new Label(etiqueta);
        lblEtiqueta.setStyle("-fx-font-weight: bold; -fx-min-width: 120;");

        Label lblValor = new Label(valor);

        fila.getChildren().addAll(lblEtiqueta, lblValor);
        return fila;
    }

    private void confirmarCompra(Stage stage, double total, int puntosGanados) {
        if (asientosSeleccionados == 0) {
            mostrarAlerta("Error", "Debe seleccionar al menos un asiento");
            return;
        }

        // Agregar puntos de fidelidad
        cliente.agregarPuntos(puntosGanados);

        // Guardar la compra en el historial
        HistorialCompras.agregarCompra(cliente, funcionSeleccionada, asientosSeleccionados, total);

        mostrarAlerta("Compra Exitosa",
                "¡Compra realizada con éxito!\n" +
                        "Asientos: " + asientosSeleccionados + "\n" +
                        "Total: $" + total + "\n" +
                        "Puntos ganados: " + puntosGanados + "\n\n" +
                        "¡Gracias por su compra!");

        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}


 */