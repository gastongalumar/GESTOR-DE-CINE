package Clases;
import Clases.MetodoDePago;

;

//crear la clase Pago con los atributos: id, monto, metodoDePago, fechaPago, idReserva
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


    public boolean getIdPago() {
        return false;

    }

}