# Guía Didáctica: Servicio SOAP para la Venta de Productos en Línea

Este laboratorio (`lab07`) está diseñado específicamente para servir como recurso educativo en **Sistemas Distribuidos** y **Arquitectura de Software Empresarial**. Aquí aprenderás qué es SOAP (Simple Object Access Protocol), cómo se estructuran sus contratos (WSDL), cómo opera a nivel de red y cómo se implementan los clientes tanto de alto nivel como de bajo nivel (XML puro).

---

## 1. Fundamentos Teóricos de SOAP

### ¿Qué es SOAP?
**SOAP** (inicialmente *Simple Object Access Protocol*) es un protocolo de mensajería ligero y estructurado diseñado para el intercambio de información en entornos descentralizados y distribuidos. A diferencia de REST (que es un estilo arquitectónico), SOAP es un **estándar formal** definido por el W3C.

Se basa exclusivamente en **XML** para formatear sus mensajes y opera típicamente sobre **HTTP**, aunque es independiente del protocolo de transporte subyacente (puede funcionar sobre SMTP, TCP o JMS).

### Anatomía de un Mensaje SOAP
Cada mensaje SOAP es un documento XML que contiene obligatoriamente una estructura jerárquica muy específica, conocida como el **SOAP Envelope** (Sobre SOAP):

```
+---------------------------------------------------------+
| SOAP Envelope                                           |
|   +-------------------------------------------------+   |
|   | SOAP Header (Opcional)                          |   |
|   |   - Metadatos (Seguridad, Enrutamiento, etc.)   |   |
|   +-------------------------------------------------+   |
|   | SOAP Body (Obligatorio)                         |   |
|   |   - Carga útil (Petición o Respuesta RPC)       |   |
|   |   +-----------------------------------------+   |   |
|   |   | SOAP Fault (Solo si ocurre un error)    |   |   |
|   |   |   - faultcode, faultstring, detail      |   |   |
|   |   +-----------------------------------------+   |   |
|   +-------------------------------------------------+   |
+---------------------------------------------------------+
```

1. **`Envelope` (Sobre)**: Es el elemento raíz que identifica el documento XML como un mensaje SOAP. Define los espacios de nombres (namespaces) del protocolo.
2. **`Header` (Encabezado - Opcional)**: Contiene información contextual y metadatos que no pertenecen a la lógica de negocio directa, sino a aspectos transversales de la comunicación:
   - Seguridad (p.ej. credenciales, firmas digitales con *WS-Security*).
   - Gestión de transacciones distribuidas (*WS-Coordination*).
   - Direccionamiento de red (*WS-Addressing*).
3. **`Body` (Cuerpo - Obligatorio)**: Contiene los datos reales de la llamada al método (parámetros de entrada) o la información que el servidor devuelve al cliente (respuesta).
4. **`Fault` (Falla - Condicional)**: Si ocurre un error durante el procesamiento en el servidor, el `Body` no retorna la respuesta habitual, sino un elemento `<soap:Fault>`. Este contiene:
   - `faultcode`: Código de error identificable por máquinas (ej. `Client` o `Server`).
   - `faultstring`: Explicación en lenguaje humano sobre el error (ej. "Stock insuficiente").

---

## 2. Contratos Formales: ¿Qué es el WSDL?

El **WSDL** (*Web Services Description Language*) es un documento XML que describe de forma exhaustiva y rigurosa cómo interactuar con un servicio web SOAP. Es el **contrato formal** entre el proveedor del servicio y sus consumidores.

