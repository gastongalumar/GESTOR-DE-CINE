package Clases.login;

import Clases.login.usuario.Usuario;
import Enumeradores.login.TipoUsuario;
import Excepciones.UsuarioException;
import ManejoJSON.GestorJsonLogin;
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

public class RegistroUsuario extends Application {
    private TextField nombreField, apellidoField, emailField, telefonoField;
    private PasswordField passwordField, confirmPasswordField;
    private ComboBox<TipoUsuario> tipoUsuarioCombo;
    private Button registrarButton, cancelarButton;
    private boolean esAdministrador;
    private GestorJsonLogin gestorJson;
    private Stage stage;
    private GestorUsuarios gestorUsuarios;

    public RegistroUsuario() {
        this(false);
    }

    public RegistroUsuario(boolean esAdministrador) {
        this.esAdministrador = esAdministrador;
        this.gestorUsuarios = new GestorUsuarios();
        this.gestorUsuarios.cargarUsuariosPrueba();
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        inicializarInterfaz();
    }


    private void inicializarInterfaz() {
        stage.setTitle("CINE LOS CULIA - " + (esAdministrador ? "Registro Administrativo" : "Registro de Usuario"));
        stage.setOnCloseRequest(e -> stage.close());
        stage.setWidth(500);
        stage.setHeight(esAdministrador ? 650 : 600);
        stage.setResizable(false);

        // Panel principal con gradiente
        VBox mainPanel = new VBox();
        mainPanel.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.rgb(25, 25, 35)),
                        new Stop(1, Color.rgb(75, 0, 130))),
                CornerRadii.EMPTY, Insets.EMPTY)));
        mainPanel.setPadding(new Insets(20));
        mainPanel.setSpacing(15);

        // Header
        VBox headerPanel = crearHeaderPanel();
        mainPanel.getChildren().add(headerPanel);

        // Formulario
        VBox formPanel = crearFormPanel();
        mainPanel.getChildren().add(formPanel);

        // Footer
        HBox footerPanel = crearFooterPanel();
        mainPanel.getChildren().add(footerPanel);

        VBox.setVgrow(formPanel, Priority.ALWAYS);

        Scene scene = new Scene(mainPanel);
        stage.setScene(scene);
        stage.centerOnScreen();
        configurarComponentes();
    }

    private VBox crearHeaderPanel() {
        VBox headerPanel = new VBox();
        headerPanel.setAlignment(Pos.CENTER);
        headerPanel.setSpacing(5);
        headerPanel.setPadding(new Insets(15, 0, 10, 0));

        Label titleLabel = new Label(esAdministrador ? "REGISTRO ADMINISTRATIVO" : "CREAR CUENTA");
        titleLabel.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: white;");

        Label subtitleLabel = new Label(esAdministrador ?
                "Registro de nuevos usuarios del sistema" :
                "Complete sus datos para registrarse");
        subtitleLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #b4b4b4;");

        headerPanel.getChildren().addAll(titleLabel, subtitleLabel);
        return headerPanel;
    }

    private VBox crearFormPanel() {
        VBox formPanel = new VBox();
        formPanel.setPadding(new Insets(10, 30, 10, 30));
        formPanel.setSpacing(15);

        // Campos de Nombre y Apellido
        HBox nombreApellidoPanel = new HBox(20);
        nombreApellidoPanel.setAlignment(Pos.CENTER_LEFT);

        VBox nombreBox = crearCampo("Nombre", nombreField = crearTextField(), 200);
        VBox apellidoBox = crearCampo("Apellido", apellidoField = crearTextField(), 200);
        nombreApellidoPanel.getChildren().addAll(nombreBox, apellidoBox);

        // Email
        VBox emailBox = crearCampo("E-mail", emailField = crearTextField(), 420);

        // Teléfono
        VBox telefonoBox = crearCampo("Teléfono", telefonoField = crearTextField(), 420);

        formPanel.getChildren().addAll(nombreApellidoPanel, emailBox, telefonoBox);

        // Tipo de Usuario (solo visible para administradores)
        if (esAdministrador) {
            VBox tipoBox = new VBox(5);
            tipoBox.setAlignment(Pos.CENTER_LEFT);

            Label tipoLabel = new Label("Tipo de Usuario");
            tipoLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12;");

            tipoUsuarioCombo = new ComboBox<>();
            tipoUsuarioCombo.getItems().addAll(TipoUsuario.values());
            tipoUsuarioCombo.setPrefSize(420, 35);
            tipoUsuarioCombo.setStyle("-fx-background-color: #f0f0f5; -fx-font-size: 14;");

            tipoBox.getChildren().addAll(tipoLabel, tipoUsuarioCombo);
            formPanel.getChildren().add(tipoBox);
        }

        // Contraseña
        VBox passwordBox = crearCampo("Contraseña", passwordField = crearPasswordField(), 420);

        // Confirmar Contraseña
        VBox confirmPasswordBox = crearCampo("Confirmar Contraseña", confirmPasswordField = crearPasswordField(), 420);

        formPanel.getChildren().addAll(passwordBox, confirmPasswordBox);

        // Botones
        HBox buttonPanel = new HBox(20);
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.setPadding(new Insets(25, 0, 0, 0));

        registrarButton = new Button("REGISTRARSE");
        registrarButton.setStyle("-fx-background-color: #009664; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; -fx-padding: 10 25 10 25;");
        registrarButton.setPrefSize(140, 45);

        cancelarButton = new Button("CANCELAR");
        cancelarButton.setStyle("-fx-background-color: #963232; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; -fx-padding: 10 25 10 25;");
        cancelarButton.setPrefSize(140, 45);

        buttonPanel.getChildren().addAll(registrarButton, cancelarButton);
        formPanel.getChildren().add(buttonPanel);

        return formPanel;
    }

    private VBox crearCampo(String label, Control field, double ancho) {
        VBox panel = new VBox(5);
        panel.setAlignment(Pos.CENTER_LEFT);

        Label jLabel = new Label(label);
        jLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12;");

        field.setPrefSize(ancho, 35);
        field.setMaxWidth(ancho);

        panel.getChildren().addAll(jLabel, field);
        return panel;
    }

    private TextField crearTextField() {
        TextField field = new TextField();
        field.setStyle("-fx-font-size: 14; -fx-border-color: #646496; -fx-border-width: 1; -fx-padding: 8 10 8 10; -fx-background-color: #f0f0f5;");
        return field;
    }

    private PasswordField crearPasswordField() {
        PasswordField field = new PasswordField();
        field.setStyle("-fx-font-size: 14; -fx-border-color: #646496; -fx-border-width: 1; -fx-padding: 8 10 8 10; -fx-background-color: #f0f0f5;");
        return field;
    }

    private HBox crearFooterPanel() {
        HBox footerPanel = new HBox();
        footerPanel.setAlignment(Pos.CENTER);
        footerPanel.setPadding(new Insets(5, 0, 5, 0));

        String mensaje = esAdministrador ?
                "Registro administrativo - Todos los campos son obligatorios" :
                "Todos los campos son obligatorios";

        Label infoLabel = new Label(mensaje);
        infoLabel.setStyle("-fx-font-size: 11; -fx-font-style: italic; -fx-text-fill: #b4b4b4;");

        footerPanel.getChildren().add(infoLabel);
        return footerPanel;
    }

    private void configurarComponentes() {
        registrarButton.setOnAction(e -> registrarUsuario());
        cancelarButton.setOnAction(e -> cancelarRegistro());

        // Enter key listener
        emailField.setOnAction(e -> registrarUsuario());
        passwordField.setOnAction(e -> registrarUsuario());
    }

    private void registrarUsuario() {
        try {
            if (!validarCampos()) {
                return;
            }

            // Crear nuevo usuario
            Usuario nuevoUsuario = new Usuario(
                    nombreField.getText().trim(),
                    apellidoField.getText().trim(),
                    emailField.getText().trim().toLowerCase(), // Normalizar email
                    new String(passwordField.getText()),
                    telefonoField.getText().trim(),
                    esAdministrador ? tipoUsuarioCombo.getValue() : TipoUsuario.CLIENTE
            ) {
                @Override
                public boolean puedeRealizarAccion(String accion) {
                    return false;
                }
            };

            // Registrar el usuario
            gestorUsuarios.registrarUsuario(nuevoUsuario);

            // Mostrar mensaje de éxito
            mostrarExito("¡Registro exitoso!\n\n" +
                    "Nombre: " + nuevoUsuario.getNombre() + " " + nuevoUsuario.getApellido() + "\n" +
                    "Email: " + nuevoUsuario.getEmail() + "\n" +
                    "Tipo: " + nuevoUsuario.getTipoUsuario().getDescripcion() + "\n\n" +
                    "Ya puedes iniciar sesión con tus credenciales.");

            // Limpiar formulario y cerrar
            limpiarFormulario();
            stage.close();

        } catch (UsuarioException e) {
            mostrarError("Error en el registro:\n" + e.getMessage());
        } catch (Exception e) {
            mostrarError("Error inesperado:\n" + e.getMessage());
            e.printStackTrace();
        }
    }


    private boolean validarCampos() {
        // Validar que todos los campos estén completos
        if (nombreField.getText().trim().isEmpty() ||
                apellidoField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty() ||
                telefonoField.getText().trim().isEmpty() ||
                passwordField.getText().isEmpty() ||
                confirmPasswordField.getText().isEmpty()) {

            mostrarError("Todos los campos son obligatorios");
            return false;
        }

        // Validar formato de email
        if (!Usuario.validarEmail(emailField.getText().trim())) {
            mostrarError("Por favor ingrese un email válido\nEjemplo: usuario@correo.com");
            emailField.requestFocus();
            return false;
        }

        // Validar teléfono
        if (!Usuario.validarTelefono(telefonoField.getText().trim())) {
            mostrarError("El teléfono debe contener entre 7 y 15 dígitos");
            telefonoField.requestFocus();
            return false;
        }

        // Validar que las contraseñas coincidan
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!password.equals(confirmPassword)) {
            mostrarError("Las contraseñas no coinciden");
            passwordField.clear();
            confirmPasswordField.clear();
            passwordField.requestFocus();
            return false;
        }

        // Validar fortaleza de contraseña
        if (!Usuario.validarPassword(password)) {
            mostrarError("La contraseña debe tener al menos 6 caracteres");
            return false;
        }

        return true;
    }

    private void limpiarFormulario() {
        nombreField.clear();
        apellidoField.clear();
        emailField.clear();
        telefonoField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        if (tipoUsuarioCombo != null) {
            tipoUsuarioCombo.setValue(TipoUsuario.CLIENTE);
        }
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registro Exitoso");
        alert.setHeaderText("¡Bienvenido a CINE LOS CULIA!");
        alert.setContentText(mensaje);

        // Agregar ícono personalizado (opcional)
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
//        stage.getIcons().add(new Image("/iconos/exito.png")); // Si tienes íconos

        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de Registro");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }



    private void cancelarRegistro() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Cancelación");
        alert.setHeaderText(null);
        alert.setContentText("¿Está seguro que desea cancelar el registro?\nSe perderán los datos ingresados.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            stage.close();
        }
    }

    // Métodos estáticos para abrir ventanas
    public static void abrirRegistroCliente() {
        Platform.runLater(() -> {
            RegistroUsuario registro = new RegistroUsuario(false);
            registro.start(new Stage());
        });
    }

    public static void abrirRegistroAdministrativo() {
        Platform.runLater(() -> {
            RegistroUsuario registro = new RegistroUsuario(true);
            registro.start(new Stage());
        });
    }

}

