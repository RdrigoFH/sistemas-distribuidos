import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CalculatorServer {
    public static void main(String[] args) {
        int port = 1099;
        String serviceName = "Calculator";

        try {
            Registry registry = LocateRegistry.createRegistry(port);
            Calculator calculator = new CalculatorImpl();
            registry.rebind(serviceName, calculator);

            System.out.println("=========================================");
            System.out.println(" RMI Calculator Server");
            System.out.println(" Port: " + port);
            System.out.println(" Service: " + serviceName);
            System.out.println(" Status: ACTIVE");
            System.out.println("=========================================");
            System.out.println("Waiting for client connections...");
        } catch (RemoteException e) {
            System.err.println("Failed to start RMI server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
