package com.example.dsfrontendproject;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;


public class MainActivity extends AppCompatActivity {
    TextView welcomeView;
    private EditText LocationFilter, startDate, endDate, numOfPeople,price;
    private RatingBar stars;
    private Button searchButton;
    private String filter;
    private  List<Room> roomList = new ArrayList<>();

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
                filter = "";
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
                String pr= TextUtils.isEmpty(max_price) ? "x" : max_price;
                String FD = TextUtils.isEmpty(firstDate) ? "x" : firstDate;
                String LD = TextUtils.isEmpty(lastDate) ? "x" : lastDate;

                filter = loc + "," + FD + "," + LD + "," + num + "," + pr + "," +st;
                System.out.println(filter); // testing

                // Run the network operation in a separate thread
                new Thread(() -> {
                    try {
                        roomList.clear();
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

                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                        StringBuilder jsonStringBuilder = new StringBuilder();
                        String line;

                        // Read until the end of the stream
                        while ((line = in.readLine()) != null) {
                            jsonStringBuilder.append(line);
                        }

                        String jsonString = jsonStringBuilder.toString();// DIABAZEI TO JSON
                        jsonString = removeNonJSONCharacters(jsonString);

                        // Parse JSON data
                        JSONArray roomListJson = new JSONArray(jsonString);

                        for (int i = 0; i < roomListJson.length(); i++) {
                            JSONObject roomJson = roomListJson.getJSONObject(i);
                            Room room = Room.fromJson(roomJson);
                            roomList.add(room);
                        }

                        runOnUiThread(() -> {
                            Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
                            intent.putExtra("FirstDay", FD);
                            intent.putExtra("LastDay", LD);
                            intent.putExtra("RoomList", (Serializable) roomList);
                            startActivity(intent);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error connecting to server", Toast.LENGTH_SHORT).show());
                    }
                }).start();

            }
        });

    }

    private String removeNonJSONCharacters(String input) {
        StringBuilder output = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (c >= 32 && c <= 126 || c == '\t' || c == '\n' || c == '\r') {
                output.append(c);
            }
        }
        return output.toString();
    }

}