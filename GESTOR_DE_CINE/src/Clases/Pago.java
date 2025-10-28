package Clases;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Pago {
    private int id;
    private double monto;
    private MetodoDePago metodoDePago;
    private String fechaPago;
    private int idReserva;

    public Pago(int id, double monto, MetodoDePago metodoDePago, String fechaPago, int idReserva) {
        this.id = id;
        this.monto = monto;
        this.metodoDePago = metodoDePago;
        this.fechaPago = fechaPago;
        this.idReserva = idReserva;
    }

    // ✅ NUEVO: Constructor simplificado para uso en SelectorAsientos
    public Pago(MetodoDePago metodoDePago, double monto, String descripcion) {
        this.id = generarIdPago();
        this.monto = monto;
        this.metodoDePago = metodoDePago;
        this.fechaPago = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        this.idReserva = generarIdReserva(); // Puedes ajustar esto según tu lógica de reservas
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public MetodoDePago getMetodoDePago() {
        return metodoDePago;
    }

    public void setMetodoDePago(MetodoDePago metodoDePago) {
        this.metodoDePago = metodoDePago;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    // ✅ CORREGIDO: Métodos que devolvían boolean ahora devuelven lo correcto
    public int getIdPago() {
        return this.id;
    }

    public MetodoDePago getMetodoPago() {
        return this.metodoDePago;
    }

    // ✅ NUEVO: Método para generar ID único
    private int generarIdPago() {
        return (int) (System.currentTimeMillis() % 1000000);
    }

    // ✅ NUEVO: Método para generar ID de reserva
    private int generarIdReserva() {
        return (int) (System.currentTimeMillis() % 100000) + 1000;
    }

    // ✅ NUEVO: Método toString para mejor visualización
    @Override
    public String toString() {
        return String.format(
                "Pago [ID: %d, Monto: $%,.2f, Método: %s, Fecha: %s, Reserva: %d]",
                id, monto, metodoDePago.getNombre(), fechaPago, idReserva
        );
    }
}

//package Clases;
//import Clases.MetodoDePago;
//
//;
//
////crear la clase Pago con los atributos: id, monto, metodoDePago, fechaPago, idReserva
//public class Pago {
//    private int id;
//    private double monto;
//    private MetodoDePago metodoDePago;
//    private String fechaPago;
//    private int idReserva;
//
//    public Pago(int id, double monto, MetodoDePago metodoDePago, String fechaPago, int idReserva) {
//        this.id = id;
//        this.monto = monto;
//        this.metodoDePago = metodoDePago;
//        this.fechaPago = fechaPago;
//        this.idReserva = idReserva;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public double getMonto() {
//        return monto;
//    }
//
//    public void setMonto(double monto) {
//        this.monto = monto;
//    }
//
//    public MetodoDePago getMetodoDePago() {
//        return metodoDePago;
//    }
//
//    public void setMetodoDePago(MetodoDePago metodoDePago) {
//        this.metodoDePago = metodoDePago;
//    }
//
//    public String getFechaPago() {
//        return fechaPago;
//    }
//
//    public void setFechaPago(String fechaPago) {
//        this.fechaPago = fechaPago;
//    }
//
//    public int getIdReserva() {
//        return idReserva;
//    }
//
//    public void setIdReserva(int idReserva) {
//        this.idReserva = idReserva;
//    }
//
//
//    public boolean getIdPago() {
//        return false;
//
//    }
//
//    public boolean getMetodoPago() {
//
//        return false;
//    }
//}