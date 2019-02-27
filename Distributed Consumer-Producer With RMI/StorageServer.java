

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Creates 3 types of storages and binds them to the RMI Registry.
 */
public class StorageServer {
    public static void main(String[] args) throws RemoteException, MalformedURLException {
        int storage_size;
        StorageInterface[] allStorages = new Storage[3];
        storage_size = Integer.valueOf(args[0]);

        // Create 3 types of Storages, and bind them to unique names.
        for(int i=0; i<3; i++) {
            allStorages[i] = new Storage(storage_size);
            Naming.rebind("//localhost/storage" + i, allStorages[i]);
            System.out.println("Storage" + i +" bound in registry.");
        }
    }
}

/**
 * This class represents a Storage and has methods that are used by remote Consumers and Producers.
 */
class Storage extends UnicastRemoteObject implements StorageInterface{
    private int numItems, storage_size;
    Storage(int storage_size) throws RemoteException {
        this.numItems = 0;
        this.storage_size = storage_size;
    }

    public synchronized void addItems(int id, int prod_items, int type){

        // Wait for the consumers to consume till there's enough space in allStorages[type] to add
        // new items.
        while (numItems + prod_items > storage_size) {
            try {
                System.out.println("Not enough space to complete request by Producer "+id+", " +
                        "waiting for consumers to consume type " + (type+1) + " items");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Space available, add new items.
        numItems += prod_items;

        System.out.println("Request by Producer " + id + " to produce "  + prod_items + " type" + (type+1) +
                " items complete. Storage" + (type+1) +": " + numItems);

        // Notify Consumers that items are available to consume from this storage.
        notifyAll();
        }

    public synchronized void removeItems(int id, int cons_items, int type){
        // Wait for the producers to produce till there are no items available to consume from this storage.
        while (numItems - cons_items < 0) {
            try {
                System.out.println("Not enough items to complete request by Consumer " + id +
                        ", waiting for producers to produce Type " + (type+1) + " items.");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // remove items from this storage.
        numItems -= cons_items;
        notifyAll();

        System.out.println("Request by Consumer " + id + " complete.");
    }
}