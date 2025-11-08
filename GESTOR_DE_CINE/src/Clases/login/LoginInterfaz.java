package Clases.login;

import Clases.*;
import Clases.login.usuario.Cliente;
import Clases.login.usuario.Usuario;
import Excepciones.AutenticacionException;
import Excepciones.UsuarioException;
import ManejoJSON.FuncionesJSON;
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

import java.util.List;

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
        GestorFunciones gestorFunciones = new GestorFunciones();
        cargarPeliculasActualizadas(gestorFunciones, new SalaCine("Sala 1", 200), new SalaCine("Sala 2", 200));
        crearInterfaz();
        configurarEventos(gestorFunciones);
        GestorAdministrador.iniciarAdministrador(gestorFunciones);   //BORRAR ANTES DE ENTREGAR
        stage.show();
    }

    public LoginInterfaz() {
        this.gestorUsuarios = new GestorUsuarios();
        //this.gestorUsuarios.cargarUsuariosPrueba(); // Solo carga admin/empleado si no existen
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
        configurarEventos(new GestorFunciones());
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
        emailField.setPrefSize(300, 40);  // ✅ Mismo tamaño
        emailField.setMaxWidth(280);      // ✅ Importante: forzar el ancho máximo
        emailField.setStyle("-fx-font-size: 14; -fx-border-color: #646496; -fx-border-width: 1; -fx-padding: 10;");

// Contraseña CON OJITO
        Label passLabel = new Label("CONTRASEÑA:");
        passLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12;");

// Crear contenedor para password field + ojito
        HBox passwordContainer = new HBox();
        passwordContainer.setSpacing(5);
        passwordContainer.setAlignment(Pos.CENTER_LEFT);
        passwordContainer.setPrefWidth(300);  // ✅ Mismo ancho total
        passwordContainer.setMaxWidth(300);   // ✅ Forzar ancho máximo

        passwordField = new PasswordField();
        passwordField.setPrefSize(280, 40);   // ✅ Un poco menos para el ojito
        passwordField.setStyle("-fx-font-size: 14; -fx-border-color: #646496; -fx-border-width: 1; -fx-padding: 10;");

// Campo de texto visible (oculto inicialmente)
        TextField visiblePasswordField = new TextField();
        visiblePasswordField.setPrefSize(270, 40);
        visiblePasswordField.setStyle("-fx-font-size: 14; -fx-border-color: #646496; -fx-border-width: 1; -fx-padding: 10;");
        visiblePasswordField.setVisible(false);
        visiblePasswordField.setManaged(false);

// Botón ojito
        Button eyeButton = new Button("👁");
        eyeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #646496; -fx-font-size: 16; -fx-padding: 5;");
        eyeButton.setPrefSize(30, 40);

// ... resto del código del ojito igual
        // Tooltip para el ojito
        Tooltip eyeTooltip = new Tooltip("Mostrar/Ocultar contraseña");
        eyeButton.setTooltip(eyeTooltip);

        // Lógica del ojito
        eyeButton.setOnAction(e -> {
            if (passwordField.isVisible()) {
                // Mostrar contraseña
                visiblePasswordField.setText(passwordField.getText());
                visiblePasswordField.setVisible(true);
                visiblePasswordField.setManaged(true);
                passwordField.setVisible(false);
                passwordField.setManaged(false);
                eyeButton.setText("🔒");
                eyeTooltip.setText("Ocultar contraseña");
            } else {
                // Ocultar contraseña
                passwordField.setText(visiblePasswordField.getText());
                passwordField.setVisible(true);
                passwordField.setManaged(true);
                visiblePasswordField.setVisible(false);
                visiblePasswordField.setManaged(false);
                eyeButton.setText("👁");
                eyeTooltip.setText("Mostrar contraseña");
            }
        });

        // Sincronizar los dos campos
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            visiblePasswordField.setText(newVal);
        });

        visiblePasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
            passwordField.setText(newVal);
        });

        passwordContainer.getChildren().addAll(passwordField, visiblePasswordField, eyeButton);

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
        formPanel.add(passwordContainer, 0, 3, 2, 1);
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

    private void configurarEventos(GestorFunciones gestorFunciones) {
        loginButton.setOnAction(e -> realizarLogin(gestorFunciones));
        registerButton.setOnAction(e -> registrarUsuario());

        // Enter para login
        emailField.setOnAction(e -> realizarLogin(gestorFunciones));
        passwordField.setOnAction(e -> realizarLogin(gestorFunciones));
    }

    private void registrarUsuario() {
        System.out.println("🎯 BOTÓN REGISTRAR PRESIONADO EN LOGININTERFAZ");

        Platform.runLater(() -> {
            try {
                System.out.println("🎯 ABRIENDO FORMULARIO DE REGISTRO...");
                // ✅ Esto abre tu formulario de registro que YA funciona
                RegistroUsuario.abrirRegistroCliente();
                System.out.println("🎯 FORMULARIO ABIERTO EXITOSAMENTE");

            } catch (Exception e) {
                System.out.println("❌ ERROR AL ABRIR REGISTRO: " + e.getMessage());
                e.printStackTrace();

                // Mensaje de emergencia si falla
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("No se pudo abrir el formulario de registro");
                alert.setContentText("Error: " + e.getMessage());
                alert.showAndWait();
            }
        });
    }
    private void realizarLogin(GestorFunciones gestorFunciones) {
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


            abrirSistemaPrincipal(email, tipoUsuario, gestorFunciones);

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

    // En LoginInterfaz.java - Modificar el métoo autenticarUsuario
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
        try {
            Usuario usuario = gestorUsuarios.buscarPorEmail(email);
            return usuario.getTipoUsuario().getDescripcion();
        } catch (UsuarioException e) {
            return "Cliente"; // Por defecto si hay algún error
        }
    }


    private void abrirSistemaPrincipal(String usuario, String tipoUsuario, GestorFunciones gestorFunciones) {
        mostrarAlerta("Login Exitoso",
                "¡Bienvenido " + usuario + "!\nTipo: " + tipoUsuario,
                Alert.AlertType.INFORMATION);

        stage.close();

        // Redirigir según el tipo de usuario
        if (tipoUsuario.equals("Administrador")) {
            abrirPanelAdministrador(gestorFunciones);
        } else {
            abrirPanelCliente(usuario, gestorFunciones);
        }
    }

    private void abrirPanelAdministrador(GestorFunciones gestorFunciones) {
        System.out.println("🎯 INTENTANDO ABRIR PANEL ADMIN...");

        Platform.runLater(() -> {
            try {
                System.out.println("🎯 CREANDO DASHBOARD ADMIN...");


                DashboardAdmin dashboard = new DashboardAdmin(
                        gestorUsuarios,
                        gestorFunciones  // ✅ AGREGAR ESTO
                );
                dashboard.mostrarDashboard();

            } catch (Exception e) {
                System.out.println("❌ ERROR EN PANEL ADMIN: " + e.getMessage());
                e.printStackTrace();

                // Mensaje de error
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("No se pudo abrir el panel de administrador");
                alert.setContentText("Error: " + e.getMessage());
                alert.showAndWait();
            }
        });
    }

    private void abrirPanelCliente(String usuario, GestorFunciones gestorFunciones) {
        Platform.runLater(() -> {
            // Buscar el cliente por email (necesitas implementar esto)
            Cliente cliente = buscarClientePorEmail(usuario);

            if (cliente == null) {
                // Cliente temporal si no lo encuentras
                cliente = new Cliente();
            }

            // Crear UNA sola ventana que integre todo
            Stage clienteStage = new Stage();
            clienteStage.setTitle("CINE LOS CULIA - Cartelera");

            // Contenedor principal
            VBox panelPrincipal = new VBox();
            panelPrincipal.setStyle("-fx-background-color: #1a1a2a;");

            // === HEADER CON BIENVENIDA ===
            VBox header = new VBox(10);
            header.setPadding(new Insets(20));
            header.setAlignment(Pos.CENTER);
            header.setStyle("-fx-background-color: #2a2a3a;");

            Label titulo = new Label("BIENVENIDO CLIENTE: " + usuario);
            titulo.setStyle("-fx-text-fill: white; -fx-font-size: 24; -fx-font-weight: bold;");

            Label mensaje = new Label("Interfaz para seleccionar películas y asientos");
            mensaje.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 14;");

            Button btnCerrar = new Button("Cerrar Sesión");
            btnCerrar.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-weight: bold;");
            btnCerrar.setOnAction(e -> {
                clienteStage.close();
                abrirLogin();
            });

            header.getChildren().addAll(titulo, mensaje, btnCerrar);

            // === CONTENIDO PRINCIPAL (GESTOR CLIENTE) ===
            HBox contenidoGestor = crearContenidoGestorCliente(gestorFunciones, cliente, clienteStage);

            panelPrincipal.getChildren().addAll(header, contenidoGestor);

            Scene scene = new Scene(panelPrincipal, 1300, 600);
            clienteStage.setScene(scene);
            clienteStage.show();
        });
    }

    private HBox crearContenidoGestorCliente(GestorFunciones gestorFunciones, Cliente cliente, Stage ventana) {
        HBox contenedor = new HBox(20);
        contenedor.setPadding(new Insets(20));
        contenedor.setStyle("-fx-background-color: #6E0A17;");

        // Cargar películas
        List<Pelicula> listaPeliculas = FuncionesJSON.deserializarPeliculas();

        // Mostrar películas
        for(Pelicula p: listaPeliculas) {
            VBox vista = VistaCartelera.crearVista(p, gestorFunciones.getListaFunciones().getElementos());
            contenedor.getChildren().add(vista);
        }

        // === PANEL LATERAL CON BOTONES ===
        VBox panelLateral = crearPanelLateralCliente(cliente, gestorFunciones, ventana);
        contenedor.getChildren().add(panelLateral);

        return contenedor;
    }

    private VBox crearPanelLateralCliente(Cliente cliente, GestorFunciones gestorFunciones, Stage ventana) {
        // Títulos
        Label tituloFunciones = new Label("CARTELERA");
        tituloFunciones.setStyle("-fx-font-size: 20px; -fx-text-fill: #0A6E61; -fx-font-weight: bold; -fx-padding: 5 10 5 10;");

        Label tituloPeliculas = new Label("MI CUENTA");
        tituloPeliculas.setStyle("-fx-font-size: 20px; -fx-text-fill: #0A6E61; -fx-font-weight: bold; -fx-padding: 5 10 5 10;");

        Separator separacion = new Separator();
        separacion.setStyle("-fx-background-color: #800080;");
        separacion.setPrefWidth(150);

        // === BOTONES DEL CLIENTE ===

        // Historial de Compras
        Button botonHistorial = new Button("Mi Historial de Compras");
        botonHistorial.setStyle("-fx-background-color: #4169E1; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 10;");
        botonHistorial.setOnAction(e -> HistorialCompras.mostrarHistorial(cliente));

        // Puntos de Fidelidad
        Button botonPuntos = new Button("Mis Puntos: " + cliente.getPuntosFidelidad());
        botonPuntos.setStyle("-fx-background-color: #FFAA4A; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 10;");
        botonPuntos.setOnAction(e -> mostrarInfoPuntos(cliente));

        // Promociones
        Button botonPromociones = new Button("Promociones Activas");
        botonPromociones.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 10;");
        botonPromociones.setOnAction(e -> mostrarPromociones(cliente));

        // Cancelar Reserva
        Button botonCancelarReserva = new Button("Cancelar Reserva");
        botonCancelarReserva.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 10;");
        botonCancelarReserva.setOnAction(e -> mostrarCancelarReserva(cliente, gestorFunciones));

        // Mi Perfil
        Button botonPerfil = new Button("Mi Perfil");
        botonPerfil.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 10;");
        botonPerfil.setOnAction(e -> mostrarPerfil(cliente));

        VBox panelBotones = new VBox(10,
                tituloFunciones,
                botonHistorial,
                botonPuntos,
                botonPromociones,
                botonCancelarReserva,
                separacion,
                tituloPeliculas,
                botonPerfil
        );

        return panelBotones;
    }

    // Métodos auxiliares (agrégalos en la misma clase)
    private void mostrarInfoPuntos(Cliente cliente) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Mis Puntos de Fidelidad");
        alert.setHeaderText("Tus puntos: " + cliente.getPuntosFidelidad());
        alert.setContentText("💡 Canjea 100 puntos por un 10% de descuento en tu próxima compra!");
        alert.showAndWait();
    }

    private void mostrarPromociones(Cliente cliente) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Promociones Activas");
        alert.setHeaderText("🎁 PROMOCIONES ESPECIALES");
        alert.setContentText("• Miercoles de Descuento: 20% OFF\n• Combo Familiar: 25% OFF\n• Canje de Puntos: 100 pts = 10% OFF");
        alert.showAndWait();
    }

    private void mostrarPerfil(Cliente cliente) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Mi Perfil");
        alert.setHeaderText("👤 " + cliente.getNombre() + " " + cliente.getApellido());
        alert.setContentText("Email: " + cliente.getEmail() + "\nTeléfono: " + cliente.getTelefono() + "\nPuntos: " + cliente.getPuntosFidelidad());
        alert.showAndWait();
    }

    private void mostrarCancelarReserva(Cliente cliente, GestorFunciones gestorFunciones) {

        // o implementar la lógica directamente
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cancelar Reserva");
        alert.setHeaderText("Funcionalidad de Cancelación");
        alert.setContentText("Aquí se mostrarían tus reservas activas para cancelar.");
        alert.showAndWait();
    }

    private Cliente buscarClientePorEmail(String email) {
        try {
            System.out.println("🔍 BUSCANDO CLIENTE: " + email);

            // Buscar el usuario en el GestorUsuarios
            Usuario usuario = gestorUsuarios.buscarPorEmail(email);

            if (usuario instanceof Cliente) {
                Cliente cliente = (Cliente) usuario;
                System.out.println("✅ CLIENTE ENCONTRADO: " + cliente.getNombre() + " " + cliente.getApellido());
                return cliente;
            } else {
                System.out.println("❌ El usuario no es un Cliente: " + usuario.getTipoUsuario());
                // Si no es cliente, crear uno temporal con los datos básicos
                return crearClienteTemporal(usuario);
            }

        } catch (UsuarioException e) {
            System.out.println("❌ CLIENTE NO ENCONTRADO: " + e.getMessage());
            // Crear cliente temporal con el email
            return crearClienteTemporal(email);
        }
    }
    private Cliente crearClienteTemporal(Usuario usuario) {
        Cliente temp = new Cliente(
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                "", // password vacío
                usuario.getTelefono()
        );
        temp.setPuntosFidelidad(0);
        return temp;
    }

    private Cliente crearClienteTemporal(String email) {
        Cliente temp = new Cliente();
        temp.setEmail(email);
        temp.setNombre("Cliente");
        temp.setApellido("Temporal");
        temp.setPuntosFidelidad(0);
        return temp;
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

    public void cargarPeliculasActualizadas(GestorFunciones gestorFunciones, SalaCine s1, SalaCine s2) {
        FuncionesJSON.deserializarPeliculas();
        FuncionesJSON.deserializarFunciones(GestorPeliculas.getListaPeliculas(), List.of(s1, s2), gestorFunciones);
    }
}