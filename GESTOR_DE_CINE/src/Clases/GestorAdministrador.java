package Clases;

import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class GestorAdministrador {

    private static List<Pelicula> listaPeliculas = new ArrayList<>();

    public static void PeliculaAdministrador(Pelicula pelicula){
       // Stage ventana = new Stage();
        VBox vista = VistaCartelera.crearVista(pelicula);
        listaPeliculas.add(pelicula);
        //HBox contenedor = new HBox(20, vista);
       // Scene escena = new Scene(contenedor,900,500);
        //ventana.setTitle("GESTOR ADMINISTRADOR");
        //ventana.setScene(escena);
       // ventana.show();
    }

    public static void vistaAdministrador(){
        Stage ventana = new Stage();
        int i = 0;
        HBox contenedor = new HBox(20);
        for(Pelicula p: listaPeliculas){
            VBox vista = VistaCartelera.crearVista(p);
            contenedor.getChildren().add(vista);

        }

        Scene escena = new Scene(contenedor,900,500);
        ventana.setTitle("GESTOR ADMINISTRADOR");
        ventana.setScene(escena);
         ventana.show();


    }

}
