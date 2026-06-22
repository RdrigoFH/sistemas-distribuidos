
import java.rmi.Naming;

public class CreditCardClient {
    public static void main(String[] args) {
        try {
            CreditCardInterface cardService =
                    (CreditCardInterface) Naming.lookup("rmi://localhost:1099/CreditCardService");

            String myCard = "1234-5678";
            System.out.println("Consultando saldo para: " + myCard);
            System.out.println("Saldo actual: " + cardService.getBalance(myCard));

            if (cardService.makePurchase(myCard, 200.0)) {
                System.out.println("Compra exitosa de 200.0");
            } else {
                System.out.println("No se pudo procesar la compra");
            }

            System.out.println("Nuevo saldo: " + cardService.getBalance(myCard));
        } catch (Exception e) {
            System.err.println("Error en el cliente: " + e);
            e.printStackTrace();
        }
    }
}
