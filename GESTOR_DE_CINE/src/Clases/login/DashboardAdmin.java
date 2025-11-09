package Clases.login;
import Clases.GestorAdministrador;
import Clases.GestorFunciones;
import Clases.login.usuario.Cliente;
import Clases.login.usuario.Usuario;
import Enumeradores.login.TipoUsuario;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardAdmin {
    private GestorUsuarios gestorUsuarios;
    private GestorFunciones gestorFunciones;
    private Stage stage;

    // ✅ Constructor modificado para recibir GestorFunciones
    public DashboardAdmin(GestorUsuarios gestorUsuarios, GestorFunciones gestorFunciones,Cliente cliente) {
        this.gestorUsuarios = gestorUsuarios;
        this.gestorFunciones = gestorFunciones;
    }
    public DashboardAdmin (){}

    public void mostrarDashboard(Cliente cliente) {
        Platform.runLater(() -> {
            stage = new Stage();
            stage.setTitle("Dashboard Administrador - CINE LOS CULIA");
            stage.setWidth(1200);
            stage.setHeight(800);

            TabPane tabPane = new TabPane();

            // Tab Resumen
            Tab tabResumen = new Tab("Resumen", crearPanelResumen());
            tabResumen.setClosable(false);

            // Tab Usuarios
            Tab tabUsuarios = new Tab("Gestión de Usuarios", crearPanelUsuarios());
            tabUsuarios.setClosable(false);

            // Tab Estadísticas
            Tab tabEstadisticas = new Tab("Estadísticas", crearPanelEstadisticas());
            tabEstadisticas.setClosable(false);

            // ✅ NUEVA PESTAÑA: Gestión de Cine
            Tab tabCine = new Tab("Gestión de Cine", crearPanelCine(cliente));
            tabCine.setClosable(false);

            // ✅ Agregar todas las pestañas
            tabPane.getTabs().addAll(tabResumen, tabUsuarios, tabEstadisticas, tabCine);

            Scene scene = new Scene(tabPane);
            stage.setScene(scene);
            stage.show();
        });
    }

    // ✅ Método corregido - sin parámetros
    private BorderPane crearPanelCine(Cliente cliente) {
        BorderPane panel = new BorderPane();
        panel.setPadding(new Insets(20));

        // Header
        Label titulo = new Label("Gestión de Cine - Películas y Funciones");
        titulo.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        panel.setTop(titulo);

        // Contenido central con botón
        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(50));

        Label descripcion = new Label("Accede al gestor completo de películas, funciones y salas");
        descripcion.setStyle("-fx-font-size: 14; -fx-text-fill: #666;");

        Button btnAbrirGestorCine = new Button("Abrir Gestor de Cine Completo");
        btnAbrirGestorCine.setStyle("""
            -fx-background-color: #e67e22;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-font-size: 14;
            -fx-padding: 12 24 12 24;
            -fx-background-radius: 8;
        """);

        btnAbrirGestorCine.setOnAction(e -> {
            // Cerrar el dashboard y abrir el gestor de cine
            stage.close();
            GestorAdministrador.iniciarAdministrador(gestorFunciones,cliente);
        });

        centerBox.getChildren().addAll(descripcion, btnAbrirGestorCine);
        panel.setCenter(centerBox);

        return panel;
    }

    // Los demás métodos permanecen igual...
    private BorderPane crearPanelResumen() {
        BorderPane panel = new BorderPane();
        panel.setPadding(new Insets(20));

        // Header
        Label titulo = new Label("Resumen del Sistema");
        titulo.setFont(Font.font(20));
        titulo.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        panel.setTop(titulo);

        // Métricas rápidas
        GridPane metricas = new GridPane();
        metricas.setHgap(20);
        metricas.setVgap(15);
        metricas.setPadding(new Insets(20));

        List<Usuario> usuarios = gestorUsuarios.obtenerTodosUsuarios();
        long totalUsuarios = usuarios.size();
        long admins = usuarios.stream().filter(u -> u.getTipoUsuario() == TipoUsuario.ADMINISTRADOR).count();
        long clientes = usuarios.stream().filter(u -> u.getTipoUsuario() == TipoUsuario.CLIENTE).count();

        // Tarjetas de métricas
        metricas.add(crearTarjetaMetrica("Total Usuarios", String.valueOf(totalUsuarios), "#3498db"), 0, 0);
        metricas.add(crearTarjetaMetrica("Administradores", String.valueOf(admins), "#e74c3c"), 1, 0);
        metricas.add(crearTarjetaMetrica("Clientes", String.valueOf(clientes), "#27ae60"), 3, 0);

        // Estadísticas de login
        GestorEstadisticasLogin stats = GestorEstadisticasLogin.getInstance();
        metricas.add(crearTarjetaMetrica("Logins Hoy", String.valueOf(stats.getTotalLogins()), "#9b59b6"), 0, 1);
        metricas.add(crearTarjetaMetrica("Logins Admin", String.valueOf(stats.getLoginsAdmin()), "#34495e"), 1, 1);
        metricas.add(crearTarjetaMetrica("Logins Cliente", String.valueOf(stats.getLoginsCliente()), "#1abc9c"), 2, 1);

        panel.setCenter(metricas);

        return panel;
    }

    private VBox crearTarjetaMetrica(String titulo, String valor, String color) {
        VBox tarjeta = new VBox(10);
        tarjeta.setPadding(new Insets(15));
        tarjeta.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 10; -fx-border-radius: 10;");

        Label labelTitulo = new Label(titulo);
        labelTitulo.setStyle("-fx-text-fill: white; -fx-font-size: 14;");

        Label labelValor = new Label(valor);
        labelValor.setStyle("-fx-text-fill: white; -fx-font-size: 24; -fx-font-weight: bold;");

        tarjeta.getChildren().addAll(labelTitulo, labelValor);
        return tarjeta;
    }

    private BorderPane crearPanelUsuarios() {
        BorderPane panel = new BorderPane();
        panel.setPadding(new Insets(20));

        // Header con botones
        VBox header = new VBox(10);
        header.setPadding(new Insets(0, 0, 20, 0));

        Label titulo = new Label("Gestión de Usuarios");
        titulo.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        // Botones en horizontal
        HBox botonesPanel = new HBox(10);
        botonesPanel.setAlignment(Pos.CENTER_LEFT);

        Button btnActualizar = new Button("Actualizar Lista");
        btnActualizar.setOnAction(e -> actualizarTablaUsuarios(panel));

        Button btnEstadisticas = new Button("Ver Estadísticas Login");
        btnEstadisticas.setOnAction(e -> GestorEstadisticasLogin.getInstance().mostrarGraficaLogins());

        // ✅ NUEVO BOTÓN: Registrar Administrador
        Button btnRegistrarAdmin = new Button("Registrar Nuevo Administrador");
        btnRegistrarAdmin.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        btnRegistrarAdmin.setOnAction(e -> {
            System.out.println("🎯 ADMIN SOLICITA REGISTRAR NUEVO ADMIN");
//            RegistroUsuario.abrirRegistroAdministrativo();
            RegistroUsuario registro = new RegistroUsuario(true, gestorFunciones); // ✅ Pasar gestorFunciones
            registro.mostrarVentana();
        });

        botonesPanel.getChildren().addAll(btnActualizar, btnEstadisticas, btnRegistrarAdmin);
        header.getChildren().addAll(titulo, botonesPanel);
        panel.setTop(header);

        // Tabla de usuarios
        actualizarTablaUsuarios(panel);

        return panel;
    }
    private void actualizarTablaUsuarios(BorderPane panel) {
        TableView<Usuario> tabla = new TableView<>();

        // Columnas
        TableColumn<Usuario, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Usuario, String> colApellido = new TableColumn<>("Apellido");
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));

        TableColumn<Usuario, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Usuario, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoUsuario"));

        TableColumn<Usuario, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        TableColumn<Usuario, String> colUltimoAcceso = new TableColumn<>("Último Acceso");
        colUltimoAcceso.setCellValueFactory(cellData -> {
            String fecha = cellData.getValue().getFechaUltimoAcceso()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            return new javafx.beans.property.SimpleStringProperty(fecha);
        });

        tabla.getColumns().addAll(colNombre, colApellido, colEmail, colTipo, colEstado, colUltimoAcceso);

        // Datos
        ObservableList<Usuario> usuarios = FXCollections.observableArrayList(
                gestorUsuarios.obtenerTodosUsuarios()
        );
        tabla.setItems(usuarios);

        panel.setCenter(tabla);
    }

    private VBox crearPanelEstadisticas() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(20));

        Label titulo = new Label("Estadísticas de Usuarios");
        titulo.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        // Gráfico de torta de tipos de usuario
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Distribución de Usuarios");

        List<Usuario> usuarios = gestorUsuarios.obtenerTodosUsuarios();
        long admins = usuarios.stream().filter(u -> u.getTipoUsuario() == TipoUsuario.ADMINISTRADOR).count();
        long clientes = usuarios.stream().filter(u -> u.getTipoUsuario() == TipoUsuario.CLIENTE).count();

        pieChart.getData().add(new PieChart.Data("Administradores", admins));
        pieChart.getData().add(new PieChart.Data("Clientes", clientes));

        panel.getChildren().addAll(titulo, pieChart);
        return panel;
    }

    public void mostrarDashboard() {
        mostrarDashboard(null);
    }
}