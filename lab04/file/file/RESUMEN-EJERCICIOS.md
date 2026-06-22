# Resumen de ejercicios RMI

Este repositorio contiene 4 ejercicios de Java RMI distribuidos en:

- `ejercicios-resueltos/ejer1`
- `ejercicios-propuestos/ejer1`
- `ejercicios-propuestos/ejer2`
- `ejercicios-propuestos/ejer3`

Todos los ejercicios usan el registro RMI en el puerto `1099` y separan servidor y cliente.

---

## Ejercicio resuelto 1: Calculadora RMI

**Ruta:** `ejercicios-resueltos/ejer1`

**Archivos principales:**
- `Calculator.java` - interfaz remota RMI.
- `CalculatorImplementation.java` - implementación de los métodos remotos.
- `CalculatorServer.java` - crea el registro RMI y publica el servicio `CalculatorService`.
- `CalculatorClient.java` - cliente que consume el servicio remoto y muestra resultados de suma, resta y multiplicación.

**Descripción:**
Aplicación RMI de calculadora con operaciones matemáticas básicas expuestas como servicio remoto.

**Ejecución:**

```powershell
cd ejercicios-resueltos/ejer1/
javac *.java
java CalculatorServer
```

En otra terminal:

```powershell
cd ejercicios-resueltos/ejer1/
java CalculatorClient 20 5
```

---

## Ejercicio propuesto 1: Farmacia RMI

**Ruta:** `ejercicios-propuestos/ejer1`

**Archivos principales:**
- `StockInterface.java` - interfaz remota para el inventario de farmacia.
- `Stock.java` - implementación del servicio de inventario y venta de medicamentos.
- `MedicineInterface.java` - interfaz remota que representa un medicamento.
- `Medicine.java` - implementación de los datos de un medicamento.
- `StockException.java` - excepción de negocio para el inventario.
- `ServerSide.java` - inicializa el registro RMI y publica `PHARMACY`.
- `ClienteSide.java` - cliente que lista productos o compra una medicina.

**Descripción:**
Servicio RMI para una farmacia que permite listar productos y comprar medicamentos remotamente.

**Ejecución:**

```powershell
cd ejercicios-propuestos/ejer1/
javac *.java
java ServerSide
```

En otra terminal:

```powershell
cd ejercicios-propuestos/ejer1/
java ClienteSide
```

---

## Ejercicio propuesto 2: Tarjetas de crédito RMI

**Ruta:** `ejercicios-propuestos/ejer2`

**Archivos principales:**
- `CreditCardInterface.java` - interfaz remota del servicio de tarjeta.
- `CreditCardImpl.java` - implementación de la lógica de saldo y compra.
- `CreditCardServer.java` - crea el registro RMI y publica `CreditCardService`.
- `CreditCardClient.java` - cliente que consulta saldo y realiza una compra.

**Descripción:**
Servicio RMI que simula la validación de una tarjeta de crédito, consulta de saldo y autorización de compra.

**Ejecución:**

```powershell
cd ejercicios-propuestos/ejer2/
javac *.java
java CreditCardServer
```

En otra terminal:

```powershell
cd ejercicios-propuestos/ejer2/
java CreditCardClient
```

---

## Ejercicio propuesto 3: Conversor de moneda RMI

**Ruta:** `ejercicios-propuestos/ejer3`

**Archivos principales:**
- `CurrencyConverterInterface.java` - interfaz remota del servicio de conversión.
- `CurrencyConverterImpl.java` - implementación de las conversiones.
- `CurrencyConverterServer.java` - crea el registro RMI y publica `CurrencyConverterService`.
- `CurrencyConverterClient.java` - cliente que solicita un monto en soles y recibe conversiones a dólares y euros.

**Descripción:**
Servicio RMI para convertir montos desde soles (PEN) a dólares y euros.

**Ejecución:**

```powershell
cd ejercicios-propuestos/ejer3/
javac *.java
java CurrencyConverterServer
```

En otra terminal:

```powershell
cd ejercicios-propuestos/ejer3/
java CurrencyConverterClient
```

---

## Observaciones generales

- Todos los ejercicios usan la carpeta raíz de cada ejercicio para compilar los archivos Java.
- La carpeta `src` ya no es necesaria en las rutas de ejecución actuales.
- Si aparece conflicto en el puerto `1099`, cierra servidores RMI anteriores antes de iniciar uno nuevo.
