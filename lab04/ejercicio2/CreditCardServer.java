package lab04.ejercicio2;
import java.rmi.Naming;

public class CreditCardServer {
    public static void main(String[] args) {
        try {
            CreditCardInterface service = new CreditCardImpl();
            Naming.rebind("rmi://localhost:1099/CreditCardService", service);
            System.out.println("Servidor de Tarjetas de Crédito listo...");
        } catch (Exception e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }
}