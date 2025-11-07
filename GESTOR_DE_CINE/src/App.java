// App.java - VERSIÓN FINAL INTEGRADA (Login + Cartelera + Selector)
import Clases.*;
import Clases.login.CineLogin;
import ManejoJSON.FuncionesJSON;
import javafx.application.Application;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        // ✅ INICIALIZAR DATOS DE PRUEBA COMPLETOS
        System.out.println("🎬 Iniciando Sistema de Cine Integrado...");
        
        GestorPeliculas gestorPeliculas = new GestorPeliculas();
        
        // Crear películas de ejemplo (de Cartelera)
        Pelicula p1 = new Pelicula("Avengers: Endgame", LocalDate.of(2024, 1, 15), LocalDate.of(2024, 3, 15));
        Pelicula p2 = new Pelicula("The Batman", LocalDate.of(2024, 2, 1), LocalDate.of(2024, 4, 1));
        Pelicula p3 = new Pelicula("Spider-Man: No Way Home", LocalDate.of(2024, 1, 20), LocalDate.of(2024, 3, 20));
        Pelicula p4 = new Pelicula("Dune: Part Two", LocalDate.of(2024, 2, 10), LocalDate.of(2024, 4, 10));

        gestorPeliculas.getListaPeliculas().add(p1);
        gestorPeliculas.getListaPeliculas().add(p2);
        gestorPeliculas.getListaPeliculas().add(p3);
        gestorPeliculas.getListaPeliculas().add(p4);

        // Crear salas y funciones (de Selector)
        SalaCine s1 = new SalaCine("Sala 1", 150);
        SalaCine s2 = new SalaCine("Sala 2", 120);
        SalaCine s3 = new SalaCine("Sala 3", 200);
        
        // Crear funciones de ejemplo
        new Funcion(s1, p1, LocalDateTime.of(2024, 2, 20, 18, 30));
        new Funcion(s1, p1, LocalDateTime.of(2024, 2, 20, 21, 0));
        new Funcion(s1, p2, LocalDateTime.of(2024, 2, 21, 19, 0));
        new Funcion(s2, p3, LocalDateTime.of(2024, 2, 22, 17, 30));
        new Funcion(s2, p4, LocalDateTime.of(2024, 2, 23, 20, 0));

        System.out.println("✅ Sistema inicializado con:");
        System.out.println("   🎬 " + gestorPeliculas.getListaPeliculas().size() + " películas");
        System.out.println("   🎭 " + GestorFunciones.getListaFunciones().size() + " funciones");
        System.out.println("   🔐 Sistema de login integrado");

        // Cargar funciones desde JSON si existen
        try {
            FuncionesJSON.deserializarFunciones(gestorPeliculas.getListaPeliculas(), List.of(s1, s2, s3));
            System.out.println("📁 Funciones cargadas desde JSON");
        } catch (Exception e) {
            System.out.println("📝 No se encontraron funciones guardadas, usando datos de ejemplo");
        }

        // ✅ ABRIR SISTEMA DE LOGIN (de tu rama)
        abrirSistemaLogin();
    }

    private void abrirSistemaLogin() {
        // Usar tu sistema de login existente
        System.out.println("🔐 Iniciando sistema de login...");
        CineLogin login = new CineLogin();
        login.start(new Stage());
    }

    public static void main(String[] args) {
        try {
            // Iniciar JavaFX
            launch(args);
        } catch (Throwable t) {
            System.err.println("❌ Error iniciando JavaFX: " + t.getMessage());
            System.err.println("🔄 Cayendo al modo fallback...");
            
            // Fallback a Swing si JavaFX falla
            SwingUtilities.invokeLater(() -> {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    
                    // Crear datos mínimos para el selector
                    Pelicula peliculaEjemplo = new Pelicula("Película de Ejemplo");
                    SalaCine salaEjemplo = new SalaCine("Sala 1", 200);
                    Funcion funcionEjemplo = new Funcion(salaEjemplo, peliculaEjemplo, 
                        LocalDateTime.of(2024, 2, 20, 18, 30));
                    
                    // Abrir selector directamente
                    SelectorAsientos selector = new SelectorAsientos(funcionEjemplo);
                    selector.setVisible(true);
                    
                    // Y también el login
                    CineLogin login = new CineLogin();
                    login.start(new Stage());
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }
}