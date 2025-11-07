package Clases.GestionDePagos;


public class MetodoDePago {
    private int id;
    private String nombre;

    //CONSTRUCTOR
    public MetodoDePago(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    //GETTERS Y SETTERS
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


//TO STRING
    @Override
    public String toString() {
        return "MetodoDePago : " + nombre + '\n';
    }

}
