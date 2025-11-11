package Interfaces;

import Clases.Funcion;
import org.json.JSONObject;

import java.util.List;

public interface ConversorJson{

    void crearCarpetaJSON();
    void inicializarArchivo();
    boolean cargarEstadoGuardado();
    void guardarEstadoCompleto();
    int confirmarSelecciones();
    JSONObject generarReporte();
}
