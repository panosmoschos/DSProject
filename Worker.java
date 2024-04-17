import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Worker extends Thread {
    private ObjectInputStream in;
    private int id,port;
    private String host;
    private List<Room> assignedRooms;
    
    public Worker(int id,int port, String host) {
        this.id = id;
        this.port = port;
        this.host = host;
        this.assignedRooms = new ArrayList<>();
    }

    public static void main(String[] args) {
        int port = 8000;
        String host = "localhost";
        Worker worker = new Worker(0, port, host);
        worker.start();
    }
    
    @SuppressWarnings("unchecked")
    public void run() {
        
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Worker started " + port + " " + host);

            Socket mastersocket = serverSocket.accept();
            in = new ObjectInputStream(mastersocket.getInputStream());//Read the rooms from master
            assignedRooms = (List<Room>) in.readObject();
            System.out.println(assignedRooms);

            while (true) {
                Socket userHandlersocket = serverSocket.accept();
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

        // Client - Filtering 
        if (request.function.equals("1") && request.type.equals("Client")){
            Pair<Integer, List<Room>> result = map(key, request);
            sendResultsToReducer(result);
        }
        
        // Client - Booking
        if (request.function.equals("2") && request.type.equals("Client")){
            String[] details = request.details.split(",");
            String roomname = details[0];
            //int bookingsBefore = 0;
            //int bookingsAfter = 0;
            for ( Room room : assignedRooms){
                if (room.getRoomName().equals(roomname)){
                    //bookingsBefore= room.getNumberOfBookings();
                    room.addBooking(request);
                    //bookingsAfter = room.getNumberOfBookings();

                    /* 
                    if (bookingsBefore == bookingsAfter){
                        // emfanise to ston user
                    }
                    */
                    
                    break;
                }
            }
        }

        // Client - Rating
        if (request.function.equals("3") && request.type.equals("Client")){
            String[] details = request.details.split(",");
            String roomname = details[0];
            //int reviewsBefore = 0;
            //int reviewsAfter = 0;
            for ( Room room : assignedRooms){
                if (room.getRoomName().equals(roomname)){
                    //reviewsBefore = room.getNoReviews();
                    room.ratingChanges(request);
                    //reviewsAfter = room.getNoReviews();

                    /*
                    if (reviewsBefore == reviewsAfter){
                        // emfanise to ston user
                    }
                    */

                    break;
                }
            }
        }


        // Manager - Add room
        if (request.type.equals("Manager") && request.function.equals("1")){
            Room newRoom = Room.addRoom(request);
           // int numberOfRoomsBefore = assignedRooms.size();
            assignedRooms.add(newRoom);
            //int numberOfRoomsAfter = assignedRooms.size();
            
            /* 
            if (numberOfRoomsBefore == numberOfRoomsAfter){
                // emfanise to ston user
            }
            */
        }

        // Manager - Add availability
        if (request.type.equals("Manager") && request.function.equals("2")){
            String[] details = request.details.split(",");
            String roomname = details[0];
            //int daysBefore = 0;
            //int daysAfter = 0;
            for (Room room : assignedRooms){
                if (room.getRoomName().equals(roomname)){
                    //daysBefore = Available_Date.DaysAvailable(room.getAvailability());
                    room.addAvailability(request);
                    //daysAfter = Available_Date.DaysAvailable(room.getAvailability());
                    
                    /* 
                    if (daysBefore == daysAfter){
                        // emfanise to ston user
                    }
                    */
                    
                    break;
                }
            }
        }

        // Manager - Gather bookings of owner
        if (request.type.equals("Manager") && request.function.equals("3")){
            Pair<Integer, List<Booking>> result = mapbookings(key, request);
            sendOwnerBookingsToReducer(result);
            
        }

        // Manager - Gather bookings by area
        if (request.type.equals("Manager") && request.function.equals("4")){
            Pair<Integer, List<Booking>> result = mapbookingsByArea(key, request);
            sendOwnerBookingsToReducer(result);
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

    public Pair<Integer,List<Booking>> mapbookings(int key,Request value){
        List<Booking> bookings = new ArrayList<>();
        for (Room room : assignedRooms){
            bookings = room.getOwnerBookings(value);
        }
        return new Pair<>(key,bookings);
    }

    public Pair<Integer,List<Booking>> mapbookingsByArea(int key,Request value){
        List<Booking> bookings = new ArrayList<>();
        for (Room room : assignedRooms){
            bookings = room.getAreaBookingsBetween(value);
        }
        return new Pair<>(key,bookings);
    }

    private void sendResultsToReducer(Pair<Integer, List<Room>> result) {
        try (Socket reducerSocket = new Socket("localhost", 23456);
            ObjectOutputStream out = new ObjectOutputStream(reducerSocket.getOutputStream())) {
            out.writeObject(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendOwnerBookingsToReducer(Pair<Integer, List<Booking>> result) {
        try (Socket reducerSocket = new Socket("localhost", 23456); //NEEDS SETTING
            ObjectOutputStream out = new ObjectOutputStream(reducerSocket.getOutputStream())) {
            out.writeObject(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
}


