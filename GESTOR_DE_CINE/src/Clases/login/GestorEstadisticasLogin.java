package Clases.login;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GestorEstadisticasLogin {
    private static GestorEstadisticasLogin instance;
    private List<RegistroLogin> registros;
    private int loginsAdmin;
    private int loginsEmpleado;
    private int loginsCliente;
    private int[] loginsPorHora;

    private GestorEstadisticasLogin() {
        this.registros = new ArrayList<>();
        this.loginsPorHora = new int[24];
        reiniciarContadores();
    }

    public static GestorEstadisticasLogin getInstance() {
        if (instance == null) {
            instance = new GestorEstadisticasLogin();
        }
        return instance;
    }

    private void reiniciarContadores() {
        loginsAdmin = 0;
        loginsCliente = 0;
        for (int i = 0; i < 24; i++) {
            loginsPorHora[i] = 0;
        }
    }

    public void registrarLogin(String usuario, String tipoUsuario) {
        LocalDateTime ahora = LocalDateTime.now();
        RegistroLogin registro = new RegistroLogin(usuario, tipoUsuario, ahora);
        registros.add(registro);

        // Actualizar contadores
        if (tipoUsuario.equals("Administrador")) {
            loginsAdmin++;
        } else if (tipoUsuario.equals("Empleado")) {
            loginsEmpleado++;
        } else {
            loginsCliente++;
        }

        // Actualizar por hora
        int hora = ahora.getHour();
        loginsPorHora[hora]++;
    }

    public void mostrarGraficaLogins() {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setTitle("Estadísticas de Logins - CINE MARCENTER");
            stage.setWidth(800);
            stage.setHeight(600);

            TabPane tabbedPane = new TabPane();

            // Gráfica por tipo de usuario
            Tab tabTipoUsuario = new Tab("Por Tipo de Usuario", crearPanelTipoUsuario());
            tabTipoUsuario.setClosable(false);

            // Gráfica por hora del día
            Tab tabHoraDia = new Tab("Por Hora del Día", crearPanelHoraDia());
            tabHoraDia.setClosable(false);

            // Tabla de registros
            Tab tabRegistros = new Tab("Registros", crearPanelRegistros());
            tabRegistros.setClosable(false);

            tabbedPane.getTabs().addAll(tabTipoUsuario, tabHoraDia, tabRegistros);

            Scene scene = new Scene(tabbedPane);
            stage.setScene(scene);
            stage.show();
        });
    }

    private VBox crearPanelTipoUsuario() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(20));

        Label titleLabel = new Label("Logins por Tipo de Usuario");
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        // Crear gráfico de barras
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

        barChart.setTitle("Total de Logins: " + getTotalLogins());
        xAxis.setLabel("Tipo de Usuario");
        yAxis.setLabel("Número de Logins");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Administrador", loginsAdmin));
        series.getData().add(new XYChart.Data<>("Empleado", loginsEmpleado));
        series.getData().add(new XYChart.Data<>("Cliente", loginsCliente));

        barChart.getData().add(series);
        barChart.setPrefSize(700, 400);

        panel.getChildren().addAll(titleLabel, barChart);
        return panel;
    }

    private VBox crearPanelHoraDia() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(20));

        Label titleLabel = new Label("Logins por Hora del Día");
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        // Crear gráfico de barras por hora
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

        barChart.setTitle("Distribución por Hora");
        xAxis.setLabel("Hora del Día");
        yAxis.setLabel("Número de Logins");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int i = 0; i < 24; i++) {
            if (i % 4 == 0) { // Mostrar cada 4 horas para mejor visualización
                series.getData().add(new XYChart.Data<>(i + "h", loginsPorHora[i]));
            }
        }

        barChart.getData().add(series);
        barChart.setPrefSize(700, 400);

        panel.getChildren().addAll(titleLabel, barChart);
        return panel;
    }

    private BorderPane crearPanelRegistros() {
        BorderPane panel = new BorderPane();
        panel.setPadding(new Insets(20));

        Label titleLabel = new Label("Registros de Login (" + registros.size() + " totales)");
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        panel.setTop(titleLabel);

        // Crear tabla
        TableView<RegistroLogin> tabla = new TableView<>();

        TableColumn<RegistroLogin, String> colUsuario = new TableColumn<>("Usuario");
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));

        TableColumn<RegistroLogin, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoUsuario"));

        TableColumn<RegistroLogin, String> colFecha = new TableColumn<>("Fecha y Hora");
        colFecha.setCellValueFactory(cellData -> {
            LocalDateTime fecha = cellData.getValue().getFechaHora();
            String fechaFormateada = fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            return new javafx.beans.property.SimpleStringProperty(fechaFormateada);
        });

        tabla.getColumns().addAll(colUsuario, colTipo, colFecha);

        ObservableList<RegistroLogin> data = FXCollections.observableArrayList(registros);
        tabla.setItems(data);

        panel.setCenter(tabla);
        return panel;
    }

    // Métodos para obtener estadísticas (mantener igual)
    public int getTotalLogins() {
        return registros.size();
    }

    public int getLoginsAdmin() {
        return loginsAdmin;
    }

    public int getLoginsCliente() {
        return loginsCliente;
    }

    public int[] getLoginsPorHora() {
        return loginsPorHora.clone();
    }
}