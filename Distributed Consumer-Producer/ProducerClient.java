import java.io.*;
import java.net.Socket;

public class ProducerClient {

    public static void main(String[] args) throws IOException {

        int[] num_prod = new int[3];
        int[] prod_items = new int[3];

        for(int type = 0, arg_cnt = 0; type < 3; type++) {
            num_prod[type] = Integer.valueOf(args[arg_cnt++]);
            prod_items[type] = Integer.valueOf(args[arg_cnt++]);
        }

        // Start ProducerServiceThread threads.
        for(int type = 0, cnt=0; type < 3; type++) {
            for (int j=0; j < num_prod[type]; j++) {
                new Thread(new ProdRequestor(cnt++, type, prod_items[type])).start();
            }
        }
    }
}

class ProdRequestor implements Runnable {
    private int id, prod_items, type;
    private DataOutputStream writer;
    private DataInputStream reader;
    private Socket server;

    ProdRequestor(int id, int type, int prod_items) throws IOException {
        this.id = id;
        this.prod_items = prod_items;
        this.type = type;
    }

    @Override
    public void run() {
        // Run indefinitely.
        while (true) {
        try {
            server = new Socket("localhost", 12345);
            writer = new DataOutputStream(server.getOutputStream());
            reader = new DataInputStream(server.getInputStream());
            // Send info
            writer.writeInt(0);
            writer.writeInt(id);
            writer.writeInt(prod_items);
            writer.writeInt(type);
            writer.flush();

            // Get ACK.
            if(reader.readInt() == 1) {
                System.out.println("Producer " + id + " successfully produced " + prod_items + " type" + (type + 1) +
                        " items.");
            }
            Thread.sleep(1000);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        }
    }
}

