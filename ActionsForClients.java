import java.io.*;
import java.net.*;

public class ActionsForClients extends Thread/****/ {
ObjectInputStream in;
ObjectOutputStream out;
Socket socket;

	public ActionsForClients(Socket connection) {
		try {
			socket = new Socket("localhost", 5555);

			out = new ObjectOutputStream(socket.getOutputStream());//send data to reducer
			in = new ObjectInputStream(connection.getInputStream());

				/*
				*
				*
				*
				*/

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			int a = in.readInt();
			int b = in.readInt();

			int result = a+b;

			out.writeInt(result);
			out.flush();
				/*
				*
				*
				*
				*/
	
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
	}
}
