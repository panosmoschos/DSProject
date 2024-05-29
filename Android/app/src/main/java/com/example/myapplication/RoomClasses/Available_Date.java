package com.example.myapplication.RoomClasses;

import java.time.*;
import java.io.Serializable;

public class Available_Date implements Serializable{
    // FORMAT DATE: yyyy/MM/dd

    private LocalDate FirstDay;
    private LocalDate LastDay;

    Available_Date(LocalDate FirstDay, LocalDate LastDay){
        this.FirstDay = FirstDay;
        this.LastDay = LastDay;
    }

    public LocalDate getFirstDay() {
        return FirstDay;
    }

    public LocalDate getLastDay() {
        return LastDay;
    }

    public void setFirstDay(LocalDate firstDay) {
        FirstDay = firstDay;
    }

    public void setLastDay(LocalDate lastDay) {
        LastDay = lastDay;
    }
}

