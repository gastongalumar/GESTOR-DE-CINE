// App.java - VERSIÓN FINAL INTEGRADA (Login + Cartelera + Selector + GestorCine)
import Clases.*;
import Clases.GestionSelectorAsientos.SelectorAsientos;
import Clases.login.CineLogin;
import Clases.login.LoginInterfaz;
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

        
        // Inicializar gestor principal


    //GestorAdministrador.iniciarAdministrador(gestorFunciones);


//     System.out.println("prueba"+ gestorPeliculas.getListaPeliculas());
        System.out.println("🎬 Iniciando Sistema de Cine Integrado...");
    //    GestorCine gestorCine = new GestorCine();
        //GestorFunciones gestorFunciones = new GestorFunciones();
       /* SalaCine s1 = new SalaCine("Sala 1", 200);
        SalaCine s2 = new SalaCine("Sala 2", 200);*/

      //  gestorCine.inicializarSistema();
        LoginInterfaz.abrirLogin();
        //GestorAdministrador.iniciarAdministrador(gestorFunciones);

//
//        System.out.println("✅ Sistema inicializado con:");
//        System.out.println("   🎬 " + gestorCine.getGestorPeliculas().getListaPeliculas().size() + " películas");
//        System.out.println("   🎭 " + GestorFunciones.getListaFunciones().size() + " funciones");
//        System.out.println("   🔐 Sistema de login integrado");

        // Cargar datos desde JSON si existen
//        try {
//            FuncionesJSON.deserializarFunciones(
//                gestorCine.getGestorPeliculas().getListaPeliculas(),
//                gestorCine.getSalas()
//            );
//            System.out.println("📁 Funciones cargadas desde JSON");
//        } catch (Exception e) {
//            System.out.println("📝 No se encontraron funciones guardadas, usando datos de ejemplo");
//        }

        // ✅ ABRIR SISTEMA DE LOGIN




//    private void abrirSistemaLogin() {
//        System.out.println("🔐 Iniciando sistema de login...");
//        CineLogin login = new CineLogin();
//        login.start(new Stage());
//    }

//    public static void main(String[] args) {
//        try {
//            // Iniciar JavaFX
//            launch(args);
//        } catch (Throwable t) {
//            System.err.println("❌ Error iniciando JavaFX: " + t.getMessage());
//            System.err.println("🔄 Cayendo al modo fallback...");
//
//            // Fallback a Swing si JavaFX falla
//            SwingUtilities.invokeLater(() -> {
//                try {
//                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//
//                    // Crear datos mínimos para demo
//                    GestorCine gestor = new GestorCine();
//                    gestor.inicializarSistema();
//
////                    Pelicula peliculaEjemplo = gestor.getGestorPeliculas().getListaPeliculas().get(0);
////                    SalaCine salaEjemplo = gestor.getSalas().get(0);
////                    Funcion funcionEjemplo = new Funcion(salaEjemplo, peliculaEjemplo,
////                        LocalDateTime.of(2024, 2, 20, 18, 30));
////
////                    // Abrir selector y login
////                    SelectorAsientos selector = new SelectorAsientos(funcionEjemplo);
////                    selector.setVisible(true);
//
////                    CineLogin login = new CineLogin();
////                    login.start(new Stage());
////
////                } catch (Exception ex) {
////                    ex.printStackTrace();
//                }
//            });
//        }
//    }
}
}


//// App.java - JavaFX launcher para la cartelera
//import Clases.*;
//        import Clases.login.GestorUsuarios;
//import Clases.login.LoginInterfaz;
//import ManejoJSON.FuncionesJSON;
//import ManejoJSON.GestorJsonLogin;
//import javafx.application.Application;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//
//import javax.swing.SwingUtilities;
//import javax.swing.UIManager;
//
//
//public class App extends Application {
//
//    @Override
//    public void start(Stage stage) {
//        // Crear datos de prueba
//
//        // HOLA MUNDO
//
//        GestorFunciones gestorFunciones = new GestorFunciones();
//        GestorAdministrador.iniciarAdministrador(gestorFunciones);
//
//        //System.out.println("prueba"+ gestorPeliculas.getListaPeliculas());
//        SalaCine s1 = new SalaCine("Sala 1", 200);
//        SalaCine s2 = new SalaCine("Sala 2", 200);
//
//
//
//        FuncionesJSON.deserializarPeliculas();
//        FuncionesJSON.deserializarFunciones(GestorPeliculas.getListaPeliculas(), List.of(s1, s2), gestorFunciones);
//
//
//        abrirSistemaLogin();
//
//    }
//
//
//
//    private void abrirSistemaLogin() {
//        // Esto abre la ventana de login interactiva
//        LoginInterfaz.abrirLogin();
//    }
//}
