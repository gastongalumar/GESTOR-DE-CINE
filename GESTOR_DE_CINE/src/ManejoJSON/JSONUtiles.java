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

    /**
     * Graba un JSONArray en un archivo
     */
    public static void grabar(JSONArray array, String archivo) {
        try {
            FileWriter file = new FileWriter(archivo);
            try {
                file.write(array.toString(4)); // Formato con indentación
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            file.flush();
            file.close();
            System.out.println("💾 JSONArray guardado en: " + archivo);
        } catch (IOException e) {
            System.err.println("❌ Error al guardar JSONArray: " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Graba un JSONObject en un archivo
     */
    public static void grabar(JSONObject obj, String archivo) {
        try {
            FileWriter file = new FileWriter(archivo);
            try {
                file.write(obj.toString(4)); // Formato con indentación
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            file.flush();
            file.close();
            System.out.println("💾 JSONObject guardado en: " + archivo);
        } catch (IOException e) {
            System.err.println("❌ Error al guardar JSONObject: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Lee un archivo JSON y retorna un JSONTokener
     */
    public static JSONTokener leer(String archivo) {
        try {
            return new JSONTokener(new FileReader(archivo));
        } catch (FileNotFoundException e) {
            System.err.println("📁 Archivo no encontrado: " + archivo);
            return null;
        }
    }

    /**
     * Lee un archivo JSON y retorna un JSONObject
     */
    public static JSONObject leerObject(String archivo) {
        try {
            JSONTokener tokener = new JSONTokener(new FileReader(archivo));
            try {
                return new JSONObject(tokener);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        } catch (FileNotFoundException e) {
            System.err.println("📁 Archivo no encontrado: " + archivo);
            return null;
        }
    }

    /**
     * Lee un archivo JSON y retorna un JSONArray
     */
    public static JSONArray leerArray(String archivo) {
        try {
            JSONTokener tokener = new JSONTokener(new FileReader(archivo));
            try {
                return new JSONArray(tokener);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        } catch (FileNotFoundException e) {
            System.err.println("📁 Archivo no encontrado: " + archivo);
            return null;
        }
    }
}

//package ManejoJSON;
//
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.json.JSONTokener;
//
//public class JSONUtiles {
//
//    /**
//     * Graba un JSONArray en un archivo
//     */
//    public static void grabar(JSONArray array, String archivo) {
//        try {
//            FileWriter file = new FileWriter(archivo);
//            try {
//                file.write(array.toString(4)); // Formato con indentación
//            } catch (JSONException e) {
//                throw new RuntimeException(e);
//            }
//            file.flush();
//            file.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Graba un JSONObject en un archivo
//     */
//    public static void grabar(JSONObject obj, String archivo) {
//        try {
//            FileWriter file = new FileWriter(archivo);
//            try {
//                file.write(obj.toString(4)); // Formato con indentación
//            } catch (JSONException e) {
//                throw new RuntimeException(e);
//            }
//            file.flush();
//            file.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Lee un archivo JSON y retorna un JSONTokener
//     */
//    public static JSONTokener leer(String archivo) {
//        try {
//            return new JSONTokener(new FileReader(archivo));
//        } catch (FileNotFoundException e) {
//            System.err.println("Archivo no encontrado: " + archivo);
//            return null;
//        }
//    }
//
//    /**
//     * Lee un archivo JSON y retorna un JSONObject
//     */
//    public static JSONObject leerObject(String archivo) {
//        try {
//            JSONTokener tokener = new JSONTokener(new FileReader(archivo));
//            try {
//                return new JSONObject(tokener);
//            } catch (JSONException e) {
//                throw new RuntimeException(e);
//            }
//        } catch (FileNotFoundException e) {
//            System.err.println("Archivo no encontrado: " + archivo);
//            return null;
//        }
//    }
//
//    /**
//     * Lee un archivo JSON y retorna un JSONArray
//     */
//    public static JSONArray leerArray(String archivo) {
//        try {
//            JSONTokener tokener = new JSONTokener(new FileReader(archivo));
//            try {
//                return new JSONArray(tokener);
//            } catch (JSONException e) {
//                throw new RuntimeException(e);
//            }
//        } catch (FileNotFoundException e) {
//            System.err.println("Archivo no encontrado: " + archivo);
//            return null;
//        }
//    }
//}