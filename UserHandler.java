import java.io.*;
import java.net.*;
//import java.util.ArrayList;
import java.util.List;

public class UserHandler extends Thread {
    private Socket connection;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private List<Worker> workers;
    private int userid;

    public UserHandler(Socket connection,List<Worker> workers) throws IOException {
        this.connection = connection;
        this.workers = workers;
        in = new ObjectInputStream(connection.getInputStream());
    }

    public void run() {
        try {
            String type = in.readUTF(); // receives the type of user

            if (type.equals("Client")) {
                String function = in.readUTF(); // receives the request e.x filtering
                String FilterDetails = in.readUTF(); // receives the details for the filtering
                userid = connection.getPort();
                Request newrequest = new Request(type, function, FilterDetails,userid);

               // Στέλνουμε το αίτημα σε κάθε Worker
                for (Worker worker : workers) {
                    try {
                        Socket socket = new Socket("localhost", worker.getPort());
                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                        out.writeObject(newrequest); // Αποστολή του αιτήματος
                        out.flush();
                        out.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                connection.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
