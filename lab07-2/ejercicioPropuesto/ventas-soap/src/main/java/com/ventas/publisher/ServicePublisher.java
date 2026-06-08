package com.ventas.publisher;

import javax.xml.ws.Endpoint;
import com.ventas.service.VentaServiceImpl;

public class ServicePublisher {
    public static void main(String[] args) {
        String url = "http://localhost:1518/ventas";

        Endpoint.publish(url, new VentaServiceImpl());

        System.out.println("=== SERVICIO SOAP DE VENTAS ===");
        System.out.println("Publicado en: " + url + "?wsdl");
        System.out.println("");
        System.out.println("Operaciones disponibles:");
        System.out.println("  - listarProductos()");
        System.out.println("  - obtenerProducto(id)");
        System.out.println("  - agregarAlCarrito(cliente, idProducto, cantidad)");
        System.out.println("  - verCarrito(cliente)");
        System.out.println("  - eliminarDelCarrito(cliente, idProducto)");
        System.out.println("  - actualizarCantidad(cliente, idProducto, nuevaCantidad)");
        System.out.println("  - realizarPedido(cliente, metodoPago)");
        System.out.println("  - consultarPedido(numeroPedido)");
        System.out.println("");
        System.out.println("Servidor corriendo. Presiona Enter para detener.");

        try {
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.exit(0);
    }
}
