package Clases;

import Clases.login.GestorEstadisticasLogin;
import Clases.login.usuario.Cliente;
import ManejoJSON.FuncionesJSON;
import ManejoJSON.GestorJsonAsientos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static Clases.Formularios.compararFechas;

public class GestorAdministrador {

    /*public static void PeliculaAdministrador(Pelicula pelicula, List<Pelicula> listaPeliculas){
        VBox vista = VistaCartelera.crearVista(pelicula);
        listaPeliculas.add(pelicula);

    }


*/

    public static void iniciarAdministrador(GestorFunciones gestorFunciones,Cliente cliente) {
        Clases.GestorPeliculas.setListaPeliculas(FuncionesJSON.deserializarPeliculas());

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
                    if (archivo.exists()) {
                        if (archivo.delete()) {
                            System.out.println("🗑️ Archivo eliminado: " + nombreArchivo);
                        } else {
                            System.err.println("⚠️ No se pudo eliminar: " + nombreArchivo);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("❌ Error al intentar borrar JSON de " + f.getPelicula().getNombrePelicula() + ": " + e.getMessage());
                }
            }
        }

        gestorFunciones.setListaFunciones(new ListaGenerica<>(vigentes));
        return vigentes;
    }

    public static void vistaAdministrador(List<Pelicula> listaPeliculas, GestorFunciones gestorFunciones,Cliente cliente){


        gestorFunciones.setListaFunciones(
                new ListaGenerica<>(filtrarYBorrarFuncionesPasadas(
                        gestorFunciones.getListaFunciones().getElementos(),
                        gestorFunciones
                ))
        );

   // public static void vistaAdministrador(List<Pelicula> listaPeliculas, GestorFunciones gestorFunciones){
        Stage ventana = new Stage();
        int i = 0;
        HBox contenedor = new HBox(20);
        contenedor.setStyle("""
        -fx-background-color: #6E0A17;
    """);

        for(Pelicula p: listaPeliculas){
            //VBox vista = VistaCartelera.crearVista(p, gestorFunciones.getListaFunciones().getElementos(),cliente);
           //VBox vista = VistaCartelera.crearVista(p,filtrarYBorrarFuncionesPasadas(gestorFunciones.getListaFunciones().getElementos(), gestorFunciones),cliente);
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
            formularioAgregar(listaPeliculas, gestorFunciones,cliente);
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
            formularioEliminarFuncion(gestorFunciones,cliente);
        });

        Button botonModificarFuncion = new Button("Modificar función");
        botonModificarFuncion.setStyle("""
        -fx-background-color: #FFAA4A;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-padding: 10 20 10 20;
        -fx-background-radius: 10;
    """);
        botonModificarFuncion.setOnAction(e -> {
            formularioEditarFuncion(gestorFunciones,cliente);
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

        // === MODIFICAR ESTA LÍNEA para incluir el nuevo botón ===
        VBox contieneBotonesFunciones = new VBox(10,
                tituloFunciones, botonAgregar, botonEliminar, botonModificarFuncion,
                separacion, tituloPeliculas, botonAgregarPelicula, botonEliminarPelicula, botonModificarPelicula
        );

        contenedor.getChildren().addAll(contieneBotonesFunciones);
        Scene escena = new Scene(contenedor,1350,500);
        ventana.setTitle("GESTOR ADMINISTRADOR");
        ventana.setScene(escena);
        ventana.show();
    }

    public static void formularioAgregar(List<Pelicula> listaPeliculas, GestorFunciones gestorFunciones,Cliente cliente){
        Stage ventana = new Stage();
        ventana.setTitle("Agregar nueva función");

        Label titulo = new Label("Agregar nueva función");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label tituloPelicula = new Label("Película");
        tituloPelicula.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #ffcc00;");

        TextField campoPelicula = new TextField();
        campoPelicula.setPromptText("Nombre de la película");
        campoPelicula.setPrefWidth(250);

        VBox seccionPelicula = new VBox(5, tituloPelicula, campoPelicula);
        seccionPelicula.setAlignment(Pos.CENTER_LEFT);

        Label tituloDatos = new Label("Datos de función");
        tituloDatos.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #ffcc00;");

        Label labelSala = new Label("Sala:");
        TextField campoSala = new TextField();
        campoSala.setPromptText("Número de sala");
        Label textoSala = new Label("→ Número de sala");
        textoSala.setStyle("-fx-text-fill: #bbbbbb; -fx-font-size: 11px;");


        Label labelFechaInicial = new Label("Fecha inicial:");
        TextField campoFechaInicial = new TextField();
        campoFechaInicial.setPromptText("Ej: 2025-10-15");
        Label textoFechaInicial = new Label("→ Fecha de inicio del período");
        textoFechaInicial.setStyle("-fx-text-fill: #bbbbbb; -fx-font-size: 11px;");


        Label labelFechaFinal = new Label("Fecha final:");
        TextField campoFechaFinal = new TextField();
        campoFechaFinal.setPromptText("Ej: 2025-10-15");
        Label textoFechaFinal = new Label("→ Último día del período");
        textoFechaFinal.setStyle("-fx-text-fill: #bbbbbb; -fx-font-size: 11px;");


        Label labelHorario = new Label("Horario:");
        TextField campoHorario = new TextField();
        campoHorario.setPromptText("Ej: 20:30");
        Label textoHorario = new Label("→ Hora de inicio de la función");
        textoHorario.setStyle("-fx-text-fill: #bbbbbb; -fx-font-size: 11px;");

        Label labelPrecio = new Label("Precio:");
        TextField campoPrecio = new TextField();
        campoPrecio.setPromptText("2000");
        Label textoPrecio= new Label("→ Precio de la función");
        textoHorario.setStyle("-fx-text-fill: #bbbbbb; -fx-font-size: 11px;");


        GridPane gridDatos = new GridPane();
        gridDatos.setHgap(10);
        gridDatos.setVgap(10);
        gridDatos.addRow(0, labelSala, campoSala, textoSala);
        gridDatos.addRow(1, labelFechaInicial, campoFechaInicial, textoFechaInicial);
        gridDatos.addRow(2, labelFechaFinal, campoFechaFinal, textoFechaFinal);
        gridDatos.addRow(3, labelHorario, campoHorario, textoHorario);
        gridDatos.addRow(4, labelPrecio, campoPrecio,textoPrecio);

        VBox seccionDatos = new VBox(5, tituloDatos, gridDatos);
        seccionDatos.setAlignment(Pos.CENTER_LEFT);
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
            double precio = Double.parseDouble(campoPrecio.getText());
            boolean encontrado = false;
            for(Pelicula p: listaPeliculas){
                if(p.getNombrePelicula().equalsIgnoreCase(nombrePelicula)){
                    encontrado = true;
                }
            }

            if (nombrePelicula.isEmpty() || sala.isEmpty() || horario.isEmpty()|| !encontrado || precio < 0) {
                mostrarAlerta("Por favor, completa todos los campos.");
            } else {
                try {
                    DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");

                    LocalDateTime fechaInicialTime = LocalDateTime.parse(fechaInicial + " " + horario, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    LocalDateTime fechaFinalTime = LocalDateTime.parse(fechaFinal + " " + horario, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                    long diasDiferencia = ChronoUnit.DAYS.between(fechaInicialTime.toLocalDate(), fechaFinalTime.toLocalDate());

                    if(diasDiferencia < 0){
                        mostrarAlerta("Ingresá un rango de fechas válido");
                    }
                    LocalDateTime fechaAgregar = fechaInicialTime;
                    System.out.println("Días de diferencia: " + diasDiferencia);
                    System.out.println("Fecha inicial: " + fechaInicialTime);
                    System.out.println("Fecha final: " + fechaFinalTime);

                    for (long i = 0; i <= diasDiferencia; i++) {
                        Funcion funcion = new Funcion(sala, nombrePelicula, fechaAgregar, listaPeliculas, precio, gestorFunciones);
                        gestorFunciones.agregarFuncion(funcion);
                        System.out.println(funcion);
                        fechaAgregar = fechaAgregar.plusDays(1);
                    }

                    FuncionesJSON.serializarFunciones(gestorFunciones.getListaFunciones().getElementos());
                    mostrarAlerta("Funciones agregadas correctamente.");
                    ventana.close();

                } catch (DateTimeParseException ex) {
                    mostrarAlerta("Formato de fecha u hora incorrecto");
                }

            }

            ventana.close();
            ManejoVentanas.reiniciarGestorAdministrador(gestorFunciones,cliente);
        });


        // --- Layout principal ---
        VBox layout = new VBox(20, titulo, seccionPelicula, seccionDatos, botonGuardar);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #2a2a2a;");

        Scene escena = new Scene(layout, 500, 450);
        ventana.setScene(escena);
        ventana.show();

    }


    private static void formularioEliminarFuncion(GestorFunciones gestorFunciones,Cliente cliente){
        Stage ventana = new Stage();
        ventana.setTitle("Eliminar funcion");

        Label titulo = new Label("Eliminar una función");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label tituloPelicula = new Label("Película");
        tituloPelicula.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #ffcc00;");

        TextField campoPelicula = new TextField();
        campoPelicula.setPromptText("Nombre de la película");
        campoPelicula.setPrefWidth(250);

        VBox seccionPelicula = new VBox(5, tituloPelicula, campoPelicula);
        seccionPelicula.setAlignment(Pos.CENTER_LEFT);

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
            for(Funcion f:gestorFunciones.getListaFunciones().getElementos()){
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
                    gestorFunciones.eliminarFuncion(funcionEliminar);
                    mostrarAlerta("Funcion eliminada correctamente");

                    ventana.close();
                    ManejoVentanas.reiniciarGestorAdministrador(gestorFunciones,cliente);
                }catch (DateTimeParseException ex){

                    mostrarAlerta("Formato de fecha y hora incorrecto");
                }

            }
        });


        VBox layout = new VBox(20, titulo, seccionPelicula, seccionDatos, botonEliminar);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #2a2a2a;");

        Scene escena = new Scene(layout, 400, 350);
        ventana.setScene(escena);
        ventana.show();

    }

    /*----------------------------------------------------------------------------------------------------------------------*/

    private static void formularioEditarFuncion(GestorFunciones gestorFunciones, Cliente cliente){
        Stage ventana = new Stage();

        ventana.setTitle("Formulario para modificar funcion");

        Label titulo = new Label("Ingrese nombre pelicula");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        TextField campoNombrePelicula= new TextField();
        campoNombrePelicula.setPromptText("Nombre pelicula:");
        campoNombrePelicula.setPrefWidth(250);


        Button botonBuscar = new Button("Buscar funciones");
        botonBuscar.setStyle("-fx-background-color: #228B22; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 20 8 20;");
        VBox layout = new VBox(20, titulo, campoNombrePelicula);

        botonBuscar.setOnAction(e->{

            for(Funcion f: gestorFunciones.getListaFunciones().getElementos()){
                if(f.getPelicula().getNombrePelicula().equalsIgnoreCase(campoNombrePelicula.getText())){
                    Label funcion = new Label(f.getHorarioFuncion().toString().replace("T", " "));
                    funcion.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: white;");
                    layout.getChildren().add(funcion);

                    funcion.setOnMouseClicked(p->{
                        editarFuncion(f, gestorFunciones,cliente);


                    });

                    funcion.setOnMouseEntered(r->{
                            funcion.setStyle("-fx-text-fill: green");
                });

                    funcion.setOnMouseExited(o->{
                        funcion.setStyle("-fx-text-fill: white");


                    });


                }
            }

        });




        layout.getChildren().add(botonBuscar);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #2a2a2a;");
        Scene escena = new Scene(layout, 400,250);
        ventana.setScene(escena);
        ventana.show();



    }

    private static void editarFuncion (Funcion f, GestorFunciones gestorFunciones,Cliente cliente){
        Stage ventana = new Stage();

        ventana.setTitle("Modificar funcion");

        Label titulo = new Label("Modificar funcion");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        TextField campoNombrePelicula= new TextField(f.getPelicula().getNombrePelicula());
        campoNombrePelicula.setPromptText("Nombre pelicula:");
        campoNombrePelicula.setPrefWidth(250);

        TextField campoSala= new TextField(f.getSala().getNombreSala());
        campoSala.setPromptText("Sala:");
        campoSala.setPrefWidth(250);

        TextField campoFecha= new TextField(f.getHorarioFuncion().toLocalDate().toString());
        campoFecha.setPromptText("Fecha:");
        campoFecha.setPrefWidth(250);

        TextField campoHorario= new TextField(f.getHorarioFuncion().toLocalTime().toString());
        campoHorario.setPromptText("Fecha:");
        campoHorario.setPrefWidth(250);


        Button botonGuardar = new Button("Guardar cambios");
        botonGuardar.setStyle("-fx-background-color: #228B22; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 20 8 20;");

        botonGuardar.setOnAction(e->{

            DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            LocalDate fecha = LocalDate.parse(campoFecha.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalTime hora = LocalTime.parse(campoHorario.getText().trim(), DateTimeFormatter.ofPattern("HH:mm"));

            LocalDateTime nuevaFechaHora = LocalDateTime.of(fecha, hora);
            for(int i = 0; i < gestorFunciones.getListaFunciones().getElementos().size(); i++){
                if(f == gestorFunciones.getListaFunciones().getElementos().get(i)){
                   gestorFunciones.getListaFunciones().getElementos().get(i).getSala().setNombreSala(campoSala.getText());
                  gestorFunciones.getListaFunciones().getElementos().get(i).setHorarioFuncion(nuevaFechaHora);
                    break;
                }
            }
            FuncionesJSON.serializarFunciones(gestorFunciones.getListaFunciones().getElementos());

            ventana.close();
            ManejoVentanas.reiniciarGestorAdministrador(gestorFunciones,cliente);
        });

        VBox layout = new VBox(20, titulo, campoNombrePelicula, campoSala, campoFecha, campoHorario, botonGuardar);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #2a2a2a;");
        Scene escena = new Scene(layout, 400,250);
        ventana.setScene(escena);
        ventana.show();
    }



   /* private static void formularioEditarPelicula(GestorFunciones gestorFunciones){
        Stage ventana = new Stage();

        ventana.setTitle("Formulario para modificar pelicula");

        Label titulo = new Label("Ingrese nombre pelicula");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        TextField campoNombrePelicula= new TextField();
        campoNombrePelicula.setPromptText("Nombre pelicula:");
        campoNombrePelicula.setPrefWidth(250);


        Button botonBuscar = new Button("Buscar pelicula");
        botonBuscar.setStyle("-fx-background-color: #228B22; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 20 8 20;");
        VBox layout = new VBox(20, titulo, campoNombrePelicula);

        botonBuscar.setOnAction(e->{

            for(Pelicula p: GestorPeliculas.getListaPeliculas()){
                if(p.getNombrePelicula().equalsIgnoreCase(campoNombrePelicula.getText())){
                    editarPelicula(p, gestorFunciones);
                    break;
                }
            }

        });




        layout.getChildren().add(botonBuscar);
        // VBox layout = new VBox(20, titulo, campoNombrePelicula);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #2a2a2a;");
        Scene escena = new Scene(layout, 400,250);
        ventana.setScene(escena);
        ventana.show();



    }*/


   /* private static void editarPelicula (Pelicula p, GestorFunciones gestorFunciones){
        Stage ventana = new Stage();

        ventana.setTitle("Modificar pelicula");

        Label titulo = new Label("Modificar pelicula");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        TextField campoNombrePelicula= new TextField(p.getNombrePelicula());
        campoNombrePelicula.setPromptText("Nombre pelicula:");
        campoNombrePelicula.setPrefWidth(250);

        TextField campoFechaEstreno= new TextField(p.getFechaEstreno().toString());
        campoFechaEstreno.setPromptText("Fecha estreno:");
        campoFechaEstreno.setPrefWidth(250);

        TextField campoFechaSalida= new TextField(p.getFechaSalida().toString());
        campoFechaSalida.setPromptText("Fecha salida/finalización:");
        campoFechaSalida.setPrefWidth(250);

        ImageView imagenVista = new ImageView();
        try {
            Image imagenActual = new Image(p.getRutaImagen(), 120, 180, true, true);
            imagenVista.setImage(imagenActual);
        } catch (Exception ex) {
            System.out.println("No se pudo cargar la imagen actual de la película.");
        }

        Button botonCambiarImagen = new Button("Cambiar imagen");
        botonCambiarImagen.setStyle("-fx-background-color: #4169E1; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        final String[] nuevaRutaImagen = {p.getRutaImagen()};

        botonCambiarImagen.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar nueva imagen de película");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Archivos de imagen", "*.png", "*.jpg", "*.jpeg")
            );

            File archivoSeleccionado = fileChooser.showOpenDialog(ventana);
            if (archivoSeleccionado != null) {
                try {
                    String rutaGuardada = guardarImagenPelicula(archivoSeleccionado);
                    nuevaRutaImagen[0] = rutaGuardada;

                    Image nuevaImg = new Image(rutaGuardada, 120, 180, true, true);
                    imagenVista.setImage(nuevaImg);

                } catch (IOException ex) {
                    ex.printStackTrace();
                    Alert alerta = new Alert(Alert.AlertType.ERROR, "Error al guardar la imagen seleccionada.");
                    alerta.showAndWait();
                }
            }
        });



        Button botonGuardar = new Button("Guardar cambios");
        botonGuardar.setStyle("-fx-background-color: #228B22; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 20 8 20;");

        botonGuardar.setOnAction(e->{

            DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");


            LocalDate nuevaFechaEstreno = LocalDate.parse(campoFechaEstreno.getText(), formato);
            LocalDate nuevaFechaSalida = LocalDate.parse(campoFechaSalida.getText(), formato);


            String nombreAnterior = p.getNombrePelicula();
            String nuevoNombre = campoNombrePelicula.getText();


            if (!nombreAnterior.equals(nuevoNombre)) {
                GestorJsonAsientos.copiarArchivosAsientos(nombreAnterior, nuevoNombre, gestorFunciones);
            }
            p.setNombrePelicula(campoNombrePelicula.getText());
            p.setFechaEstreno(nuevaFechaEstreno);
            p.setFechaSalida(nuevaFechaSalida);
            p.setRutaImagen(nuevaRutaImagen[0]);
            FuncionesJSON.serializarPeliculas(GestorPeliculas.getListaPeliculas());
            FuncionesJSON.serializarFunciones(gestorFunciones.getListaFunciones().getElementos());


            ventana.close(); // Cierra esta ventana primero
            ManejoVentanas.reiniciarGestorAdministrador(gestorFunciones);

        });

        VBox layout = new VBox(20, titulo, campoNombrePelicula,campoFechaEstreno, campoFechaSalida,imagenVista,botonCambiarImagen, botonGuardar);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #2a2a2a;");
        Scene escena = new Scene(layout, 700,550);
        ventana.setScene(escena);
        ventana.show();
    }

*/


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

   /* public static void PeliculaAdministrador(Pelicula pelicula, List<Pelicula> listaPeliculas){
        VBox vista = VistaCartelera.crearVista(pelicula); // Crear vista de la película
        listaPeliculas.add(pelicula); // Agregar a la lista
        mostrarAlerta("Película \"" + pelicula.getNombrePelicula() + "\" agregada correctamente.");
    }*/

    public static void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Información");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }




}

