import java.io.*;
import java.net.*;
import java.util.*;

class Worker extends Thread {
    private Socket socket;
    private Map<String, Integer> data;

    public Worker(Socket socket, Map<String, Integer> data) {
        this.socket = socket;
        this.data = data;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String request = in.readLine();
            String[] parts = request.split(":");
            String command = parts[0];

            if (command.equals("COUNT")) {
                String key = parts[1];
                int count = data.containsKey(key) ? data.get(key) : 0;
                out.println(count);
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class Master {
    private static final int PORT = 12345;

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
