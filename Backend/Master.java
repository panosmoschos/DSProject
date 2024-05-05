import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Master {
    public static final int NUM_WORKERS = 3; //Change based on the number of workers
    private static List<Room> rooms = new ArrayList<>();
    private static Map<Integer, Socket> portSockets = new HashMap<>();
    private static Map<Integer, Integer> workerPorts = new HashMap<>(); // Map to store worker ports
    private static Map<Integer, String> workerHosts = new HashMap<>(); // Map to store worker host

    

    public static void main(String[] args) {

        //Initialize the rooms.
        String folderPath = "Backend/bin/rooms";
        rooms = Room.roomsOfFolder(folderPath);

        // Specify the port and host for each worker
        int[] ports = {8000, 8001, 8002}; //Change based on the number of workers
        String [] hosts = {"localhost","localhost","localhost"}; //Change based on the number of workers
        for (int i = 0; i < NUM_WORKERS; i++) {
            workerPorts.put(i, ports[i]); // Store worker port
            workerHosts.put(i,hosts[i]); //Store worker host
        }

        //Start the Master Server.
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Master Server started...");

            // Send rooms to workers
            sendRoomsToWorkers(rooms);

            while (true) {
                Socket socket = serverSocket.accept();
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                // Check if the connection is from the Reducer or a new user.

                int connection  = ConnectionFrom(socket,inputStream);
                if(connection == 1){ //user
                    System.out.println("New USER connection: " + socket);
                    portSockets.put(socket.getPort(), socket);
                    new UserHandler(socket, workerPorts, workerHosts, inputStream).start();
                }else if (connection == 2){//reducer
                    System.out.println("New REDUCER connection: " + socket);
                    portSockets.put(socket.getPort(), socket);
                    new ReducerHandler(socket,portSockets,inputStream).start();
                }else if (connection == 3){// worker
                    System.out.println("New WORKER connection: " + socket);
                    portSockets.put(socket.getPort(), socket);
                    new WorkerHandler(socket,portSockets,inputStream).start();
                } 
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //Checks where the connection is coming from
    private static int ConnectionFrom(Socket socket, ObjectInputStream inputStream){
        try {
            String type = inputStream.readUTF();
            if (type.equals("USER")){
                return 1;
            }else if(type.equals("REDUCER")){
                return 2;
            }else if(type.equals("WORKER")){
                return 3;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }return 0;
    }



    public static int hash(String roomName){ // simple hash function
        int hash=7;
        for (int i = 0; i < roomName.length(); i++) {
            hash = (hash*11 + roomName.charAt(i)) % 3;
        }
        return hash;
    }

    public static String getHost(Map<Integer, String> workerHosts, int workerID){
        return workerHosts.get(workerID);
    }

    private static void sendRoomsToWorkers(List<Room> rooms) {
        // Distribute rooms among workers
        for (Map.Entry<Integer, Integer> entry : workerPorts.entrySet()) {
            int workerId = entry.getKey();
            int port = entry.getValue();
            String host = getHost(workerHosts, workerId);
    
            // Get the subset of rooms assigned to this worker
            List<Room> workerRooms = getWorkerRooms(workerId, rooms);
    
            try (Socket socket = new Socket(host, port);
                 ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())) {
    
                // Send rooms to worker
                outputStream.writeObject(workerRooms);
                System.out.println("Sent rooms to Worker " + workerId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static List<Room> getWorkerRooms(int workerId, List<Room> allRooms) {
        List<Room> workerRooms = new ArrayList<>();
        for (Room room : allRooms) {
            if (hash(room.getRoomName()) % NUM_WORKERS == workerId) {
                workerRooms.add(room);
            }
        }
        return workerRooms;
    }
}