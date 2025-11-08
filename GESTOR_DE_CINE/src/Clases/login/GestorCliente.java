package Clases.login;



import Clases.GestorFunciones;
import Clases.GestorPeliculas;
import Clases.Pelicula;
import Clases.VistaCartelera;
import Clases.login.HistorialCompras;
import Clases.login.usuario.Cliente;
import ManejoJSON.FuncionesJSON;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

//public class GestorCliente {
//
//    public static void iniciarCliente(GestorFunciones gestorFunciones, Cliente cliente) {
//        GestorPeliculas.setListaPeliculas(FuncionesJSON.deserializarPeliculas());
//        vistaCliente(GestorPeliculas.getListaPeliculas(), gestorFunciones, cliente);
//    }
//
//    public static void vistaCliente(List<Pelicula> listaPeliculas, GestorFunciones gestorFunciones, Cliente cliente) {
//        Stage ventana = new Stage();
//        HBox contenedor = new HBox(20);
//        contenedor.setStyle("""
//            -fx-background-color: #6E0A17;
//        """);
//
//        // Mostrar películas
//        for(Pelicula p: listaPeliculas){
//            VBox vista = VistaCartelera.crearVista(p, gestorFunciones.getListaFunciones().getElementos());
//            contenedor.getChildren().add(vista);
//        }
//
//        // === BOTONES ESPECÍFICOS PARA CLIENTES ===
//
//        // Título de Funciones
//        Label tituloFunciones = new Label("CARTELERA");
//        tituloFunciones.setStyle("""
//            -fx-font-size: 20px;
//            -fx-text-fill: #0A6E61;
//            -fx-font-weight: bold;
//            -fx-padding: 5 10 5 10;
//            -fx-background-radius: 2;
//        """);
//
//        // Botón para ver historial de compras
//        Button botonHistorial = new Button("Mi Historial de Compras");
//        botonHistorial.setStyle("""
//            -fx-background-color: #4169E1;
//            -fx-text-fill: white;
//            -fx-font-weight: bold;
//            -fx-padding: 10 20 10 20;
//            -fx-background-radius: 10;
//        """);
//        botonHistorial.setOnAction(e -> {
//            HistorialCompras.mostrarHistorial(cliente);
//        });
//
//        // Botón para ver puntos de fidelidad
//        Button botonPuntos = new Button("Mis Puntos: " + cliente.getPuntosFidelidad());
//        botonPuntos.setStyle("""
//            -fx-background-color: #FFAA4A;
//            -fx-text-fill: white;
//            -fx-font-weight: bold;
//            -fx-padding: 10 20 10 20;
//            -fx-background-radius: 10;
//        """);
//        botonPuntos.setOnAction(e -> {
//            mostrarInfoPuntos(cliente);
//        });
//
//        // Botón para promociones (opcional)
//        Button botonPromociones = new Button("Promociones Activas");
//        botonPromociones.setStyle("""
//            -fx-background-color: #27ae60;
//            -fx-text-fill: white;
//            -fx-font-weight: bold;
//            -fx-padding: 10 20 10 20;
//            -fx-background-radius: 10;
//        """);
//        botonPromociones.setOnAction(e -> {
//            mostrarPromociones(cliente);
//        });
//
//        Separator separacion = new Separator();
//        separacion.setStyle("-fx-background-color: #800080;");
//        separacion.setPrefWidth(150);
//
//        // Título de Información Personal
//        Label tituloInfo = new Label("MI CUENTA");
//        tituloInfo.setStyle("""
//            -fx-font-size: 20px;
//            -fx-text-fill: #0A6E61;
//            -fx-font-weight: bold;
//            -fx-padding: 5 10 5 10;
//            -fx-background-radius: 2;
//        """);
//
//        // Botón para información del perfil
//        Button botonPerfil = new Button("Mi Perfil");
//        botonPerfil.setStyle("""
//            -fx-background-color: #9b59b6;
//            -fx-text-fill: white;
//            -fx-font-weight: bold;
//            -fx-padding: 10 20 10 20;
//            -fx-background-radius: 10;
//        """);
//        botonPerfil.setOnAction(e -> {
//            mostrarPerfil(cliente);
//        });
//
//        // Botón para cerrar sesión
//        Button botonCerrarSesion = new Button("Cerrar Sesión");
//        botonCerrarSesion.setStyle("""
//            -fx-background-color: #e74c3c;
//            -fx-text-fill: white;
//            -fx-font-weight: bold;
//            -fx-padding: 10 20 10 20;
//            -fx-background-radius: 10;
//        """);
//        botonCerrarSesion.setOnAction(e -> {
//            ventana.close();
//            // Aquí podrías volver al login si lo necesitas
//        });
//
//        // Panel de botones para clientes
//        VBox contieneBotonesCliente = new VBox(10,
//                tituloFunciones,
//                botonHistorial,
//                botonPuntos,
//                botonPromociones,
//                separacion,
//                tituloInfo,
//                botonPerfil,
//                botonCerrarSesion
//        );
//
//        contenedor.getChildren().add(contieneBotonesCliente);
//
//        Scene escena = new Scene(contenedor, 1300, 500);
//        ventana.setTitle("CINE LOS CULIA - Bienvenido " + cliente.getNombre());
//        ventana.setScene(escena);
//        ventana.show();
//    }
//
//    // === MÉTODOS AUXILIARES PARA CLIENTES ===
//
//    private static void mostrarInfoPuntos(Cliente cliente) {
//        Stage ventana = new Stage();
//        ventana.setTitle("Mis Puntos de Fidelidad");
//
//        VBox panel = new VBox(15);
//        panel.setPadding(new Insets(20));
//        panel.setAlignment(Pos.CENTER);
//        panel.setStyle("-fx-background-color: #2a2a2a;");
//
//        Label titulo = new Label("🏆 MIS PUNTOS DE FIDELIDAD");
//        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ffcc00;");
//
//        Label puntosActuales = new Label("Puntos actuales: " + cliente.getPuntosFidelidad());
//        puntosActuales.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
//
//        Label infoPuntos = new Label("💡 Canjea 100 puntos por un 10% de descuento en tu próxima compra!");
//        infoPuntos.setStyle("-fx-font-size: 14px; -fx-text-fill: #bbbbbb; -fx-text-alignment: center;");
//
//        Button btnCerrar = new Button("Cerrar");
//        btnCerrar.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
//        btnCerrar.setOnAction(e -> ventana.close());
//
//        panel.getChildren().addAll(titulo, puntosActuales, infoPuntos, btnCerrar);
//
//        Scene escena = new Scene(panel, 400, 250);
//        ventana.setScene(escena);
//        ventana.show();
//    }
//
//    private static void mostrarPromociones(Cliente cliente) {
//        Stage ventana = new Stage();
//        ventana.setTitle("Promociones Activas");
//
//        VBox panel = new VBox(15);
//        panel.setPadding(new Insets(20));
//        panel.setAlignment(Pos.CENTER_LEFT);
//        panel.setStyle("-fx-background-color: #2a2a2a;");
//
//        Label titulo = new Label("🎁 PROMOCIONES ACTIVAS");
//        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ffcc00;");
//
//        // Promoción 1
//        VBox promo1 = crearPromocion(
//                "Martes de Descuento",
//                "Todos los martes 20% OFF en todas las funciones",
//                "Válido: Todos los martes"
//        );
//
//        // Promoción 2
//        VBox promo2 = crearPromocion(
//                "Combo Familiar",
//                "4 entradas + 4 combos de pochoclos = 25% OFF",
//                "Válido: Viernes a domingo"
//        );
//
//        // Promoción 3 - Basada en puntos
//        VBox promo3 = crearPromocion(
//                "Canje de Puntos",
//                "100 puntos = 10% OFF | 200 puntos = 20% OFF",
//                "Tus puntos: " + cliente.getPuntosFidelidad()
//        );
//
//        Button btnCerrar = new Button("Cerrar");
//        btnCerrar.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
//        btnCerrar.setOnAction(e -> ventana.close());
//
//        panel.getChildren().addAll(titulo, promo1, promo2, promo3, btnCerrar);
//
//        Scene escena = new Scene(panel, 500, 400);
//        ventana.setScene(escena);
//        ventana.show();
//    }
//
//    private static VBox crearPromocion(String titulo, String descripcion, String validez) {
//        VBox promo = new VBox(5);
//        promo.setStyle("-fx-background-color: #34495e; -fx-padding: 10; -fx-border-radius: 5;");
//
//        Label lblTitulo = new Label(titulo);
//        lblTitulo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1abc9c;");
//
//        Label lblDesc = new Label(descripcion);
//        lblDesc.setStyle("-fx-font-size: 12px; -fx-text-fill: white;");
//
//        Label lblValidez = new Label(validez);
//        lblValidez.setStyle("-fx-font-size: 11px; -fx-text-fill: #bbbbbb; -fx-font-style: italic;");
//
//        promo.getChildren().addAll(lblTitulo, lblDesc, lblValidez);
//        return promo;
//    }
//
//    private static void mostrarPerfil(Cliente cliente) {
//        Stage ventana = new Stage();
//        ventana.setTitle("Mi Perfil - " + cliente.getNombre());
//
//        VBox panel = new VBox(15);
//        panel.setPadding(new Insets(20));
//        panel.setAlignment(Pos.CENTER_LEFT);
//        panel.setStyle("-fx-background-color: #2a2a2a;");
//
//        Label titulo = new Label("👤 MI PERFIL");
//        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ffcc00;");
//
//        // Información del cliente
//        VBox info = new VBox(8);
//        info.setStyle("-fx-background-color: #34495e; -fx-padding: 15; -fx-border-radius: 5;");
//
//        info.getChildren().addAll(
//                crearFilaPerfil("Nombre:", cliente.getNombre() + " " + cliente.getApellido()),
//                crearFilaPerfil("Email:", cliente.getEmail()),
//                crearFilaPerfil("Teléfono:", cliente.getTelefono()),
//                crearFilaPerfil("Puntos de fidelidad:", String.valueOf(cliente.getPuntosFidelidad())),
//                crearFilaPerfil("Tipo de cuenta:", "Cliente")
//        );
//
//        Button btnCerrar = new Button("Cerrar");
//        btnCerrar.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
//        btnCerrar.setOnAction(e -> ventana.close());
//
//        panel.getChildren().addAll(titulo, info, btnCerrar);
//
//        Scene escena = new Scene(panel, 400, 350);
//        ventana.setScene(escena);
//        ventana.show();
//    }
//
//    private static HBox crearFilaPerfil(String etiqueta, String valor) {
//        HBox fila = new HBox(10);
//
//        Label lblEtiqueta = new Label(etiqueta);
//        lblEtiqueta.setStyle("-fx-font-weight: bold; -fx-text-fill: #1abc9c; -fx-min-width: 150;");
//
//        Label lblValor = new Label(valor);
//        lblValor.setStyle("-fx-text-fill: white;");
//
//        fila.getChildren().addAll(lblEtiqueta, lblValor);
//        return fila;
//    }
//}


