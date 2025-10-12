package Clases;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VistaCartelera {

    public static VBox crearVista(Pelicula pelicula){
        String rutaImagen = "/img/" + pelicula.getNombrePelicula() + ".jpg";
        System.out.println(VistaCartelera.class.getResourceAsStream(rutaImagen));
        Image imagen = new Image(VistaCartelera.class.getResourceAsStream(rutaImagen));

        ImageView imgPelicula = new ImageView(imagen);
        imgPelicula.setFitHeight(300);
        imgPelicula.setFitWidth(220);
        imgPelicula.setPreserveRatio(true);

        Label titulo = new Label(pelicula.getNombrePelicula());
        titulo.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
        Label ultimaFecha = new Label("Finaliza el "+ pelicula.getFechaSalida());
        ultimaFecha.setStyle("-fx-text-fill: red;");
        VBox contenedor = new VBox(10, imgPelicula,titulo,ultimaFecha);
        contenedor.setAlignment(Pos.BASELINE_LEFT);
        contenedor.setStyle("-fx-background-color: #2a2a2a; -fx-padding: 15; -fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 15, 0, 0, 5);");

        contenedor.setOnMouseClicked(e -> {verFunciones(contenedor, pelicula);
        });

        return contenedor;
    }


    public static HBox verFunciones(VBox peliculaSeleccionada, Pelicula pelicula){
        VBox copia = crearVista(pelicula);
        Stage ventana = new Stage();
        copia.setScaleX(0.5);
        copia.setScaleY(0.5);
        List<Funcion> listaFunciones = buscarFuncionesPorNombrePelicula(pelicula.getNombrePelicula());

        HBox contenedor = new HBox(20);
        VBox contenedorFunciones = new VBox(10);
        contenedorFunciones.setAlignment(Pos.CENTER);

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for(Funcion f: listaFunciones){
            Label horarioFuncion = new Label(f.getHorarioFuncion().format(formato).toString());
            horarioFuncion.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
            contenedorFunciones.getChildren().add(horarioFuncion);

            horarioFuncion.setOnMouseEntered(e->{
                horarioFuncion.setStyle("-fx-font-size: 14px; -fx-text-fill: blue;");
            });
            horarioFuncion.setOnMouseExited(e->{
                horarioFuncion.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
            });

        }


        contenedor.getChildren().addAll(copia, contenedorFunciones);
        Scene escena = new Scene(contenedor,500,500);
        ventana.setScene(escena);
        ventana.show();
        return contenedor;
    }


    public static List<Funcion> buscarFuncionesPorNombrePelicula(String nombre){
        List<Funcion> listaFunciones = new ArrayList<>();
        for(Funcion f: GestorFunciones.getListaFunciones()){
            if(f.getPelicula().getNombrePelicula().equals(nombre)){
                listaFunciones.add(f);
            }
        }
        return listaFunciones;
    }
}
