package ManejoJSON;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JSONUtiles {

    public static void grabar(JSONArray array, String archivo) {
        try {
            FileWriter file = new FileWriter(archivo);
            file.write(array.toString(4));
            file.flush();
            file.close();
            System.out.println("💾 JSONArray guardado en: " + archivo);
        } catch (IOException e) {
            System.err.println("❌ Error al guardar JSONArray: " + e.getMessage());
            e.printStackTrace();
        } catch (JSONException e) {
            System.err.println("❌ Error de JSON al guardar array: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void grabar(JSONObject obj, String archivo) {
        try {
            FileWriter file = new FileWriter(archivo);
            file.write(obj.toString(4));
            file.flush();
            file.close();
            System.out.println("💾 JSONObject guardado en: " + archivo);
        } catch (IOException e) {
            System.err.println("❌ Error al guardar JSONObject: " + e.getMessage());
            e.printStackTrace();
        } catch (JSONException e) {
            System.err.println("❌ Error de JSON al guardar objeto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static JSONTokener leer(String archivo) {
        try {
            return new JSONTokener(new FileReader(archivo));
        } catch (FileNotFoundException e) {
            System.err.println("📁 Archivo no encontrado: " + archivo);
            return null;
        }
    }

    public static JSONObject leerObject(String archivo) {
        try {
            JSONTokener tokener = new JSONTokener(new FileReader(archivo));
            return new JSONObject(tokener);
        } catch (FileNotFoundException e) {
            System.err.println("📁 Archivo no encontrado: " + archivo);
            return null;
        } catch (JSONException e) {
            System.err.println("❌ Error de JSON al leer objeto: " + e.getMessage());
            return null;
        }
    }

    public static JSONArray leerArray(String archivo) {
        try {
            JSONTokener tokener = new JSONTokener(new FileReader(archivo));
            return new JSONArray(tokener);
        } catch (FileNotFoundException e) {
            System.err.println("📁 Archivo no encontrado: " + archivo);
            return null;
        } catch (JSONException e) {
            System.err.println("❌ Error de JSON al leer array: " + e.getMessage());
            return null;
        }
    }
}