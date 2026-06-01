import os
import uuid
from datetime import datetime
from xml.sax.saxutils import escape
import xml.etree.ElementTree as ET
from flask import Flask, request, Response, make_response

app = Flask(__name__)

# Base de datos en memoria para propósitos didácticos
PRODUCTOS = {
    101: {
        "id": 101,
        "nombre": "Laptop Lenovo ThinkPad X1",
        "descripcion": "Laptop empresarial ultra ligera, 16GB RAM, 512GB SSD",
        "precio": 1250.00,
        "stock": 8
    },
    102: {
        "id": 102,
        "nombre": "Mouse Logitech MX Master 3S",
        "descripcion": "Mouse inalámbrico ergonómico avanzado para productividad",
        "precio": 99.99,
        "stock": 20
    },
    103: {
        "id": 103,
        "nombre": "Monitor LG UltraWide 34 pulgadas",
        "descripcion": "Monitor panorámico IPS QHD (3440 x 1440) de 60Hz",
        "precio": 450.00,
        "stock": 4
    }
}

VENTAS = []

# --- HELPER FUNCTIONS ---

def parse_soap_request(xml_str):
    """
    Parsea el XML de la petición SOAP y extrae la operación y sus argumentos.
    Ignora los namespaces de XML para simplificar el código educativo.
    """
    try:
        root = ET.fromstring(xml_str)
        
        # Encontrar la etiqueta <Body> buscando por su local name
        body = None
        for elem in root.iter():
            local_name = elem.tag.split('}')[-1]
            if local_name == 'Body':
                body = elem
                break
        
        if body is None or len(body) == 0:
            return None, {}
        
        # El primer elemento dentro de <Body> representa la operación (request element)
        request_node = body[0]
        operation = request_node.tag.split('}')[-1]
        
        # Extraer parámetros del nodo de petición
        params = {}
        for child in request_node:
            name = child.tag.split('}')[-1]
            params[name] = child.text
            
        return operation, params
    except Exception as e:
        print(f"[\033[31mERROR\033[0m] Error al parsear XML: {e}")
        return None, {}

def make_soap_envelope(body_content):
    """
    Envuelve el contenido de respuesta XML en un Envelope SOAP 1.1 estándar.
    """
    return f"""<?xml version="1.0" encoding="utf-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:tns="http://sistemasdistribuidos.org/ventas">
  <soapenv:Body>
    {body_content}
  </soapenv:Body>
</soapenv:Envelope>"""

def make_soap_fault(error_message, fault_code="soapenv:Client"):
    """
    Genera un error SOAP Fault estándar con código HTTP 500.
    """
    return f"""<?xml version="1.0" encoding="utf-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
  <soapenv:Body>
    <soapenv:Fault>
      <faultcode>{fault_code}</faultcode>
      <faultstring>{escape(error_message)}</faultstring>
    </soapenv:Fault>
  </soapenv:Body>
</soapenv:Envelope>"""

# --- SOAP ENDPOINTS HANDLERS ---

def handle_listar_productos():
    """
    Devuelve la lista completa de productos.
    """
    productos_xml = []
    for p in PRODUCTOS.values():
        productos_xml.append(f"""      <tns:productos>
        <tns:id>{p['id']}</tns:id>
        <tns:nombre>{escape(p['nombre'])}</tns:nombre>
        <tns:descripcion>{escape(p['descripcion'])}</tns:descripcion>
        <tns:precio>{p['precio']}</tns:precio>
        <tns:stock>{p['stock']}</tns:stock>
      </tns:productos>""")
    
    body = f"""<tns:listarProductosResponse>
{chr(10).join(productos_xml)}
    </tns:listarProductosResponse>"""
    return make_soap_envelope(body), 200

def handle_obtener_producto(params):
    """
    Devuelve la información de un producto por su ID.
    Si no existe, devuelve un SOAP Fault.
    """
    prod_id_str = params.get('id')
    if not prod_id_str:
        return make_soap_fault("El parametro 'id' es requerido."), 500
    
    try:
        prod_id = int(prod_id_str)
    except ValueError:
        return make_soap_fault("El parametro 'id' debe ser un numero entero."), 500
    
    p = PRODUCTOS.get(prod_id)
    if not p:
        return make_soap_fault(f"Producto con ID {prod_id} no encontrado."), 500
    
    body = f"""<tns:obtenerProductoResponse>
      <tns:producto>
        <tns:id>{p['id']}</tns:id>
        <tns:nombre>{escape(p['nombre'])}</tns:nombre>
        <tns:descripcion>{escape(p['descripcion'])}</tns:descripcion>
        <tns:precio>{p['precio']}</tns:precio>
        <tns:stock>{p['stock']}</tns:stock>
      </tns:producto>
    </tns:obtenerProductoResponse>"""
    return make_soap_envelope(body), 200

