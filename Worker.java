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
            System.out.println(assignedRooms);
            while (true) {
                Socket userHandlersocket = serverSocket.accept();
                out = new ObjectOutputStream(userHandlersocket.getOutputStream());
                in = new ObjectInputStream(userHandlersocket.getInputStream());
                // Read the request object from the socket
                Request request = (Request) in.readObject();
                int key = request.UserPort;
                System.out.println("Worker " + id + " processing request: " + request);
                
                WorkerThread workerThread = new WorkerThread(request,key);
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

    private class WorkerThread extends Thread {
        private Request request;
        private int key;

    public WorkerThread(Request request, int key) {
        this.request = request;
        this.key = key;
    }

    public void run() {
        // Εδώ γίνεται η επεξεργασία του αιτήματος
        //System.out.println("WorkerThread processing request: " + request);
        //Filtering 
        Pair<Integer, List<Room>> result = map(key, request);
        System.out.println(result.getKey());
        System.out.println(result.getValue());
    
    }
}
    private class Pair<K, V> implements Serializable{
        private final K key;
        private final V value;

        public Pair(K key, V value)
        {
            this.key = key;
            this.value = value;
        }

        public K getKey()
        {
            return key;
        }

        public V getValue()
        {
            return value;
        }
    }   

      // Method to assign a room to the worker
      public void assignRoom(Room room) {
        assignedRooms.add(room);
    }

    public int getPort() {
        return port;
    }
    
    public Pair<Integer, List<Room>> map(int key, Request value) {
        List<Room> roomList = new ArrayList<>();
        if (value.type.equals("Client") && value.function.equals("1")) {
            if (value.details.equals("Location")) {
                for (Room room : assignedRooms) {
                    roomList.add(room);
                }
            }
        }
        return new Pair<>(key, roomList);
    }
}


