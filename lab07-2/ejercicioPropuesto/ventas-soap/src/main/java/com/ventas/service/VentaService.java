package com.ventas.service;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebService;
import com.ventas.model.Pedido;
import com.ventas.model.Producto;

@WebService
public interface VentaService {

    @WebMethod
    List<Producto> listarProductos();

    @WebMethod
    Producto obtenerProducto(int id);

    @WebMethod
    String agregarAlCarrito(String idCliente, int idProducto, int cantidad);

    @WebMethod
    List<Producto> verCarrito(String idCliente);

    @WebMethod
    String eliminarDelCarrito(String idCliente, int idProducto);

    @WebMethod
    int realizarPedido(String idCliente, String metodoPago);

    @WebMethod
    Pedido consultarPedido(int numeroPedido);

    @WebMethod
    String actualizarCantidad(String idCliente, int idProducto, int nuevaCantidad);
}
