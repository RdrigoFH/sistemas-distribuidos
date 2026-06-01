import sys
from zeep import Client
from zeep.exceptions import Fault

def main():
    wsdl_url = 'http://localhost:5000/soap?wsdl'
    print("\n" + "="*70)
    print(" \033[1;36mINICIALIZANDO CLIENTE SOAP (ZEEP)\033[0m")
    print(f" Conectándose al WSDL en: {wsdl_url}")
    print("="*70)

    try:
        # Zeep lee el archivo WSDL y crea una clase proxy con los métodos del servicio
        client = Client(wsdl=wsdl_url)
    except Exception as e:
        print(f"\033[1;31m[ERROR]\033[0m No se pudo conectar al servidor SOAP. ¿Está encendido server.py?")
        print(f"Detalle: {e}")
        sys.exit(1)

    print("\n\033[1;32m[OPERACIÓN 1] Listar todos los productos (listarProductos)\033[0m")
    try:
        # Llamamos al método definido en el WSDL
        productos = client.service.listarProductos()
        
        # En SOAP, las listas/arreglos se mapean a listas de objetos en Python
        if productos:
            print(f"{'ID':<6} | {'Nombre':<30} | {'Precio':<10} | {'Stock':<6}")
            print("-" * 60)
            for prod in productos:
                # Cada 'prod' es un objeto con los atributos del tipo de dato complejo
                print(f"{prod.id:<6} | {prod.nombre:<30} | ${prod.precio:<9.2f} | {prod.stock:<6}")
        else:
            print("No se encontraron productos.")
    except Fault as f:
        print(f"Ocurrió un error SOAP: {f.message}")

    print("\n\033[1;32m[OPERACIÓN 2] Obtener detalle de un producto específico (obtenerProducto)\033[0m")
    id_producto = 101
    print(f"Buscando producto con ID: {id_producto}...")
    try:
        prod = client.service.obtenerProducto(id=id_producto)
        print(f" -> Nombre: {prod.nombre}")
        print(f" -> Descripción: {prod.descripcion}")
        print(f" -> Precio: ${prod.precio:.2f}")
        print(f" -> Stock actual: {prod.stock}")
    except Fault as f:
        print(f"\033[1;31m[SOAP FAULT]\033[0m {f.message}")

    print("\n\033[1;32m[OPERACIÓN 3] Realizar una venta válida (realizarVenta)\033[0m")
    cliente_nombre = "Rodrigo Fernandez"
    cantidad_compra = 2
    print(f"Comprando {cantidad_compra} unidades del producto {id_producto} para '{cliente_nombre}'...")
    try:
        # Ejecutamos la venta. El servidor modificará el stock.
        comprobante = client.service.realizarVenta(
            producto_id=id_producto,
            cantidad=cantidad_compra,
            cliente=cliente_nombre
        )
        print("\033[1;32m✓ Venta procesada con éxito. Comprobante recibido:\033[0m")
        print(f"   - ID Ticket: {comprobante.venta_id}")
        print(f"   - Cliente: {comprobante.cliente}")
        print(f"   - Producto: {comprobante.producto_nombre}")
        print(f"   - Cantidad: {comprobante.cantidad}")
        print(f"   - Total Pagado: ${comprobante.total:.2f}")
        print(f"   - Fecha/Hora: {comprobante.fecha}")
    except Fault as f:
        print(f"\033[1;31m[SOAP FAULT]\033[0m {f.message}")

    print("\n\033[1;32m[OPERACIÓN 4] Verificar disminución de stock (obtenerProducto)\033[0m")
    print(f"Consultando nuevamente el stock del producto {id_producto}...")
    try:
        prod = client.service.obtenerProducto(id=id_producto)
        print(f" -> Nuevo Stock en el servidor: {prod.stock} (Debería haber bajado de 8 a 6)")
    except Fault as f:
        print(f"\033[1;31m[SOAP FAULT]\033[0m {f.message}")

    print("\n\033[1;31m[OPERACIÓN 5 - PRUEBA DE ERROR] Intentar obtener un producto que no existe\033[0m")
    id_falso = 999
    print(f"Intentando buscar producto con ID: {id_falso}...")
    try:
        client.service.obtenerProducto(id=id_falso)
    except Fault as f:
        # Aquí capturamos el SOAP Fault que lanzó el servidor
        print(f"\033[1;33m✓ Capturado SOAP Fault esperado del servidor:\033[0m")
        print(f"   Código de Falla: {f.code if hasattr(f, 'code') else 'Server/Client'}")
        print(f"   Mensaje de Error (faultstring): {f.message}")

    print("\n\033[1;31m[OPERACIÓN 6 - PRUEBA DE ERROR] Intentar comprar más del stock disponible\033[0m")
    cantidad_excesiva = 50
    print(f"Intentando comprar {cantidad_excesiva} unidades del producto 103 (Monitor LG) cuando el stock es menor...")
    try:
        client.service.realizarVenta(
            producto_id=103,
            cantidad=cantidad_excesiva,
            cliente=cliente_nombre
        )
    except Fault as f:
        print(f"\033[1;33m✓ Capturado SOAP Fault esperado del servidor:\033[0m")
        print(f"   Mensaje de Error (faultstring): {f.message}")

    print("\n" + "="*70)
    print(" \033[1;36mDEMOSTRACIÓN DE CLIENTE SOAP COMPLETADA CON ÉXITO\033[0m")
    print("="*70 + "\n")

if __name__ == '__main__':
    main()
