package com.example.dsfrontendproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    TextView welcomeView;
    private EditText LocationFilter, startDate, endDate, numOfPeople,price;
    private RatingBar stars;
    private Button searchButton;

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
                String pr= TextUtils.isEmpty(max_price) ? "x" : max_price;
                String FD = TextUtils.isEmpty(firstDate) ? "x" : firstDate;
                String LD = TextUtils.isEmpty(lastDate) ? "x" : lastDate;

                String filter = loc + "," + FD + "," + LD + "," + num + "," + pr + "," +st;
                System.out.println(filter);


                // change activity
                Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
                startActivity(intent);
            }
        });

    }

}