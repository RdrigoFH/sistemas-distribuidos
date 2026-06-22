
import java.rmi.Naming;
import java.util.Map;
import java.util.Scanner;

public class ClienteSide {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            StockInterface pharm = (StockInterface) Naming.lookup("rmi://localhost:1099/PHARMACY");

            System.out.println("Ingresa la opcion");
            System.out.println("1: Listar productos");
            System.out.println("2: Comprar producto");
            int selection = sc.nextInt();

            if (selection == 1) {
                Map<String, MedicineInterface> stock = pharm.getStockProducts();
                for (String key : stock.keySet()) {
                    MedicineInterface med = stock.get(key);
                    System.out.println(med.print());
                    System.out.println("*--------------*");
                }
            } else if (selection == 2) {
                System.out.println("Ingrese nombre de la medicina");
                String medicine = sc.next();
                System.out.println("Ingrese cantidad a comprar");
                int amount = sc.nextInt();
                MedicineInterface bought = pharm.buyMedicine(medicine, amount);
                System.out.println("Usted acaba de comprar:");
                System.out.println(bought.print());
            } else {
                System.out.println("Seleccione una opcion valida");
            }
        } catch (Exception e) {
            System.err.println("Client error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
