# RMI Calculator - Java RMI + Swing (No Maven)

## Structure

```
rmi-calculator/
├── src/
│   ├── Calculator.java
│   ├── CalculatorImpl.java
│   ├── CalculatorServer.java
│   └── CalculatorClient.java
├── out/
├── compile.sh / compile.bat
├── run_server.sh / run_server.bat
├── run_client.sh / run_client.bat
└── README.md
```

## Requirements

- JDK 11+

## Build

Linux/macOS:

```bash
chmod +x compile.sh run_server.sh run_client.sh
./compile.sh
```

Windows:

```bat
compile.bat
```

## Run

Start server (new terminal):

```bash
./run_server.sh
```

Start client (another terminal):

```bash
./run_client.sh
```

## Notes

- Division by zero throws an error in the server.
- If port 1099 is busy, change it in CalculatorServer and CalculatorClient.