import Clases.login.GestorEstadisticasLogin;
import ManejoJSON.FuncionesJSON;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class GestorCliente {

    public static void iniciarCliente(GestorFunciones gestorFunciones) {
        Clases.GestorPeliculas.setListaPeliculas(FuncionesJSON.deserializarPeliculas());
        vistaCliente(GestorPeliculas.getListaPeliculas(), gestorFunciones);
    }

    public static void vistaCliente(List<Pelicula> listaPeliculas, GestorFunciones gestorFunciones) {
        Stage ventana = new Stage();
        HBox contenedor = new HBox(20);
        contenedor.setStyle("""
            -fx-background-color: #6E0A17;
        """);

        // Mostrar las películas disponibles
        for(Pelicula p: listaPeliculas) {
            VBox vista = VistaCartelera.crearVista(p, gestorFunciones.getListaFunciones().getElementos());
            contenedor.getChildren().add(vista);
        }

        // === SEPARADOR Y TÍTULOS (solo informativos) ===
        Label tituloFunciones = new Label("FUNCIONES DISPONIBLES");
        tituloFunciones.setStyle("""
            -fx-font-size: 20px;
            -fx-text-fill: #0A6E61;
            -fx-font-weight: bold;
            -fx-padding: 5 10 5 10;
            -fx-background-radius: 2;
        """);

        Label tituloPeliculas = new Label("CARTELERA");
        tituloPeliculas.setStyle("""
            -fx-font-size: 20px;
            -fx-text-fill: #0A6E61;
            -fx-font-weight: bold;
            -fx-padding: 5 10 5 10;
            -fx-background-radius: 2;
        """);

        Separator separacion = new Separator();
        separacion.setStyle("-fx-background-color: #800080;");
        separacion.setPrefWidth(150);

        // === BOTÓN DE CERRAR SESIÓN ===
        Button botonCerrarSesion = new Button("Cerrar Sesión");
        botonCerrarSesion.setStyle("""
            -fx-background-color: #FF4444;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-padding: 10 20 10 20;
            -fx-background-radius: 10;
        """);

        botonCerrarSesion.setOnAction(e -> {
            ventana.close();
            // Aquí puedes agregar la lógica para volver a la pantalla de login si es necesario
        });

        // === PANEL LATERAL SOLO CON INFORMACIÓN Y CERRAR SESIÓN ===
        VBox panelLateral = new VBox(10,
                tituloPeliculas,
                tituloFunciones,
                separacion,
                botonCerrarSesion
        );

        contenedor.getChildren().add(panelLateral);

        Scene escena = new Scene(contenedor, 1300, 500);
        ventana.setTitle("CINE - VISTA CLIENTE");
        ventana.setScene(escena);
        ventana.show();
    }

    public static void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Información");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}