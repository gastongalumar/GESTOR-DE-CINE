package Clases.login;

import Clases.Funcion;
import Clases.ListaGenerica;
import Clases.login.usuario.Cliente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HistorialCompras {
    private static ListaGenerica<Compra> compras = new ListaGenerica<>();

    public static class Compra {
        private String clienteEmail;
        private String pelicula;
        private LocalDateTime fechaFuncion;
        private int asientos;
        private double total;
        private LocalDateTime fechaCompra;

        public Compra(String clienteEmail, String pelicula, LocalDateTime fechaFuncion, int asientos, double total) {
            this.clienteEmail = clienteEmail;
            this.pelicula = pelicula;
            this.fechaFuncion = fechaFuncion;
            this.asientos = asientos;
            this.total = total;
            this.fechaCompra = LocalDateTime.now();
        }

        // Getters para la tabla
        public String getPelicula() { return pelicula; }
        public String getFechaFuncion() {
            return fechaFuncion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
        public String getAsientos() { return String.valueOf(asientos); }
        public String getTotal() { return String.format("$%.2f", total); }
        public String getFechaCompra() {
            return fechaCompra.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }

        // Getters para búsquedas y cálculos (AGREGADOS)
        public String getClienteEmail() { return clienteEmail; }
        public LocalDateTime getFechaFuncionOriginal() { return fechaFuncion; }
        public double getTotalNumerico() { return total; }
        public int getAsientosNumerico() { return asientos; } // ✅ NUEVO MÉTODO
    }

    public static void agregarCompra(Cliente cliente, Funcion funcion, int asientos, double total) {
        Compra compra = new Compra(
                cliente.getEmail(),
                funcion.getPelicula().getNombrePelicula(),
                funcion.getHorarioFuncion(),
                asientos,
                total
        );
        compras.agregar(compra);

        System.out.println("✅ Compra registrada: " + cliente.getEmail() +
                " - " + funcion.getPelicula().getNombrePelicula() +
                " - " + asientos + " asientos - $" + total);
    }

    public static void mostrarHistorial(Cliente cliente) {
        Stage stage = new Stage();
        stage.setTitle("Mi Historial de Compras - CINE LOS CULIA");

        BorderPane panel = new BorderPane();
        panel.setPadding(new Insets(20));

        Label titulo = new Label("Mis Compras");
        titulo.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        panel.setTop(titulo);

        // Filtrar compras del cliente usando ListaGenerica
        var comprasCliente = compras.buscarTodos(c -> c.getClienteEmail().equals(cliente.getEmail()));

        // Tabla de compras
        TableView<Compra> tabla = new TableView<>();

        TableColumn<Compra, String> colPelicula = new TableColumn<>("Película");
        colPelicula.setCellValueFactory(new PropertyValueFactory<>("pelicula"));

        TableColumn<Compra, String> colFechaFuncion = new TableColumn<>("Función");
        colFechaFuncion.setCellValueFactory(new PropertyValueFactory<>("fechaFuncion"));

        TableColumn<Compra, String> colAsientos = new TableColumn<>("Asientos");
        colAsientos.setCellValueFactory(new PropertyValueFactory<>("asientos"));

        TableColumn<Compra, String> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        TableColumn<Compra, String> colFechaCompra = new TableColumn<>("Fecha Compra");
        colFechaCompra.setCellValueFactory(new PropertyValueFactory<>("fechaCompra"));

        tabla.getColumns().addAll(colPelicula, colFechaFuncion, colAsientos, colTotal, colFechaCompra);

        ObservableList<Compra> data = FXCollections.observableArrayList(comprasCliente);
        tabla.setItems(data);

        // Mostrar puntos de fidelidad
        Label puntosLabel = new Label("Tus puntos de fidelidad: " + cliente.getPuntosFidelidad());
        puntosLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #e67e22;");

        // Mostrar total gastado
        double totalGastado = comprasCliente.stream()
                .mapToDouble(Compra::getTotalNumerico)
                .sum();
        Label totalLabel = new Label("Total gastado: $" + String.format("%.2f", totalGastado));
        totalLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        VBox contenido = new VBox(15, puntosLabel, totalLabel, tabla);
        contenido.setPadding(new Insets(20));
        panel.setCenter(contenido);

        Scene scene = new Scene(panel, 800, 500);
        stage.setScene(scene);
        stage.show();
    }

    public static ListaGenerica<Compra> getComprasCliente(String email) {
        var comprasCliente = compras.buscarTodos(c -> c.getClienteEmail().equals(email));
        return new ListaGenerica<>(comprasCliente);
    }

    // Métodos adicionales para estadísticas - CORREGIDOS
    public static int getTotalCompras() {
        return compras.tamaño();
    }

    public static double getIngresosTotales() {
        return compras.obtenerTodos().stream()
                .mapToDouble(Compra::getTotalNumerico)
                .sum();
    }

    public static int getAsientosVendidos() {
        return compras.obtenerTodos().stream()
                .mapToInt(Compra::getAsientosNumerico) // ✅ USAR EL NUEVO MÉTODO
                .sum();
    }
}