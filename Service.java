import java.io.*;
import java.util.*;

public class Service {
    
    public static String ShowAllIDs(){
        return "Bye Bye";
    }

    // Simple HashFunction
    public static int Hash(String roomName){
        int hash = 11; 
        for (int i = 0; i < roomName.length(); i++) {
            hash = hash*37 + roomName.charAt(i);
        }
        return hash;
    }

}
