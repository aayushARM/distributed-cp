import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Main class, has code that spawns given number of consumers.
 */
public class ConsumerClient {

    /**
     * @param args args[0] is number of Consumers.
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, NotBoundException {
        int num_cons = Integer.valueOf(args[0]);
        StorageInterface[] allStorages = new StorageInterface[3];
        for(int type = 0; type<3; type++)
            allStorages[type] = (StorageInterface)Naming.lookup("//glados.cs.rit.edu/storage" + type);

        for (int i = 0; i < num_cons; i++) {

            // Spawn threads that make RMI calls to remote object.
            new Thread(new Consumer(i, allStorages)).start();
        }
    }
}

/**
 * A thread that makes the RMI calls to consume items from remote Storage object.
 */
class Consumer implements Runnable {
    private int id;
    private StorageInterface[] allStorages;
    private int[] cons_items = {3, 5, 2};

    Consumer(int id, StorageInterface[] allStorages) {
        this.id = id;
        this.allStorages = allStorages;
    }

    @Override
    public void run() {

        // Run indefinitely.
        while (true) {
            // Iterate through 3 types of storages.
            for(int type = 0; type < 3; type++) {
                try {
                    // Remove items from this storage.
                    allStorages[type].removeItems(id, cons_items[type], type);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}