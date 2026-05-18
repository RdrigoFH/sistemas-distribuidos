@echo off
set OUT=out

if not exist %OUT% mkdir %OUT%

REM Build all classes (Swing client, no JavaFX)
javac -d %OUT% src\Calculator.java src\CalculatorImpl.java src\CalculatorServer.java src\CalculatorClient.java

echo Build complete. Classes in %OUT%
