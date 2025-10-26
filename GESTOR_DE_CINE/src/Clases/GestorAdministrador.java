package Clases;

import ManejoJSON.FuncionesJSON;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class GestorAdministrador {

    /*public static void PeliculaAdministrador(Pelicula pelicula, List<Pelicula> listaPeliculas){
        VBox vista = VistaCartelera.crearVista(pelicula);
        listaPeliculas.add(pelicula);

    }
*/
    public static void vistaAdministrador(List<Pelicula> listaPeliculas){
        Stage ventana = new Stage();
        int i = 0;
        HBox contenedor = new HBox(20);
        for(Pelicula p: listaPeliculas){
            VBox vista = VistaCartelera.crearVista(p);
            contenedor.getChildren().add(vista);

        }
        Button botonAgregar = new Button("Agregar función");
        botonAgregar.setStyle("""
            -fx-background-color: #006600;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-padding: 10 20 10 20;
            -fx-background-radius: 10;
        """);
        botonAgregar.setOnAction(e -> {
            formularioAgregar(listaPeliculas);
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
            formularioEliminarFuncion(GestorFunciones.getListaFunciones());
        });

        VBox contieneBotonesFunciones = new VBox(10, botonAgregar, botonEliminar);
        contenedor.getChildren().add(contieneBotonesFunciones);
        Scene escena = new Scene(contenedor,1300,500);
        ventana.setTitle("GESTOR ADMINISTRADOR");
        ventana.setScene(escena);
        ventana.show();
    }

    public static void formularioAgregar(List<Pelicula> listaPeliculas){
        Stage ventana = new Stage();
        ventana.setTitle("Agregar nueva función");

        // --- Título principal ---
        Label titulo = new Label("Agregar nueva función");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        // --- Sección Película ---
        Label tituloPelicula = new Label("Película");
        tituloPelicula.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #ffcc00;");

        TextField campoPelicula = new TextField();
        campoPelicula.setPromptText("Nombre de la película");
        campoPelicula.setPrefWidth(250);

        VBox seccionPelicula = new VBox(5, tituloPelicula, campoPelicula);
        seccionPelicula.setAlignment(Pos.CENTER_LEFT);

        // --- Sección Datos de función ---
        Label tituloDatos = new Label("Datos de función");
        tituloDatos.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #ffcc00;");

        Label labelSala = new Label("Sala:");
        TextField campoSala = new TextField();
        campoSala.setPromptText("Número de sala");

        Label labelFechaInicial = new Label("Fecha inicial:");
        TextField campoFechaInicial = new TextField();
        campoFechaInicial.setPromptText("Ej: 2025-10-15");

        Label labelFechaFinal = new Label("Fecha final:");
        TextField campoFechaFinal = new TextField();
        campoFechaFinal.setPromptText("Ej: 2025-10-15");

        Label labelHorario = new Label("Horario:");
        TextField campoHorario = new TextField();
        campoHorario.setPromptText("Ej: 20:30");

        GridPane gridDatos = new GridPane();
        gridDatos.setHgap(10);
        gridDatos.setVgap(10);
        gridDatos.addRow(0, labelSala, campoSala);
        gridDatos.addRow(1, labelFechaInicial, campoFechaInicial);
        gridDatos.addRow(2, labelFechaFinal, campoFechaFinal);
        gridDatos.addRow(3, labelHorario, campoHorario);

        VBox seccionDatos = new VBox(5, tituloDatos, gridDatos);
        seccionDatos.setAlignment(Pos.CENTER_LEFT);

        // --- Botón Guardar ---
        Button botonGuardar = new Button("Guardar función");
        botonGuardar.setStyle("""
            -fx-background-color: #228B22;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-background-radius: 8;
            -fx-padding: 8 20 8 20;
        """);


        botonGuardar.setOnAction(e -> {
            String nombrePelicula = campoPelicula.getText();
            String sala = campoSala.getText();
            String fechaInicial = campoFechaInicial.getText();
            String fechaFinal = campoFechaFinal.getText();
            String horario = campoHorario.getText();
            boolean encontrado = false;
            for(Pelicula p: listaPeliculas){
                if(p.getNombrePelicula().equalsIgnoreCase(nombrePelicula)){
                    encontrado = true;
                }
            }

            if (nombrePelicula.isEmpty() || sala.isEmpty() || horario.isEmpty()|| !encontrado) {
                mostrarAlerta("Por favor, completa todos los campos.");
            } else {
                try {
                    DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");

                    LocalDateTime fechaInicialTime = LocalDateTime.parse(fechaInicial + " " + horario, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    LocalDateTime fechaFinalTime = LocalDateTime.parse(fechaFinal + " " + horario, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                    long diasDiferencia = ChronoUnit.DAYS.between(fechaInicialTime.toLocalDate(), fechaFinalTime.toLocalDate());

                    LocalDateTime fechaAgregar = fechaInicialTime;
                    System.out.println("Días de diferencia: " + diasDiferencia);
                    System.out.println("Fecha inicial: " + fechaInicialTime);
                    System.out.println("Fecha final: " + fechaFinalTime);

                    for (long i = 0; i <= diasDiferencia; i++) {
                        Funcion funcion = new Funcion(sala, nombrePelicula, fechaAgregar, listaPeliculas);
                        GestorFunciones.agregarFuncion(funcion);
                        fechaAgregar = fechaAgregar.plusDays(1);
                        System.out.println(GestorFunciones.getListaFunciones());
                    }

                    FuncionesJSON.serializarFunciones(GestorFunciones.getListaFunciones());
                    mostrarAlerta("Funciones agregadas correctamente.");
                    ventana.close();

                } catch (DateTimeParseException ex) {
                    mostrarAlerta("Formato de fecha u hora incorrecto");
                }

            }
        });


        // --- Layout principal ---
        VBox layout = new VBox(20, titulo, seccionPelicula, seccionDatos, botonGuardar);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #2a2a2a;");

        Scene escena = new Scene(layout, 400, 350);
        ventana.setScene(escena);
        ventana.show();

    }


    private static void formularioEliminarFuncion(List<Funcion> listaFunciones){
        Stage ventana = new Stage();
        ventana.setTitle("Agregar nueva función");

        // --- Título principal ---
        Label titulo = new Label("Agregar nueva función");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        // --- Sección Película ---
        Label tituloPelicula = new Label("Película");
        tituloPelicula.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #ffcc00;");

        TextField campoPelicula = new TextField();
        campoPelicula.setPromptText("Nombre de la película");
        campoPelicula.setPrefWidth(250);

        VBox seccionPelicula = new VBox(5, tituloPelicula, campoPelicula);
        seccionPelicula.setAlignment(Pos.CENTER_LEFT);

        // --- Sección Datos de función ---
        Label tituloDatos = new Label("Datos de función");
        tituloDatos.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #ffcc00;");

        Label labelSala = new Label("Sala:");
        TextField campoSala = new TextField();
        campoSala.setPromptText("Número de sala");

        Label labelFecha = new Label("Fecha::");
        TextField campoFecha = new TextField();
        campoFecha.setPromptText("Ej: 2025-10-15");

        Label labelHorario = new Label("Horario:");
        TextField campoHorario = new TextField();
        campoHorario.setPromptText("Ej: 20:30");

        GridPane gridDatos = new GridPane();
        gridDatos.setHgap(10);
        gridDatos.setVgap(10);
        gridDatos.addRow(0, labelSala, campoSala);
        gridDatos.addRow(1, labelFecha, campoFecha);
        gridDatos.addRow(2, labelHorario, campoHorario);

        VBox seccionDatos = new VBox(5, tituloDatos, gridDatos);
        seccionDatos.setAlignment(Pos.CENTER_LEFT);

        // --- Botón Guardar ---
        Button botonEliminar = new Button("Eliminar funcion");
        botonEliminar.setStyle("""
            -fx-background-color: #228B22;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-background-radius: 8;
            -fx-padding: 8 20 8 20;
        """);


        botonEliminar.setOnAction(e -> {
            String nombrePelicula = campoPelicula.getText();
            String sala = campoSala.getText();
            String fecha = campoFecha.getText();
            String horario = campoHorario.getText();
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String fechaTotal = campoFecha.getText().trim().concat(" ").concat(campoHorario.getText().trim());
            LocalDateTime fechaHora = LocalDateTime.parse(fechaTotal, formato);
            boolean encontrado = false;
            Funcion funcionEliminar = null;
            for(Funcion f: listaFunciones){
                if(f.getPelicula().getNombrePelicula().equalsIgnoreCase(nombrePelicula) && f.getSala().getNombreSala().equalsIgnoreCase(sala) && f.getHorarioFuncion().equals(fechaHora)){
                    encontrado = true;
                    funcionEliminar = f;
                    break;
                }
            }

            if (nombrePelicula.isEmpty() || sala.isEmpty() || horario.isEmpty()|| !encontrado) {
                mostrarAlerta("Por favor, completa todos los campos.");
            } else {
                try {
                    //String fechaTotal = campoFecha.getText().trim().concat(" ").concat(campoHorario.getText().trim());

                    //LocalDateTime fechaHora = LocalDateTime.parse(fechaTotal, formato);
                    GestorFunciones.eliminarFuncion(funcionEliminar);
                    mostrarAlerta("Funcion eliminada correctamente");

                    ventana.close();
                }catch (DateTimeParseException ex){

                    mostrarAlerta("Formato de fecha y hora incorrecto");
                }

            }
        });


        // --- Layout principal ---
        VBox layout = new VBox(20, titulo, seccionPelicula, seccionDatos, botonEliminar);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #2a2a2a;");

        Scene escena = new Scene(layout, 400, 350);
        ventana.setScene(escena);
        ventana.show();

    }

    public static void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Información");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }




}
