
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class CurrencyConverterImpl extends UnicastRemoteObject implements CurrencyConverterInterface {
    private static final double TASA_DOLAR = 3.80;
    private static final double TASA_EURO = 4.05;

    public CurrencyConverterImpl() throws RemoteException {
        super();
    }

    @Override
    public double convertirADolares(double monto) throws RemoteException {
        return monto / TASA_DOLAR;
    }

    @Override
    public double convertirAEuros(double monto) throws RemoteException {
        return monto / TASA_EURO;
    }
}
