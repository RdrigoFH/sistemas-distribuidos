
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface StockInterface extends Remote {
    Map<String, MedicineInterface> getStockProducts() throws RemoteException;

    void addMedicine(String name, float price, int stock) throws RemoteException;

    MedicineInterface buyMedicine(String name, int amount) throws RemoteException, StockException;
}
