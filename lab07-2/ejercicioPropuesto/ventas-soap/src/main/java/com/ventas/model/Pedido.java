package com.ventas.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Pedido implements Serializable {
    private static final long serialVersionUID = 1L;

    private int numeroPedido;
    private String idCliente;
    private Date fecha;
    private List<ItemCarrito> items;
    private double total;
    private String estado;
    private String metodoPago;

    public Pedido() {
        this.items = new ArrayList<>();
        this.fecha = new Date();
        this.estado = "PENDIENTE";
    }

    public Pedido(int numeroPedido, String idCliente, List<ItemCarrito> items, double total, String metodoPago) {
        this.numeroPedido = numeroPedido;
        this.idCliente = idCliente;
        this.fecha = new Date();
        this.items = new ArrayList<>(items);
        this.total = total;
        this.estado = "PENDIENTE";
        this.metodoPago = metodoPago;
    }

    public int getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(int numeroPedido) {
        this.numeroPedido = numeroPedido;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public List<ItemCarrito> getItems() {
        return items;
    }

    public void setItems(List<ItemCarrito> items) {
        this.items = items;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    @Override
    public String toString() {
        return "Pedido [numero=" + numeroPedido + ", cliente=" + idCliente + ", total=" + total + ", estado=" + estado + "]";
    }
}
