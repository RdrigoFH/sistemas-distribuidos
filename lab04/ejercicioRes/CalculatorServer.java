package lab04.ejercicioRes;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class CalculatorServer {
    public CalculatorServer() {
        try {
            // Crear el registro en el puerto 1099
            LocateRegistry.createRegistry(1099);
            System.out.println("Registro RMI creado en puerto 1099");
            
            Calculator c = new CalculatorImplementation();
            Naming.rebind("rmi://localhost:1099/CalculatorService", c);
            System.out.println("Servidor calculadora listo y esperando...");
        } catch (Exception e) {
            System.out.println("Trouble: " + e);
            e.printStackTrace();
        }
    }
    
    public static void main(String args[]) {
        new CalculatorServer();
    }
}
