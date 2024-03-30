import java.io.*;
import java.net.*;

public class Reducer {
    public static void main(String[] args) {
        try {
            try (// Δηλώσεις για το socket και τον αναγνώστη/γραφέα
            ServerSocket serverSocket = new ServerSocket(5555)) {
                Socket socket;
                ObjectInputStream inputStream;
                ObjectOutputStream outputStream;
                String result;

                while (true) {
                    // Αποδοχή σύνδεσης από τον ActionsForClients
                    socket = serverSocket.accept();

                    // Δημιουργία αντικειμένων εισόδου/εξόδου
                    inputStream = new ObjectInputStream(socket.getInputStream());
                    outputStream = new ObjectOutputStream(socket.getOutputStream());

                    // Ανάγνωση αποτελέσματος από τον ActionsForClients
                    result = (String) inputStream.readObject();
                    System.out.println("Received result from ActionsForClients: " + result);

                    // Προώθηση αποτελέσματος στον Master
                    sendResultToMaster(result);

                    // Κλείσιμο σύνδεσης
                    inputStream.close();
                    outputStream.close();
                    socket.close();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Συνάρτηση για την προώθηση του αποτελέσματος στον Master
    private static void sendResultToMaster(String result) {
        try {
            // Δημιουργία σύνδεσης με τον Master
            Socket masterSocket = new Socket("localhost", 4444);

            // Δημιουργία αντικειμένου εξόδου για τον Master
            ObjectOutputStream outputStream = new ObjectOutputStream(masterSocket.getOutputStream());

            // Αποστολή του αποτελέσματος στον Master
            outputStream.writeObject(result);
            outputStream.flush();

            // Κλείσιμο της σύνδεσης
            outputStream.close();
            masterSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
