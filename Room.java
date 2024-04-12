import java.io.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import java.time.*;
import java.time.format.DateTimeFormatter;


public class Room implements Serializable {
    private String roomName;
    private int noOfPersons;
    private String area;
    private int stars;
    private int noOfReviews;
    private String roomImage;
    private int price;
    private List<Available_Date> availability;
    private String owner;

    // Constructor
    public Room(String roomName, int noOfPersons, String area, int stars, int noOfReviews, String roomImage, int price, List<Available_Date> availability, String owner) {
        this.roomName = roomName;
        this.noOfPersons = noOfPersons;
        this.area = area;
        this.stars = stars;
        this.noOfReviews = noOfReviews;
        this.roomImage = roomImage;
        this.price = price;
        this.availability = availability;
        this.owner = owner;
    }
   
    // for testing
    public List<Available_Date> getAvailability() {
        return availability;
    }

    // for testing
    public static String getOwner(Room room){
        return room.owner;
    }

    public  String getArea(Room room){
        return room.area;
    }

    public String getRoomName(){
        return roomName;
    }



    // Owner adding new available dates
    public void addAvailability(List<Available_Date> newDates){
        boolean overlapping = false;    // assuming the dates do not overlap

        for (Available_Date existingDate : availability){
            for (Available_Date newDate : newDates){
                if (newDate.OverlapsWith(existingDate)){
                    System.out.println("The dates are overlapping. Please try again.");
                    overlapping = true;
                }
            }
        }

        // If there are truly no overlapping dates
        if (!overlapping){
            for (Available_Date newDate: newDates){
                availability.add(newDate);
            }
        }
       
    }
    

    public static List<Room> roomsOfFolder(String path) {
        File dir = new File(path);
        File[] files = dir.listFiles();

        List<Room> rooms = new ArrayList<>();

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


    // TESTING
    public static void main(String[] args) {
        String folderPath = "bin/rooms";
        List<Room> rooms = roomsOfFolder(folderPath);
        for (Room room: rooms){
            System.out.println(room.area);
        }
    }

}
