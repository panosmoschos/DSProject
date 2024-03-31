import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ActionsForClients extends Thread {

	private Socket connection;
	public static ArrayList<ActionsForClients> ClientHandler = new ArrayList<>();

	ObjectInputStream in;
	ObjectOutputStream out;

	public ActionsForClients(Socket connection) {
		try {
			this.connection = connection;
			out = new ObjectOutputStream(connection.getOutputStream());
			in = new ObjectInputStream(connection.getInputStream());
			ClientHandler.add(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {

		String received;

			//while(true){

				try {
					received = in.readUTF();
					if (received.equals("1")){
						
						out.writeUTF(Service.ShowAllIDs());
						out.flush();
					}

			
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						in.close();
						out.close();
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}
				}
		//}
	}

}


