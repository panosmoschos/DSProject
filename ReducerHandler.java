import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class ReducerHandler extends Thread {
    private Socket socket;
    private Map<Integer, Socket> portSockets;
    private List<Pair<Integer, List<Room>>> results;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ReducerHandler(Socket socket,Map<Integer, Socket> portSockets) {
        this.socket = socket;
        this.portSockets = portSockets;
    }

    @Override
    public void run() {
        try {
            handleReducerCommunication();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   

    private void handleReducerCommunication() throws IOException {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            @SuppressWarnings("unchecked")
            Pair<Integer, List<Room>> result = (Pair<Integer, List<Room>>) in.readObject();

            // Process the received result (e.g., store it)
            synchronized (results) {
                results.add(result);
            }

            // Get the port number from the result
            int portNumber = result.getKey();

            // Get the socket associated with the port number
            Socket userSocket = portSockets.get(portNumber);

            // Write the result to the user's socket
            if (userSocket != null) {
                try (ObjectOutputStream out = new ObjectOutputStream(userSocket.getOutputStream())) {
                    out.writeObject(result);
                }
            } else {
                System.out.println("User socket not found for user ID: " );
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            // Close the socket
            socket.close();
        }
    }

   
}
