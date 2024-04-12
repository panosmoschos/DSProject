import java.io.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import java.time.*;
import java.time.format.DateTimeFormatter;


public class Room {
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

    // (Owner) Adds new available dates
    public void addAvailability(Request req){
        List<Available_Date> newDates = new ArrayList<>();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        String[] details = req.details.split(",");

        int i = 1; // skipping 0 because details[0] is the name of the room
        while (i<=details.length-1){
            LocalDate newdate = LocalDate.parse(details[i],df);  
            Available_Date desiredDate = new Available_Date(newdate);
            i++;
            newDates.add(desiredDate);
        }

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

    
    // Changes the rating and the noOfReviews of the room
    public void ratingChanges(Request req){
        String[] details = req.details.split(",");
        int newStarRating = Integer.parseInt(details[1]);
        noOfReviews++;
        stars = (stars*noOfReviews + newStarRating)/noOfPersons;
    }

    // Filters a list of rooms
    public List<Room> filterRooms(List<Room> givenRooms, Request req) {
        List<Room> matchingRooms = new ArrayList<>();

        String[] arrayFilter = req.details.split(",");
        // [Location, First Day Of Stay, Last Day Of Stay, NumOfPeople, Price, Stars]

        for (Room r: givenRooms) {
            if(r.filterByArea(arrayFilter[0]) && 
             r.filterByPersons(arrayFilter[3]) && 
             r.filterByPrice(arrayFilter[4]) && 
             r.filterByStars(arrayFilter[5]) && 
             r.filterByDates(arrayFilter[1], arrayFilter[2])){
                matchingRooms.add(r);
            }
        }
        return matchingRooms;
    }

    public boolean filterByArea(String desiredArea){
        if (desiredArea.equals("x")){
            return true;
        }else{
            return area.equals(desiredArea);
        }
    }

    public boolean filterByPersons(String desiredNoOfPersons){
        if (desiredNoOfPersons.equals("x")){
            return true;
        }else{
            return noOfPersons == Integer.parseInt(desiredNoOfPersons);
        }
    }

    public boolean filterByPrice(String desiredPrice){
        if (desiredPrice.equals("x")){
            return true;
        }else{
            return price <= Integer.parseInt(desiredPrice);
        }
    }

    public boolean filterByStars(String desiredStars){
        if (desiredStars.equals("x")){
            return true;
        }else{
            return stars >= Integer.parseInt(desiredStars);
        }
    }

    public boolean filterByDates(String FirstDay, String LastDay){
        if (FirstDay.equals("x") || LastDay.equals("x")){
            return true;
        }else{
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDate FD = LocalDate.parse(FirstDay,df);  
            LocalDate LD =  LocalDate.parse(LastDay,df);
            Available_Date desiredDate = new Available_Date(FD, LD);
            return desiredDate.isAvailable(availability, desiredDate);
        }
    }

    // Returns the room with the desired roomName
    public Room findRoomByName(String rName, List<Room> RoomList){
        for (Room r : RoomList){
            if(rName.equals(r.roomName)){
                return r;
            }
        }
        System.out.println("There was no room with the name" + rName);
        return null;
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
                System.out.println("Exception:" + e);
            }   
        }
        return rooms;
    }


    // TESTING
    public static void main(String[] args) {
        String folderPath = "bin/rooms";
        List<Room> rooms = roomsOfFolder(folderPath);
        
        String filter = "x,x,x,x,200,x";
        Request r = new Request(filter, folderPath, filter, 0);

        /* prepei na kaneis th filterRooms static gia na treksei
        
        List<Room> filtered = filterRooms(rooms, r);
        for (Room f : filtered){
            System.out.println(f.getRoomName());
        }

        */
    }

}
