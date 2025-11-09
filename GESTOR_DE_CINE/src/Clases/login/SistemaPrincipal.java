package Clases.login;

import Clases.*;
import Clases.GestionDePagos.HistorialCompras;
import Clases.login.usuario.Cliente;
import Clases.login.usuario.Usuario;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class SistemaPrincipal extends Application {
    private String usuarioActual;
    private String tipoUsuario;
    private Stage stage;
    private GestorFunciones gestorFunciones;
    private GestorUsuarios gestorUsuarios;
    private BorderPane mainPanel;
    private StackPane contentPanel;

    public SistemaPrincipal(String usuario, String tipoUsuario) {
        this.usuarioActual = usuario;
        this.tipoUsuario = tipoUsuario;
        this.gestorFunciones = new GestorFunciones();
        this.gestorUsuarios = new GestorUsuarios();
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;

        // Si es cliente, abrir directamente la vista de cliente
        if (tipoUsuario.equalsIgnoreCase("cliente")) {
            Usuario cliente = obtenerClienteActual();
            if (cliente != null) {
                GestorCliente.iniciarCliente(gestorFunciones,cliente);
                stage.close();
                return;
            }
        }

        inicializarInterfaz(obtenerClienteActual());
    }

    private void inicializarInterfaz(Usuario cliente) {
        stage.setTitle("CINEMAX - Sistema de Gestión");
        stage.setOnCloseRequest(e -> Platform.exit());
        stage.setWidth(1200);
        stage.setHeight(800);
        stage.setMaximized(true);

        // Panel principal
        mainPanel = new BorderPane();

        // Header
        HBox headerPanel = crearHeaderPanel(cliente);
        mainPanel.setTop(headerPanel);

        // Menu lateral
        VBox menuPanel = crearMenuPanel(cliente);
        mainPanel.setLeft(menuPanel);

        // Contenido principal
        contentPanel = crearContentPanel();
        mainPanel.setCenter(contentPanel);

        Scene scene = new Scene(mainPanel);
        stage.setScene(scene);
        stage.centerOnScreen();
    }

    private HBox crearHeaderPanel(Usuario cliente) {
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
        logoutButton.setOnAction(e -> cerrarSesion(cliente));

        userPanel.getChildren().addAll(userLabel, logoutButton);
        headerPanel.getChildren().addAll(titleLabel, userPanel);

        return headerPanel;
    }

    private VBox crearMenuPanel(Usuario cliente) {
        VBox menuPanel = new VBox();
        menuPanel.setStyle("-fx-background-color: #28283c;");
        menuPanel.setPrefWidth(250);
        menuPanel.setPadding(new Insets(20, 10, 20, 10));
        menuPanel.setSpacing(10);

        // Botones del menú según tipo de usuario
        String[] opcionesMenu = getOpcionesMenu();

        for (String opcion : opcionesMenu) {
            Button menuButton = crearBotonMenu(opcion,cliente);
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

    private Button crearBotonMenu(String texto,Usuario cliente) {
        Button button = new Button(texto);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(45);
        button.setStyle("-fx-background-color: #3c3c50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; -fx-padding: 10 15 10 15;");
        button.setOnAction(e -> manejarOpcionMenu(texto,cliente));
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

    private void manejarOpcionMenu(String opcion, Usuario cliente) {
        switch (opcion) {
            case "Dashboard":
                if (esAdministrador()) {
                    new DashboardAdmin(gestorUsuarios,gestorFunciones,cliente).mostrarDashboard();
                }
                break;

            case "Gestión de Películas":
            case "Gestión de Salas":
            case "Gestión de Usuarios":
            case "Reportes y Estadísticas":
            case "Configuración del Sistema":
                if (esAdministrador()) {
                    GestorAdministrador.iniciarAdministrador(gestorFunciones,cliente);
                }
                break;

            case "Venta de Entradas":
            case "Clientes":
            case "Reportes de Ventas":
            case "Configuración Horarios":
                if (esEmpleado()) {
                    mostrarFuncionalidadEmpleado(opcion);
                }
                break;

            case "Cartelera":
            case "Comprar Entradas":
                if (esCliente()) {
                   cliente = obtenerClienteActual();
                    if (cliente != null) {
                        GestorCliente.iniciarCliente(gestorFunciones,cliente);
                    }
                } else {
                    abrirCarteleraGeneral(cliente);
                }
                break;

            case "Mis Compras":
                if (esCliente()) {
                    cliente = obtenerClienteActual();
                    if (cliente != null) {
                        HistorialCompras.mostrarHistorial(cliente);
                    }
                }
                break;

            case "Promociones":
                if (esCliente()) {
                   cliente = obtenerClienteActual();
                    if (cliente != null) {
                        mostrarPromocionesCliente(cliente);
                    }
                }
                break;

            case "Perfil":
                if (esCliente()) {
                     cliente = obtenerClienteActual();
                    if (cliente != null) {
                        mostrarPerfilCliente(cliente);
                    }
                }
                break;

            case "Ventas y Facturación":
                if (esAdministrador()) {
                    mostrarVentasFacturacion();
                }
                break;

            default:
                mostrarMensajeDesarrollo(opcion);
                break;
        }
    }

    // ===== MÉTODOS AUXILIARES =====

    private boolean esAdministrador() {
        return tipoUsuario.equalsIgnoreCase("administrador");
    }

    private boolean esEmpleado() {
        return tipoUsuario.equalsIgnoreCase("empleado");
    }

    private boolean esCliente() {
        return tipoUsuario.equalsIgnoreCase("cliente");
    }

    private Cliente obtenerClienteActual() {
        try {
            Usuario usuario = gestorUsuarios.buscarPorEmail(usuarioActual);
            if (usuario instanceof Cliente) {
                return (Cliente) usuario;
            }
        } catch (Exception e) {
            System.out.println("❌ Error obteniendo cliente: " + e.getMessage());
            mostrarAlerta("Error", "No se pudo obtener la información del cliente");
        }
        return null;
    }

    private void abrirCarteleraGeneral(Usuario cliente) {
        Stage carteleraStage = new Stage();
        carteleraStage.setTitle("Cartelera - CINE LOS CULIA");

        HBox contenedor = new HBox(20);
        contenedor.setStyle("-fx-background-color: #6E0A17; -fx-padding: 20;");

        // Cargar películas
        GestorPeliculas.setListaPeliculas(ManejoJSON.FuncionesJSON.deserializarPeliculas());
        List<Pelicula> listaPeliculas = GestorPeliculas.getListaPeliculas();

        for(Pelicula p: listaPeliculas){
            VBox vista = VistaCartelera.crearVista(p, gestorFunciones.getListaFunciones().getElementos(),cliente);
            contenedor.getChildren().add(vista);
        }

        Scene escena = new Scene(contenedor, 1200, 500);
        carteleraStage.setScene(escena);
        carteleraStage.show();
    }

    private void mostrarFuncionalidadEmpleado(String opcion) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Funcionalidad Empleado");
        alert.setHeaderText(opcion);
        alert.setContentText("Esta funcionalidad está disponible para empleados.\n\n" +
                "Próximamente: Gestión de ventas y atención al cliente.");
        alert.showAndWait();
    }

    private void mostrarPromocionesCliente(Usuario cliente) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Promociones Activas");
        alert.setHeaderText("¡Aprovecha nuestras promociones!");
        alert.setContentText("🎁 PROMOCIONES PARA TI:\n\n" +
                "• Martes de Descuento: 20% OFF\n" +
                "• Combo Familiar: 25% OFF\n" +
      //        "• Canje de Puntos: " + cliente.getPuntosFidelidad() + " puntos disponibles\n\n" +
                "¡Disfruta del cine con los mejores precios!");
        alert.showAndWait();
    }

    private void mostrarPerfilCliente(Usuario cliente) {
        Stage perfilStage = new Stage();
        perfilStage.setTitle("Mi Perfil - " + cliente.getNombre());

        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setAlignment(Pos.CENTER_LEFT);
        panel.setStyle("-fx-background-color: #2a2a2a;");

        Label titulo = new Label("👤 MI PERFIL");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ffcc00;");

        // Información del cliente
        VBox info = new VBox(8);
        info.setStyle("-fx-background-color: #34495e; -fx-padding: 15; -fx-border-radius: 5;");

        info.getChildren().addAll(
                crearFilaPerfil("Nombre:", cliente.getNombre() + " " + cliente.getApellido()),
                crearFilaPerfil("Email:", cliente.getEmail()),
                crearFilaPerfil("Teléfono:", cliente.getTelefono()),
           //   crearFilaPerfil("Puntos de fidelidad:", String.valueOf(cliente.getPuntosFidelidad())),
                crearFilaPerfil("Tipo de cuenta:", "Cliente")
        );

        Button btnCerrar = new Button("Cerrar");
        btnCerrar.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        btnCerrar.setOnAction(e -> perfilStage.close());

        panel.getChildren().addAll(titulo, info, btnCerrar);

        Scene escena = new Scene(panel, 400, 350);
        perfilStage.setScene(escena);
        perfilStage.show();
    }

    private HBox crearFilaPerfil(String etiqueta, String valor) {
        HBox fila = new HBox(10);

        Label lblEtiqueta = new Label(etiqueta);
        lblEtiqueta.setStyle("-fx-font-weight: bold; -fx-text-fill: #1abc9c; -fx-min-width: 150;");

        Label lblValor = new Label(valor);
        lblValor.setStyle("-fx-text-fill: white;");

        fila.getChildren().addAll(lblEtiqueta, lblValor);
        return fila;
    }

    private void mostrarVentasFacturacion() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ventas y Facturación");
        alert.setHeaderText("Módulo de Ventas y Facturación");
        alert.setContentText("Esta funcionalidad permite:\n\n" +
                "• Gestión de ventas de entradas\n" +
                "• Facturación electrónica\n" +
                "• Reportes de ingresos\n" +
                "• Control de inventario\n\n" +
                "Próximamente disponible...");
        alert.showAndWait();
    }

    private void mostrarMensajeDesarrollo(String opcion) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Opción del Menú");
        alert.setHeaderText(null);
        alert.setContentText("Has seleccionado: " + opcion + "\n\nEsta funcionalidad está en desarrollo.");
        alert.showAndWait();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cerrarSesion(Usuario cliente) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Cierre de Sesión");
        alert.setHeaderText(null);
        alert.setContentText("¿Está seguro que desea cerrar sesión?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            stage.close();
            // Volver al login
            Platform.runLater(() -> {
                LoginInterfaz login = new LoginInterfaz();
                login.start(new Stage(),cliente);
            });
        }
    }
}