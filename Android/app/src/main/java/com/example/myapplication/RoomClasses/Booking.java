package com.example.myapplication.RoomClasses;

import java.io.Serializable;

public class Booking implements Serializable{
    Available_Date DateOfStay;
    String roomName;
    String area;

    public Booking(Available_Date DateOfStay, String roomName,String area){
        this.DateOfStay = DateOfStay;
        this.roomName = roomName;
        this.area=area;
    }
}

