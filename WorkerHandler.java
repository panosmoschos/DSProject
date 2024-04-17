import java.io.*;
import java.net.Socket;
import java.util.Map;

public class WorkerHandler extends Thread {
    private Socket socket;
    private Map<Integer, Socket> portSockets;
    private ObjectInputStream in;

    public WorkerHandler(Socket socket,Map<Integer, Socket> portSockets,ObjectInputStream inputStream) {
        this.socket = socket;
        this.portSockets = portSockets;
        this.in = inputStream;
    }

    @Override
    public void run() {
        try{

            @SuppressWarnings("unchecked")
            Pair<Integer, String> result =  (Pair<Integer, String>) in.readObject();
            
            // Get the port number from the result
            int portNumber = result.getKey();

            // Get the socket associated with the port number
            Socket userSocket = portSockets.get(portNumber);

            // Write the result to the user's socket
            if (userSocket != null) {
                try (ObjectOutputStream out = new ObjectOutputStream(userSocket.getOutputStream())) {
                    
                    out.writeObject(result);
                    System.out.println("WorkerHandler sent the results!!" );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("User socket not found for user ID: " );
            }

        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        } finally {
            // Close the socket
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
