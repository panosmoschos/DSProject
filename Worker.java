import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Worker extends Thread {
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
                System.out.println(userHandlersocket);
                in = new ObjectInputStream(userHandlersocket.getInputStream());
                // Read the request object from the socket
                Request request = (Request) in.readObject();
                int key = request.UserPort;
                System.out.println("Worker " + id + " processing request: " + request);
                
                WorkerThread workerThread = new WorkerThread(request,key);
                workerThread.start();
                
                // Close the socket and streams
                in.close();
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
        if (request.function.equals("1")){
            Pair<Integer, List<Room>> result = map(key, request);
            sendResultsToReducer(result);
            System.out.println(result.getKey());
            System.out.println(result.getValue());
        }
        
        //Booking
        if (request.function.equals("2")){
            String[] details = request.details.split(",");
            String roomname = details[0];
            String dates = details[1];
            for ( Room room : assignedRooms){
                if (room.getRoomName().equals(roomname)){
                   // room.addBooking(dates); //NEEDS IMPLEMENTATION! IN ROOM.JAVA
                    continue;
                }
            }
        }

        //Rating
        if (request.function.equals("3")){
            String[] details = request.details.split(",");
            String roomname = details[0];
            for ( Room room : assignedRooms){
                if (room.getRoomName().equals(roomname)){
                    room.ratingChanges(request);
                    System.out.println(room.getStars());
                    continue;
                }
            }
        }

        
    
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
        roomList = Room.filterRooms(assignedRooms,value);
        return new Pair<>(key, roomList);
    }

    private void sendResultsToReducer(Pair<Integer, List<Room>> result) {
        try (Socket reducerSocket = new Socket("localhost", 23456);
            ObjectOutputStream out = new ObjectOutputStream(reducerSocket.getOutputStream())) {
            out.writeObject(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


