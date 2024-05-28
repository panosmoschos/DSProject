package com.example.dsfrontendproject;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.dsfrontendproject.roomClasses.Available_Date;
import com.example.dsfrontendproject.roomClasses.Room;

import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_results);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //TEST
        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room("Oasis", 2, "Naxos", 3, 10, "https://athensprimehotels.com/wp-content/uploads/stantard-room-photo1.jpg", 120, null, "Marika"));
        rooms.add(new Room("Blue Sky", 2, "Naxos", 3, 10, "https://athensprimehotels.com/wp-content/uploads/stantard-room-photo1.jpg", 120, null, "Mitsos"));

        RecyclerView roomsRecycler = findViewById(R.id.roomsRecyclerView);
        roomsRecycler.setLayoutManager(new LinearLayoutManager(this));
        roomsRecycler.setAdapter(new RoomsAdapter(this, rooms));

    }
}