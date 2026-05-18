#!/usr/bin/env bash
set -e

OUT="out"

mkdir -p "$OUT"

# Build all classes (Swing client, no JavaFX)
javac -d "$OUT" src/Calculator.java src/CalculatorImpl.java src/CalculatorServer.java src/CalculatorClient.java

echo "Build complete. Classes in $OUT"
