package ManejoJSON;

import Clases.*;
import Clases.GestionDePagos.Pago;
import Clases.login.GestorUsuarios;
import Clases.login.usuario.Administrador;
import Clases.login.usuario.Cliente;
import Clases.login.usuario.Usuario;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FuncionesJSON {

    public static void serializarFunciones(List<Funcion> listaFunciones){
        JSONArray jsonFunciones = new JSONArray();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            try {
                if(!listaFunciones.isEmpty()) {
                    for (int i = 0; i < listaFunciones.size(); i++) {
                       // System.out.println(listaFunciones.get(i));
                        JSONObject jsonFuncion = new JSONObject();
                        Funcion funcion = listaFunciones.get(i);
                      //  System.out.println(funcion.getSala().getNombreSala()); descartamos aca NO NULL EL GETTER
                        jsonFuncion.put("Sala", funcion.getSala().getNombreSala());
                        jsonFuncion.put("Pelicula", funcion.getPelicula().getNombrePelicula());
                        jsonFuncion.put("Fecha y hora", funcion.getHorarioFuncion().format(formato));
                        jsonFuncion.put("Precio", funcion.getPrecio());

                        jsonFunciones.put(jsonFuncion);
                    }

                    JSONUtiles.grabar(jsonFunciones, "funciones.json");
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }



    }



    public static List<Funcion> deserializarFunciones(List<Pelicula> listaPeliculas, List<SalaCine> listaSalas, GestorFunciones gestorFunciones) {
        List<Funcion> listaFunciones = new ArrayList<>();

        try {
            JSONArray jsonFunciones = new JSONArray(JSONUtiles.leer("funciones.json"));
            if (jsonFunciones == null) {
                System.out.println("⚠️ No hay funciones guardadas en el JSON.");
                return listaFunciones;
            }

            for (int i = 0; i < jsonFunciones.length(); i++) {
                JSONObject obj = jsonFunciones.getJSONObject(i);

                String nombreSala = obj.getString("Sala");
                String nombrePelicula = obj.getString("Pelicula");
                String fechaHoraStr = obj.getString("Fecha y hora");
                double precioFuncion = obj.getDouble("Precio");

                SalaCine salaEncontrada = buscarSalaPorNombre(listaSalas, nombreSala);
                //System.out.println(salaEncontrada); DESCARTAMOS ACA TAMBIEN, LA SALA LA ENCUENTRA
                Pelicula peliculaEncontrada = buscarPeliculaPorNombre(listaPeliculas, nombrePelicula);
                DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                if (salaEncontrada != null && peliculaEncontrada != null) {
                    LocalDateTime fechaHora = LocalDateTime.parse(fechaHoraStr, formato);
                    Funcion f = new Funcion(salaEncontrada, peliculaEncontrada, fechaHora, precioFuncion, gestorFunciones);
                    listaFunciones.add(f);
                   // gestorFunciones.agregarFuncion(f);
                } else {
                    System.out.println("⚠️ No se encontró coincidencia para: " + nombrePelicula + " / " + nombreSala);
                }
            }

            //GestorFunciones.setListaFunciones(new ListaGenerica<>(listaFunciones));

        } catch (Exception e) {
            System.out.println("❌ Error al deserializar funciones: " + e.getMessage());
        }

        return listaFunciones;
    }

    // 🔸 Métodos auxiliares de búsqueda
    private static Pelicula buscarPeliculaPorNombre(List<Pelicula> lista, String nombre) {
        for (Pelicula p : lista) {
            if (p.getNombrePelicula().equalsIgnoreCase(nombre)) {
                return p;
            }
        }
        return null;
    }

    private static SalaCine buscarSalaPorNombre(List<SalaCine> lista, String nombre) {
        for (SalaCine s : lista) {
            if (s.getNombreSala().equalsIgnoreCase(nombre)) {
                return s;
            }
        }
        return null;
    }




    private static final String RUTA_JSON = "peliculas.json";

    public static void serializarPeliculas(List<Pelicula> listaPeliculas) {
        JSONArray jsonPeliculas = new JSONArray();
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            for (Pelicula p : listaPeliculas) {
                JSONObject jsonPelicula = new JSONObject();
                jsonPelicula.put("Nombre", p.getNombrePelicula());
                jsonPelicula.put("RutaImagen", p.getRutaImagen());
                jsonPelicula.put("FechaEstreno", p.getFechaEstreno().format(formatoFecha));
                jsonPelicula.put("FechaSalida", p.getFechaSalida().format(formatoFecha));

                jsonPeliculas.put(jsonPelicula);
            }

            JSONUtiles.grabar(jsonPeliculas, RUTA_JSON);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static List<Pelicula> deserializarPeliculas() {
        List<Pelicula> listaPeliculas = new ArrayList<>();
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            JSONArray jsonPeliculas = new JSONArray(JSONUtiles.leer(RUTA_JSON));

            for (int i = 0; i < jsonPeliculas.length(); i++) {
                JSONObject obj = jsonPeliculas.getJSONObject(i);

                String nombre = obj.getString("Nombre");
                String rutaImagen = obj.getString("RutaImagen");
                LocalDate fechaEstreno = LocalDate.parse(obj.getString("FechaEstreno"), formatoFecha);
                LocalDate fechaSalida = LocalDate.parse(obj.getString("FechaSalida"), formatoFecha);

                Pelicula p = new Pelicula(nombre, rutaImagen, fechaEstreno, fechaSalida);
                listaPeliculas.add(p);
            }

            GestorPeliculas.setListaPeliculas(listaPeliculas);
        } catch (Exception e) {
            System.out.println("❌ Error al deserializar películas: " + e.getMessage());
        }

        return listaPeliculas;
    }



    public static List<Usuario> deserializarUsuarios() {
        List<Usuario> listaUsuarios = new ArrayList<>();

        try {
            JSONObject raiz = new JSONObject(JSONUtiles.leer("usuarios.json"));
            JSONArray jsonUsuarios = raiz.getJSONArray("data");

            for (int i = 0; i < jsonUsuarios.length(); i++) {
                JSONObject obj = jsonUsuarios.getJSONObject(i);

                String tipoUsuario = obj.getString("tipoUsuario");
                String nombre = obj.getString("nombre");
                String apellido = obj.getString("apellido");
                String email = obj.getString("email");
                String password = obj.getString("password");
                String telefono = obj.getString("telefono");
                String estado = obj.optString("estado", "ACTIVO");

                LocalDateTime fechaRegistro = LocalDateTime.parse(
                        obj.optString("fechaRegistro", LocalDateTime.now().toString())
                );
                LocalDateTime fechaUltimoAcceso = LocalDateTime.parse(
                        obj.optString("fechaUltimoAcceso", LocalDateTime.now().toString())
                );

                int intentosFallidos = obj.optInt("intentosFallidos", 0);

                Usuario usuario = null;

                if (tipoUsuario.equalsIgnoreCase("ADMINISTRADOR")) {
                    String nivelAcceso = obj.optString("nivelAcceso", "NORMAL");
                    usuario = new Administrador(nombre, apellido, email, password, telefono,
                            estado, fechaRegistro, fechaUltimoAcceso, intentosFallidos, nivelAcceso);
                } else if (tipoUsuario.equalsIgnoreCase("CLIENTE")) {
                    int puntosFidelidad = obj.optInt("puntosFidelidad", 0);
                    usuario = new Cliente(nombre, apellido, email, password, telefono,
                            estado, fechaRegistro, fechaUltimoAcceso, intentosFallidos, puntosFidelidad);
                }

                if (usuario != null) {
                    listaUsuarios.add(usuario);
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Error al deserializar usuarios: " + e.getMessage());
        }

        return listaUsuarios;
    }

    public static void serializarUsuarios(List<Usuario> listaUsuarios) {
        JSONArray dataArray = new JSONArray();
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        try {
            for (Usuario u : listaUsuarios) {
                JSONObject obj = new JSONObject();

                try {
                    obj.put("nombre", u.getNombre());
                    obj.put("apellido", u.getApellido());
                    obj.put("email", u.getEmail());
                    obj.put("password", u.getPassword());
                    obj.put("telefono", u.getTelefono());
                    obj.put("estado", u.getEstado());
                    obj.put("fechaRegistro", u.getFechaRegistro().format(formatoFecha));
                    obj.put("fechaUltimoAcceso", u.getFechaUltimoAcceso().format(formatoFecha));
                    obj.put("intentosFallidos", u.getIntentosFallidos());
                    obj.put("tipoUsuario", u.getTipoUsuario().toString());

                    if (u instanceof Administrador admin) {
                        obj.put("nivelAcceso", admin.getNivelAcceso());
                    } else if (u instanceof Cliente cli) {
                        obj.put("puntosFidelidad", cli.getPuntosFidelidad());
                    }

                    dataArray.put(obj);

                } catch (Exception ex) {
                    System.out.println("⚠️ Error al procesar usuario " + u.getEmail() + ": " + ex.getMessage());
                }
            }

            JSONObject raiz = new JSONObject();
            raiz.put("data", dataArray);
            raiz.put("ultimaActualizacion", LocalDateTime.now().toString());
            raiz.put("totalElementos", listaUsuarios.size());

            JSONUtiles.grabar(raiz, "usuarios.json");
            System.out.println("💾 Usuarios guardados correctamente en usuarios.json");

        } catch (Exception e) {
            System.out.println("❌ Error al serializar usuarios: " + e.getMessage());
        }
    }


    public static void serializarPagos(List<Pago> listaPagos) {
        JSONArray jsonPagos = new JSONArray();

        try {
            for (Pago pago : listaPagos) {
                JSONObject jsonPago = new JSONObject();
                jsonPago.put("ID", pago.getIdPago());
                jsonPago.put("Monto", pago.getMonto());
                jsonPago.put("Fecha", pago.getFechaPago().toString());
                jsonPago.put("Metodo", pago.getMetodoPago());

                jsonPagos.put(jsonPago);
            }

            JSONUtiles.grabar(jsonPagos, "pagos.json");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
