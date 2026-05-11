package lab04.ejercicio2;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class CreditCardImpl extends UnicastRemoteObject implements CreditCardInterface {
    private Map<String, Double> accounts;

    public CreditCardImpl() throws RemoteException {
        super();
        accounts = new HashMap<>();
        accounts.put("1234-5678", 5000.0);
        accounts.put("8765-4321", 1200.0);
    }

    @Override
    public double getBalance(String cardNumber) throws RemoteException {
        return accounts.getOrDefault(cardNumber, -1.0);
    }

    @Override
    public boolean makePayment(String cardNumber, double amount) throws RemoteException {
        if (accounts.containsKey(cardNumber)) {
            accounts.put(cardNumber, accounts.get(cardNumber) + amount);
            return true;
        }
        return false;
    }

    @Override
    public boolean makePurchase(String cardNumber, double amount) throws RemoteException {
        if (accounts.containsKey(cardNumber) && accounts.get(cardNumber) >= amount) {
            accounts.put(cardNumber, accounts.get(cardNumber) - amount);
            return true;
        }
        return false;
    }
}