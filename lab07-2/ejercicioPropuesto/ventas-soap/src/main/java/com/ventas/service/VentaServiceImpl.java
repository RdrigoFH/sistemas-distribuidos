package com.ventas.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jws.WebService;
import com.ventas.model.Carrito;
import com.ventas.model.ItemCarrito;
import com.ventas.model.Pedido;
import com.ventas.model.Producto;

@WebService(endpointInterface = "com.ventas.service.VentaService")
public class VentaServiceImpl implements VentaService {

    private static final List<Producto> catalogo;
    private static final Map<String, Carrito> carritos;
    private static final Map<Integer, Pedido> pedidos;
    private static int nextNumeroPedido = 1000;

    static {
        catalogo = new ArrayList<>();
        catalogo.add(new Producto(1, "Laptop Gamer", "16GB RAM, 512GB SSD", 899.99, 10));
        catalogo.add(new Producto(2, "Mouse Inalámbrico", "Logitech MX Master", 45.50, 50));
        catalogo.add(new Producto(3, "Teclado Mecánico", "RGB, switches azules", 89.90, 30));
        catalogo.add(new Producto(4, "Monitor 24 pulgadas", "Full HD, 75Hz", 199.99, 15));
        catalogo.add(new Producto(5, "Auriculares Gaming", "7.1 surround", 59.99, 25));

        carritos = new HashMap<>();
        pedidos = new HashMap<>();
    }

    @Override
    public List<Producto> listarProductos() {
        return new ArrayList<>(catalogo);
    }

    @Override
    public Producto obtenerProducto(int id) {
        return catalogo.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public String agregarAlCarrito(String idCliente, int idProducto, int cantidad) {
        Producto producto = obtenerProducto(idProducto);
        if (producto == null) {
            return "ERROR: Producto no encontrado";
        }

        if (cantidad <= 0) {
            return "ERROR: La cantidad debe ser mayor que cero";
        }

        if (producto.getStock() < cantidad) {
            return "ERROR: Stock insuficiente. Disponible: " + producto.getStock();
        }

        Carrito carrito = carritos.get(idCliente);
        if (carrito == null) {
            carrito = new Carrito(idCliente);
            carritos.put(idCliente, carrito);
        }

        carrito.agregarItem(producto, cantidad);
        return "OK: Producto agregado al carrito";
    }

    @Override
    public List<Producto> verCarrito(String idCliente) {
        Carrito carrito = carritos.get(idCliente);
        if (carrito == null) {
            return new ArrayList<>();
        }

        List<Producto> productosEnCarrito = new ArrayList<>();
        for (ItemCarrito item : carrito.getItems()) {
            Producto producto = item.getProducto();
            productosEnCarrito.add(new Producto(producto.getId(), producto.getNombre(), producto.getDescripcion(), producto.getPrecio(), item.getCantidad()));
        }
        return productosEnCarrito;
    }

    @Override
    public String eliminarDelCarrito(String idCliente, int idProducto) {
        Carrito carrito = carritos.get(idCliente);
        if (carrito == null) {
            return "ERROR: Carrito no encontrado";
        }

        carrito.eliminarItem(idProducto);
        return "OK: Producto eliminado del carrito";
    }

    @Override
    public int realizarPedido(String idCliente, String metodoPago) {
        Carrito carrito = carritos.get(idCliente);
        if (carrito == null || carrito.getItems().isEmpty()) {
            return -1;
        }

        for (ItemCarrito item : carrito.getItems()) {
            Producto producto = item.getProducto();
            if (producto.getStock() < item.getCantidad()) {
                return -2;
            }
        }

        for (ItemCarrito item : carrito.getItems()) {
            Producto producto = item.getProducto();
            producto.setStock(producto.getStock() - item.getCantidad());
        }

        int numeroPedido = nextNumeroPedido++;
        Pedido pedido = new Pedido(numeroPedido, idCliente, carrito.getItems(), carrito.getTotal(), metodoPago);
        pedidos.put(numeroPedido, pedido);
        carrito.vaciar();

        return numeroPedido;
    }

    @Override
    public Pedido consultarPedido(int numeroPedido) {
        return pedidos.get(numeroPedido);
    }

    @Override
    public String actualizarCantidad(String idCliente, int idProducto, int nuevaCantidad) {
        Carrito carrito = carritos.get(idCliente);
        if (carrito == null) {
            return "ERROR: Carrito no encontrado";
        }

        Producto producto = obtenerProducto(idProducto);
        if (producto == null) {
            return "ERROR: Producto no encontrado";
        }

        if (nuevaCantidad < 0) {
            return "ERROR: La cantidad no puede ser negativa";
        }

        for (ItemCarrito item : carrito.getItems()) {
            if (item.getProducto().getId() == idProducto) {
                if (nuevaCantidad == 0) {
                    carrito.eliminarItem(idProducto);
                } else {
                    int cantidadActual = item.getCantidad();
                    int stockDisponible = producto.getStock() + cantidadActual;
                    if (nuevaCantidad > stockDisponible) {
                        return "ERROR: Stock insuficiente. Disponible: " + stockDisponible;
                    }
                    item.setCantidad(nuevaCantidad);
                }
                return "OK: Cantidad actualizada";
            }
        }

        return "ERROR: Producto no está en el carrito";
    }
}
