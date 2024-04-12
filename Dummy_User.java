import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Scanner;

public class Dummy_User extends Thread {

	String Type;//Manager or Client
	String received;
	Scanner scanner = new Scanner(System.in);
    Pair<Integer, List<Room>> finalresult;
    Socket reducerSocket = null;
    ObjectInputStream reducerIn = null;

	
	public void run() {
        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        String answer = "";
        String RoomDetails = "";
        String path = "";

        try {
            //while (true) {
                /* Create socket for contacting the server on port 4444 */
                socket = new Socket("localhost", 12345);
                System.out.println(socket);
                /* Create the streams to send and receive data from server */
                out = new ObjectOutputStream(socket.getOutputStream());
                
                
                System.out.println("Choose: Manager/Client");
                Type = scanner.nextLine();

                if (Type.equals("Manager")) {
                    out.writeUTF("Manager");
                    System.out.println("1)Add Room.\n2)Add available dates.\n3)Show reservations.\nExit");
                    answer = scanner.nextLine();
                    if(answer.equals("1")){
                        System.out.println("Provide the path of the room.");
                        path = scanner.nextLine();

                    }else if(answer.equals("2")){
                        System.out.println("Provide the name of the room and the dates you want to add.");

                    }else if(answer.equals("3")){
                        System.out.println("Provide your name.");

                    }else if (answer.equalsIgnoreCase("Exit")) {
                        System.out.println("Closing this connection : " + socket);
                        out.writeUTF("exit");
                        socket.close();
                        System.out.println("Connection closed");
                        //  break;
                }


                } else if (Type.equals("Client")) {
                    out.writeUTF("Client");
                    System.out.println("1)Filter Rooms.\n2)Book a room.\n3)Rate a room.\nExit");
                    answer = scanner.nextLine();
                    
                    if (answer.equals("1")) {
                        System.out.println("Describe your filter as: Location,Available Dates,Number of people,Price,Stars");
                        RoomDetails = scanner.nextLine();
                        
                    }else if(answer.equals("2")){
                        System.out.println("Name of the room you want to book?");

                    }else if(answer.equals("3")){
                        System.out.println("Name of the room you want to rate, rating?");

                    }
                } else if (answer.equalsIgnoreCase("Exit")) {
                    System.out.println("Closing this connection : " + socket);
                    out.writeUTF("exit");
                    socket.close();
                    System.out.println("Connection closed");
                  //  break;
                }

                out.flush(); // out type
                out.writeUTF(answer); // Request
                out.flush();
                out.writeUTF(RoomDetails); // Details for filtering
                out.flush();

                in = new ObjectInputStream(socket.getInputStream());
                System.out.println(in.readUTF());

                // Connect to the ReducerHandler
               // reducerSocket = new Socket("localhost", finalresult.getKey());
                //reducerIn = new ObjectInputStream(reducerSocket.getInputStream());

                // Listen for results from the ReducerHandler
               // Pair<Integer, List<Room>> reducerResult = (Pair<Integer, List<Room>>) reducerIn.readObject();
                //System.out.println(reducerResult);

             // }

        } catch (UnknownHostException unknownHost) {
			System.err.println("You are trying to connect to an unknown host!");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			try {
				if (in != null) in.close();
				if (out != null) out.close();
				if (socket != null) socket.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}}
    }
	


	public static void main(String args[]) {
		new Dummy_User().start();
	}
}
