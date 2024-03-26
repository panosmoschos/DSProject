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
