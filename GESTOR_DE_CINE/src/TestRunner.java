import Clases.*;
import java.time.LocalDateTime;

public class TestRunner {
    public static void main(String[] args) {
        System.out.println("Iniciando TestRunner...");

        //agregar 4 peliculas y funciones

        Pelicula p1 = new Pelicula("pelicula1");


        Sala s1 = new Sala("Sala 1", 200);

        LocalDateTime horario = LocalDateTime.of(2025, 10, 15, 18, 30);
        Funcion f1 = new Funcion(s1, p1, horario);

        int total = GestorFunciones.getListaFunciones().size();
        System.out.println("Funciones registradas en GestorFunciones: " + total);

        for (Funcion f : GestorFunciones.getListaFunciones()) {
            System.out.println("Funcion -> Pelicula: " + f.getPelicula().getNombrePelicula() + ", Horario: " + f.getHorarioFuncion());
        }

        // Reemplazo de la llamada a VistaCartelera por un conteo directo
        int encontrados = 0;
        for (Funcion f : GestorFunciones.getListaFunciones()) {
            if (f.getPelicula() != null && "pelicula1".equals(f.getPelicula().getNombrePelicula())) {
                encontrados++;
            }
        }
        System.out.println("Buscar por nombre 'pelicula1' -> encontrado: " + encontrados);

        System.out.println("TestRunner finalizado.");
    }
}
