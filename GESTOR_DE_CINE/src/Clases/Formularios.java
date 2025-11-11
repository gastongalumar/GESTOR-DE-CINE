package Clases;

import Clases.login.usuario.Cliente;
import Excepciones.CamposIncompletosException;
import Excepciones.FechaInvalidaException;
import Excepciones.PeliculaInvalidaException;
import ManejoJSON.FuncionesJSON;
import ManejoJSON.GestorJsonAsientos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static Clases.GestorAdministrador.guardarImagenPelicula;
import static Clases.GestorAdministrador.mostrarAlerta;

public class Formularios {

    public static void formularioAgregarPelicula(GestorFunciones gestorFunciones, Cliente cliente) {
        Stage ventana = crearVentana("Agregar nueva película");
        Label titulo = crearTitulo("Agregar nueva película");

        // --- Campos de texto ---
        TextField campoNombre = crearCampoTexto("Nombre de la película");
        TextField campoEstreno = crearCampoTexto("Ej: 2025-10-15");
        TextField campoSalida = crearCampoTexto("Ej: 2025-12-31");
        ComboBox<Integer> comboDuracion = new ComboBox<>();
        comboDuracion.getItems().addAll(90,120,150,180);
        comboDuracion.setValue(0);

        // --- Sección Fechas ---
        Label labelEstreno = crearLabelSeccion("Fecha de estreno:");
        Label labelSalida = crearLabelSeccion("Fecha de finalización:");

        GridPane gridFechas = new GridPane();
        gridFechas.setHgap(10);
        gridFechas.setVgap(10);
        gridFechas.addRow(0, labelEstreno, campoEstreno);
        gridFechas.addRow(1, labelSalida, campoSalida);

        VBox seccionFechas = new VBox(5, gridFechas, comboDuracion);
        seccionFechas.setAlignment(Pos.CENTER_LEFT);

        // --- Selección de imagen ---
        Button botonImagen = new Button("Seleccionar imagen");
        Label labelImagen = new Label("No se seleccionó ninguna imagen");
        labelImagen.setStyle("-fx-text-fill: #bbbbbb;");

        final File[] archivoSeleccionado = new File[1];
        botonImagen.setOnAction(e -> seleccionarImagen(ventana, archivoSeleccionado, labelImagen));

        VBox seccionImagen = new VBox(5, botonImagen, labelImagen);
        seccionImagen.setAlignment(Pos.CENTER_LEFT);

        // --- Botón Guardar ---
        Button botonGuardar = crearBotonVerde("Guardar película");
        botonGuardar.setOnAction(e -> {
            String nombre = campoNombre.getText().trim();
            String fechaEstrenoStr = campoEstreno.getText().trim();
            String fechaSalidaStr = campoSalida.getText().trim();
            Integer minutosSeleccionados = comboDuracion.getValue();
            Duration duracionSelecciona = Duration.ofMinutes(minutosSeleccionados);


            if (nombre.isEmpty() || fechaEstrenoStr.isEmpty() || fechaSalidaStr.isEmpty() || archivoSeleccionado[0] == null || duracionSelecciona.isZero()) {
                mostrarAlerta("Por favor, completa todos los campos y selecciona una imagen.");
                return;
            }

            try {
                DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate fechaEstreno = LocalDate.parse(fechaEstrenoStr, formato);
                LocalDate fechaSalida = LocalDate.parse(fechaSalidaStr, formato);

                String rutaImagen = guardarImagenPelicula(archivoSeleccionado[0]);
                Pelicula pelicula = new Pelicula(nombre, rutaImagen, fechaEstreno, fechaSalida, duracionSelecciona);

                GestorPeliculas.agregarPelicula(pelicula);
                mostrarAlerta("Película agregada correctamente.");
                ventana.close();

            } catch (Exception ex) {
                ex.printStackTrace();
                mostrarAlerta("Error al procesar las fechas o la imagen.");
            }

            ventana.close();
            ManejoVentanas.reiniciarGestorAdministrador(gestorFunciones,cliente);
        });

        VBox layout = crearLayout(titulo, campoNombre, seccionFechas, seccionImagen, botonGuardar);
        ventana.setScene(new Scene(layout, 500, 450));
        ventana.show();
    }


    private static Label crearTitulo(String texto) {
        Label label = new Label(texto);
        label.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        return label;
    }

    private static TextField crearCampoTexto(String placeholder) {
        TextField campo = new TextField();
        campo.setPromptText(placeholder);
        campo.setPrefWidth(250);
        return campo;
    }

