import java.io.*;
import java.net.*;
//import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserHandler extends Thread {
    private Socket connection;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private List<Worker> workers;
    private int userid;
    private Map<Integer, Integer> workerPorts;

    public UserHandler(Socket connection, Map<Integer, Integer> workerPorts, ObjectInputStream inputStream) throws IOException {
        this.connection = connection;
       // this.workers = workers;
        this.in = inputStream;
        this.workerPorts = workerPorts;
        
    }

    public void run() {
        try {
            String type = in.readUTF(); // receives the type of user

            if (type.equals("Client")) {
                String function = in.readUTF(); // receives the request e.x filtering
                String FilterDetails = in.readUTF(); // receives the details for the filtering
                userid = connection.getPort();
                Request newrequest = new Request(type, function, FilterDetails,userid);

                if (function.equals("1")){ //Filtering
                    // Στέλνουμε το αίτημα σε κάθε Worker
                    for (Map.Entry<Integer, Integer> entry : workerPorts.entrySet()) {
                        int workerId = entry.getKey();
                        int port = entry.getValue();

                        try {
                            Socket socket = new Socket("localhost", port); //Needs setting if workers are on different hosts!!
                            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                            out.writeObject(newrequest);
                            out.flush();
                            out.close();
                            socket.close();
                            System.out.println("Sent request to Worker " + workerId);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }else if(function.equals("2")){//Rating
                    String[] details = FilterDetails.split(",");
                    String roomname = details[0];
                }
                
            }

            try {
                // Make the current thread sleep for 1 second
                Thread.sleep(1000); // Sleep for 1 second (1000 milliseconds)
            } catch (InterruptedException e) {
                // Handle the interruption if needed
                e.printStackTrace();
            }
            

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (!connection.isClosed()) {
                    if (in != null) in.close();
                    if (out != null) out.close();
                    System.out.println("User handler Closing connection");
                    connection.close();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
