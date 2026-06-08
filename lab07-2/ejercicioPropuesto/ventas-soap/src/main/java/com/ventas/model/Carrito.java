package com.ventas.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Carrito implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idCliente;
    private List<ItemCarrito> items;

    public Carrito() {
        this.items = new ArrayList<>();
    }

    public Carrito(String idCliente) {
        this.idCliente = idCliente;
        this.items = new ArrayList<>();
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public List<ItemCarrito> getItems() {
        return items;
    }

    public void setItems(List<ItemCarrito> items) {
        this.items = items;
    }

    public void agregarItem(Producto producto, int cantidad) {
        for (ItemCarrito item : items) {
            if (item.getProducto().getId() == producto.getId()) {
                item.setCantidad(item.getCantidad() + cantidad);
                return;
            }
        }
        items.add(new ItemCarrito(producto, cantidad));
    }

    public void eliminarItem(int idProducto) {
        items.removeIf(item -> item.getProducto().getId() == idProducto);
    }

    public double getTotal() {
        return items.stream().mapToDouble(ItemCarrito::getSubtotal).sum();
    }

    public void vaciar() {
        items.clear();
    }

    @Override
    public String toString() {
        return "Carrito [cliente=" + idCliente + ", items=" + items.size() + ", total=" + getTotal() + "]";
    }
}
