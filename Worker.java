import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Worker extends Thread {
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private int id,port;
    private List<Room> assignedRooms;
    
    public Worker(int id,int port) {
        this.id = id;
        this.port = port;
        this.assignedRooms = new ArrayList<>();
        
    }
    
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Worker started" + port);
            while (true) {
                Socket userHandlersocket = serverSocket.accept();
                out = new ObjectOutputStream(userHandlersocket.getOutputStream());
                in = new ObjectInputStream(userHandlersocket.getInputStream());
                // Read the request object from the socket
                Request request = (Request) in.readObject();
                System.out.println("Worker " + id + " processing request: " + request);
                WorkerThread workerThread = new WorkerThread(request);
                workerThread.start();

                // Apply the map function on the request
                // Here you can implement your map function logic

                // Close the socket and streams
                in.close();
                out.close();
                userHandlersocket.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    

    // Method to assign a room to the worker
    public void assignRoom(Room room) {
        assignedRooms.add(room);
    }

    public int getPort() {
        return port;
    }


    private class WorkerThread extends Thread {
        private Request request;

    public WorkerThread(Request request) {
        this.request = request;
    }

    public void run() {
        // Εδώ γίνεται η επεξεργασία του αιτήματος
        System.out.println("WorkerThread processing request: " + request);
        //Filtering 
        if (request.type.equals("Client") && request.function.equals("1")){
            if(request.details.equals("Location")){
                for (Room room : assignedRooms){
                    System.out.println(room.getArea(room));
                }
            }
            

        }
        // Παράδειγμα επεξεργασίας: Καθυστέρηση για να προσομοιώσουμε επεξεργασία
        try {
            System.out.println("WorkerThread processing request: " + request.type);
            Thread.sleep(2000); // Καθυστέρηση για 2 δευτερόλεπτα
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Ολοκληρώθηκε η επεξεργασία, μπορούμε να επιστρέψουμε το αποτέλεσμα ή να ενημερώσουμε κάποιον άλλο μηχανισμό
        // Για παράδειγμα, μπορούμε να ενημερώσουμε τον αιτούντα για την ολοκλήρωση του αιτήματος
    }
}
}


