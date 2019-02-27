import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Main class, has code that spawns given number of producers..
 */
public class ProducerClient {

    /**
     * @param args the number of prodcers of each type and the number of items produced by each type
     *             of producer.
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, NotBoundException {
        int[] num_prod = new int[3];
        int[] prod_items = new int[3];
        StorageInterface[] allStorages = new StorageInterface[3];

        // Lookup all the remote storage objects and store them.
        for(int type = 0; type<3; type++)
            allStorages[type] = (StorageInterface)Naming.lookup("//glados.cs.rit.edu/storage" + type);

        // Parse command line inputs.
        for(int type = 0, arg_cnt = 0; type < 3; type++) {
            num_prod[type] = Integer.valueOf(args[arg_cnt++]);
            prod_items[type] = Integer.valueOf(args[arg_cnt++]);
        }

        // Start Producer threads.
        for(int type = 0, cnt=0; type < 3; type++) {
            for (int j=0; j < num_prod[type]; j++) {
                new Thread(new Producer(cnt++, allStorages[type], prod_items[type], type)).start();
            }
        }
    }
}

/**
 * A thread that makes RMI call to put items in remote storage object..
 */
class Producer implements Runnable {
    private int id, type, prod_items;
    private StorageInterface storage;

    Producer(int id, StorageInterface storage, int prod_items, int type){
        this.id = id;
        this.storage = storage;
        this.type = type;
        this.prod_items = prod_items;
    }

    @Override
    public void run() {
        // Run indefinitely.
        while (true) {
            try {
                // Put items.
                storage.addItems(id, prod_items, type);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}