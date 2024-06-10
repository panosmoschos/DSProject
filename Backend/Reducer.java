
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.net.ServerSocket;
import java.net.Socket;


public class Reducer {
    private int port;
    private List<Pair<Integer, List<Room>>> results;
    private List<Pair<Integer,List<Booking>>> bookings;

    public Reducer(int port) {
        this.port = port;
        this.results = new ArrayList<>();
        this.bookings = new ArrayList<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Reducer started on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                new ReducerThread(socket,results,bookings).start();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ReducerThread extends Thread {
        private Socket socket;
        private List<Pair<Integer, List<Room>>> results;
        private List<Pair<Integer, List<Booking>>> bookings;

        public ReducerThread(Socket socket, List<Pair<Integer, List<Room>>> results, List<Pair<Integer, List<Booking>>> bookings){
            this.socket = socket;
            this.results = results;
            this.bookings = bookings;
        }

        @SuppressWarnings("unchecked")
        public void run() {
            
            try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                
                Pair <Integer, List<?>> result1 = (Pair<Integer, List<?>>) in.readObject();
                List<?> value = result1.getValue();
                //System.out.println(result1.getKey());

                boolean isRoom = false;
                boolean isBooking= false;
                int count1 = 0;
                int tempkey = result1.getKey();
                //System.out.println(result1.getValue());
        
                if (!value.isEmpty()) {
                    Object firstElement = value.get(0);
                    if (firstElement instanceof Room) {
                        isRoom = true;
                    } else if (firstElement instanceof Booking) {
                        isBooking = true;
                    }
                }else{
                    synchronized(results){
                        results.add(new Pair<Integer,List<Room>>(tempkey,(List<Room>) value));
                        count1++;
                    }

                    synchronized(bookings){
                        bookings.add(new Pair<Integer,List<Booking>>(tempkey,(List<Booking>) value));
                        count1++;
                    }
                }


                if (isRoom) {
                    Pair<Integer, List<Room>> roomsPair = new Pair<>(tempkey, (List<Room>) value);    
                    
                    synchronized(results){

                        results.add(roomsPair); //add the result in the results list
    
                        for (Pair<Integer, List<Room>> res : results){
                            if(tempkey == res.getKey()){
                                count1 ++;
                            }
                        }

    
                        if (count1 == Master.NUM_WORKERS) {
                            Pair<Integer, List<Room>> finalResult = reduceForUserId(tempkey);
        
                            synchronized (results) {
                                Iterator<Pair<Integer, List<Room>>> iterator = results.iterator();
                                while (iterator.hasNext()) {
                                    Pair<Integer, List<Room>> res = iterator.next();
                                    if (tempkey == res.getKey()) {
                                        iterator.remove();
                                    }
                                }
                            }
                         
                            // Send the final result to the Master
                            //System.out.println(count1);
                            sendResultToMaster(finalResult);
                        }
                    }

                } else if (isBooking) {

                    Pair<Integer, List<Booking>> bookingPair = new Pair<>(tempkey, (List<Booking>) value);          
                    
                    synchronized(bookings){

                        bookings.add(bookingPair);
    
                        for (Pair<Integer, List<Booking>> book : bookings){
                            if(tempkey == book.getKey()){
                                count1 ++;
                            }
                        }
    
                        if (count1 == Master.NUM_WORKERS) {
                            Pair<Integer, List<Booking>> finalResult = BOOKINGreduceForUserID(tempkey);
        
                            synchronized (bookings) {
                                Iterator<Pair<Integer, List<Booking>>> iterator = bookings.iterator();
                                while (iterator.hasNext()) {
                                    Pair<Integer, List<Booking>> res = iterator.next();
                                    if (tempkey == res.getKey()) {
                                        iterator.remove();
                                    }
                                }
                            }
                         
                            // Send the final result to the Master
                            sendBookingsToMaster(finalResult);
                        }
    
                    }
                    
                }


            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        private Pair<Integer, List<Room>> reduceForUserId(int userId) {
            // Filter the results list to get only the results for the specified user ID
            List<Pair<Integer, List<Room>>> filteredResults = results.stream()
                    .filter(pair -> pair.getKey() == userId)
                    .collect(Collectors.toList());
        
            // Perform reduction on the filtered results
            List<Room> reducedList = filteredResults.stream()
                    .flatMap(pair -> pair.getValue().stream())
                    .collect(Collectors.toList());
        
            // Return the reduced result
            return new Pair<>(userId, reducedList);
        }

        private Pair<Integer, List<Booking>> BOOKINGreduceForUserID(int userId){
            // Filter the results list to get only the results for the specified user ID
            List<Pair<Integer, List<Booking>>> reducedBookings = bookings.stream()
                    .filter(pair -> pair.getKey() == userId)
                    .collect(Collectors.toList());
        
            // Perform reduction on the filtered results
            List<Booking> reducedList = reducedBookings.stream()
                    .flatMap(pair -> pair.getValue().stream())
                    .collect(Collectors.toList());
        
            // Return the reduced result
            return new Pair<>(userId, reducedList);
        }
 
        private void sendResultToMaster(Pair<Integer, List<Room>> finalResult) {
            try (Socket newsocket = new Socket("localhost", 12345);
                ObjectOutputStream out = new ObjectOutputStream(newsocket.getOutputStream())) {
                out.writeUTF("REDUCER");
                out.flush();
                out.writeObject(finalResult);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void sendBookingsToMaster(Pair<Integer, List<Booking>> finalResult) {
            try (Socket newsocket = new Socket("localhost", 12345);
                ObjectOutputStream out = new ObjectOutputStream(newsocket.getOutputStream())) {
                out.writeUTF("REDUCER");
                out.flush();
                out.writeObject(finalResult);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }

    public static void main(String[] args) {
        int port = 23456; // Choose a port for the reducer
        Reducer reducer = new Reducer(port);
        reducer.start();
    }
}
