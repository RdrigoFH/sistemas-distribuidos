
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class CurrencyConverterServer {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            CurrencyConverterInterface service = new CurrencyConverterImpl();
            Naming.rebind("rmi://localhost:1099/CurrencyConverterService", service);
            System.out.println("Servidor de conversion de moneda listo...");
        } catch (Exception e) {
            System.err.println("Error en el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
