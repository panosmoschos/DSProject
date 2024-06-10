
import java.io.*;
import java.net.Socket;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ReducerHandler extends Thread {
    private Socket socket;
    private Map<Integer, Socket> portSockets;
    private ObjectInputStream in;

    public ReducerHandler(Socket socket,Map<Integer, Socket> portSockets,ObjectInputStream inputStream) {
        this.socket = socket;
        this.portSockets = portSockets;
        this.in = inputStream;
    }

    @Override
    public void run() {
        try{
            Object ob = in.readObject();
            if (ob instanceof Pair){
                @SuppressWarnings("unchecked")
                Pair<Integer,?> res = (Pair<Integer,?>) ob;
                if (res.getValue() instanceof List){
                    List<?> whatType = (List<?>) res.getValue();
                    if (whatType.get(0) instanceof Room){
                        @SuppressWarnings("unchecked")
                        Pair <Integer,List<Room>> result = (Pair<Integer,List<Room>>) ob;
                        int portNumber = result.getKey();
                        Socket userSocket = portSockets.get(portNumber);
                        if (userSocket != null) {
                            try (ObjectOutputStream out = new ObjectOutputStream(userSocket.getOutputStream())) {

                                int i = 0 ; //Change based on using only backend or frontend
                                
                                if (i==0){
                                    //FRONTEND ONLY:
                                    List<Room> roomlist = result.getValue();
                                    // Convert list of Room objects to a JSON array
                                    JSONArray roomListJson = roomListToJson(roomlist);

                                    // Convert JSON array to a string
                                    String jsonString = roomListJson.toString();

                                    // Remove leading non-printable characters or binary data
                                    jsonString = jsonString.replaceFirst("^\\p{C}+", "");

                                    // Send JSON data over the socket
                                    PrintWriter out2 = new PrintWriter(new OutputStreamWriter(userSocket.getOutputStream(), "UTF-8"), true);
                                    out2.println(jsonString);
                                    out2.flush();
                                    System.out.println(jsonString); // Optional: Print the JSON string for debugging

                                }else if (i==1){

                                    //BACKEND ONLY:
                                    out.writeObject(result); 
                                }
                                System.out.println("ReudcerHandler sent the results!!" );
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("User socket not found for user ID: " );
                        }
                    }else{
                        @SuppressWarnings("unchecked")
                        Pair<Integer,List<Booking>> result = (Pair<Integer,List<Booking>>) ob;
                        int portNumber = result.getKey();
                        Socket userSocket = portSockets.get(portNumber);
                        if (userSocket != null) {
                            try (ObjectOutputStream out = new ObjectOutputStream(userSocket.getOutputStream())) {
                                
                                out.writeObject(result);
                                System.out.println("ReudcerHandler sent the results!!" );
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("User socket not found for user ID: " );
                        }
                    }
                }
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

    @SuppressWarnings("unchecked")
    public static JSONObject roomToJson(Room room) {
        JSONObject roomJson = new JSONObject();

        roomJson.put("roomName", room.getRoomName());
        roomJson.put("noOfPersons", room.getNoPerson());
        roomJson.put("area", room.getArea(room));
        roomJson.put("stars", room.getStars());
        roomJson.put("noOfReviews", room.getNoReviews());
        roomJson.put("roomImage", room.getImage());
        roomJson.put("price", room.getPrice());
        roomJson.put("owner", room.getOwner(room));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        // Convert availability list to JSON array
        JSONArray availabilityJson = new JSONArray();
        for (Available_Date date : room.getAvailability()) {
            JSONObject dateJson = new JSONObject();
            dateJson.put("start_date", date.getFirstDay().format(formatter));
            dateJson.put("end_date", date.getLastDay().format(formatter));
            availabilityJson.add(dateJson);  // Use put method to add JSON object to array
        }
        roomJson.put("availability", availabilityJson);
     
        return roomJson;
    }

    @SuppressWarnings("unchecked")
    public static JSONArray roomListToJson(List<Room> roomList) {
        JSONArray roomListJson = new JSONArray();

        for (Room room : roomList) {
            roomListJson.add(roomToJson(room));
        }

        return roomListJson;
    }
}
