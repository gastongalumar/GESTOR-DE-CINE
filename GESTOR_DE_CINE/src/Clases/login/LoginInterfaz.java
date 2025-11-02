package Clases.login;

import Excepciones.AutenticacionException;
import Excepciones.UsuarioException;
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

public class LoginInterfaz extends Application {
    private Stage stage;
    private TextField emailField;
    private PasswordField passwordField;
    private Button loginButton, registerButton;
    private int intentosLogin = 0;
    private GestorUsuarios gestorUsuarios;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        crearInterfaz();
        configurarEventos();
        stage.show();
    }

    public LoginInterfaz() {
        this.gestorUsuarios = new GestorUsuarios();
        this.gestorUsuarios.cargarUsuariosPrueba(); // Solo carga admin/empleado si no existen
    }


    private void crearInterfaz() {
        stage.setTitle("CINE LOS CULIA - Inicio de Sesión");
        stage.setOnCloseRequest(e -> stage.close());
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
        mainPanel.setPadding(new Insets(20));
        mainPanel.setSpacing(20);

        // Header
        VBox headerPanel = crearHeaderPanel();
        mainPanel.getChildren().add(headerPanel);

        // Formulario
        GridPane formPanel = crearFormPanel();
        mainPanel.getChildren().add(formPanel);

        // Footer
        HBox footerPanel = crearFooterPanel();
        mainPanel.getChildren().add(footerPanel);

        VBox.setVgrow(formPanel, Priority.ALWAYS);

        Scene scene = new Scene(mainPanel);
        stage.setScene(scene);
        stage.centerOnScreen();
        configurarEventos();
    }

    private VBox crearHeaderPanel() {
        VBox headerPanel = new VBox();
        headerPanel.setAlignment(Pos.CENTER);
        headerPanel.setSpacing(5);
        headerPanel.setPadding(new Insets(0, 0, 20, 0));

        Label titleLabel = new Label("INICIAR SESIÓN");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: white;");

        Label subtitleLabel = new Label("Ingresa tus credenciales");
        subtitleLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #c8c8c8;");

        headerPanel.getChildren().addAll(titleLabel, subtitleLabel);
        return headerPanel;
    }

    private GridPane crearFormPanel() {
        GridPane formPanel = new GridPane();
        formPanel.setAlignment(Pos.CENTER);
        formPanel.setHgap(10);
        formPanel.setVgap(10);
        formPanel.setPadding(new Insets(20));

        // Email
        Label emailLabel = new Label("E-MAIL:");
        emailLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12;");

        emailField = new TextField();
        emailField.setPrefSize(300, 40);
        emailField.setStyle("-fx-font-size: 14; -fx-border-color: #646496; -fx-border-width: 1; -fx-padding: 10;");

        // Contraseña
        Label passLabel = new Label("CONTRASEÑA:");
        passLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12;");

        passwordField = new PasswordField();
        passwordField.setPrefSize(300, 40);
        passwordField.setStyle("-fx-font-size: 14; -fx-border-color: #646496; -fx-border-width: 1; -fx-padding: 10;");

        // Botones
        HBox buttonPanel = new HBox(20);
        buttonPanel.setAlignment(Pos.CENTER);

        loginButton = new Button("INGRESAR");
        loginButton.setStyle("-fx-background-color: #0096ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; -fx-padding: 10 20 10 20;");
        loginButton.setPrefSize(120, 45);

        registerButton = new Button("REGISTRARSE");
        registerButton.setStyle("-fx-background-color: #009664; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; -fx-padding: 10 20 10 20;");
        registerButton.setPrefSize(120, 45);

        buttonPanel.getChildren().addAll(loginButton, registerButton);

        // Agregar al grid
        formPanel.add(emailLabel, 0, 0, 2, 1);
        formPanel.add(emailField, 0, 1, 2, 1);
        formPanel.add(passLabel, 0, 2, 2, 1);
        formPanel.add(passwordField, 0, 3, 2, 1);
        formPanel.add(buttonPanel, 0, 4, 2, 1);

        return formPanel;
    }

    private HBox crearFooterPanel() {
        HBox footerPanel = new HBox();
        footerPanel.setAlignment(Pos.CENTER);
        footerPanel.setPadding(new Insets(10, 0, 0, 0));

        Label infoLabel = new Label("© 2025 Cine Los Culia - Sistema de Gestión");
        infoLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #969696;");

        footerPanel.getChildren().add(infoLabel);
        return footerPanel;
    }

    private void configurarEventos() {
        loginButton.setOnAction(e -> realizarLogin());
        registerButton.setOnAction(e -> registrarUsuario());

        // Enter para login
        emailField.setOnAction(e -> realizarLogin());
        passwordField.setOnAction(e -> realizarLogin());
    }

    private void realizarLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Error", "Por favor, complete todos los campos", Alert.AlertType.ERROR);
            return;
        }

        if (autenticarUsuario(email, password)) {
            String tipoUsuario = determinarTipoUsuario(email);

            // Registrar el login en estadísticas
            GestorEstadisticasLogin.getInstance().registrarLogin(email, tipoUsuario);

            mostrarAlerta("Login Exitoso",
                    "¡Bienvenido a CINE LOS CULIA!\nUsuario: " + email + "\nTipo: " + tipoUsuario,
                    Alert.AlertType.INFORMATION);

            intentosLogin = 0;
            stage.close();

            abrirSistemaPrincipal(email, tipoUsuario);

        } else {
            intentosLogin++;
            mostrarAlerta("Error de Login",
                    "Credenciales incorrectas\nIntentos fallidos: " + intentosLogin,
                    Alert.AlertType.ERROR);

            if (intentosLogin >= 3) {
                mostrarAlerta("Seguridad",
                        "Demasiados intentos fallidos. Cerrando aplicación.",
                        Alert.AlertType.WARNING);
                Platform.exit();
            }
        }
    }

    private boolean autenticarUsuario(String email, String password) {
        try {
            Usuario usuario = gestorUsuarios.autenticarUsuario(email, password);

            // Registrar el login en estadísticas
            GestorEstadisticasLogin.getInstance().registrarLogin(
                    usuario.getEmail(),
                    usuario.getTipoUsuario().getDescripcion()
            );

            return true;

        } catch (AutenticacionException | UsuarioException e) {
            System.out.println("❌ Error de autenticación: " + e.getMessage());
            return false;
        }
    }

    private String determinarTipoUsuario(String email) {
        if (email.equals("admin@cine.com")) return "Administrador";
        if (email.equals("empleado@cine.com")) return "Empleado";
        return "Cliente";
    }

    private void abrirSistemaPrincipal(String usuario, String tipoUsuario) {
        mostrarAlerta("Redirigiendo",
                "Abriendo sistema para: " + tipoUsuario + "\nUsuario: " + usuario,
                Alert.AlertType.INFORMATION);

        if (tipoUsuario.equals("Administrador")) {
            GestorEstadisticasLogin.getInstance().mostrarGraficaLogins();
        }
    }

    private void registrarUsuario() {
        // ✅ Ahora abre el formulario REAL de registro
        Platform.runLater(() -> {
            RegistroUsuario registro = new RegistroUsuario(false); // false = registro normal de cliente
            registro.start(new Stage());
        });
    }


    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }


    public static void abrirLogin() {
        Platform.runLater(() -> {
            LoginInterfaz login = new LoginInterfaz();
            login.start(new Stage());
        });
    }


}