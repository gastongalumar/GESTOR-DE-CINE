package Clases.login;

import Clases.GestorFunciones;
import Clases.HashUtil;
import Clases.ManejoVentanas;
import Clases.login.usuario.Cliente;
import Clases.login.usuario.Usuario;
import Excepciones.UsuarioException;
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
import Clases.login.usuario.Administrador;

public class RegistroUsuario {
    private TextField nombreField, apellidoField, emailField, telefonoField;
    private PasswordField passwordField, confirmPasswordField;
    private Button registrarButton, cancelarButton;
    private Stage stage;
    private GestorUsuarios gestorUsuarios;
    private boolean esAdministrador;
    private GestorFunciones gestorFunciones;

    // ✅ CONSTRUCTOR PARA CLIENTES
    public RegistroUsuario() {
        this(false, null);
    }

    // ✅ CONSTRUCTOR PARA ADMINS (con GestorFunciones)
    public RegistroUsuario(boolean esAdministrador) {
        this(esAdministrador, null);
    }

    // ✅ CONSTRUCTOR CON PARÁMETRO (para modo admin)
    public RegistroUsuario(boolean esAdministrador, GestorFunciones gestorFunciones) {
        this.esAdministrador = esAdministrador;
        this.gestorUsuarios = new GestorUsuarios();
        this.gestorFunciones = new GestorFunciones();

    }

