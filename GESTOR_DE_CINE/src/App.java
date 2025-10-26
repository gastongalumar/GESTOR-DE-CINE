// App.java - JavaFX launcher para la cartelera
import Clases.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        // Crear datos de prueba
        Pelicula p1 = new Pelicula("pelicula1");
        Pelicula p2 = new Pelicula("pelicula2");
        Pelicula p3 = new Pelicula("pelicula3");
        Pelicula p4 = new Pelicula("pelicula4");
        System.out.println("prueba rebase");
        SalaCine s1 = new SalaCine("Sala 1", 200);
        SalaCine s2 = new SalaCine("Sala 2", 200);

        // Crear funciones asociadas a las películas (se registran automáticamente en GestorFunciones)
        Funcion f1 = new Funcion(s1, p1, LocalDateTime.of(2025, 10, 15, 18, 30));
        Funcion f2 = new Funcion(s1, p1, LocalDateTime.of(2025, 10, 15, 21, 0));
        Funcion f3 = new Funcion(s1, p2, LocalDateTime.of(2025, 10, 16, 17, 0));
        Funcion f4 = new Funcion(s2, p3, LocalDateTime.of(2025, 10, 16, 17, 0));
        Funcion f5 = new Funcion(s2, p4, LocalDateTime.of(2025, 10, 16, 20, 0));

        // Crear vistas de cartelera para las películas
        VBox vista1 = VistaCartelera.crearVista(p1);
        VBox vista2 = VistaCartelera.crearVista(p2);
        VBox vista3 = VistaCartelera.crearVista(p3);
        VBox vista4 = VistaCartelera.crearVista(p4);
        HBox contenedor = new HBox(20, vista1, vista2,vista3,vista4);
        contenedor.setAlignment(Pos.CENTER);

        Scene escena = new Scene(contenedor, 1360, 768);
        stage.setTitle("CARTELERA DE PELICULAS");
        stage.setScene(escena);
        stage.show();
    }

    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Throwable t) {
            System.err.println("No se pudo iniciar JavaFX: " + t.getMessage());
            System.err.println("Cayendo al modo fallback Swing. Si quieres ejecutar la cartelera en JavaFX, configura JavaFX en el classpath.");

            // Como fallback, abrir directamente el selector con una función de prueba
            SwingUtilities.invokeLater(() -> {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                Pelicula p1 = new Pelicula("pelicula1");
                SalaCine s1 = new SalaCine("Sala 1", 200);
                Funcion f1 = new Funcion(s1, p1, LocalDateTime.of(2025, 10, 15, 18, 30));
                SelectorAsientos selector = new SelectorAsientos(f1);
                selector.setVisible(true);
            });
        }
    }
}