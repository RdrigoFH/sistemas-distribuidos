import requests
import xml.dom.minidom

def pretty_print_xml(xml_string):
    """
    Parsea e indenta una cadena XML para mostrarla de forma agradable en consola.
    """
    try:
        dom = xml.dom.minidom.parseString(xml_string)
        return dom.toprettyxml(indent="  ")
    except Exception:
        return xml_string

def send_soap_request(action_name, xml_payload):
    """
    Envía un POST HTTP con el payload de XML SOAP y los encabezados correctos.
    """
    url = "http://localhost:5000/soap"
    
    # SOAP 1.1 requiere el encabezado 'SOAPAction' indicando la operación a realizar.
    # El Content-Type debe ser obligatoriamente 'text/xml' con codificación adecuada.
    headers = {
        "Content-Type": "text/xml; charset=utf-8",
        "SOAPAction": f"http://sistemasdistribuidos.org/ventas/{action_name}"
    }

    print("\n" + "="*80)
    print(f"\033[1;36m[RAW HTTP CLIENT] Enviando peticion a: {url}\033[0m")
    print(f"\033[1;32m[ENCABEZADOS HTTP]\033[0m {headers}")
    print("\033[1;32m[CUERPO DE PETICION XML (SOAP ENVELOPE)]:\033[0m")
    print(xml_payload.strip())
    print("="*80)

    try:
        response = requests.post(url, data=xml_payload, headers=headers)
        
        print("\n" + "="*80)
        print(f"\033[1;35m[RESPUESTA HTTP RECIBIDA] (Código de Estado: {response.status_code})\033[0m")
        print("\033[1;35m[CUERPO DE RESPUESTA XML (SOAP ENVELOPE)]:\033[0m")
        print(pretty_print_xml(response.text).strip())
        print("="*80)
        return response
    except Exception as e:
        print(f"\033[1;31m[ERROR]\033[0m No se pudo completar la peticion HTTP: {e}")
        return None

def main():
    print("\n" + "*"*80)
    print("  DEMOSTRACIÓN DE COMUNICACIÓN SOAP A NIVEL DE PROTOCOLO (XML PURO)")
    print("  (Simula lo que viaja por los sockets a través de la red)")
    print("*"*80)

    # --- CASO 1: LISTAR PRODUCTOS (PETICIÓN EXITOSA) ---
    print("\n\033[1;33m--- PRUEBA 1: Listar Productos (Operacion: listarProductos) ---\033[0m")
    xml_listar = """<?xml version="1.0" encoding="utf-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:vent="http://sistemasdistribuidos.org/ventas">
   <soapenv:Header/>
   <soapenv:Body>
      <vent:listarProductosRequest/>
   </soapenv:Body>
</soapenv:Envelope>"""
    
    send_soap_request("listarProductos", xml_listar)

    input("\nPresiona [ENTER] para enviar la siguiente peticion (Comprar Producto)...")

    # --- CASO 2: COMPRAR PRODUCTO (PETICIÓN EXITOSA CON PARÁMETROS) ---
    print("\n\033[1;33m--- PRUEBA 2: Realizar Venta (Operacion: realizarVenta) ---\033[0m")
    # Vamos a comprar 3 ratones Logitech MX Master (ID: 102)
    xml_venta = """<?xml version="1.0" encoding="utf-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:vent="http://sistemasdistribuidos.org/ventas">
   <soapenv:Header/>
   <soapenv:Body>
      <vent:realizarVentaRequest>
         <vent:producto_id>102</vent:producto_id>
         <vent:cantidad>3</vent:cantidad>
         <vent:cliente>Estudiante de Sistemas Distribuidos</vent:cliente>
      </vent:realizarVentaRequest>
   </soapenv:Body>
</soapenv:Envelope>"""

    send_soap_request("realizarVenta", xml_venta)

    input("\nPresiona [ENTER] para enviar la siguiente peticion (Generar un Error)...")

    # --- CASO 3: CAUSAR ERROR (PETICIÓN QUE DEVOLVERÁ UN SOAP FAULT) ---
    print("\n\033[1;33m--- PRUEBA 3: Realizar Venta sin Stock Suficiente (Debe retornar SOAP Fault) ---\033[0m")
    # Intentamos comprar 50 Laptops Lenovo (ID: 101)
    xml_error = """<?xml version="1.0" encoding="utf-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:vent="http://sistemasdistribuidos.org/ventas">
   <soapenv:Header/>
   <soapenv:Body>
      <vent:realizarVentaRequest>
         <vent:producto_id>101</vent:producto_id>
         <vent:cantidad>50</vent:cantidad>
         <vent:cliente>Cliente Ambicioso</vent:cliente>
      </vent:realizarVentaRequest>
   </soapenv:Body>
</soapenv:Envelope>"""

    send_soap_request("realizarVenta", xml_error)

if __name__ == "__main__":
    main()
