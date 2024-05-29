package com.example.myapplication.RoomClasses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Room implements Serializable {
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
}
