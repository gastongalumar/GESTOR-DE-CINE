
import Clases.SelectorAsientos;
import ManejoJSON.JSONUtiles;
import javax.swing.*;

import Clases.Funcion;
import Clases.Pelicula;
import Clases.Sala;
import Clases.VistaCartelera;
import ManejoJSON.JSONUtiles;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Date;

public class App extends Application {

    public void start(Stage stage){
        Pelicula p1 = new Pelicula("pelicula1");
        Sala s1 = new Sala("Sala 1", 200);
        Funcion f1 = new Funcion(s1, p1, LocalDate.of(2025,10,15));

        VBox vista1 =VistaCartelera.crearVista(f1);
        VBox vista2 =VistaCartelera.crearVista(f1);
        HBox contenedor = new HBox(20, vista1, vista2);
        contenedor.setAlignment(Pos.CENTER);

        Scene escena = new Scene(contenedor, 900,500);
        stage.setTitle("CARTELERA DE PELICULAS");
        stage.setScene(escena);
        stage.show();


        // LLAMA AL SELECTOR DESDE MAIN
//        SwingUtilities.invokeLater(() -> {
//            try {
//                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//            new SelectorAsientos();
//        });

    }





}

}

