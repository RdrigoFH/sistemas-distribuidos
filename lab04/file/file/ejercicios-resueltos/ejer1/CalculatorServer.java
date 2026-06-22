
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class CalculatorServer {
    public CalculatorServer() {
        try {
            LocateRegistry.createRegistry(1099);
            System.out.println("Registro RMI creado en puerto 1099");

            Calculator calculator = new CalculatorImplementation();
            Naming.rebind("rmi://localhost:1099/CalculatorService", calculator);
            System.out.println("Servidor calculadora listo y esperando...");
        } catch (Exception e) {
            System.err.println("Error en el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new CalculatorServer();
    }
}
