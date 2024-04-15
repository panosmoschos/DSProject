import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Master {
    private static final int NUM_WORKERS = 3;
    private static List<Worker> workers = new ArrayList<>();
    private static List<Room> rooms = new ArrayList<>();
    private static Map<Integer, Socket> portSockets = new HashMap<>();
    

    public static void main(String[] args) {

        //Initialize the rooms.
        String folderPath = "bin/rooms";
        rooms = Room.roomsOfFolder(folderPath);

        //Start the Master Server.
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Master Server started...");

            int[] ports = {8000, 8001, 8002};
            for (int i = 0; i < NUM_WORKERS; i++) {
                Worker worker = new Worker(i,ports[i]);
                workers.add(worker);
                worker.start();
            }

            int numRooms = rooms.size();
            int workerIndex = 0;
            for (int i = 0; i < numRooms; i++) {
                Room room = rooms.get(i);
                Worker worker = workers.get(workerIndex);
                worker.assignRoom(room);
                workerIndex = (workerIndex + 1) % NUM_WORKERS;
            }

            // Hashing and assigning the starting rooms to the workers
            //addRooms(folderPath);

            while (true) {
                Socket socket = serverSocket.accept();
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                // Check if the connection is from the Reducer or a new user.
                if (isConnectionFromReducer(socket,inputStream)) {
                    System.out.println("New REDUCER connection: " + socket);
                    portSockets.put(socket.getPort(), socket);
                    new ReducerHandler(socket,portSockets,inputStream).start();
                } else {
                    System.out.println("New USER connection: " + socket);
                    portSockets.put(socket.getPort(), socket);
                    new UserHandler(socket, workers, inputStream).start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    private static boolean isConnectionFromReducer(Socket socket, ObjectInputStream inputStream) {//Checks if the new connection is from a new user of the reducer.
       
       try {
            String type = inputStream.readUTF();
            if (type.equals("USER")){
                return false;
            }else if(type.equals("REDUCER")){
                return true;
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }return false;
    }
    

    public static int hash(String roomName){ // simple hash function
        int hash=7;
        for (int i = 0; i < roomName.length(); i++) {
            hash = (hash*11 + roomName.charAt(i)) % 3;
        }
        return hash;
    }

    /*private static void addRooms(String givenPath) { // distributing room(s) to workers 
        rooms = Room.roomsOfFolder(givenPath);
        for (Room room : rooms) {
            int workerID = hash(room.getRoomName()) % NUM_WORKERS;                
            Worker worker = workers.get(workerID);
            worker.assignRoom(room);
        }
    }*/
}