    public static void abrirRegistroAdministrativo() {
        System.out.println("🎯 abrirRegistroAdministrativo() EJECUTADO");

        Platform.runLater(() -> {
            System.out.println("🎯 Platform.runLater EN RegistroUsuario (ADMIN) EJECUTADO");

            try {
                System.out.println("🎯 CREANDO NUEVA INSTANCIA RegistroUsuario PARA ADMIN");
                RegistroUsuario registro = new RegistroUsuario(true); // ✅ true para modo admin
                System.out.println("🎯 INSTANCIA CREADA: " + registro);

                System.out.println("🎯 LLAMANDO mostrarVentana()");
                registro.mostrarVentana();
                System.out.println("🎯 mostrarVentana() COMPLETADO");

            } catch (Exception e) {
                System.out.println("❌ ERROR EN RegistroUsuario ADMIN: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void inicializarInterfaz() {
        // ✅ TÍTULO DINÁMICO SEGÚN MODO
        stage.setTitle("CINE LOS CULIA - " +
                (esAdministrador ? "Registro de Administrador" : "Registro de Cliente"));
        stage.setOnCloseRequest(e -> stage.close());
        stage.setWidth(500);
        stage.setHeight(800);
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
        stage.show();

        System.out.println("🎯 VENTANA REGISTRO " + (esAdministrador ? "ADMIN" : "CLIENTE") + " MOSTRADA");
    }

    private VBox crearHeaderPanel() {
        VBox headerPanel = new VBox();
        headerPanel.setAlignment(Pos.CENTER);
        headerPanel.setSpacing(5);
        headerPanel.setPadding(new Insets(15, 0, 10, 0));

        // ✅ TEXTO DINÁMICO SEGÚN MODO
        Label titleLabel = new Label(esAdministrador ?
                "REGISTRO DE ADMINISTRADOR" : "CREAR CUENTA CLIENTE");
        titleLabel.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: white;");

        Label subtitleLabel = new Label(esAdministrador ?
                "Registro de nuevo administrador del sistema" :
                "Complete sus datos para registrarse como cliente");
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

        // ✅ AGREGAR INDICADOR DE TIPO EN MODO ADMIN
        if (esAdministrador) {
            VBox tipoBox = new VBox(5);
            tipoBox.setAlignment(Pos.CENTER_LEFT);

            Label tipoLabel = new Label("Tipo de Usuario");
            tipoLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12;");

            TextField tipoField = new TextField("ADMINISTRADOR");
            tipoField.setPrefSize(420, 35);
            tipoField.setStyle("-fx-font-size: 14; -fx-border-color: #646496; -fx-border-width: 1; -fx-padding: 8 10 8 10; -fx-background-color: #e0e0e0;");
            tipoField.setDisable(true); // Solo lectura
            tipoField.setOpacity(0.8);

            tipoBox.getChildren().addAll(tipoLabel);
            formPanel.getChildren().add(tipoBox);
        }

        // Contraseña
        VBox passwordBox = crearCampo("Contraseña", passwordField = crearPasswordField(), 420);

        // Confirmar Contraseña
        VBox confirmPasswordBox = crearCampo("Confirmar Contraseña", confirmPasswordField = crearPasswordField(), 420);

        formPanel.getChildren().addAll(nombreApellidoPanel, emailBox, telefonoBox, passwordBox, confirmPasswordBox);

        // Botones
        HBox buttonPanel = new HBox(20);
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.setPadding(new Insets(25, 0, 0, 0));

        // ✅ TEXTO DEL BOTÓN SEGÚN MODO
        String textoBoton = esAdministrador ? "REGISTRAR ADMIN" : "REGISTRARSE";
        registrarButton = new Button(textoBoton);
        registrarButton.setStyle("-fx-background-color: #009664; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; -fx-padding: 10 25 10 25;");
        registrarButton.setPrefSize(esAdministrador ? 160 : 140, 45);

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

        // ✅ MENSAJE DINÁMICO SEGÚN MODO
        String mensaje = esAdministrador ?
                "Registro administrativo - Todos los campos son obligatorios" :
                "Registro de cliente - Todos los campos son obligatorios";

        Label infoLabel = new Label(mensaje);
        infoLabel.setStyle("-fx-font-size: 11; -fx-font-style: italic; -fx-text-fill: #b4b4b4;");

        footerPanel.getChildren().add(infoLabel);
        return footerPanel;
    }

    private void configurarComponentes() {
        System.out.println("🎯 CONFIGURANDO BOTONES REGISTRO " + (esAdministrador ? "ADMIN" : "CLIENTE") + "...");

        registrarButton.setOnAction(e -> {
            System.out.println("🎯 ¡BOTÓN REGISTRAR " + (esAdministrador ? "ADMIN" : "CLIENTE") + " PRESIONADO!");
            registrarUsuario();
        });

        cancelarButton.setOnAction(e -> {
            System.out.println("🎯 BOTÓN CANCELAR PRESIONADO");
            cancelarRegistro();
        });
    }

    private void registrarUsuario() {
        try {
            if (!validarCampos()) {
                return;
            }

            // ✅ CREAR USUARIO SEGÚN MODO
            Usuario nuevoUsuario;
            String passwordHasheada = HashUtil.hashSHA256(passwordField.getText());

            if (esAdministrador) {
                // En modo administrativo, siempre crea ADMINISTRADOR
                nuevoUsuario = new Administrador(
                        nombreField.getText().trim(),
                        apellidoField.getText().trim(),
                        emailField.getText().trim().toLowerCase(),
                        passwordHasheada,
                        telefonoField.getText().trim()
                );
            } else {
                // Modo normal: siempre CLIENTE
                nuevoUsuario = new Cliente(
                        nombreField.getText().trim(),
                        apellidoField.getText().trim(),
                        emailField.getText().trim().toLowerCase(),
                        passwordHasheada,
                        telefonoField.getText().trim()
                );
            }

            gestorUsuarios.registrarUsuario(nuevoUsuario);

            // ✅ MENSAJE DE ÉXITO SEGÚN TIPO
            String mensajeExito;
            if (esAdministrador) {
                mensajeExito = "¡Administrador registrado exitosamente!\n\n" +
                        "Nombre: " + nuevoUsuario.getNombre() + " " + nuevoUsuario.getApellido() + "\n" +
                        "Email: " + nuevoUsuario.getEmail() + "\n" +
                        "Tipo: Administrador\n\n" +
                        "El nuevo administrador ya puede iniciar sesión.";
            } else {
                mensajeExito = "¡Registro exitoso!\n\n" +
                        "Nombre: " + nuevoUsuario.getNombre() + " " + nuevoUsuario.getApellido() + "\n" +
                        "Email: " + nuevoUsuario.getEmail() + "\n" +
                        "Tipo: Cliente\n\n" +
                        "Ya puedes iniciar sesión con tus credenciales.";
            }

            mostrarExito(mensajeExito);
            limpiarFormulario();
            //reiniciarAplicacion();
            stage.close();

        } catch (UsuarioException e) {
            mostrarError("Error en el registro:\n" + e.getMessage());
        } catch (Exception e) {
            mostrarError("Error inesperado:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validarCampos() {
        if (nombreField.getText().trim().isEmpty() ||
                apellidoField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty() ||
                telefonoField.getText().trim().isEmpty() ||
                passwordField.getText().isEmpty() ||
                confirmPasswordField.getText().isEmpty()) {

            mostrarError("Todos los campos son obligatorios");
            return false;
        }

        if (!Usuario.validarEmail(emailField.getText().trim())) {
            mostrarError("Por favor ingrese un email válido\nEjemplo: usuario@correo.com");
            emailField.requestFocus();
            return false;
        }

        if (!Usuario.validarTelefono(telefonoField.getText().trim())) {
            mostrarError("El teléfono debe contener entre 7 y 15 dígitos");
            telefonoField.requestFocus();
            return false;
        }

        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!password.equals(confirmPassword)) {
            mostrarError("Las contraseñas no coinciden");
            passwordField.clear();
            confirmPasswordField.clear();
            passwordField.requestFocus();
            return false;
        }

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
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registro Exitoso");
        alert.setHeaderText(esAdministrador ?
                "¡Nuevo Administrador Registrado!" : "¡Bienvenido a CINE LOS CULIA!");
        alert.setContentText(mensaje);
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

    public static void abrirRegistroCliente() {
        System.out.println("🎯 abrirRegistroCliente() EJECUTADO");

        Platform.runLater(() -> {
            System.out.println("🎯 Platform.runLater EN RegistroUsuario EJECUTADO");

            try {
                System.out.println("🎯 CREANDO NUEVA INSTANCIA RegistroUsuario");
                RegistroUsuario registro = new RegistroUsuario(); // ✅ Usa constructor sin parámetro
                System.out.println("🎯 INSTANCIA CREADA: " + registro);

                System.out.println("🎯 LLAMANDO mostrarVentana()");
                registro.mostrarVentana();
                System.out.println("🎯 mostrarVentana() COMPLETADO");

            } catch (Exception e) {
                System.out.println("❌ ERROR EN RegistroUsuario: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public void mostrarVentana() {
        System.out.println("🎯 mostrarVentana() EJECUTADO");
        this.stage = new Stage();
        System.out.println("🎯 STAGE CREADO: " + this.stage);
        inicializarInterfaz();
    }
    private void reiniciarAplicacion(Cliente cliente) {
        System.out.println("🔄 REINICIANDO APLICACIÓN...");

        Platform.runLater(() -> {
            try {
                // Cerrar esta ventana de registro
                if (stage != null && stage.isShowing()) {
                    stage.close();
                }

                if (esAdministrador) {
                    // ✅ CASO: ADMIN REGISTRANDO OTRO ADMIN
                    System.out.println("🎯 ADMIN REGISTRÓ NUEVO ADMIN - REINICIANDO DASHBOARD");

                    // Cerrar solo el dashboard actual del admin
                    for (Stage ventana : Stage.getWindows().toArray(new Stage[0])) {
                        if (ventana.getTitle() != null &&
                                ventana.getTitle().contains("Dashboard Administrador")) {
                            ventana.close();
                        }
                    }

                    // Pequeña pausa
                    Thread.sleep(300);

                    // Reabrir dashboard fresco (con los nuevos usuarios cargados)
                    // Necesitamos recrear el dashboard con el gestor actualizado
                    GestorUsuarios gestorActualizado = new GestorUsuarios();
                    DashboardAdmin nuevoDashboard = new DashboardAdmin(gestorActualizado, gestorFunciones, cliente);
                    nuevoDashboard.mostrarDashboard();

                } else {
                    // ✅ CASO: CLIENTE REGISTRÁNDOSE
                    System.out.println("🎯 CLIENTE REGISTRADO - REINICIANDO LOGIN");

                    // Cerrar ventanas de login existentes
                    for (Stage ventana : Stage.getWindows().toArray(new Stage[0])) {
                        if (ventana.getTitle() != null &&
                                ventana.getTitle().contains("CINE LOS CULIA - Inicio de Sesión")) {
                            ventana.close();
                        }
                    }

                    // Pequeña pausa
                    Thread.sleep(300);

                    // Reabrir login
                    Clases.login.LoginInterfaz.abrirLogin();
                }

            } catch (Exception e) {
                System.out.println("❌ Error al reiniciar: " + e.getMessage());
                // Fallback según el caso
                if (esAdministrador) {
                    Clases.login.LoginInterfaz.abrirLogin();
                } else {
                    Clases.login.LoginInterfaz.abrirLogin();
                }
            }
        });
    }
}