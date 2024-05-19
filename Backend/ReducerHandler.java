import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

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
                        int portNumber = result.getKey();;
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
                    }else{
                        @SuppressWarnings("unchecked")
                        Pair<Integer,List<Booking>> result = (Pair<Integer,List<Booking>>) ob;
                        int portNumber = result.getKey();;
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
}
