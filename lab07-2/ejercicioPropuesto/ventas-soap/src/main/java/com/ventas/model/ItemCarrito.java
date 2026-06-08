package com.ventas.model;

import java.io.Serializable;

public class ItemCarrito implements Serializable {
    private static final long serialVersionUID = 1L;

    private Producto producto;
    private int cantidad;

    public ItemCarrito() {
        super();
    }

    public ItemCarrito(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getSubtotal() {
        return producto.getPrecio() * cantidad;
    }

    @Override
    public String toString() {
        return "ItemCarrito [producto=" + producto.getNombre() + ", cantidad=" + cantidad + ", subtotal=" + getSubtotal() + "]";
    }
}
