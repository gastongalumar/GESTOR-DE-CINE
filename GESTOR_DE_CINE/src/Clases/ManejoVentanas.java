package Clases;

import Clases.login.usuario.Cliente;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ManejoVentanas {

    public static void cerrarVentanasPorTitulo(String textoEnTitulo) {
        for (Stage stage : Stage.getWindows().toArray(new Stage[0])) {
            if (stage.getTitle() != null && stage.getTitle().contains(textoEnTitulo)) {
                stage.close();
            }
        }
    }



    public static void reiniciarGestorAdministrador(GestorFunciones gestorFunciones, Cliente cliente) {
        // Cerrar todas las ventanas del administrador
        cerrarVentanasPorTitulo("GESTOR ADMINISTRADOR");
        cerrarVentanasPorTitulo("Formulario para modificar");
        cerrarVentanasPorTitulo("Modificar pelicula");
        cerrarVentanasPorTitulo("Modificar funcion");
        cerrarVentanasPorTitulo("Agregar");
        cerrarVentanasPorTitulo("Eliminar");


        Platform.runLater(() -> {
            try {
                Thread.sleep(1000);
                GestorAdministrador.iniciarAdministrador(gestorFunciones,cliente);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

}