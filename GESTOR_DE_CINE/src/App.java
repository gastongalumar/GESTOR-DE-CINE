// App.java - VERSIÓN FINAL INTEGRADA (Login + Cartelera + Selector + GestorCine)
import Clases.*;
import Clases.GestionSelectorAsientos.SelectorAsientos;
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
        // ✅ INICIALIZAR SISTEMA COMPLETO
        System.out.println("🎬 Iniciando Sistema de Cine Integrado...");
        
        // Inicializar gestor principal
        GestorCine gestorCine = new GestorCine();
        gestorCine.inicializarSistema();
        
        System.out.println("✅ Sistema inicializado con:");
        System.out.println("   🎬 " + gestorCine.getGestorPeliculas().getListaPeliculas().size() + " películas");
        System.out.println("   🎭 " + GestorFunciones.getListaFunciones().size() + " funciones");
        System.out.println("   🔐 Sistema de login integrado");

        // Cargar datos desde JSON si existen
        try {
            FuncionesJSON.deserializarFunciones(
                gestorCine.getGestorPeliculas().getListaPeliculas(), 
                gestorCine.getSalas()
            );
            System.out.println("📁 Funciones cargadas desde JSON");
        } catch (Exception e) {
            System.out.println("📝 No se encontraron funciones guardadas, usando datos de ejemplo");
        }

        // ✅ ABRIR SISTEMA DE LOGIN
        abrirSistemaLogin();
    }

    private void abrirSistemaLogin() {
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
                    
                    // Crear datos mínimos para demo
                    GestorCine gestor = new GestorCine();
                    gestor.inicializarSistema();
                    
                    Pelicula peliculaEjemplo = gestor.getGestorPeliculas().getListaPeliculas().get(0);
                    SalaCine salaEjemplo = gestor.getSalas().get(0);
                    Funcion funcionEjemplo = new Funcion(salaEjemplo, peliculaEjemplo, 
                        LocalDateTime.of(2024, 2, 20, 18, 30));
                    
                    // Abrir selector y login
                    SelectorAsientos selector = new SelectorAsientos(funcionEjemplo);
                    selector.setVisible(true);
                    
                    CineLogin login = new CineLogin();
                    login.start(new Stage());
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }
}
>>>>>>> origin/selector
}