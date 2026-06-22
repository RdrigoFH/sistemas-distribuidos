
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class CreditCardImpl extends UnicastRemoteObject implements CreditCardInterface {
    private final Map<String, Double> accounts;

    public CreditCardImpl() throws RemoteException {
        super();
        accounts = new HashMap<>();
        accounts.put("1234-5678", 5000.0);
        accounts.put("8765-4321", 1200.0);
    }

    @Override
    public synchronized double getBalance(String cardNumber) throws RemoteException {
        return accounts.getOrDefault(cardNumber, -1.0);
    }

    @Override
    public synchronized boolean makePayment(String cardNumber, double amount) throws RemoteException {
        if (amount <= 0 || !accounts.containsKey(cardNumber)) {
            return false;
        }
        accounts.put(cardNumber, accounts.get(cardNumber) + amount);
        return true;
    }

    @Override
    public synchronized boolean makePurchase(String cardNumber, double amount) throws RemoteException {
        if (amount <= 0 || !accounts.containsKey(cardNumber)) {
            return false;
        }
        if (accounts.get(cardNumber) >= amount) {
            accounts.put(cardNumber, accounts.get(cardNumber) - amount);
            return true;
        }
        return false;
    }
}
