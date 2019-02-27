import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConsumerClient {

    public static void main(String[] args) throws IOException {
        int num_cons = Integer.valueOf(args[0]);

        for (int i = 0; i < num_cons; i++) {
            new Thread(new ConsRequestor(i)).start();

        }
    }
}

class ConsRequestor implements Runnable {
    private int id;
    private Socket server;
    private DataOutputStream writer;
    private DataInputStream reader;

    ConsRequestor(int id) throws IOException {
        this.id = id;
    }

    @Override
    public void run() {
        while (true) {
            try {
                server = new Socket("glados.cs.rit.edu", 55444);
                reader = new DataInputStream(server.getInputStream());
                writer = new DataOutputStream(server.getOutputStream());
                //Send info
                writer.writeInt(1);
                writer.writeInt(id);

                // Get ACK.
                if(reader.readInt() == 1) {
                    System.out.println("Consumer " + id + " successfully consumed 3 type1 items, 5 type2 items, and 2 type3 items");
                }
                Thread.sleep(1000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}