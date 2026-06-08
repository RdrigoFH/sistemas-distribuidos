package com.ventas.client;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import com.ventas.model.Pedido;
import com.ventas.model.Producto;
import com.ventas.service.VentaService;

public class VentaClient {
    public static void main(String[] args) {
        try {
            URL url = new URL("http://localhost:1518/ventas?wsdl");
            QName qname = new QName("http://service.ventas.com/", "VentaServiceImplService");

            Service service = Service.create(url, qname);
            VentaService ventaService = service.getPort(VentaService.class);

            System.out.println("=== BIENVENIDO AL SISTEMA DE VENTAS ===\n");

            System.out.println("=== 1. CATÁLOGO DE PRODUCTOS ===");
            for (Producto producto : ventaService.listarProductos()) {
                System.out.println(producto);
            }

            System.out.println("\n=== 2. AGREGANDO PRODUCTOS AL CARRITO ===");
            System.out.println(ventaService.agregarAlCarrito("cliente001", 1, 1));
            System.out.println(ventaService.agregarAlCarrito("cliente001", 3, 2));
            System.out.println(ventaService.agregarAlCarrito("cliente001", 2, 1));

            System.out.println("\n=== 3. CONTENIDO DEL CARRITO ===");
            for (Producto producto : ventaService.verCarrito("cliente001")) {
                System.out.println(producto);
            }

            System.out.println("\n=== 4. ACTUALIZANDO CANTIDAD ===");
            System.out.println(ventaService.actualizarCantidad("cliente001", 3, 3));

            System.out.println("\n=== 5. CARRITO ACTUALIZADO ===");
            for (Producto producto : ventaService.verCarrito("cliente001")) {
                System.out.println(producto);
            }

            System.out.println("\n=== 6. REALIZANDO PEDIDO ===");
            int numeroPedido = ventaService.realizarPedido("cliente001", "Tarjeta de Crédito");
            System.out.println("Pedido generado N°: " + numeroPedido);

            System.out.println("\n=== 7. CONSULTANDO PEDIDO ===");
            Pedido pedido = ventaService.consultarPedido(numeroPedido);
            System.out.println("Pedido: " + pedido);
            System.out.println("Items del pedido:");
            pedido.getItems().forEach(item -> System.out.println("  - " + item));
            System.out.println("Total: S/" + pedido.getTotal());

            System.out.println("\n=== 8. CATÁLOGO ACTUALIZADO (STOCK DISMINUIDO) ===");
            for (Producto producto : ventaService.listarProductos()) {
                System.out.println(producto);
            }

            System.out.println("\n=== FIN DE LA DEMOSTRACIÓN ===");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
