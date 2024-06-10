package com.example.dsfrontendproject;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Booking implements Serializable{
    Available_Date DateOfStay;
    String roomName;
    String area;

    public Booking(Available_Date DateOfStay, String roomName,String area){
        this.DateOfStay = DateOfStay;
        this.roomName = roomName;
        this.area=area;
    }

    public void ShowBooking(){
        System.out.println("Room: " + roomName + "\n" + DateOfStay.getTimePeriod());
    }

    public String getArea(){
        return area;
    }

    public static void showBookingsByArea(List<Booking> bookings){

        // seeing how many areas are there
        List<String> areas = new ArrayList<>();
        for (Booking booking : bookings){
            if (!areas.contains(booking.area)){
                areas.add(booking.getArea());
            }
        }

        int[] numOfBookings = new int[areas.size()];

        // array that counts how many times each area had a booking
        for (Booking b : bookings) {
            int index = areas.indexOf(b.area);
            if (index != -1) {
                numOfBookings[index]++;
            }
        }

        for (int i = 0; i < areas.size(); i++) {
            System.out.println(areas.get(i) + ": " + numOfBookings[i]);
        }

    }


    // TESTING
    public static void main(String[] args) {
        List<Booking> BOOKINGS = new ArrayList<>();

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate test = LocalDate.parse("2024/06/06",df);

        Available_Date coco = new Available_Date(test, test);
        BOOKINGS.add(new Booking(coco, "maria", "Naxos"));
        BOOKINGS.add(new Booking(coco, "maria", "Ios"));
        BOOKINGS.add(new Booking(coco, "maria", "Naxos"));
        BOOKINGS.add(new Booking(coco, "maria", "Patra"));
        BOOKINGS.add(new Booking(coco, "maria", "Patra"));
        BOOKINGS.add(new Booking(coco, "maria", "Patra"));

        showBookingsByArea(BOOKINGS);
    }
}