/*
 * ConsumerProducer2.java
 *
 * Version:
 *     1.0
 *
 * Revisions:
 *     0
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The main class, contains main() method that parses the user inputs and starts the producer
 * and consumer threads accordingly.
 */
public class StorageServer {
    public static void main(String[] args) throws IOException {
        int storage_size;
        Storage[] allStorages = new Storage[3];
        for(int i=0; i<3; i++)
            allStorages[i] = new Storage();

        storage_size = Integer.valueOf(args[0]);
        ServerSocket serverSocket = new ServerSocket(55444);
        Socket client;
        DataInputStream reader;
        DataOutputStream writer;

        while(true){
            client = serverSocket.accept(); //ProdRequestor or Consumer.
            reader = new DataInputStream(client.getInputStream());
            writer = new DataOutputStream(client.getOutputStream());
            int clientType = reader.readInt();
            int id = reader.readInt();
            if(clientType == 0) {
                int num_items = reader.readInt();
                int storageType = reader.readInt();
                new Thread(new ProducerServiceThread(id, storageType, allStorages[storageType], num_items,
                        storage_size, writer)).start();
            }
            else if(clientType == 1)
                new Thread(new ConsumerServiceThread(id, allStorages, writer)).start();
        }
    }
}

/**
 * This class represents the Storages used by consumers and producers.
 */
class Storage{
    private int numItems;
    Storage(){
        this.numItems = 0;
    }

    void addItems(int n){
        numItems += n;
    }

    void removeItems(int n){
        numItems -= n;
    }

    int getNumItems(){
        return numItems;
    }
}

/**
 * This class represents a single ProducerServiceThread thread.
 */
class ProducerServiceThread implements Runnable {
    private int id, prod_items, storage_size, type;
    private Storage storage;
    private DataOutputStream writer;

    ProducerServiceThread(int id, int type, Storage storage, int prod_items, int storage_size, DataOutputStream writer) {
        this.id = id;
        this.storage = storage;
        this.prod_items = prod_items;
        this.storage_size = storage_size;
        this.type = type;
        this.writer = writer;
    }

    @Override
    public void run() {

            // Synchronize on Storage object.
            synchronized (storage) {
                // Wait for the consumers to consume till there's enough space in allStorages[type] to add
                // new items.
                while (storage.getNumItems() + prod_items > storage_size) {
                    try {
                        System.out.println("Not enough space to complete request by Producer "+id+", " +
                                "waiting for consumers to consume type " + (type+1) + " items");
                        storage.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Space available, add new items.
                storage.addItems(prod_items);
                System.out.println("Request by Producer " + id + " to produce "  + prod_items + " type" + (type+1) +
                        " items complete. Storage" + (type+1) +": " + storage.getNumItems());

                //Send ACK to Producer
                try {
                    writer.writeInt(1);
                    writer.flush();
                    // writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Notify Consumers that items are available to consume from this storage.
                storage.notifyAll();
            }
    }
}

/**
 * This class represents a single Consumer thread.
 */
class ConsumerServiceThread implements Runnable {
    private int id;
    private int[] cons_item = {3, 5, 2};
    private Storage[] allStorages;
    private DataOutputStream writer;

    ConsumerServiceThread(int id, Storage[] allStorages, DataOutputStream writer) {
        this.id = id;
        this.allStorages = allStorages;
        this.writer = writer;
    }

    @Override
    public void run() {
            // Loop through all 3 types of storages.
            for (int i = 0; i < 3; i++) {
                synchronized (allStorages[i]) {
                    // Wait for the producers to produce till there are no items available to consume from this storage.
                    while (allStorages[i].getNumItems() - cons_item[i] < 0) {
                        try {
                            System.out.println("Not enough items to complete request by Consumer " + id +
                                    ", waiting for producers to produce Type " + i + " items.");
                            allStorages[i].wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    // remove items from this storage.
                    allStorages[i].removeItems(cons_item[i]);

                    // Send ACK.
                    try {
                        writer.writeInt(1);
                        writer.flush();
//                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    allStorages[i].notifyAll();
                }
            }
            System.out.println("Request by Consumer " + id + " complete.");
    }
}

/**
 * Class representing a custom exception thrown when illegal numbers are given as input.
 */
class IllegalNumberException extends Exception{
    IllegalNumberException(String m){
        super(m);
    }
}
