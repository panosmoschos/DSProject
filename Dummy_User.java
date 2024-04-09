import java.io.*;
import java.net.*;
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

        try {
            //while (true) {
                /* Create socket for contacting the server on port 4444 */
                socket = new Socket("localhost", 12345);

                /* Create the streams to send and receive data from server */
                out = new ObjectOutputStream(socket.getOutputStream());
                //in = new ObjectInputStream(socket.getInputStream());

                System.out.println("Choose: Manager/Client");
                Type = scanner.nextLine();

                if (Type.equals("Manager")) {
                    out.writeUTF("Manager");
                    System.out.println("1)Add Room.\n2)Add available dates.\n3)Show reservations.\nExit");
                    answer = scanner.nextLine();
                } else if (Type.equals("Client")) {
                    out.writeUTF("Client");
                    System.out.println("1)Filter Rooms.\n2)Book a room.\n3)Rate a room.\nExit");
                    answer = scanner.nextLine();
                    if (answer.equals("1")) {
                        System.out.println("Location,Available Dates,Number of people,Price,Stars");
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
          //  }

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
