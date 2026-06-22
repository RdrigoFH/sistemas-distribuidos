
import java.rmi.Naming;
import java.util.Scanner;

public class CurrencyConverterClient {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            CurrencyConverterInterface converter = (CurrencyConverterInterface) Naming.lookup(
                    "rmi://localhost:1099/CurrencyConverterService");

            System.out.print("Ingrese el monto en Soles (PEN) a convertir: ");
            double monto = scanner.nextDouble();

            double dolares = converter.convertirADolares(monto);
            double euros = converter.convertirAEuros(monto);

            System.out.printf("S/ %.2f equivalen a: $ %.2f Dolares%n", monto, dolares);
            System.out.printf("S/ %.2f equivalen a: EUR %.2f Euros%n", monto, euros);
        } catch (Exception e) {
            System.err.println("Error en el cliente: " + e);
            e.printStackTrace();
        }
    }
}
