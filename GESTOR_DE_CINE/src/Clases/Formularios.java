package Clases;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static Clases.GestorAdministrador.guardarImagenPelicula;
import static Clases.GestorAdministrador.mostrarAlerta;

public class Formularios {

    public static void formularioAgregarPelicula() {
        Stage ventana = crearVentana("Agregar nueva película");
        Label titulo = crearTitulo("Agregar nueva película");

        // --- Campos de texto ---
        TextField campoNombre = crearCampoTexto("Nombre de la película");
        TextField campoEstreno = crearCampoTexto("Ej: 2025-10-15");
        TextField campoSalida = crearCampoTexto("Ej: 2025-12-31");

        // --- Sección Fechas ---
        Label labelEstreno = crearLabelSeccion("Fecha de estreno:");
        Label labelSalida = crearLabelSeccion("Fecha de finalización:");

        GridPane gridFechas = new GridPane();
        gridFechas.setHgap(10);
        gridFechas.setVgap(10);
        gridFechas.addRow(0, labelEstreno, campoEstreno);
        gridFechas.addRow(1, labelSalida, campoSalida);

        VBox seccionFechas = new VBox(5, gridFechas);
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

            if (nombre.isEmpty() || fechaEstrenoStr.isEmpty() || fechaSalidaStr.isEmpty() || archivoSeleccionado[0] == null) {
                mostrarAlerta("Por favor, completa todos los campos y selecciona una imagen.");
                return;
            }

            try {
                DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate fechaEstreno = LocalDate.parse(fechaEstrenoStr, formato);
                LocalDate fechaSalida = LocalDate.parse(fechaSalidaStr, formato);

                String rutaImagen = guardarImagenPelicula(archivoSeleccionado[0]);
                Pelicula pelicula = new Pelicula(nombre, rutaImagen, fechaEstreno, fechaSalida);

                GestorPeliculas.agregarPelicula(pelicula);
                mostrarAlerta("Película agregada correctamente.");
                ventana.close();

            } catch (Exception ex) {
                ex.printStackTrace();
                mostrarAlerta("Error al procesar las fechas o la imagen.");
            }

            ventana.close();
            ManejoVentanas.reiniciarGestorAdministrador();
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

    private static TextField crearCampoTexto(String placeholder, int ancho) {
        TextField campo = new TextField();
        campo.setPromptText(placeholder);
        campo.setPrefWidth(ancho);
        return campo;
    }

    private static TextField crearCampoTexto(String placeholder) {
        TextField campo = new TextField();
        campo.setPromptText(placeholder);
        campo.setPrefWidth(250);
        return campo;
    }

    private static VBox crearSeccionConCampo(Label label, TextField campo) {
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #ffcc00;");
        VBox seccion = new VBox(5, label, campo);
        seccion.setAlignment(Pos.CENTER_LEFT);
        return seccion;
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

    private static VBox crearSeccionImagen(Stage ventana) {
        Button botonImagen = new Button("Seleccionar imagen");
        Label labelImagen = new Label("No se seleccionó ninguna imagen");
        labelImagen.setStyle("-fx-text-fill: #bbbbbb;");

        final File[] archivoSeleccionado = new File[1];
        botonImagen.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar imagen de la película");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));

            File archivo = fileChooser.showOpenDialog(ventana);
            if (archivo != null) {
                archivoSeleccionado[0] = archivo;
                labelImagen.setText(archivo.getName());
            }
        });

        VBox seccion = new VBox(5, botonImagen, labelImagen);
        seccion.setAlignment(Pos.CENTER_LEFT);
        seccion.setUserData(archivoSeleccionado); // guardamos el archivo para usarlo despues
        return seccion;
    }

