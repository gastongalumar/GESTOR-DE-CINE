package Interfaces;

import org.json.JSONObject;

public interface ConversorJson{

    void crearCarpetaJSON();
    void inicializarArchivo();
    boolean cargarEstadoGuardado();
    void guardarEstadoCompleto();
    int confirmarSelecciones();
    JSONObject generarReporte();
}
