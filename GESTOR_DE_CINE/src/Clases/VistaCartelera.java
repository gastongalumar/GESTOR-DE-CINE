package Clases;

import Clases.GestionSelectorAsientos.SelectorAsientos;
import Clases.login.usuario.Cliente;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.SwingUtilities;
public class VistaCartelera {

    public static VBox crearVista(Pelicula pelicula, List<Funcion> listaFunciones,Cliente cliente){
        String rutaImagen = pelicula.getRutaImagen();

        Node imageNode;
        if(rutaImagen != null && !rutaImagen.isEmpty()){
            try (InputStream is = VistaCartelera.class.getResourceAsStream(rutaImagen)) {
                if(is != null){
                    Image imagen = new Image(is);
                    ImageView imgPelicula = new ImageView(imagen);
                    imgPelicula.setFitHeight(300);
                    imgPelicula.setFitWidth(220);
                    imgPelicula.setPreserveRatio(true);
                    imageNode = imgPelicula;
                } else {
                    imageNode = crearPlaceholder();
                }
            } catch(Exception ex){
                ex.printStackTrace();
                imageNode = crearPlaceholder();
            }
        } else {
            imageNode = crearPlaceholder();
        }

        DateTimeFormatter fechaFormateador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Label titulo = new Label(pelicula.getNombrePelicula());
        titulo.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");

        Label ultimaFecha = new Label("Finaliza el " + pelicula.getFechaSalida().format(fechaFormateador));
        ultimaFecha.setStyle("-fx-text-fill: black;");

        VBox contenedor = new VBox(10, imageNode, titulo, ultimaFecha);
        contenedor.setAlignment(Pos.BASELINE_LEFT);
        contenedor.setStyle("-fx-background-color: #0A6E61; -fx-padding: 15; -fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 15, 0, 0, 5);");

        contenedor.setOnMouseClicked(e -> verFunciones(pelicula, listaFunciones,cliente));

        return contenedor;
    }

    private static Label crearPlaceholder(){
        Label placeholder = new Label("[imagen no disponible]");
        placeholder.setPrefSize(220,300);
        placeholder.setStyle("-fx-background-color: #444; -fx-text-fill: white; -fx-alignment: center; -fx-padding: 10;");
        return placeholder;
    }


    public static void verFunciones(Pelicula pelicula, List<Funcion> listaFunciones, Cliente cliente){
        Stage ventana = new Stage();

        VBox peliculaSeleccionada = crearVista(pelicula, listaFunciones,cliente);
        peliculaSeleccionada.setScaleX(0.7);
        peliculaSeleccionada.setScaleY(0.7);


        HBox contenedor = new HBox(20);


        VBox contenedorFunciones = new VBox(15);
        contenedorFunciones.setAlignment(Pos.CENTER_LEFT);


        Label titulo = new Label("FUNCIONES PARA " + pelicula.getNombrePelicula());
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");


        Map<LocalDate, List<Funcion>> funcionesPorDia = new TreeMap<>();
        for(Funcion f : buscarFuncionesPorNombrePelicula(pelicula.getNombrePelicula(), listaFunciones)){
            LocalDate dia = f.getHorarioFuncion().toLocalDate();


            if(!funcionesPorDia.containsKey(dia)){
                funcionesPorDia.put(dia, new ArrayList<>());
            }


            funcionesPorDia.get(dia).add(f);
        }


        for(LocalDate dia : funcionesPorDia.keySet()){
            Label fechaLabel = new Label(dia.format(formatoFecha));
            fechaLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");


            HBox horariosDelDia = new HBox(10);
            for (Funcion f : funcionesPorDia.get(dia)) {
                Label horario = new Label(f.getHorarioFuncion().format(formatoHora));
                horario.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");

                horario.setOnMouseEntered(ev -> horario.setStyle("-fx-font-size: 14px; -fx-text-fill: yellow;"));
                horario.setOnMouseExited(ev -> horario.setStyle("-fx-font-size: 14px; -fx-text-fill: white;"));

                /// ACA ES DONDE LLAMA AL METODO DE SELECCION DE ASIENTOS

                horario.setOnMouseClicked(ev -> {
                    SwingUtilities.invokeLater(() -> {
                        SelectorAsientos.mostrarSelectorAsientos(f,cliente);

                    });
                });

                horariosDelDia.getChildren().add(horario);
            }

            contenedorFunciones.getChildren().addAll(fechaLabel, horariosDelDia);
        }


        VBox contenedorVertical = new VBox(15, titulo, contenedor);
        contenedorVertical.setAlignment(Pos.TOP_CENTER);
        contenedorVertical.setStyle("-fx-background-color: linear-gradient(to bottom, #4b0000, #666666); -fx-padding: 15;");


        contenedor.getChildren().addAll(peliculaSeleccionada, contenedorFunciones);


        Scene escena = new Scene(contenedorVertical, 600, 500);
        ventana.setScene(escena);
        ventana.setTitle("Funciones de la película");
        ventana.show();


        ventana.setOnCloseRequest(ev -> {
            peliculaSeleccionada.setScaleX(1);
            peliculaSeleccionada.setScaleY(1);
        });

        // antes se devolvía el contenedor pero no era usado; ahora es void
    }


    public static List<Funcion> buscarFuncionesPorNombrePelicula(String nombre, List<Funcion> listaFuncionesGestor){
        List<Funcion> listaFunciones = new ArrayList<>();
        for(Funcion f: listaFuncionesGestor){
            if(f.getPelicula().getNombrePelicula().equals(nombre)){
                listaFunciones.add(f);
            }
        }
        return listaFunciones;
    }

}