    private static Button crearBotonGuardarPelicula(Stage ventana, TextField campoNombre, TextField campoEstreno, TextField campoSalida, VBox seccionImagen) {
        Button boton = new Button("Guardar película");
        boton.setStyle("""
        -fx-background-color: #228B22;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-background-radius: 8;
        -fx-padding: 8 20 8 20;
    """);

        boton.setOnAction(e -> {
            String nombre = campoNombre.getText().trim();
            String fechaEstrenoStr = campoEstreno.getText().trim();
            String fechaSalidaStr = campoSalida.getText().trim();
            File[] archivoSeleccionado = (File[]) seccionImagen.getUserData();

            if (nombre.isEmpty() || fechaEstrenoStr.isEmpty() || fechaSalidaStr.isEmpty() || archivoSeleccionado[0] == null) {
                mostrarAlerta("Por favor, completa todos los campos y selecciona una imagen.");
                return;
            }

            try {
                DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate fechaEstreno = LocalDate.parse(fechaEstrenoStr, formato);
                LocalDate fechaSalida = LocalDate.parse(fechaSalidaStr, formato);

                String rutaImagen = guardarImagenPelicula(archivoSeleccionado[0]);
                Pelicula pelicula = new Pelicula(nombre, rutaImagen, fechaEstreno, fechaSalida);

                GestorPeliculas.agregarPelicula(pelicula);
                mostrarAlerta("Película agregada correctamente.");
                ventana.close();

            } catch (Exception ex) {
                ex.printStackTrace();
                mostrarAlerta("Error al procesar las fechas o la imagen.");
            }

            ManejoVentanas.reiniciarGestorAdministrador();
        });

        return boton;
    }






    public static void formularioEliminarPelicula() {
        Stage ventana = crearVentana("Eliminar película");
        Label titulo = crearTitulo("Eliminar película");

        TextField campoPelicula = crearCampoTexto("Nombre de la película");

        Button botonEliminar = crearBotonVerde("Eliminar película");
        botonEliminar.setOnAction(e -> {
            String nombrePelicula = campoPelicula.getText().trim();
            if (nombrePelicula.isEmpty()) {
                mostrarAlerta("Por favor, completa el campo con el nombre de la película.");
                return;
            }

            Pelicula pelicula = buscarPeliculaPorNombre(nombrePelicula);

            if (pelicula == null) {
                mostrarAlerta("No se encontró ninguna película con ese nombre.");
                return;
            }

            // Eliminamos funciones relacionadas (comentado en tu versión original)
            // GestorFunciones.getListaFunciones().removeIf(f -> f.getPelicula().equals(pelicula));
            // FuncionesJSON.serializarFunciones(GestorFunciones.getListaFunciones());

            GestorPeliculas.eliminarPelicula(pelicula);
            mostrarAlerta("Película eliminada correctamente.");

            ventana.close();
            ManejoVentanas.reiniciarGestorAdministrador();
        });

        VBox layout = crearLayout(titulo, campoPelicula, botonEliminar);
        ventana.setScene(new Scene(layout, 400, 200));
        ventana.show();
    }



    // ----------------- Helper botón eliminar -----------------
    private static Button crearBotonEliminarPelicula(Stage ventana, TextField campoPelicula) {
        Button boton = new Button("Eliminar película");
        boton.setStyle("""
        -fx-background-color: #228B22;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-background-radius: 8;
        -fx-padding: 8 20 8 20;
    """);

        boton.setOnAction(e -> {
            String nombrePelicula = campoPelicula.getText().trim();
            if(nombrePelicula.isEmpty()){
                mostrarAlerta("Por favor, completa el campo con el nombre de la película.");
                return;
            }

            Pelicula pelicula = null;
            for(Pelicula p : GestorPeliculas.getListaPeliculas()){
                if(p.getNombrePelicula().equalsIgnoreCase(nombrePelicula)){
                    pelicula = p;
                    break;
                }
            }

            if(pelicula == null){
                mostrarAlerta("No se encontró ninguna película con ese nombre.");
                return;
            }

            // Eliminar película
            GestorPeliculas.eliminarPelicula(pelicula);

            mostrarAlerta("Película eliminada correctamente.");
            ventana.close();
            ManejoVentanas.reiniciarGestorAdministrador();
        });

        return boton;
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

    public static Button crearBotonRojo(String texto) {
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
