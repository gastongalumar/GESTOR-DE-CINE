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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

        contenedor.setOnMouseClicked(e -> {verFunciones(pelicula);
        });

        return contenedor;
    }


    public static HBox verFunciones(Pelicula pelicula){
        Stage ventana = new Stage();

        VBox peliculaSeleccionada = crearVista(pelicula);
        peliculaSeleccionada.setScaleX(0.7);
        peliculaSeleccionada.setScaleY(0.7);


        HBox contenedor = new HBox(20);


        VBox contenedorFunciones = new VBox(15);
        contenedorFunciones.setAlignment(Pos.CENTER_LEFT);


        Label titulo = new Label("FUNCIONES PARA " + pelicula.getNombrePelicula());
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");


        Map<LocalDate, List<Funcion>> funcionesPorDia = new TreeMap<>();
        for(Funcion f : buscarFuncionesPorNombrePelicula(pelicula.getNombrePelicula())){
            LocalDate dia = f.getHorarioFuncion().toLocalDate();


            if(!funcionesPorDia.containsKey(dia)){
                funcionesPorDia.put(dia, new ArrayList<>());
            }


            funcionesPorDia.get(dia).add(f);
        }


        for(LocalDate dia : funcionesPorDia.keySet()){
            Label fechaLabel = new Label(dia.format(formatoFecha));
            fechaLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");


            HBox horariosDelDia = new HBox(10);
            for(Funcion f : funcionesPorDia.get(dia)){
                Label horario = new Label(f.getHorarioFuncion().format(formatoHora));
                horario.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");


                horario.setOnMouseEntered(e -> horario.setStyle("-fx-font-size: 14px; -fx-text-fill: blue;"));
                horario.setOnMouseExited(e -> horario.setStyle("-fx-font-size: 14px; -fx-text-fill: black;"));

                horariosDelDia.getChildren().add(horario);
            }


            contenedorFunciones.getChildren().addAll(fechaLabel, horariosDelDia);
        }


        VBox contenedorVertical = new VBox(15, titulo, contenedor);
        contenedorVertical.setAlignment(Pos.TOP_CENTER);
        contenedorVertical.setStyle("-fx-background-color: linear-gradient(to bottom, #4b0000, #666666); -fx-padding: 15;");


        contenedor.getChildren().addAll(peliculaSeleccionada, contenedorFunciones);


        Scene escena = new Scene(contenedorVertical, 600, 500);
        ventana.setScene(escena);
        ventana.setTitle("Funciones de la película");
        ventana.show();


        ventana.setOnCloseRequest(e -> {
            peliculaSeleccionada.setScaleX(1);
            peliculaSeleccionada.setScaleY(1);
        });

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
