// App.java - JavaFX launcher para la cartelera
import Clases.*;
import ManejoJSON.FuncionesJSON;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class App extends Application {



    @Override
    public void start(Stage stage) {
        // Crear datos de prueba

        // HOLA MUNDO
        GestorAdministrador.iniciarAdministrador();
        Pelicula p1 = new Pelicula("pelicula1", LocalDate.of(2025, 7, 2), LocalDate.of(2025, 11, 5));
        Pelicula p2 = new Pelicula("pelicula2", LocalDate.of(2025, 10, 5), LocalDate.of(2025, 12, 6));
        Pelicula p3 = new Pelicula("pelicula3", LocalDate.of(2025, 10, 15), LocalDate.of(2026, 1, 12));
        Pelicula p4 = new Pelicula("pelicula4", LocalDate.of(2025, 8, 4), LocalDate.of(2025, 11, 6));

        /*gestorPeliculas.getListaPeliculas().add(p1);
        gestorPeliculas.getListaPeliculas().add(p2);
        gestorPeliculas.getListaPeliculas().add(p3);
        gestorPeliculas.getListaPeliculas().add(p4);*/

        //System.out.println("prueba"+ gestorPeliculas.getListaPeliculas());
        SalaCine s1 = new SalaCine("Sala 1", 200);
        SalaCine s2 = new SalaCine("Sala 2", 200);

        // Crear funciones asociadas a las películas (se registran automáticamente en GestorFunciones)
       /* Funcion f1 = new Funcion(s1, p1, LocalDateTime.of(2025, 10, 15, 18, 30));
        Funcion f2 = new Funcion(s1, p1, LocalDateTime.of(2025, 10, 15, 21, 0));
        Funcion f3 = new Funcion(s1, p2, LocalDateTime.of(2025, 10, 16, 17, 0));
        Funcion f4 = new Funcion(s2, p3, LocalDateTime.of(2025, 10, 16, 17, 0));
        Funcion f5 = new Funcion(s2, p4, LocalDateTime.of(2025, 10, 16, 20, 0));
        /*GestorFunciones.agregarFuncion(f1);
        GestorFunciones.agregarFuncion(f2);
        GestorFunciones.agregarFuncion(f3);
        GestorFunciones.agregarFuncion(f4);
        GestorFunciones.agregarFuncion(f5);*/

        // Crear vistas de cartelera para las películas
       /* VBox vista1 = VistaCartelera.crearVista(p1);
        VBox vista2 = VistaCartelera.crearVista(p2);
        VBox vista3 = VistaCartelera.crearVista(p3);
        VBox vista4 = VistaCartelera.crearVista(p4);
        //VBox vista1 = GestorAdministrador.replica(p1);
        HBox contenedor = new HBox(20, vista1,vista2,vista3,vista4);
        contenedor.setAlignment(Pos.CENTER);

        Scene escena = new Scene(contenedor, 900, 500);
        stage.setTitle("CARTELERA DE PELICULAS");
        stage.setScene(escena);
        stage.show();*/
       /* GestorAdministrador.PeliculaAdministrador(p1,gestorPeliculas.getListaPeliculas());
        GestorAdministrador.PeliculaAdministrador(p2,gestorPeliculas.getListaPeliculas());
        GestorAdministrador.PeliculaAdministrador(p3,gestorPeliculas.getListaPeliculas());
        GestorAdministrador.PeliculaAdministrador(p4,gestorPeliculas.getListaPeliculas());*/
        //GestorAdministrador.vistaAdministrador(gestorPeliculas.getListaPeliculas());
        //GestorAdministrador.replica(contenedor, stage);

       // System.out.println(GestorFunciones.getListaFunciones());
        FuncionesJSON.deserializarPeliculas();
        // FuncionesJSON.serializarFunciones(GestorFunciones.getListaFunciones());
        FuncionesJSON.deserializarFunciones(GestorPeliculas.getListaPeliculas(), List.of(s1, s2));
       // FuncionesJSON.deserializarPeliculas();

    }
}