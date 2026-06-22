
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Medicine extends UnicastRemoteObject implements MedicineInterface {
    private final String name;
    private final float unitPrice;
    private int stock;

    public Medicine(String name, float unitPrice, int stock) throws RemoteException {
        super();
        this.name = name;
        this.unitPrice = unitPrice;
        this.stock = stock;
    }

    @Override
    public synchronized MedicineInterface getMedicine(int amount) throws RemoteException, StockException {
        if (amount <= 0) {
            throw new StockException("Amount must be greater than 0");
        }
        if (stock <= 0) {
            throw new StockException("Stock empty");
        }
        if (stock < amount) {
            throw new StockException("Stock not enough for requested amount");
        }

        stock -= amount;
        return new Medicine(name, unitPrice * amount, amount);
    }

    @Override
    public synchronized int getStock() throws RemoteException {
        return stock;
    }

    @Override
    public synchronized String print() throws RemoteException {
        return "Name: " + name + "\nPrice: " + unitPrice + "\nStock: " + stock;
    }
}
