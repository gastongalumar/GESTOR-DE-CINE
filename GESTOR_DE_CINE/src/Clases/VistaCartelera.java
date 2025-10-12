package Clases;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.awt.*;

public class VistaCartelera {

    public static VBox crearVista(Funcion funcion){
        String rutaImagen = "/img/" + funcion.getPelicula().getNombrePelicula() + ".jpg";
        Image imagen = new Image(VistaCartelera.class.getResourceAsStream(rutaImagen));

        ImageView imgPelicula = new ImageView(imagen);
        imgPelicula.setFitHeight(300);
        imgPelicula.setFitWidth(220);
        imgPelicula.setPreserveRatio(true);

        Label titulo = new Label(funcion.getPelicula().getNombrePelicula());
        titulo.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
        Label ultimaFecha = new Label("Finaliza el "+funcion.getFechaSalida().toString());
        ultimaFecha.setStyle("-fx-text-fill: red;");
        VBox contenedor = new VBox(10, imgPelicula,titulo,ultimaFecha);
        contenedor.setAlignment(Pos.BASELINE_LEFT);
        contenedor.setStyle("-fx-background-color: #2a2a2a; -fx-padding: 15; -fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 15, 0, 0, 5);");

        return contenedor;
    }
}
