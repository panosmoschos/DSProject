import java.io.Serializable;
import java.time.*;
import java.time.format.DateTimeFormatter;

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

    public boolean OverlapsWith(Available_Date newDate){
        if (newDate.LastDay.isBefore(FirstDay)){
            return false;
        }
        if (newDate.FirstDay.isAfter(LastDay)){
            return false;
        }
        return true;
    }


    // TESTING
    public String getTimePeriod(){
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        return "From: " + df.format(FirstDay) + " To: " + df.format(LastDay);
    }

    public static void main(String[] args) {
        LocalDate m = LocalDate.of(2022,07,22);
        LocalDate n = LocalDate.of(2022,7,29);
        Available_Date a = new Available_Date(m,n);
        System.out.println(a.OverlapsWith(a));
    }


}

