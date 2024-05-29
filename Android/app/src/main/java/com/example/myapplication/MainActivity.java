package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.RoomClasses.Pair;
import com.example.myapplication.RoomClasses.Room;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.List;



public class MainActivity extends AppCompatActivity implements Serializable {
    TextView welcomeView;
    private EditText LocationFilter, startDate, endDate, numOfPeople, price;
    private RatingBar stars;
    private Button searchButton;
    private List<Room> rooms;

    public Handler myHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            // Extract the room list from the message
            Bundle bundle = message.getData();
            Pair<Integer, List<Room>> receivedResult = (Pair<Integer, List<Room>>) bundle.getSerializable("rooms");

            // Update the welcomeView with room names
            if (receivedResult != null) {
                List<Room> receivedRooms = receivedResult.getValue();
                StringBuilder roomNames = new StringBuilder();
                for (Room room : receivedRooms) {
                    roomNames.append(room.getRoomName()).append("\n");
                }
                welcomeView.setText(roomNames.toString());
                Toast.makeText(MainActivity.this, "Rooms received", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "No rooms found", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        welcomeView = findViewById(R.id.welcomeView);

        LocationFilter = findViewById(R.id.LocationFilter);
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);
        numOfPeople = findViewById(R.id.numOfPeople);
        price = findViewById(R.id.price);
        searchButton = findViewById(R.id.searchButton);
        stars = findViewById(R.id.stars);

        // when the search button is clicked
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = LocationFilter.getText().toString();
                String numPeople = numOfPeople.getText().toString();
                float s = stars.getRating();
                String starsRating = String.valueOf(Math.round(stars.getRating()));
                String max_price = price.getText().toString();
                String firstDate = startDate.getText().toString();
                String lastDate = endDate.getText().toString();

                // create the "details" of filter
                String loc = TextUtils.isEmpty(location) ? "x" : location;
                String num = TextUtils.isEmpty(numPeople) ? "x" : numPeople;
                String st = (s == 0.0) ? "x" : starsRating;
                String pr = TextUtils.isEmpty(max_price) ? "x" : max_price;
                String FD = TextUtils.isEmpty(firstDate) ? "x" : firstDate;
                String LD = TextUtils.isEmpty(lastDate) ? "x" : lastDate;

                String filter = loc + "," + FD + "," + LD + "," + num + "," + pr + "," + st;

                // Run the network operation in a separate thread
                new Thread(() -> {
                    try {
                        Socket socket = new Socket("192.168.1.212", 12345);
                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());



                        out.writeUTF("USER");
                        out.flush();
                        out.writeUTF("Client");
                        out.flush(); // Ensure the message is sent
                        out.writeUTF("1");
                        out.flush();
                        out.writeUTF(filter);
                        out.flush();


                        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                        //Thread.sleep(10000);
                        @SuppressWarnings("unchecked")
                        Pair<Integer, List<Room>> finalResult = (Pair<Integer, List<Room>>) in.readObject();
                        socket.close();

                        // Send the result to the handler
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("rooms", (Serializable) finalResult);
                        message.setData(bundle);
                        myHandler.sendMessage(message);

                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error connecting to server", Toast.LENGTH_SHORT).show());
                    }
                }).start();
            }
        });
    }
}