def handle_realizar_venta(params):
    """
    Procesa la venta de un producto.
    Disminuye el stock del producto y registra la venta.
    Devuelve un comprobante de venta o un SOAP Fault en caso de error.
    """
    prod_id_str = params.get('producto_id')
    cantidad_str = params.get('cantidad')
    cliente = params.get('cliente')
    
    if not prod_id_str or not cantidad_str or not cliente:
        return make_soap_fault("Los parametros 'producto_id', 'cantidad' y 'cliente' son requeridos."), 500
    
    try:
        prod_id = int(prod_id_str)
        cantidad = int(cantidad_str)
    except ValueError:
        return make_soap_fault("Los parametros 'producto_id' y 'cantidad' deben ser numeros enteros."), 500
    
    if cantidad <= 0:
        return make_soap_fault("La cantidad de productos a comprar debe ser mayor que 0."), 500
    
    p = PRODUCTOS.get(prod_id)
    if not p:
        return make_soap_fault(f"Producto con ID {prod_id} no existe."), 500
    
    if p['stock'] < cantidad:
        return make_soap_fault(f"Stock insuficiente para {p['nombre']}. Solicitado: {cantidad}, Disponible: {p['stock']}."), 500
    
    # Decrementar stock y procesar la venta
    p['stock'] -= cantidad
    venta_id = str(uuid.uuid4())[:8].upper() # Genera un código de ticket corto
    total = round(p['precio'] * cantidad, 2)
    fecha = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    
    venta_record = {
        "venta_id": venta_id,
        "cliente": cliente,
        "producto_nombre": p['nombre'],
        "cantidad": cantidad,
        "total": total,
        "fecha": fecha
    }
    VENTAS.append(venta_record)
    
    body = f"""<tns:realizarVentaResponse>
      <tns:venta_id>{venta_id}</tns:venta_id>
      <tns:cliente>{escape(cliente)}</tns:cliente>
      <tns:producto_nombre>{escape(p['nombre'])}</tns:producto_nombre>
      <tns:cantidad>{cantidad}</tns:cantidad>
      <tns:total>{total}</tns:total>
      <tns:fecha>{fecha}</tns:fecha>
    </tns:realizarVentaResponse>"""
    return make_soap_envelope(body), 200

# --- ROUTES ---

@app.route('/soap', methods=['GET'])
def get_wsdl():
    """
    Expone el archivo WSDL que define el contrato del servicio SOAP.
    """
    wsdl_path = os.path.join(os.path.dirname(__file__), 'ventas.wsdl')
    if os.path.exists(wsdl_path):
        with open(wsdl_path, 'r', encoding='utf-8') as f:
            wsdl_content = f.read()
        return Response(wsdl_content, mimetype='text/xml')
    else:
        return "Archivo WSDL no encontrado en el servidor.", 404

@app.route('/soap', methods=['POST'])
def post_soap():
    """
    Recibe la petición SOAP por POST, procesa la acción y retorna una respuesta SOAP XML.
    """
    xml_data = request.data.decode('utf-8')
    
    print("\n" + "="*80)
    print("\033[36m[PETICION SOAP RECIBIDA]\033[0m")
    print(xml_data.strip())
    print("="*80)
    
    operation, params = parse_soap_request(xml_data)
    
    print(f"\033[32m[DISPATCHER] Operacion detectada:\033[0m {operation}")
    print(f"\033[32m[DISPATCHER] Parametros:\033[0m {params}")
    
    if operation == 'listarProductosRequest':
        response_xml, status_code = handle_listar_productos()
    elif operation == 'obtenerProductoRequest':
        response_xml, status_code = handle_obtener_producto(params)
    elif operation == 'realizarVentaRequest':
        response_xml, status_code = handle_realizar_venta(params)
    else:
        response_xml, status_code = make_soap_fault(f"Operacion desconocida: {operation}"), 500
        
    print("\n" + "="*80)
    print(f"\033[35m[RESPUESTA SOAP ENVIADA] (HTTP {status_code})\033[0m")
    print(response_xml.strip())
    print("="*80 + "\n")
    
    return Response(response_xml, status=status_code, mimetype='text/xml')

if __name__ == '__main__':
    print("*"*60)
    print(" Servidor SOAP de Ventas iniciado en http://localhost:5000/soap")
    print(" Puedes acceder al WSDL en: http://localhost:5000/soap?wsdl")
    print("*"*60)
    app.run(host='0.0.0.0', port=5000, debug=True)
