
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CreditCardInterface extends Remote {
    double getBalance(String cardNumber) throws RemoteException;

    boolean makePayment(String cardNumber, double amount) throws RemoteException;

    boolean makePurchase(String cardNumber, double amount) throws RemoteException;
}
