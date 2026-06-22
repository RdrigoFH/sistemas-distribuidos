
import java.rmi.Naming;

public class CalculatorClient {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso: java lab04.CalculatorClient <num1> <num2>");
            return;
        }

        int num1 = Integer.parseInt(args[0]);
        int num2 = Integer.parseInt(args[1]);

        try {
            Calculator calculator = (Calculator) Naming.lookup("rmi://localhost:1099/CalculatorService");

            System.out.println("Subtraction: " + calculator.sub(num1, num2));
            System.out.println("Addition: " + calculator.add(num1, num2));
            System.out.println("Multiplication: " + calculator.mul(num1, num2));
            System.out.println("Division: " + calculator.div(num1, num2));
        } catch (Exception e) {
            System.err.println("Error en el cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
