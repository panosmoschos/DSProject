import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class Master {
    private static final int NUM_WORKERS = 3;
    private static List<Worker> workers = new ArrayList<>();
    private static List<Room> rooms = new ArrayList<>();
    private static Map<Integer, Socket> portSockets = new HashMap<>();
    

    public static void main(String[] args) {
        String folderPath = "bin/rooms";
        //rooms = readFolder(folderPath);
        rooms = readFolder(folderPath);

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
                //System.out.println("New connection: " + socket);
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                // Check if the connection is from the Reducer
                if (isConnectionFromReducer(socket,inputStream)) {
                    System.out.println("New REDUCER connection: " + socket);
                // Handle connection from Reducer
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


    public static List<Room> readFolder(String path) {//read and list the rooms of a folder 
        File dir = new File(path);
        File[] files = dir.listFiles();

        for (File f : files){
            try {
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(new FileReader(f));
                JSONObject JSONobj= (JSONObject) obj;

                String roomName = (String) JSONobj.get("roomName");
                int noOfPersons = ((Long) JSONobj.get("noOfPersons")).intValue();
                String area = (String) JSONobj.get("area");
                int stars = ((Long) JSONobj.get("stars")).intValue();
                int noOfReviews = ((Long) JSONobj.get("noOfReviews")).intValue();
                String roomImage = (String) JSONobj.get("roomImage");
                int price = ((Long) JSONobj.get("price")).intValue();
                String owner = (String) JSONobj.get("owner");

                List<Available_Date> dates = new ArrayList<>();

                JSONArray available_dates = (JSONArray) JSONobj.get("availability");
                for (Object Obj : available_dates){
                    JSONObject jsonOB = (JSONObject) Obj;
                    String FirstDayString = (String) jsonOB.get("start_date");
                    String LastDayString = (String) jsonOB.get("end_date");
                    
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                    LocalDate FirstDayDate = LocalDate.parse(FirstDayString,df);  
                    LocalDate LastDayDate =  LocalDate.parse(LastDayString,df);
                    

                    dates.add(new Available_Date(FirstDayDate,LastDayDate));
                }

                Room room = new Room(roomName, noOfPersons, area, stars, noOfReviews, roomImage, price, dates, owner);
                rooms.add(room);

            }catch (Exception e){
                System.out.println( "Exception:" + e);
            }   
        }
        return rooms;
    }


    private static boolean isConnectionFromReducer(Socket socket, ObjectInputStream inputStream) {
       
       try {
            //ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            String type = inputStream.readUTF();
            if (type.equals("CLIENT")){
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

    private static void addRooms(String givenPath) { // distributing room(s) to workers 
        rooms = readFolder(givenPath);
        for (Room room : rooms) {
            int workerID = hash(room.getRoomName()) % NUM_WORKERS;                
            Worker worker = workers.get(workerID);
            worker.assignRoom(room);
        }
    }



}