    private static VBox crearSeccionFechas(TextField campoEstreno, TextField campoSalida) {
        Label labelEstreno = new Label("Fecha de estreno:");
        Label labelSalida = new Label("Fecha de finalización:");

        GridPane gridFechas = new GridPane();
        gridFechas.setHgap(10);
        gridFechas.setVgap(10);
        gridFechas.addRow(0, labelEstreno, campoEstreno);
        gridFechas.addRow(1, labelSalida, campoSalida);

        VBox seccion = new VBox(5, gridFechas);
        seccion.setAlignment(Pos.CENTER_LEFT);
        return seccion;
    }



    public static void formularioEliminarPelicula(GestorFunciones gestorFunciones,Cliente cliente) throws CamposIncompletosException, PeliculaInvalidaException {
        Stage ventana = crearVentana("Eliminar película");
        Label titulo = crearTitulo("Eliminar película");

        TextField campoPelicula = crearCampoTexto("Nombre de la película");

        Button botonEliminar = crearBotonVerde("Eliminar película");
        botonEliminar.setOnAction(e -> {
            String nombrePelicula = campoPelicula.getText().trim();
            try {
                if (nombrePelicula.isEmpty()) {
                    throw new CamposIncompletosException("Por favor, completa el campo con el nombre de la película.");
                }

                Pelicula pelicula = buscarPeliculaPorNombre(nombrePelicula);

                if (pelicula == null) {
                    throw new PeliculaInvalidaException("El nombre de la pelicula no coincide con ninguna de la cartelera");
                }

                GestorPeliculas.eliminarPelicula(pelicula);
                mostrarAlerta("Película eliminada correctamente.");

                ventana.close();
                ManejoVentanas.reiniciarGestorAdministrador(gestorFunciones,cliente);

            }catch (CamposIncompletosException | PeliculaInvalidaException ex){
                mostrarAlerta(ex.getMessage());
            }
        });

        VBox layout = crearLayout(titulo, campoPelicula, botonEliminar);
        ventana.setScene(new Scene(layout, 400, 200));
        ventana.show();
    }




    public static void formularioEditarPelicula(GestorFunciones gestorFunciones,Cliente cliente) throws PeliculaInvalidaException{
        Stage ventana = crearVentana("Modificar película");
        Label titulo = crearTitulo("Buscar película para modificar");

        TextField campoBusqueda = crearCampoTexto("Nombre de la película");

        Button botonBuscar = crearBotonVerde("Buscar película");
        botonBuscar.setOnAction(e -> {
            String nombreBuscado = campoBusqueda.getText().trim();
            if (nombreBuscado.isEmpty()) {
                mostrarAlerta("Por favor, ingresa el nombre de la película.");
                throw new PeliculaInvalidaException("Por favor, ingresa el nombre de la película.");
                //return;
            }

            Pelicula pelicula = buscarPeliculaPorNombre(nombreBuscado);
            if (pelicula == null) {
                mostrarAlerta("No se encontró ninguna película con ese nombre.");
                throw new PeliculaInvalidaException("No se encontró ninguna película con ese nombre.");
               // return;
            }

            editarPelicula(pelicula, gestorFunciones,cliente);
            ventana.close();
        });

        VBox layout = crearLayout(titulo, campoBusqueda, botonBuscar);
        ventana.setScene(new Scene(layout, 400, 250));
        ventana.show();
    }

