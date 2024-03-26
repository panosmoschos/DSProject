import java.io.*;
import java.net.*;
import java.util.*;

public class Master {
    private static final int PORT = 12345;

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        Map<String, Integer> data = new HashMap<>();
        // Εδώ μπορείτε να προσθέσετε αρχικά δεδομένα στο Map, αν είναι απαραίτητο

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Master listening on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                Worker worker = new Worker(socket, data);
                worker.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}