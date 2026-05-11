package lab04.ejercicio2;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CreditCardInterface extends Remote {
    public double getBalance(String cardNumber) throws RemoteException;
    public boolean makePayment(String cardNumber, double amount) throws RemoteException;
    public boolean makePurchase(String cardNumber, double amount) throws RemoteException;
}