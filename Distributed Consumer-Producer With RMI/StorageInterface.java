import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * An interface extending Remote for use with RMI.
 */
interface StorageInterface extends Remote {

    void addItems(int id, int prod_items, int type) throws RemoteException;
    void removeItems(int id, int cons_items, int type) throws RemoteException;
}