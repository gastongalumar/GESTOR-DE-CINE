package Clases.Administracion;

import Clases.GestionFunciones.Funcion;
import Clases.GestionFunciones.GestorFunciones;
import Clases.GestionFunciones.Pelicula;
import Clases.Utilidades.ListaGenerica;
import Clases.login.GestorEstadisticasLogin;
import Clases.login.usuario.Cliente;
import ManejoJSON.FuncionesJSON;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GestorAdministrador {

    public static void iniciarAdministrador(GestorFunciones gestorFunciones, Cliente cliente) {
        GestorPeliculas.setListaPeliculas(FuncionesJSON.deserializarPeliculas());

        vistaAdministrador(GestorPeliculas.getListaPeliculas(), gestorFunciones,cliente);
    }

    public static List<Funcion> filtrarYBorrarFuncionesPasadas(List<Funcion> funciones, GestorFunciones gestorFunciones) {
        List<Funcion> vigentes = new ArrayList<>();
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");

        for (Funcion f : funciones) {
            if (f.getHorarioFuncion().isAfter(ahora)) {
                vigentes.add(f);
            } else {
                try {
                    String nombrePelicula = f.getPelicula().getNombrePelicula()
                            .replace(" ", "_")
                            .replaceAll("[^a-zA-Z0-9_]", ""); // limpiar caracteres raros

                    String nombreArchivo = String.format(
                            "Asientos_%s_%s.json",
                            nombrePelicula,
                            f.getHorarioFuncion().format(formatoFecha)
                    );

                    File archivo = new File(nombreArchivo);
                    archivo.delete();
                } catch (Exception e) {
                }
            }
        }

        gestorFunciones.setListaFunciones(new ListaGenerica<>(vigentes));
        return vigentes;
    }


    public static List<Pelicula> filtrarYBorrarPeliculasPasadas(List<Pelicula> listaPeliculas) {
        List<Pelicula> vigentes = new ArrayList<>();
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");

        for(Pelicula p: listaPeliculas){
            if(p.getFechaSalida().isAfter(ahora.toLocalDate())){
                vigentes.add(p);
            }
        }


        return vigentes;
    }
    public static void vistaAdministrador(List<Pelicula> listaPeliculas, GestorFunciones gestorFunciones, Cliente cliente){


        gestorFunciones.setListaFunciones(
                new ListaGenerica<>(filtrarYBorrarFuncionesPasadas(
                        gestorFunciones.getListaFunciones().getElementos(),
                        gestorFunciones
                ))
        );

        Stage ventana = new Stage();
        int i = 0;
        HBox contenedor = new HBox(20);
        contenedor.setStyle("""
        -fx-background-color: #6E0A17;
    """);

        List<Pelicula> peliculasVigentes = filtrarYBorrarPeliculasPasadas(listaPeliculas);
        GestorPeliculas.setListaPeliculas(peliculasVigentes);
        FuncionesJSON.serializarPeliculas(peliculasVigentes);
        for(Pelicula p: peliculasVigentes){
            VBox vista = VistaCartelera.crearVista(p, gestorFunciones.getListaFunciones().getElementos(), cliente);
            contenedor.getChildren().add(vista);
        }

        Button botonEstadisticas = new Button("Ver Estadísticas de Login");
        botonEstadisticas.setStyle("""
        -fx-background-color: #4169E1;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-padding: 10 20 10 20;
        -fx-background-radius: 10;
    """);

        botonEstadisticas.setOnAction(e -> {
            GestorEstadisticasLogin.getInstance().mostrarGraficaLogins();
        });

        Label tituloFunciones = new Label("FUNCIONES");
        tituloFunciones.setStyle("""
        -fx-font-size: 20px;
        -fx-text-fill: #0A6E61;
        -fx-font-weight: bold ;
        -fx-padding: 5 10 5 10;
        -fx-background-radius: 2;
    """);

        // === AGREGAR ESTAS DECLARACIONES QUE TE FALTAN ===
        Button botonAgregar = new Button("Agregar función");
        botonAgregar.setStyle("""
        -fx-background-color: #006600;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-padding: 10 20 10 20;
        -fx-background-radius: 10;
    """);
        botonAgregar.setOnAction(e -> {
            Formularios.formularioAgregar(listaPeliculas, gestorFunciones,cliente);
        });

        Button botonEliminar = new Button("Eliminar función");
        botonEliminar.setStyle("""
        -fx-background-color: red;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-padding: 10 20 10 20;
        -fx-background-radius: 10;
    """);
        botonEliminar.setOnAction(e -> {

            Formularios.formularioEliminarFuncion(gestorFunciones,cliente);
        });

        Separator separacion = new Separator();
        separacion.setStyle("-fx-background-color: #800080;");
        separacion.setPrefWidth(150);

        Label tituloPeliculas = new Label("PELICULAS");
        tituloPeliculas.setStyle("""
        -fx-font-size: 20px;
        -fx-text-fill: #0A6E61;
        -fx-font-weight: bold ;
        -fx-padding: 5 10 5 10;
        -fx-background-radius: 2;
    """);

        // === AGREGAR ESTOS BOTONES DE PELÍCULAS TAMBIÉN ===
        Button botonAgregarPelicula = new Button("Agregar pelicula");
        botonAgregarPelicula.setStyle("""
        -fx-background-color: #006600;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-padding: 10 20 10 20;
        -fx-background-radius: 10;
    """);
        botonAgregarPelicula.setOnAction(e -> {
            Formularios.formularioAgregarPelicula(gestorFunciones,cliente);
        });

        Button botonEliminarPelicula = new Button("Eliminar pelicula");
        botonEliminarPelicula.setStyle("""
        -fx-background-color: red;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-padding: 10 20 10 20;
        -fx-background-radius: 10;
    """);
        botonEliminarPelicula.setOnAction(e -> {
            Formularios.formularioEliminarPelicula(gestorFunciones,cliente);
        });

        Button botonModificarPelicula = new Button("Modificar pelicula");
        botonModificarPelicula.setStyle("""
        -fx-background-color: #FFAA4A;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-padding: 10 20 10 20;
        -fx-background-radius: 10;
    """);
        botonModificarPelicula.setOnAction(e -> {
            Formularios.formularioEditarPelicula(gestorFunciones,cliente);
        });

        VBox contieneBotonesFunciones = new VBox(10,
                tituloFunciones, botonAgregar, botonEliminar,
                separacion, tituloPeliculas, botonAgregarPelicula, botonEliminarPelicula, botonModificarPelicula
        );

        contenedor.getChildren().addAll(contieneBotonesFunciones);
        Scene escena = new Scene(contenedor,1550,500);
        ventana.setTitle("GESTOR ADMINISTRADOR");
        ventana.setScene(escena);
        ventana.show();
    }



    /*----------------------------------------------------------------------------------------------------------------------*/

    public static String guardarImagenPelicula(File archivoOrigen) throws IOException {
        Path carpetaImg = Paths.get("src", "img");

        //
        Path destino = carpetaImg.resolve(archivoOrigen.getName());
        try {
            Files.copy(archivoOrigen.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "/img/" + archivoOrigen.getName();
    }

    public static void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Información");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }




}