    private static void editarPelicula(Pelicula p, GestorFunciones gestorFunciones,Cliente cliente) throws CamposIncompletosException, FechaInvalidaException {
        Stage ventana = crearVentana("Modificar película");
        Label titulo = crearTitulo("Modificar película");

        // --- Campos con datos actuales ---
        TextField campoNombre = crearCampoTexto(p.getNombrePelicula());
        campoNombre.setText(p.getNombrePelicula());
        TextField campoEstreno = crearCampoTexto(p.getFechaEstreno().toString());
        campoEstreno.setText(p.getFechaEstreno().toString());
        campoEstreno.setEditable(false);
        TextField campoSalida = crearCampoTexto(p.getFechaSalida().toString());
        campoSalida.setText(p.getFechaSalida().toString());
        campoSalida.setEditable(false);
        TextField campoDuracion = crearCampoTexto(String.valueOf(p.getDuracion().toMinutes()));
        campoDuracion.setText(String.valueOf(p.getDuracion().toMinutes()));
        campoDuracion.setEditable(false);

        // --- Imagen actual ---
        Label labelImagen = new Label("Imagen actual:");
        labelImagen.setStyle("-fx-text-fill: #ffcc00; -fx-font-weight: bold;");
        javafx.scene.image.ImageView imagenVista = new javafx.scene.image.ImageView();
        try {
            javafx.scene.image.Image imagenActual = new javafx.scene.image.Image(p.getRutaImagen(), 120, 180, true, true);
            imagenVista.setImage(imagenActual);
        } catch (Exception ex) {
            System.out.println("No se pudo cargar la imagen actual de la película.");
        }

        final String[] nuevaRutaImagen = {p.getRutaImagen()};

        Button botonCambiarImagen = crearBotonAzul("Cambiar imagen");
        botonCambiarImagen.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar nueva imagen de película");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
            File archivoSeleccionado = fileChooser.showOpenDialog(ventana);
            if (archivoSeleccionado != null) {
                try {
                    String rutaGuardada = guardarImagenPelicula(archivoSeleccionado);
                    nuevaRutaImagen[0] = rutaGuardada;

                    javafx.scene.image.Image nuevaImg = new javafx.scene.image.Image(rutaGuardada, 120, 180, true, true);
                    imagenVista.setImage(nuevaImg);
                } catch (Exception ex2) {
                    ex2.printStackTrace();
                    mostrarAlerta("Error al guardar la nueva imagen.");
                }
            }
        });

        // --- Botón guardar cambios ---
        Button botonGuardar = crearBotonVerde("Guardar cambios");
        botonGuardar.setOnAction(e -> {

            try {
                if (campoNombre.getText().isEmpty() || campoEstreno.getText().isEmpty() || campoSalida.getText().isEmpty()) {
                    throw new CamposIncompletosException("Por favor, complete todos los campos");

                }

                    DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate nuevaFechaEstreno = LocalDate.parse(campoEstreno.getText().trim(), formato);
                    LocalDate nuevaFechaSalida = LocalDate.parse(campoSalida.getText().trim(), formato);


                if(!compararFechas(nuevaFechaEstreno,nuevaFechaEstreno)) {
                    throw  new FechaInvalidaException("Por favor, verifique nuevamente las fechas");
                }

                String nombreAnterior = p.getNombrePelicula();
                String nuevoNombre = campoNombre.getText().trim();

                if (!nombreAnterior.equals(nuevoNombre)) {
                    GestorJsonAsientos.copiarArchivosAsientos(nombreAnterior, nuevoNombre, gestorFunciones);
                    for(Funcion funcion: gestorFunciones.getListaFunciones().getElementos()){
                        if(funcion.getPelicula().equals(p)){
                            funcion.getPelicula().setNombrePelicula(nuevoNombre);
                        }
                    }
                }

                p.setNombrePelicula(nuevoNombre);
                p.setFechaEstreno(nuevaFechaEstreno);
                p.setFechaSalida(nuevaFechaSalida);
                p.setRutaImagen(nuevaRutaImagen[0]);
                p.setDuracion(Duration.ofMinutes((long)Long.parseLong(campoDuracion.getText())));
                FuncionesJSON.serializarPeliculas(GestorPeliculas.getListaPeliculas());

            }catch (CamposIncompletosException | FechaInvalidaException ex){
                mostrarAlerta(ex.getMessage());
            }
                mostrarAlerta("Película modificada correctamente.");
                ventana.close();
                ManejoVentanas.reiniciarGestorAdministrador(gestorFunciones,cliente);

        });

        VBox layout = crearLayout(
                titulo,
                campoNombre,
                crearSeccionFechas(campoEstreno, campoSalida),
                campoDuracion,
                labelImagen,
                imagenVista,
                botonCambiarImagen,
                botonGuardar
        );

        ventana.setScene(new Scene(layout, 600, 600));
        ventana.show();
    }



    public static void formularioAgregar(List<Pelicula> listaPeliculas, GestorFunciones gestorFunciones, Cliente cliente) throws CamposIncompletosException {
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

                    LocalDateTime fechaInicialTime = LocalDateTime.parse(fechaInicial + " " + horario, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    LocalDateTime fechaFinalTime = LocalDateTime.parse(fechaFinal + " " + horario, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                    long diasDiferencia = ChronoUnit.DAYS.between(fechaInicialTime.toLocalDate(), fechaFinalTime.toLocalDate());

                    if(diasDiferencia < 0){
                        mostrarAlerta("Ingresá un rango de fechas válido");
                    }else {
                        LocalDateTime fechaAgregar = fechaInicialTime;

                        if (sala.equalsIgnoreCase("Sala 1") || sala.equalsIgnoreCase("Sala 2")) {
                            Pelicula peliculaSeleccionada = null;
                            for (Pelicula p : listaPeliculas) {
                                if (p.getNombrePelicula().equalsIgnoreCase(nombrePelicula)) {
                                    peliculaSeleccionada = p;
                                    break;
                                }
                            }

                            if (peliculaSeleccionada == null) {
                                mostrarAlerta("La película no fue encontrada.");
                                return;
                            }

                            Duration duracion = peliculaSeleccionada.getDuracion();

                            for (long i = 0; i <= diasDiferencia; i++) {
                                LocalDateTime inicio = fechaAgregar;
                                LocalDateTime fin = inicio.plus(duracion);

                                boolean seSuperpone = false;
                                for (Funcion f : gestorFunciones.getListaFunciones().getElementos()) {
                                    if (f.getSala().getNombreSala().equalsIgnoreCase(sala)) {
                                        LocalDateTime inicioExistente = f.getHorarioFuncion();
                                        LocalDateTime finExistente = inicioExistente.plus(f.getPelicula().getDuracion());

                                        // Si los horarios se solapan
                                        if (!(fin.isBefore(inicioExistente) || inicio.isAfter(finExistente))) {
                                            seSuperpone = true;
                                            break;
                                        }
                                    }
                                }

                                if (seSuperpone) {
                                    mostrarAlerta("Ya existe una función en la misma sala que se superpone con el horario.");
                                    return;
                                }

                                Funcion funcion = new Funcion(sala, nombrePelicula, inicio, listaPeliculas, precio, gestorFunciones);
                                gestorFunciones.agregarFuncion(funcion);
                                fechaAgregar = fechaAgregar.plusDays(1);
                            }

                            FuncionesJSON.serializarFunciones(gestorFunciones.getListaFunciones().getElementos());
                            mostrarAlerta("Funciones agregadas correctamente.");


                        } else {
                            String desc = "Sala inválida";
                            throw new CamposIncompletosException(desc);
                        }
                    }
                    ventana.close();
                    ManejoVentanas.reiniciarGestorAdministrador(gestorFunciones,cliente);
                } catch (DateTimeParseException ex) {
                    mostrarAlerta("Formato de fecha u hora incorrecto");
                    ventana.close();
                    ManejoVentanas.reiniciarGestorAdministrador(gestorFunciones,cliente);
                }catch (Exception ex){
                    mostrarAlerta("Error en la carga de datos");
                    ventana.close();
                    ManejoVentanas.reiniciarGestorAdministrador(gestorFunciones,cliente);
                }

            }

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


    public static void formularioEliminarFuncion(GestorFunciones gestorFunciones,Cliente cliente){
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
    public static boolean compararFechas(LocalDate fecha1, LocalDate fecha2) {
        return !fecha1.isAfter(fecha2);
    }



    private static Stage crearVentana(String titulo) {
        Stage stage = new Stage();
        stage.setTitle(titulo);
        return stage;
    }


    private static Pelicula buscarPeliculaPorNombre(String nombre) {
        for (Pelicula p : GestorPeliculas.getListaPeliculas()) {
            if (p.getNombrePelicula().equalsIgnoreCase(nombre)) {
                return p;
            }
        }
        return null;
    }


    private static Button crearBotonVerde(String texto) {
        Button boton = new Button(texto);
        boton.setStyle("-fx-background-color: #228B22; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-background-radius: 8; -fx-padding: 8 20 8 20;");
        return boton;
    }

    private static Button crearBotonAzul(String texto) {
        Button boton = new Button(texto);
        boton.setStyle("-fx-background-color: #4169E1; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-background-radius: 8;");
        return boton;
    }



    public static void seleccionarImagen(Stage ventana, File[] archivoSeleccionado, Label labelImagen) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen de la película");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));

        File archivo = fileChooser.showOpenDialog(ventana);
        if (archivo != null) {
            archivoSeleccionado[0] = archivo;
            labelImagen.setText(archivo.getName());
        }
    }

    private static Label crearLabelSeccion(String texto) {
        Label label = new Label(texto);
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #ffcc00;");
        return label;
    }

    private static VBox crearLayout(javafx.scene.Node... elementos) {
        VBox box = new VBox(20, elementos);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: #2a2a2a;");
        return box;
    }

}