> [!IMPORTANT]
> **La analogía del WSDL:**  
> Imagina el WSDL como una interfaz de TypeScript, un archivo de cabecera `.h` en C++, o un esquema OpenAPI (Swagger) en REST, pero con un tipado y estructura sumamente rígidos. Permite que las herramientas empresariales generen automáticamente código cliente tipado en cualquier lenguaje (Java, C#, Python, C++).

### Estructura de un Documento WSDL
Un WSDL estándar se divide en dos grandes secciones: abstracta y concreta.

```
Definición Abstracta (Qué hace el servicio)
  └── types: Esquemas de Datos XSD (Estructura de objetos Producto y Venta)
  └── message: Parámetros individuales de Entrada/Salida para cada operación
  └── portType: Firma de las Operaciones (La interfaz o nombres de los métodos)

Definición Concreta (Cómo y dónde se accede)
  └── binding: Vinculación con el protocolo de transporte (SOAP 1.1 + HTTP Literal)
  └── service: URL Física del Endpoint (Dirección física http://...)
```

1. **`<types>`**: Define los tipos de datos complejos que viajan en los mensajes usando **XML Schema (XSD)**. En nuestro caso, define qué campos componen un `Producto` y una `Venta`.
2. **`<message>`**: Define las variables o parámetros individuales de entrada y salida para cada operación del servicio.
3. **`<portType>`**: Agrupa múltiples operaciones abstractas. Es equivalente a una interfaz en programación orientada a objetos (define qué firmas de métodos existen).
4. **`<binding>`**: Vincula la interfaz abstracta (`portType`) con un protocolo de transporte concreto. En nuestro caso, especifica que usaremos el estándar **SOAP 1.1 sobre HTTP** con codificación `document/literal`.
5. **`<service>`**: Declara la dirección física del endpoint (la URL exacta, ej. `http://localhost:5000/soap`) a donde los clientes deben enviar sus peticiones POST.

---

## 3. Comparativa: SOAP vs REST en el Entorno Empresarial

| Criterio | SOAP (Simple Object Access Protocol) | REST (Representational State Transfer) |
| :--- | :--- | :--- |
| **Naturaleza** | **Protocolo estricto** con especificaciones formales del W3C. | **Estilo arquitectónico** basado en principios y convenciones de diseño. |
| **Formato de datos** | Exclusivamente **XML**. | Soporta múltiples formatos: **JSON (más común)**, XML, HTML, Texto. |
| **Contrato** | **Obligatorio (WSDL)**. Rigidez extrema que asegura que cliente y servidor hablen exactamente el mismo idioma. | **Opcional (OpenAPI / Swagger)**. Mayor flexibilidad y menor acoplamiento. |
| **Transporte** | Independiente del transporte. Puede usar **HTTP, SMTP, JMS, TCP**. | Diseñado específicamente para funcionar sobre **HTTP/HTTPS**. |
| **Seguridad** | Estándares empresariales avanzados nativos como **WS-Security** (cifrado a nivel de mensaje, firmas XML). | Seguridad delegada al canal de transporte (**HTTPS/TLS**) y tokens (**JWT, OAuth2**). |
| **Transacciones** | Soporta transacciones distribuidas complejas ACID nativamente con **WS-AtomicTransaction**. | No tiene soporte nativo para transacciones distribuidas. Debe implementarse a nivel de aplicación (patrón Saga). |
| **Rendimiento** | **Mayor overhead**. XML es verboso, requiere parseo intensivo de CPU y consume más ancho de banda. | **Altamente eficiente**. JSON es sumamente ligero y rápido de parsear por navegadores y dispositivos móviles. |

### ¿Por qué SOAP se sigue usando en el Software Empresarial?
Aunque REST domina el desarrollo de APIs modernas y móviles, SOAP sigue siendo el estándar de oro en sistemas gubernamentales, bancarios, financieros y de telecomunicaciones debido a:
1. **Transaccionalidad Rigurosa:** Ideal para transferencias bancarias donde varias bases de datos deben comprometerse en una sola transacción distribuida (todo o nada).
2. **Seguridad a Nivel de Mensaje:** Si un mensaje SOAP viaja a través de múltiples servidores intermedios, su contenido puede estar encriptado y firmado digitalmente de extremo a extremo, algo que HTTPS simple no puede garantizar (ya que desencripta en cada nodo de transporte).
3. **Garantía de Contrato:** El WSDL evita malentendidos entre empresas externas integradoras. Si el cliente envía un tipo de dato erróneo, la validación XML falla inmediatamente a nivel de red antes de tocar la lógica del backend.

---

## 4. Arquitectura de nuestra Implementación

Para este laboratorio didáctico, hemos construido un sistema completo de **Venta de Productos en Línea** utilizando **Python 3** y **Flask**.

La arquitectura está diseñada para ser completamente transparente:

```
  +--------------------+             +------------------+
  |    client.py       |             |  raw_client.py   |
  |  (Cliente Zeep)    |             |  (XML directo)   |
  +---------+----------+             +--------+---------+
            |                                 |
   Llama métodos Python              Envía Envelopes XML
            |                                 |
            +----------------+----------------+
                             |
                       Petición HTTP POST
                             |
                             v
                  +--------------------+
                  |     server.py      |
                  |   (SOAP Server)    |
                  |                    |
                  |  1. Lee POST XML   |
                  |  2. Dispatcher     |
                  |  3. Ejecuta lógica |
                  |  4. Retorna XML    |
                  +---------+----------+
                            |
                  Usa para validación y URLs
                            v
                  +--------------------+
                  |    ventas.wsdl     |
                  | (Contrato de Datos)|
                  +--------------------+
```

### Componentes de Código en `lab07/`

1. **`ventas.wsdl`**: El contrato formal. Define 3 operaciones:
   - `listarProductos`: Sin argumentos. Retorna una lista de elementos tipo `Producto`.
   - `obtenerProducto`: Recibe un `id` (entero). Retorna un `Producto`.
   - `realizarVenta`: Recibe `producto_id`, `cantidad` y el nombre del `cliente`. Retorna un comprobante con fecha, total y un identificador único de ticket.

2. **`server.py`**: El servidor SOAP en Flask.
   - En lugar de usar complejas librerías SOAP que ocultan el funcionamiento interno, implementa un **procesador de XML explícito**.
   - Al recibir un `POST` en `/soap`, parsea el XML usando `xml.etree.ElementTree`, extrae la operación e invoca al manejador correspondiente.
   - Modifica el estado en memoria (disminuye el stock del producto cuando se realiza una venta válida).
   - Genera dinámicamente respuestas SOAP bien estructuradas (`Envelope` -> `Body`).
   - Genera respuestas estructuradas de error **`SOAP Fault`** con código HTTP 500 si un producto no existe o el stock es insuficiente.
   - **Visualización en Tiempo Real:** Imprime en la consola cada XML que entra y sale con un formato claro para que puedas inspeccionar los paquetes SOAP sobre la marcha.

3. **`client.py`**: Cliente de alto nivel basado en la biblioteca **Zeep**.
   - Consume el WSDL directamente desde la URL del servidor (`http://localhost:5000/soap?wsdl`).
   - Oculta por completo el XML para el programador de Python. Los métodos se llaman como funciones ordinarias: `client.service.realizarVenta(...)`.
   - Captura excepciones del tipo `zeep.exceptions.Fault` para demostrar cómo los errores del servidor SOAP se propagan limpiamente como excepciones nativas en el código cliente.

4. **`raw_client.py`**: Cliente de bajo nivel.
   - No utiliza librerías SOAP. Construye directamente cadenas de texto que representan el XML de los Envelopes SOAP.
   - Agrega el encabezado HTTP obligatorio `SOAPAction`.
   - Envía los payloads por HTTP POST ordinario usando `requests`.
   - Muestra de forma cruda e indentada qué es exactamente lo que viaja por la red de extremo a extremo, ilustrando a la perfección el comportamiento del protocolo SOAP de bajo nivel.

---

## 5. Guía de Ejecución Paso a Paso

### Prerrequisitos
Tener instalado Python 3.12 y la utilidad para crear entornos virtuales.

### Paso 1: Configurar el Entorno Virtual e Instalar Dependencias
Desde la terminal, ubícate dentro del directorio de este laboratorio y ejecuta:

```bash
# Asegurarse de estar en el directorio correcto
cd lab07

# Crear el entorno virtual (si no está creado ya)
python3 -m venv venv

# Activar el entorno virtual
source venv/bin/activate

# Instalar Flask (servidor web), Zeep (cliente SOAP) y Requests (cliente HTTP)
pip install flask zeep requests
```

### Paso 2: Iniciar el Servidor SOAP
Con el entorno virtual activado, ejecuta el archivo del servidor:

```bash
python server.py
```

*Verás un mensaje en consola indicando que el servidor está corriendo en el puerto 5000 y exponiendo el WSDL en `http://localhost:5000/soap?wsdl`.*
*Mantén esta terminal abierta.*

### Paso 3: Ejecutar el Cliente de Alto Nivel (Zeep)
Abre **otra terminal**, navega a `lab07/`, activa el entorno virtual y ejecuta el cliente interactivo:

```bash
cd lab07
source venv/bin/activate
python client.py
```

**¿Qué verás en la terminal del cliente?**
- La conexión exitosa al WSDL.
- La tabla de productos disponibles obtenida del servidor.
- La consulta del producto ID `101`.
- La realización de una venta exitosa por 2 laptops a nombre de "Rodrigo Fernandez".
- La confirmación de que el stock de laptops en el servidor disminuyó de `8` a `6`.
- El manejo exitoso de dos errores (SOAP Fault): buscar un producto con ID `999` y comprar 50 unidades de un monitor con stock insuficiente.

**¿Qué verás en la terminal del servidor?**
- Podrás observar en tiempo real la impresión formateada de los XML de petición enviados por `Zeep` y los XML de respuesta del servidor (con Envelopes SOAP de entrada y salida perfectamente estructurados).

### Paso 4: Ejecutar el Cliente de XML Puro (Bajo Nivel)
En la terminal del cliente, ejecuta el script de bajo nivel para ver el protocolo crudo:

```bash
python raw_client.py
```

**¿Cómo funciona esta prueba?**
El script se detendrá en cada paso (`[ENTER]`) para permitirte analizar detalladamente:
- La cabecera HTTP y la estructura exacta del XML de petición (`listarProductosRequest`).
- El Envelope XML SOAP que responde el servidor.
- El envío de una compra (`realizarVentaRequest`) con parámetros explícitos dentro de los tags XML.
- La respuesta formateada con los datos del ticket de compra.
- El Envelope de Falla (`soapenv:Fault`) enviado por el servidor con código HTTP 500 ante una petición con stock insuficiente.

---

## 6. Resumen de Aprendizajes Clave

1. **SOAP no es magia:** Al final del día, es XML formateado bajo una especificación rigurosa enviado mediante peticiones ordinarias HTTP POST.
2. **El poder de los Contratos:** El archivo `ventas.wsdl` contiene toda la información necesaria para que un cliente en Java, C# o Python interactúe con el servidor sin necesidad de leer documentación externa.
3. **Mapeo Automatizado vs Transparencia:** Herramientas como `zeep` ocultan el XML y mapean tipos de datos SOAP a tipos nativos del lenguaje. Sin embargo, comprender la estructura interna XML (`Envelope`, `Body`, `Fault`) es fundamental para diagnosticar fallas en integraciones corporativas reales.
4. **Manejo de Errores Robustos:** Las fallas de la lógica de negocio (como la falta de inventario) se transmiten formalmente mediante la estructura de fallas propia del protocolo (`SOAP Fault`), proporcionando un estándar global de reporte de excepciones.
