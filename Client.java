import java.io.*;
import java.net.*;

public class Client extends Thread {
	int a, b;
	Client(int a, int b) {
		this.a = a;
		this.b = b;
	}

	public void run() {
		ObjectInputStream in =null;
		ObjectOutputStream out = null;
		Socket socket = null;


		try {

			/* Create socket for contacting the server on port 4444*/
			socket = new Socket("localhost", 4444);


			/* Create the streams to send and receive data from server */
			
			in = new ObjectInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());




			/* Write the two integers */
			out.writeInt(a);
			out.flush();
			out.writeInt(b);
			out.flush();

			/* Print the received result from server */
			System.out.println("Server>" + in.readInt());

		} catch (UnknownHostException unknownHost) {
			System.err.println("You are trying to connect to an unknown host!");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			try {
				in.close();	out.close();
				socket.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}
	
	public static void main(String args[]) {
		new Client(10, 5).start();
		new Client(20, 5).start();
		new Client(30, 5).start();
		new Client(40, 5).start();
		new Client(50, 5).start();
		new Client(60, 5).start();
		new Client(70, 5).start();
		new Client(80, 5).start();
		new Client(90, 5).start();
		new Client(100, 5).start();
	}
}
