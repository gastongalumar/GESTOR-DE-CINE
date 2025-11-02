package Clases.login;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SistemaPrincipal extends Application {
    private String usuarioActual;
    private String tipoUsuario;
    private Stage stage;

    public SistemaPrincipal(String usuario, String tipoUsuario) {
        this.usuarioActual = usuario;
        this.tipoUsuario = tipoUsuario;
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        inicializarInterfaz();
    }

    private void inicializarInterfaz() {
        stage.setTitle("CINE LOS CULIA - Sistema de Gestión");
        stage.setOnCloseRequest(e -> Platform.exit());
        stage.setWidth(1200);
        stage.setHeight(800);
        stage.setMaximized(true);

        // Panel principal
        BorderPane mainPanel = new BorderPane();

        // Header
        HBox headerPanel = crearHeaderPanel();
        mainPanel.setTop(headerPanel);

        // Menu lateral
        VBox menuPanel = crearMenuPanel();
        mainPanel.setLeft(menuPanel);

        // Contenido principal
        StackPane contentPanel = crearContentPanel();
        mainPanel.setCenter(contentPanel);

        Scene scene = new Scene(mainPanel);
        stage.setScene(scene);
        stage.centerOnScreen();
    }

    private HBox crearHeaderPanel() {
        HBox headerPanel = new HBox();
        headerPanel.setStyle("-fx-background-color: #191923;");
        headerPanel.setPadding(new Insets(10, 20, 10, 20));
        headerPanel.setPrefHeight(70);
        headerPanel.setAlignment(Pos.CENTER_LEFT);

        // Título
        Label titleLabel = new Label("CINE LOS CULIA - Sistema de Gestión Cinematográfica");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 20;");

        // Info usuario
        HBox userPanel = new HBox(10);
        userPanel.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(userPanel, Priority.ALWAYS);

        Label userLabel = new Label("Usuario: " + usuarioActual + " (" + tipoUsuario + ")");
        userLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14;");

        Button logoutButton = new Button("Cerrar Sesión");
        logoutButton.setStyle("-fx-background-color: #963232; -fx-text-fill: white;");
        logoutButton.setOnAction(e -> cerrarSesion());

        userPanel.getChildren().addAll(userLabel, logoutButton);
        headerPanel.getChildren().addAll(titleLabel, userPanel);

        return headerPanel;
    }

    private VBox crearMenuPanel() {
        VBox menuPanel = new VBox();
        menuPanel.setStyle("-fx-background-color: #28283c;");
        menuPanel.setPrefWidth(250);
        menuPanel.setPadding(new Insets(20, 10, 20, 10));
        menuPanel.setSpacing(10);

        // Botones del menú según tipo de usuario
        String[] opcionesMenu = getOpcionesMenu();

        for (String opcion : opcionesMenu) {
            Button menuButton = crearBotonMenu(opcion);
            menuPanel.getChildren().add(menuButton);
        }

        VBox.setVgrow(menuPanel, Priority.ALWAYS);
        return menuPanel;
    }

    private String[] getOpcionesMenu() {
        switch (tipoUsuario.toLowerCase()) {
            case "administrador":
                return new String[]{
                        "Dashboard", "Gestión de Películas", "Gestión de Salas",
                        "Gestión de Usuarios", "Reportes y Estadísticas",
                        "Configuración del Sistema", "Ventas y Facturación"
                };
            case "empleado":
                return new String[]{
                        "Venta de Entradas", "Cartelera", "Clientes",
                        "Reportes de Ventas", "Configuración Horarios"
                };
            case "cliente":
            default:
                return new String[]{
                        "Cartelera", "Comprar Entradas", "Mis Compras",
                        "Promociones", "Perfil"
                };
        }
    }

    private Button crearBotonMenu(String texto) {
        Button button = new Button(texto);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(45);
        button.setStyle("-fx-background-color: #3c3c50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; -fx-padding: 10 15 10 15;");
        button.setOnAction(e -> manejarOpcionMenu(texto));
        return button;
    }

    private StackPane crearContentPanel() {
        StackPane contentPanel = new StackPane();
        contentPanel.setStyle("-fx-background-color: white;");

        // Panel de bienvenida por defecto
        VBox welcomePanel = new VBox(10);
        welcomePanel.setAlignment(Pos.CENTER);
        welcomePanel.setPadding(new Insets(20));

        Label welcomeLabel = new Label("¡Bienvenido al Sistema CINE LOS CULIA!");
        welcomeLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #191923;");

        Label userInfoLabel = new Label("Usuario: " + usuarioActual + " | Tipo: " + tipoUsuario);
        userInfoLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #646478;");

        Label instructionsLabel = new Label("Seleccione una opción del menú lateral para comenzar");
        instructionsLabel.setStyle("-fx-font-size: 14; -fx-font-style: italic; -fx-text-fill: #969696;");

        welcomePanel.getChildren().addAll(welcomeLabel, userInfoLabel, instructionsLabel);
        contentPanel.getChildren().add(welcomePanel);

        return contentPanel;
    }

    private void manejarOpcionMenu(String opcion) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Opción del Menú");
        alert.setHeaderText(null);
        alert.setContentText("Has seleccionado: " + opcion + "\n\nEsta funcionalidad está en desarrollo.");
        alert.showAndWait();
    }

    private void cerrarSesion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Cierre de Sesión");
        alert.setHeaderText(null);
        alert.setContentText("¿Está seguro que desea cerrar sesión?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            stage.close();
            // Volver al login
            CineLogin cineLogin = new CineLogin();
            cineLogin.start(new Stage());
        }
    }


}