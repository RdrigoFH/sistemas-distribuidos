
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MedicineInterface extends Remote {
    MedicineInterface getMedicine(int amount) throws RemoteException, StockException;

    int getStock() throws RemoteException;

    String print() throws RemoteException;
}
