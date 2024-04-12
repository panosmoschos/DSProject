import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.net.ServerSocket;
import java.net.Socket;


public class Reducer {
    private int port;
    private List<Pair<Integer, List<Room>>> results;
    private Map<Integer, Integer> requestCountMap;
    

    public Reducer(int port) {
        this.port = port;
        this.results = new ArrayList<>();
        this.requestCountMap = new HashMap<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Reducer started on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                new ReducerThread(socket,results).start();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ReducerThread extends Thread {
        private Socket socket;
        private List<Pair<Integer, List<Room>>> results;

        public ReducerThread(Socket socket, List<Pair<Integer, List<Room>>> results){
            this.socket = socket;
            this.results = results;
        }

        @SuppressWarnings("unchecked")
        public void run() {

            try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                Pair<Integer, List<Room>> result = (Pair<Integer, List<Room>>) in.readObject();//read the results from the workerthreads
                int tempkey = result.getKey();
                int count=0;

                results.add(result);//add the result in the results list

                for (Pair<Integer, List<Room>> res : results){
                    if(tempkey == res.getKey()){
                        count ++;
                    }
                }
                System.out.println(results);

                if (count == 3) {//if you find 3 results with the same userid, reduce them and delete them!
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
                    System.out.println(finalResult.getValue());
                    System.out.println(finalResult.getKey());

                    // Send the final result to the Master
                    sendResultToMaster(finalResult);
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
 
        private void sendResultToMaster(Pair<Integer, List<Room>> finalResult) {
            try (Socket newsocket = new Socket("localhost", 12345);
                ObjectOutputStream out = new ObjectOutputStream(newsocket.getOutputStream())) {
                int localPort = socket.getLocalPort();
                out.writeInt(localPort);
                out.flush();
                //out.writeUTF("CLIENT");
                //out.flush();
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
