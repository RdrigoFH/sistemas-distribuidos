
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class CreditCardServer {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            CreditCardInterface service = new CreditCardImpl();
            Naming.rebind("rmi://localhost:1099/CreditCardService", service);
            System.out.println("Servidor de Tarjetas de Credito listo...");
        } catch (Exception e) {
            System.err.println("Error en el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
