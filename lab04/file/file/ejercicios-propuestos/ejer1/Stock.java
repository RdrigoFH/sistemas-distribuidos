
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class Stock extends UnicastRemoteObject implements StockInterface {
    private final Map<String, MedicineInterface> medicines = new HashMap<>();

    public Stock() throws RemoteException {
        super();
    }

    @Override
    public synchronized void addMedicine(String name, float price, int stock) throws RemoteException {
        medicines.put(name, new Medicine(name, price, stock));
    }

    @Override
    public synchronized MedicineInterface buyMedicine(String name, int amount)
            throws RemoteException, StockException {
        MedicineInterface medicine = medicines.get(name);
        if (medicine == null) {
            throw new StockException("Impossible to find " + name);
        }
        return medicine.getMedicine(amount);
    }

    @Override
    public synchronized Map<String, MedicineInterface> getStockProducts() throws RemoteException {
        return new HashMap<>(medicines);
    }
}
