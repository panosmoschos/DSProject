import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Scanner;

public class Dummy_User extends Thread {

	String Type;//Manager or Client
	String received;
	Scanner scanner = new Scanner(System.in);
   

	
	public void run() {
        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        String answer = "";
        String RoomDetails = "";
        String path = "";

        try {
            while (true) {
                /* Create socket for contacting the server on port 4444 */
                socket = new Socket("localhost", 12345);
                //new ResponseListener(socket).start();
                /* Create the streams to send and receive data from server */
                out = new ObjectOutputStream(socket.getOutputStream());
               
                
                out.writeUTF("CLIENT");
                out.flush();
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
                        System.out.println("Describe your filter as: Location,First Day Of Stay, Last Day Of Stay, Number of people,Price,Stars");
                        System.out.println("Date format should be yyyy/mm/dd.");
                        System.out.println("If you don't want to add a filter, add an x on that field. e.g. Naxos,x,x,x,x,x");
                        RoomDetails = scanner.nextLine();
                        
                    }else if(answer.equals("2")){
                        System.out.println("Name of the room you want to book, dates?");
                        RoomDetails = scanner.nextLine();

                    }else if(answer.equals("3")){
                        System.out.println("Name of the room you want to rate, rating?");
                        RoomDetails = scanner.nextLine();

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


                if (answer.equals("1") && Type.equals("Client")){
                    // Now let's listen for the results sent from the ReducerHandler
                    try (ObjectInputStream resultInput = new ObjectInputStream(socket.getInputStream())) {
                        
                            @SuppressWarnings("unchecked")
                            Pair<Integer, List<Room>> result = (Pair<Integer, List<Room>>) resultInput.readObject();
                           
                            System.out.println("Received resultsss: " + result.getValue().get(0).getRoomName() +" "+ result.getValue().get(0).getStars());
                            System.out.println("Received resultsss: "  + result.getValue().get(1).getRoomName() +" "+ result.getValue().get(1).getStars());
                    
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                
               
                
            }

        } catch (UnknownHostException unknownHost) {
			System.err.println("You are trying to connect to an unknown host!");
        
        } catch (EOFException eof) {
            // Connection is closed, handle accordingly
            System.err.println("Connection closed by server.");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally { 
			try {
                if (!socket.isClosed()) {
                    if (in != null) in.close();
                    if (out != null) out.close();
                    System.out.println("Dummy_User Closing connection");
                    if (socket != null) socket.close();
                }
				
				
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
        }
    }
	

	public static void main(String args[]) {
		new Dummy_User().start();
	}
}


