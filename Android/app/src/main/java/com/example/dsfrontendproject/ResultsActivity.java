package com.example.dsfrontendproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import android.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;

public class ResultsActivity extends AppCompatActivity implements RoomsAdapter.bookingClickListener {
    private String FirstDay;
    private String LastDay;


    RoomsAdapter roomsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_results), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent i = getIntent();
        FirstDay = i.getStringExtra("FirstDay");
        LastDay = i.getStringExtra("LastDay");
        List<Room> roomList = (List<Room>) i.getSerializableExtra("RoomList");


        RoomsAdapter.bookingClickListener listener = this;
        RecyclerView roomsRecycler = findViewById(R.id.roomsRecyclerView);
        roomsRecycler.setLayoutManager(new LinearLayoutManager(this));
        roomsAdapter = new RoomsAdapter(this, roomList, listener); // Instantiate the RoomsAdapter
        roomsRecycler.setAdapter(roomsAdapter);

        // Load room images from the server
        for (Room room : roomList) {
            loadRoomImage(room);
        }
    }

    private void loadRoomImage(Room room) {
        new Thread(() -> {
            try {
                // Establish TCP connection with the server
                Socket socket = new Socket("192.168.1.212", 12345);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

                out.writeUTF("USER");
                out.flush();
                out.writeUTF("Client");
                out.flush(); // Ensure the message is sent
                out.writeUTF("4");
                out.flush();
                out.writeUTF("");
                out.flush();
                String roompath  = room.getImage();
                out.writeUTF(roompath);
                out.flush();


                // Update UI on the main th read
                File imageFile = new File(getFilesDir(), room.getRoomName() + ".png");

                InputStream is = socket.getInputStream();
                FileOutputStream fos = new FileOutputStream(getFilesDir() + "/" + room.getRoomName() + ".png");
                byte[] buffer = new byte[1024];
                int bytesRead;

                StringBuilder hexString = new StringBuilder();

                // Read room image data from the server
                while ((bytesRead = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);

                    for (int i = 0; i < bytesRead; i++) {
                        String hex = Integer.toHexString(0xFF & buffer[i]);
                        if (hex.length() == 1) {
                            hexString.append('0'); // Add leading zero for single digit hex values
                        }
                        hexString.append(hex);
                    }
                }
                // Close streams and socket
                fos.close();
                socket.close();

                runOnUiThread(() -> {
                    if (imageFile.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                        roomsAdapter.updateRoomImage(room.getRoomName(), bitmap); // Update the image in the adapter
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ResultsActivity.this, "Error loading room images", Toast.LENGTH_SHORT).show());
            }

        }).start();
    }

    public void onBookingClick(Room room) {
        if (FirstDay.equals("x") || LastDay.equals("x")) {
            List<Available_Date> available_dates = room.getAvailability();
            chooseDatesOfStayDialog(available_dates, room);
        } else {
            displayBookingConfirmation(FirstDay,LastDay, room);
        }
    }

    public String bytesToHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0'); // Add leading zero for single digit hex values
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void displayBookingConfirmation(String FD, String LD, Room room) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setCancelable(true);
        dialogBuilder.setTitle("Confirm Booking");
        dialogBuilder.setMessage("First Day: " + FD + "\nLast Day: " + LD);

        dialogBuilder.setPositiveButton("Confirm Booking", (dialog, id) -> {

            // apo dw kai kato
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), "Thank you for your booking.", Toast.LENGTH_SHORT).show();

            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDate firstDay = LocalDate.parse(FirstDay,df);
            LocalDate lastDay = LocalDate.parse(LastDay,df);
            String details = room.getRoomName() +  "," + FD + ","+ LD;

            //Booking method
            new Thread(() -> {
                try {
                    Socket socket = new Socket("192.168.1.212", 12345);
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

                    out.writeUTF("USER");
                    out.flush();
                    out.writeUTF("Client");
                    out.flush(); // Ensure the message is sent
                    out.writeUTF("2");
                    out.flush();
                    out.writeUTF(details);
                    out.flush();


                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(ResultsActivity.this, "Error connecting to server", Toast.LENGTH_SHORT).show());
                }
            }).start();
            finish();
        });

        dialogBuilder.setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

        AlertDialog dialog = dialogBuilder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.rounded_dialog);
        dialog.show();
    }

    private void chooseDatesOfStayDialog(List<Available_Date> available_dates, Room room) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Choose days of stay from the available ranges");

        final TextView showDates = new TextView(this);
        final EditText inputFirst = new EditText(this);
        final EditText inputLast = new EditText(this);

        StringBuilder all_ranges = new StringBuilder();
        int isLast = 0;
        for (Available_Date AD : available_dates){
            all_ranges.append(AD.getTimePeriod());
            isLast+=1;
            if (isLast != available_dates.size()){
                all_ranges.append("\n");
            }
        }

        showDates.setText(all_ranges);
        inputFirst.setHint("First day of stay (yyyy/MM/dd)");
        inputLast.setHint("Last day of stay (yyyy/MM/dd)");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        dialogBuilder.setView(layout);

        layout.addView(showDates);
        layout.addView(inputFirst);
        layout.addView(inputLast);

        showDates.setTextSize(20);
        showDates.setPadding(0,40,0,50);
        showDates.setGravity(Gravity.CENTER);
        showDates.setTypeface(null, Typeface.BOLD);

        inputFirst.setTextSize(19);
        inputFirst.setPadding(40,0,40,30);
        inputLast.setTextSize(19);
        inputLast.setPadding(40,0,40,30);

        // Set up the buttons
        dialogBuilder.setPositiveButton("OK", (dialog, which) -> {
            String fir = inputFirst.getText().toString();
            String las = inputLast.getText().toString();

            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDate clientFirst = LocalDate.parse(fir,df);
            LocalDate clientLast = LocalDate.parse(las,df);

            Available_Date clientDay = new Available_Date(clientFirst,clientLast);

            if (clientDay.isAvailable(available_dates)){
                FirstDay = fir;
                LastDay = las;
                dialog.dismiss();
                displayBookingConfirmation(FirstDay,LastDay,room);
            }else{
                Toast.makeText(this, "Please choose valid dates.", Toast.LENGTH_SHORT).show();
                chooseDatesOfStayDialog(available_dates,room);
                return;

            }
            dialog.dismiss();
        });

        dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = dialogBuilder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.rounded_dialog);
        dialog.show();
    }

}