import java.io.*;
import java.net.*;

public class Master {

	public static void main(String args[]) {
		new Master().openServer();
	}
	
	/* Define the socket that receives requests */
	ServerSocket server; 
	Socket socket = null;


	/* Define the socket that is used to handle the connection */
	
	void openServer() {
		try {

			/* Create Server Socket */
			server = new ServerSocket(4444);
			System.out.println("Waiting for connections");

			while (true) {
				/* Accept the connection */
				socket = server.accept();
				System.out.println("New connection!");
				
			
				/* Handle the request */
				Thread t = new ActionsForClients(socket);

				t.start();
			}

		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	
}
