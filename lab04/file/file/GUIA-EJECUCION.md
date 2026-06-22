# Guia de ejecucion de ejercicios RMI

## Requisitos

1. Tener Java JDK instalado.
2. Abrir terminal PowerShell en la carpeta del proyecto.
3. Usar dos terminales para cada ejercicio: una para servidor y otra para cliente.
4. Todos los servidores de estos ejercicios crean el registro RMI en el puerto 1099 automaticamente.

Si aparece error de puerto ocupado, cierra cualquier servidor anterior y vuelve a intentar.

## Estructura general

- ejercicios-resueltos/ejer1
- ejercicios-propuestos/ejer1
- ejercicios-propuestos/ejer2
- ejercicios-propuestos/ejer3

## Ejercicio resuelto 1: Calculadora RMI

### Compilar

    cd ejercicios-resueltos/ejer1/
    javac *.java

### Ejecutar servidor

    java CalculatorServer

### Ejecutar cliente (en otra terminal)

    cd ejercicios-resueltos/ejer1/
    java CalculatorClient 20 5

## Ejercicio propuesto 1: Farmacia RMI

### Compilar

    cd ejercicios-propuestos/ejer1/
    javac *.java

### Ejecutar servidor

    java ServerSide

### Ejecutar cliente (en otra terminal)

    cd ejercicios-propuestos/ejer1/
    java ClienteSide

## Ejercicio propuesto 2: Tarjetas de credito RMI

### Compilar

    cd ejercicios-propuestos/ejer2/
    javac *.java

### Ejecutar servidor

    java CreditCardServer

### Ejecutar cliente (en otra terminal)

    cd ejercicios-propuestos/ejer2/
    java CreditCardClient

## Ejercicio propuesto 3: Conversor de moneda RMI

### Compilar

    cd ejercicios-propuestos/ejer3/
    javac *.java

### Ejecutar servidor

    java CurrencyConverterServer

### Ejecutar cliente (en otra terminal)

    cd ejercicios-propuestos/ejer3/
    java CurrencyConverterClient

## Recomendaciones

1. Ejecuta un solo servidor a la vez para evitar conflictos de puerto 1099.
2. Si cambias codigo, vuelve a compilar antes de ejecutar.
3. Si una terminal queda bloqueada por el servidor, usa otra terminal para correr el cliente.
