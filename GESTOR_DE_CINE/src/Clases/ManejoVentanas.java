package Clases;

import Clases.login.usuario.Usuario;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ManejoVentanas {

    /**
     * Encuentra una ventana abierta por su título
     */
    public static Stage encontrarVentanaPorTitulo(String titulo) {
        for (Stage stage : Stage.getWindows().toArray(new Stage[0])) {
            if (stage.getTitle() != null && stage.getTitle().equals(titulo)) {
                return stage;
            }
        }
        return null;
    }

    /**
     * Encuentra una ventana abierta que contenga el texto en el título
     */
    public static Stage encontrarVentanaQueContengaTitulo(String texto) {
        for (Stage stage : Stage.getWindows().toArray(new Stage[0])) {
            if (stage.getTitle() != null && stage.getTitle().contains(texto)) {
                return stage;
            }
        }
        return null;
    }

    /**
     * Cierra todas las ventanas que contengan cierto texto en el título
     */
    public static void cerrarVentanasPorTitulo(String textoEnTitulo) {
        for (Stage stage : Stage.getWindows().toArray(new Stage[0])) {
            if (stage.getTitle() != null && stage.getTitle().contains(textoEnTitulo)) {
                stage.close();
            }
        }
    }

    /**
     * Cierra ventanas específicas del administrador y reinicia el gestor
     */
    public static void reiniciarGestorAdministrador(GestorFunciones gestorFunciones, Usuario cliente) {
        // Cerrar todas las ventanas del administrador
        cerrarVentanasPorTitulo("GESTOR ADMINISTRADOR");
        cerrarVentanasPorTitulo("Formulario para modificar");
        cerrarVentanasPorTitulo("Modificar pelicula");
        cerrarVentanasPorTitulo("Modificar funcion");
        cerrarVentanasPorTitulo("Agregar");
        cerrarVentanasPorTitulo("Eliminar");

        // Esperar un poco y reiniciar
        Platform.runLater(() -> {
            try {
                // Pequeña pausa para que cierre
                Thread.sleep(1000);
                GestorAdministrador.iniciarAdministrador(gestorFunciones,cliente);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Cierra ventanas específicas y ejecuta un Runnable después
     */
    public static void cerrarYEjecutar(List<String> titulosACerrar, Runnable accionPosterior) {
        // Cerrar ventanas especificadas
        for (String titulo : titulosACerrar) {
            cerrarVentanasPorTitulo(titulo);
        }

        // Ejecutar acción después de un delay
        Platform.runLater(() -> {
            try {
                CompletableFuture.delayedExecutor(300, TimeUnit.MILLISECONDS)
                        .execute(() -> {
                            Platform.runLater(accionPosterior);
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}