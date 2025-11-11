package Clases.login;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

public class CineLogin extends Application {
    private TextField emailField;
    private PasswordField passwordField;
    private Button loginButton, registerButton, recoverButton;
    private Stage stage;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        inicializarInterfaz();
        configurarComponentes();
    }

    private void inicializarInterfaz() {
        stage.setTitle("CINE MARCENTER - Inicio de Sesión");
        stage.setOnCloseRequest(e -> Platform.exit());
        stage.setWidth(400);
        stage.setHeight(500);
        stage.setResizable(false);

        // Panel principal con gradiente
        VBox mainPanel = new VBox();
        mainPanel.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.rgb(25, 25, 35)),
                        new Stop(1, Color.rgb(75, 0, 130))),
                CornerRadii.EMPTY, Insets.EMPTY)));

        // Header
        VBox headerPanel = createHeaderPanel();
        mainPanel.getChildren().add(headerPanel);

        // Formulario
        GridPane formPanel = createFormPanel();
        mainPanel.getChildren().add(formPanel);

        // Footer
        HBox footerPanel = createFooterPanel();
        mainPanel.getChildren().add(footerPanel);

        VBox.setVgrow(formPanel, Priority.ALWAYS);

        Scene scene = new Scene(mainPanel);
        stage.setScene(scene);
        stage.centerOnScreen();
    }

private void configurarComponentes() {
    System.out.println("🎯 CONFIGURANDO COMPONENTES CineLogin");

    loginButton.setOnAction(e -> {
        System.out.println("🎯 BOTÓN LOGIN PRESIONADO");
        realizarLogin();
    });

    registerButton.setOnAction(e -> {
        System.out.println("🎯 BOTÓN REGISTRAR PRESIONADO");
        registrarUsuario();
    });

    System.out.println("🎯 BOTONES CONECTADOS - Login: " + loginButton + ", Registrar: " + registerButton);
}

    private VBox createHeaderPanel() {
        VBox headerPanel = new VBox();
        headerPanel.setAlignment(Pos.CENTER);
        headerPanel.setPadding(new Insets(20, 0, 10, 0));
        headerPanel.setSpacing(5);

        Label titleLabel = new Label("CINE MARCENTER");
        titleLabel.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-text-fill: white;");

        Label subtitleLabel = new Label("Sistema de Gestión Cinematográfica");
        subtitleLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #c8c8c8;");

        headerPanel.getChildren().addAll(titleLabel, subtitleLabel);
        return headerPanel;
    }

    private GridPane createFormPanel() {
        GridPane formPanel = new GridPane();
        formPanel.setAlignment(Pos.CENTER);
        formPanel.setHgap(10);
        formPanel.setVgap(10);
        formPanel.setPadding(new Insets(30, 40, 30, 40));

        // Email
        Label emailLabel = new Label("E-MAIL:");
        emailLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12;");

        emailField = new TextField();
        emailField.setPrefSize(200, 35);
        emailField.setStyle("-fx-font-size: 14;");

        // Contraseña
        Label passLabel = new Label("CONTRASEÑA:");
        passLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12;");

        passwordField = new PasswordField();
        passwordField.setPrefSize(200, 35);

        // Botones
        HBox buttonPanel = new HBox();
        buttonPanel.setAlignment(Pos.CENTER);

        loginButton = new Button("INGRESAR");
        loginButton.setStyle("-fx-background-color: #0096ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; -fx-padding: 10 20 10 20;");
        loginButton.setPrefSize(120, 40);

        buttonPanel.getChildren().add(loginButton);

        // Enlaces
        HBox linkPanel = new HBox(10);
        linkPanel.setAlignment(Pos.CENTER);

        recoverButton = createLinkButton("¿Olvidaste tu contraseña?");
        registerButton = createLinkButton("Registrarme");

        Label separator = new Label("|");
        separator.setStyle("-fx-text-fill: #c8c8c8;");

        linkPanel.getChildren().addAll(recoverButton, separator, registerButton);

        // Agregar al grid
        formPanel.add(emailLabel, 0, 0, 2, 1);
        formPanel.add(emailField, 0, 1, 2, 1);
        formPanel.add(passLabel, 0, 2, 2, 1);
        formPanel.add(passwordField, 0, 3, 2, 1);
        formPanel.add(buttonPanel, 0, 4, 2, 1);
        formPanel.add(linkPanel, 0, 5, 2, 1);

        GridPane.setMargin(buttonPanel, new Insets(20, 0, 0, 0));
        GridPane.setMargin(linkPanel, new Insets(10, 0, 0, 0));

        return formPanel;
    }

    private Button createLinkButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-text-fill: #64b4ff; -fx-font-size: 12; -fx-background-color: transparent; -fx-underline: true;");
        return button;
    }

    private HBox createFooterPanel() {
        HBox footerPanel = new HBox();
        footerPanel.setAlignment(Pos.CENTER);
        footerPanel.setPadding(new Insets(10, 0, 10, 0));

        Label copyrightLabel = new Label("© 2025 Cine Marcenter - Todos los derechos reservados");
        copyrightLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #969696;");

        footerPanel.getChildren().add(copyrightLabel);
        return footerPanel;
    }

    private void realizarLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Error", "Por favor, complete todos los campos", Alert.AlertType.ERROR);
            return;
        }

        // Autenticación simulada
        if (autenticarUsuario(email, password)) {
            String tipoUsuario = determinarTipoUsuario(email);
            mostrarAlerta("Login Exitoso", "¡Bienvenido a CINE MARCENTER!", Alert.AlertType.INFORMATION);

            // Abrir sistema principal
            abrirSistemaPrincipal(email, tipoUsuario);
        } else {
            mostrarAlerta("Error de Login", "Credenciales incorrectas", Alert.AlertType.ERROR);
        }
    }

    private boolean autenticarUsuario(String email, String password) {
        return (email.equals("admin@cine.com") && password.equals("admin123")) ||
                (email.equals("cliente@cine.com") && password.equals("cliente123")) ||
                (email.equals("empleado@cine.com") && password.equals("empleado123"));
    }

    private String determinarTipoUsuario(String email) {
        if (email.equals("admin@cine.com")) return "Administrador";
        if (email.equals("empleado@cine.com")) return "Empleado";
        return "Cliente";
    }

    private void abrirSistemaPrincipal(String usuario, String tipoUsuario) {
        stage.close();
        Platform.runLater(() -> {
            SistemaPrincipal sistema = new SistemaPrincipal(usuario, tipoUsuario);
            sistema.start(new Stage());
        });
    }

    private void recuperarContrasena() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Recuperar Contraseña");
        dialog.setHeaderText(null);
        dialog.setContentText("Ingrese su email para recuperar contraseña:");

        dialog.showAndWait().ifPresent(email -> {
            if (!email.trim().isEmpty()) {
                mostrarAlerta("Recuperación Enviada",
                        "Se ha enviado un enlace de recuperación a: " + email,
                        Alert.AlertType.INFORMATION);
            }
        });
    }

private void registrarUsuario() {
    System.out.println("🎯 MÉTODO registrarUsuario EJECUTADO EN CineLogin");

    Platform.runLater(() -> {
        System.out.println("🎯 Platform.runLater EJECUTADO");

        try {
            System.out.println("🎯 LLAMANDO RegistroUsuario.abrirRegistroCliente()");
            RegistroUsuario.abrirRegistroCliente();
            System.out.println("🎯 LLAMADA EXITOSA");
        } catch (Exception e) {
            System.out.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    });
}

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }


}