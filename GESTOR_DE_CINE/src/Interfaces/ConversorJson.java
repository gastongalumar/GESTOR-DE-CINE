package Interfaces;

import org.json.JSONObject;

public interface ConversorJson<T>{
    T desdeJson(JSONObject jsonUsuario);
    JSONObject aJson(T objeto);
}
