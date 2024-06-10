
import java.io.*;
import java.net.*;
import java.util.Map;

public class UserHandler extends Thread {
    private Socket connection;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private int userid;
    private Map<Integer, Integer> workerPorts;
    private Map<Integer, String> workerHosts;
    

    public UserHandler(Socket connection, Map<Integer, Integer> workerPorts, Map<Integer, String> workerHosts, ObjectInputStream inputStream ) throws IOException {
        this.connection = connection;
        this.workerHosts = workerHosts;
        this.in = inputStream;
        this.workerPorts = workerPorts;
    
    }

    public void run() {
        try {
            String type = in.readUTF(); // receives the type of user

            if (type.equals("Client")) {
                String function = in.readUTF(); // receives the request e.x filtering
                String FilterDetails = in.readUTF(); // receives the details for the filtering
                userid = connection.getPort();
                Request newrequest = new Request(type, function, FilterDetails,userid);

                if (function.equals("1")){      //Filtering
                    //Send the request to all workers.
                    for (Map.Entry<Integer, Integer> entry : workerPorts.entrySet()) {
                        int workerId = entry.getKey();
                        int port = entry.getValue();
                        String host = Master.getHost(workerHosts, workerId);

                        try {
                            Socket socket = new Socket(host, port);
                            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                            out.writeObject(newrequest);
                            out.flush();
                            out.close();
                            socket.close();
                            System.out.println("Sent request to Worker " + workerId);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }else if(function.equals("2") || function.equals("3") ){    //Booking and Rating
                    String[] details = FilterDetails.split(",");
                    String roomname = details[0];
                    //Send the request only to the worker that has the room. 
                    int workerID =  Master.hash(roomname) % Master.NUM_WORKERS;
                    int workerport = workerPorts.get(workerID);
                    String host = Master.getHost(workerHosts, workerID);

                    try {
                        Socket socket = new Socket(host, workerport); 
                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                        out.writeObject(newrequest);
                        out.flush();
                        out.close();
                        socket.close();
                        System.out.println("Sent request to Worker " + workerID);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else if (function.equals("4") ){
                    try {
                        String roompath = in.readUTF();

                        File imageFile = new File(roompath);
                        FileInputStream fis = new FileInputStream(imageFile);
                        BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
                        DataOutputStream dos = new DataOutputStream(bos); 
           
                        byte[] imageBytes = new byte[(int) imageFile.length()];
                        fis.read(imageBytes);

                        dos.write(imageBytes); // Send image data
                        dos.flush();
                        fis.close();

                        connection.close();
                       
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                
            }else if (type.equals("Manager")){
                String function = in.readUTF(); // receives the request e.x filtering
                String FilterDetails = in.readUTF(); // receives the details for the filtering
                userid = connection.getPort();
                Request newrequest = new Request(type, function, FilterDetails,userid);

                if (function.equals("1") || function.equals("2") ){ //Add room and available dates
                    String[] details = FilterDetails.split(",");
                    String roomname = details[0];
                    int workerid = Master.hash(roomname) % Master.NUM_WORKERS;
                    int workerport = workerPorts.get(workerid);
                    String host = Master.getHost(workerHosts, workerid);
                    try {
                        Socket socket = new Socket(host, workerport); 
                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                        out.writeObject(newrequest);
                        out.flush();
                        out.close();
                        socket.close();
                        System.out.println("Sent request to Worker " + workerid);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if(function.equals("3") || function.equals("4")){
                    //Send the request to all workers.
                    for (Map.Entry<Integer, Integer> entry : workerPorts.entrySet()) {
                        int workerId = entry.getKey();
                        int port = entry.getValue();
                        String host = Master.getHost(workerHosts, workerId);

                        try {
                            Socket socket = new Socket(host, port);
                            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                            out.writeObject(newrequest);
                            out.flush();
                            out.close();
                            socket.close();
                            System.out.println("Sent request to Worker " + workerId);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            

            try {
                // Make the current thread sleep for 3 seconds
                Thread.sleep(3000); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (!connection.isClosed()) {
                    if (in != null) in.close();
                    if (out != null) out.close();
                    System.out.println("User handler Closing connection");
                    connection.close();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
