package Clases.GestionDePagos;

import Clases.GestionFunciones.Funcion;
import Clases.login.usuario.Cliente;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Reserva {
    private String numeroTicket;
    private String codigoOR;
    private String metodoPago;
    private double monto;
    private List<String> asientosSeleccionados;
    private String nombrePelicula;
    private String horarioFuncion;
    private String salaNombre;
    private LocalDateTime fechaEmision;
    private Funcion funcion;
    private String clienteEmail;
    private String clienteNombre;

    // Constructor con Cliente
   public Reserva(Cliente cliente, Funcion funcion, String metodoPago, double monto) {
        this(cliente, funcion, metodoPago, monto, List.of());
    }



    public Reserva(Cliente cliente, Funcion funcion, String metodoPago, double monto, List<String> asientosSeleccionados) {
        this.funcion = funcion;
        this.metodoPago = metodoPago;
        this.monto = monto;
        this.asientosSeleccionados = asientosSeleccionados;
        this.fechaEmision = LocalDateTime.now();

        if (cliente != null) {
            this.clienteEmail = cliente.getEmail() != null ? cliente.getEmail() : "cliente@cinemax.com";

            String nombre = cliente.getNombre() != null ? cliente.getNombre() : "Cliente";
            String apellido = cliente.getApellido() != null ? cliente.getApellido() : "Cinemax";
            this.clienteNombre = nombre + " " + apellido;
        } else {
            this.clienteEmail = "cliente@cinemax.com";
            this.clienteNombre = "Cliente Cinemax";
        }

        // Inicializar datos de la función
        if (funcion != null) {
            this.nombrePelicula = funcion.getPelicula().getNombrePelicula();
            this.horarioFuncion = funcion.getHorarioFuncion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            this.salaNombre = funcion.getSala() != null ? funcion.getSala().getNombreSala() : "Sala Principal";
        }

        // Generar números de ticket y código OR
        this.numeroTicket = generarNumeroTicket();
        this.codigoOR = "OR-CMX-" + this.numeroTicket.replace("TK", "");
    }

    public Reserva(Funcion funcion, String metodoPago, double monto, List<String> asientosSeleccionados) {
        this(null, funcion, metodoPago, monto, asientosSeleccionados);
    }

    // generar número de ticket
    private String generarNumeroTicket() {
        return "TK" + System.currentTimeMillis() + String.format("%03d", (int)(Math.random() * 1000));
    }

    // Getters
    public String getNumeroTicket() { return numeroTicket; }
    public String getCodigoOR() { return codigoOR; }
    public String getMetodoPago() { return metodoPago; }
    public double getMonto() { return monto; }
    public List<String> getAsientosSeleccionados() { return asientosSeleccionados; }
    public String getNombrePelicula() { return nombrePelicula; }
    public String getHorarioFuncion() { return horarioFuncion; }
    public String getSalaNombre() { return salaNombre; }
    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public Funcion getFuncion() { return funcion; }
    public String getClienteEmail() { return clienteEmail; }
    public String getClienteNombre() { return clienteNombre; }


    public String getAsientosComoString() {
        return asientosSeleccionados.isEmpty() ? "Por asignar" : String.join(", ", asientosSeleccionados);
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "numeroTicket='" + numeroTicket + '\'' +
                ", cliente='" + clienteEmail + '\'' +
                ", pelicula='" + nombrePelicula + '\'' +
                ", horario='" + horarioFuncion + '\'' +
                ", asientos=" + asientosSeleccionados +
                ", monto=" + monto +
                '}';
    }
}