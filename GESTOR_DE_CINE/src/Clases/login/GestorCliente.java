package Clases.login;

import Clases.GestorFunciones;
import Clases.GestorPeliculas;
import Clases.Pelicula;
import Clases.VistaCartelera;
import Clases.login.usuario.Cliente;
import ManejoJSON.FuncionesJSON;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;



import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;


public class GestorCliente {

    public static void iniciarCliente(GestorFunciones gestorFunciones, Cliente cliente) {
        Clases.GestorPeliculas.setListaPeliculas(FuncionesJSON.deserializarPeliculas());
        vistaCliente(GestorPeliculas.getListaPeliculas(), gestorFunciones,cliente);
    }

    public static void vistaCliente(List<Pelicula> listaPeliculas, GestorFunciones gestorFunciones,Cliente cliente) {
        Stage ventana = new Stage();
        HBox contenedor = new HBox(20);
        contenedor.setStyle("""
            -fx-background-color: #6E0A17;
        """);

        // Mostrar las películas disponibles
        for(Pelicula p: listaPeliculas) {
            VBox vista = VistaCartelera.crearVista(p, gestorFunciones.getListaFunciones().getElementos(),cliente);
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
        });

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

}