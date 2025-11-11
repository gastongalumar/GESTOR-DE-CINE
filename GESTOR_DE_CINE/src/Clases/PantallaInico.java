package Clases;

import Clases.login.LoginInterfaz;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PantallaInico extends Application {
    private Stage stage;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        mostrarPantallaInicio();
    }

    private void mostrarPantallaInicio() {
        stage.setTitle("CINE MARCENTER - Sistema de Gestión");
        stage.setWidth(900);
        stage.setHeight(650);
        stage.setResizable(false);
        stage.centerOnScreen();

        // Fondo con gradiente más moderno y cinematográfico
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.rgb(10, 10, 20)),
                        new Stop(0.3, Color.rgb(35, 0, 60)),
                        new Stop(0.7, Color.rgb(65, 0, 95)),
                        new Stop(1, Color.rgb(20, 20, 30))),
                CornerRadii.EMPTY, null)));

        // Efecto de partículas luminosas en el fondo
        Pane backgroundEffects = crearEfectosFondo();
        StackPane mainContainer = new StackPane();
        mainContainer.getChildren().addAll(backgroundEffects, root);

        // Contenedor principal con efecto de vidrio
        VBox glassPanel = new VBox(25);
        glassPanel.setAlignment(Pos.CENTER);
        glassPanel.setPadding(new Insets(40, 60, 40, 60));
        glassPanel.setMaxWidth(500);
        glassPanel.setBackground(new Background(new BackgroundFill(
                Color.rgb(255, 255, 255, 0.1),
                new CornerRadii(20),
                Insets.EMPTY
        )));
        glassPanel.setBorder(new Border(new BorderStroke(
                Color.rgb(255, 204, 0, 0.3),
                BorderStrokeStyle.SOLID,
                new CornerRadii(20),
                new BorderWidths(2)
        )));

        // Efecto de sombra para el panel
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(255, 204, 0, 0.5));
        shadow.setRadius(30);
        shadow.setSpread(0.1);
        glassPanel.setEffect(shadow);

        // Logo con efecto cinematográfico
        VBox logoContainer = new VBox(10);
        logoContainer.setAlignment(Pos.CENTER);

        // Círculo de fondo para el logo
        Circle circleBackground = new Circle(60);
        circleBackground.setFill(new RadialGradient(
                0, 0, 0.5, 0.5, 0.6, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(255, 204, 0, 0.8)),
                new Stop(1, Color.rgb(255, 153, 0, 0.4))
        ));
        circleBackground.setEffect(new Glow(0.3));

        Label logoIcon = new Label("🎬");
        logoIcon.setStyle("-fx-font-size: 50; -fx-text-fill: white;");
        logoIcon.setEffect(new DropShadow(10, Color.GOLD));

        StackPane logoStack = new StackPane();
        logoStack.getChildren().addAll(circleBackground, logoIcon);

        // Títulos con mejor tipografía y efectos
        Label titulo = new Label("BIENVENIDO A");
        titulo.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: white;");
        titulo.setEffect(new DropShadow(5, Color.BLACK));

        Label nombreCine = new Label("CINE MARCENTER");
        nombreCine.setStyle("-fx-font-size: 38; -fx-font-weight: bold; -fx-text-fill: linear-gradient(to right, #ffcc00, #ff9900);");
        nombreCine.setEffect(new DropShadow(15, Color.rgb(255, 204, 0, 0.7)));

        // Subtítulo mejorado
        Label subtitulo = new Label("Sistema de Gestión Cinematográfica");
        subtitulo.setStyle("-fx-font-size: 16; -fx-text-fill: #cccccc; -fx-font-style: italic;");

        // Línea decorativa
        Region separator = new Region();
        separator.setPrefHeight(2);
        separator.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.TRANSPARENT),
                        new Stop(0.3, Color.GOLD),
                        new Stop(0.7, Color.GOLD),
                        new Stop(1, Color.TRANSPARENT)),
                CornerRadii.EMPTY, null
        )));
        separator.setMaxWidth(300);
        separator.setPadding(new Insets(10, 0, 10, 0));

        // Mensaje interactivo mejorado
        Label mensaje = new Label("Haz click para comenzar");
        mensaje.setStyle("-fx-font-size: 14; -fx-text-fill: #ffcc00; -fx-font-weight: bold;");

        // Efecto de pulso para el mensaje
        aplicarEfectoPulso(mensaje);

        // Animación de puntos mejorada
        animateDots(mensaje);

        // Agregar elementos al panel de vidrio
        glassPanel.getChildren().addAll(
                logoStack,
                titulo,
                nombreCine,
                separator,
                subtitulo,
                mensaje
        );

        root.getChildren().add(glassPanel);

        Scene scene = new Scene(mainContainer);
        stage.setScene(scene);
        stage.show();

        // Transición de entrada mejorada
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(2), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // Detectar interacción
        root.setOnMouseClicked(e -> {
            cerrarConAnimacion();
        });

        scene.setOnKeyPressed(e -> {
            switch(e.getCode()) {
                case SPACE:
                case ENTER:
                case ESCAPE:
                    cerrarConAnimacion();
                    break;
            }
        });

        javafx.animation.PauseTransition autoClose = new javafx.animation.PauseTransition(Duration.seconds(15));
        autoClose.setOnFinished(e -> {
            if (stage.isShowing()) {
                cerrarConAnimacion();
            }
        });
        autoClose.play();
    }

    private Pane crearEfectosFondo() {
        Pane pane = new Pane();

        // Crear algunas estrellas/partículas
        for (int i = 0; i < 15; i++) {
            Circle star = new Circle(1 + Math.random() * 2);
            star.setFill(Color.rgb(255, 255, 255, 0.6));
            star.setCenterX(Math.random() * 900);
            star.setCenterY(Math.random() * 650);
            pane.getChildren().add(star);

            // Animación de parpadeo para las estrellas
            FadeTransition blink = new FadeTransition(Duration.seconds(1 + Math.random() * 2), star);
            blink.setFromValue(0.3);
            blink.setToValue(0.8);
            blink.setCycleCount(FadeTransition.INDEFINITE);
            blink.setAutoReverse(true);
            blink.play();
        }

        return pane;
    }

    private void aplicarEfectoPulso(Label label) {
        FadeTransition pulse = new FadeTransition(Duration.seconds(1.5), label);
        pulse.setFromValue(0.6);
        pulse.setToValue(1.0);
        pulse.setCycleCount(FadeTransition.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();
    }

    private void cerrarConAnimacion() {
        StackPane mainContainer = (StackPane) stage.getScene().getRoot();
        VBox root = (VBox) mainContainer.getChildren().get(1);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.8), root);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(event -> abrirLogin());
        fadeOut.play();
    }

    private void animateDots(Label label) {
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(Duration.seconds(0.6), e -> label.setText("Haz click para comenzar")),
                new javafx.animation.KeyFrame(Duration.seconds(1.2), e -> label.setText("Haz click para comenzar.")),
                new javafx.animation.KeyFrame(Duration.seconds(1.8), e -> label.setText("Haz click para comenzar..")),
                new javafx.animation.KeyFrame(Duration.seconds(2.4), e -> label.setText("Haz click para comenzar..."))
        );
        timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
        timeline.play();
    }

    private void abrirLogin() {
        stage.close();
        LoginInterfaz.abrirLogin();
    }


    public static void mostrarInicio() {
        PantallaInico pantalla = new PantallaInico();
        pantalla.start(new Stage());
    }
}