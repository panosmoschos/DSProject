package com.example.dsfrontendproject;


import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Room implements Serializable {
    private static final long serialVersionUID = 123456789L;
    private final Lock lock = new ReentrantLock();
    private String roomName;
    private int noOfPersons;
    private String area;
    private int stars;
    private int noOfReviews;
    private String roomImage;
    private int price;
    private List<Available_Date> availability;
    private String owner;
    private List<Booking> bookings;
    private  Bitmap imageBitmap; // transient to avoid serialization


    // Constructor
    public Room(String roomName, int noOfPersons, String area, int stars, int noOfReviews, String roomImage, int price, List<Available_Date> availability, String owner) {
        this.roomName = roomName;
        this.noOfPersons = noOfPersons;
        this.area = area;
        this.stars = stars;
        this.noOfReviews = noOfReviews;
        this.roomImage = roomImage;
        this.price = price;
        this.availability = availability;
        this.owner = owner;
        this.bookings = new ArrayList<>();
    }

    public List<Available_Date> getAvailability() {
        return availability;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public String getOwner(Room room) {
        return room.owner;
    }

    public String getArea(Room room) {
        return room.area;
    }

    public String getRoomName() {
        return roomName;
    }

    public int getStars() {
        return stars;
    }

    public int getNoPerson() {
        return noOfPersons;
    }

    public int getNoReviews() {
        return noOfReviews;
    }

    public int getPrice() {
        return price;
    }

    public String getImage() {
        return roomImage;
    }

    public int getNumberOfBookings() {
        return bookings.size();
    }

    public static Room fromJson(JSONObject jsonObject) throws JSONException {
        String roomName = jsonObject.getString("roomName");
        int noOfPersons = jsonObject.getInt("noOfPersons");
        String area = jsonObject.getString("area");
        int stars = jsonObject.getInt("stars");
        int noOfReviews = jsonObject.getInt("noOfReviews");
        String roomImage = jsonObject.getString("roomImage");
        int price = jsonObject.getInt("price");
        String owner = jsonObject.getString("owner");

        JSONArray availabilityJsonArray = jsonObject.getJSONArray("availability");
        List<Available_Date> availability = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        for (int i = 0; i < availabilityJsonArray.length(); i++) {
            JSONObject dateJson = availabilityJsonArray.getJSONObject(i);
            LocalDate startDate = LocalDate.parse(dateJson.getString("start_date"), formatter);
            LocalDate endDate = LocalDate.parse(dateJson.getString("end_date"), formatter);
            availability.add(new Available_Date(startDate, endDate));
        }


        return new Room(roomName, noOfPersons, area, stars, noOfReviews, roomImage, price, availability, owner);
    }

}