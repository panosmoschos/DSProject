
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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


    // EXAMPLE
    // if 2024/06/06 - 2024/06/08 is available and we want to add 2024/06/09 - 2024/06/10
    // then the new available date is 2024/06/06 - 2024/06/10
    // we have also ensured that the dates do NOT overlap
    public List<Available_Date> safeAdd(List<Available_Date> oldDates){
        List<Available_Date> newDates = new ArrayList<>();
        boolean added = false; // if the new date gets added
        boolean Between = isBetween(oldDates); // if the new date is just between two old dates

        //bigLoop:
        for(Available_Date oldDate : oldDates){

            LocalDate oldFirstDay = oldDate.getFirstDay();
            LocalDate oldLastDay  = oldDate.getLastDay();

            // CHECK AN H EPOMENH THS TELEUTAIAS EINAI PRWTH ALLOU
            if(LastDay.plusDays(1).isEqual(oldFirstDay)){
                
                for (Available_Date OD:oldDates){
                    if (FirstDay.minusDays(1).isEqual(OD.LastDay)){
                        newDates.add(new Available_Date(OD.FirstDay, oldLastDay));
                        added = true;
                    }
                    break;
                }
                
                if(!added && !Between){
                    newDates.add(new Available_Date(FirstDay, oldLastDay));
                    added = true;
                }
            }
            // CHECK AN H PROHGOUMENH THS PRWTHS EINAI TELEUTAIA ALLOU
            else if(FirstDay.minusDays(1).isEqual(oldLastDay)){

                //  CHECK AN YPARXEI KAPOIA WSTE H EPOMENH THS TELEUTAIAS NA NAI PRWTH ALLOU
                for (Available_Date OD :oldDates){
                    if  (LastDay.plusDays(1).isEqual(OD.FirstDay)){
                        newDates.add(new Available_Date(oldFirstDay, OD.LastDay));
                        added = true;
                    }
                    break;
                }

                if(!added && !Between){
                    newDates.add(new Available_Date(oldFirstDay, LastDay));
                    added = true;
                }
            }
            else{
                newDates.add(oldDate);
            }
            
        }

        if (added == false){
            newDates.add(new Available_Date(FirstDay, LastDay));
        }

        return newDates;
    }


    // boolean values checking if a date is between two dates from Available Dates
    private boolean isBetween(List<Available_Date> ADS){
        boolean between = false;

        for(Available_Date OD : ADS){

            LocalDate oldFD = OD.getFirstDay();
            LocalDate oldLD  = OD.getLastDay();

            // CHECK AN H EPOMENH THS TELEUTAIAS EINAI PRWTH ALLOU
            if(LastDay.plusDays(1).isEqual(oldFD)){
                for (Available_Date AD:ADS){
                    if (FirstDay.minusDays(1).isEqual(AD.LastDay)){
                        between = true;
                    }
                }
            }else if(FirstDay.minusDays(1).isEqual(oldLD)){
                for (Available_Date AD :ADS){
                    if  (LastDay.plusDays(1).isEqual(AD.FirstDay)){
                        between = true;
                    }
                }
            }
        }
        return between;
    }


    // Checks if the date is available for booking
    public boolean isAvailable(List<Available_Date> available_dates){
        for (Available_Date freeDate : available_dates){

            boolean afterFirst = FirstDay.isAfter(freeDate.FirstDay);
            boolean equalsFirst = FirstDay.isEqual(freeDate.FirstDay);
            boolean beforeLast = LastDay.isBefore(freeDate.LastDay);
            boolean equalsLast = LastDay.isEqual(freeDate.LastDay);

            if ( ( afterFirst || equalsFirst)  && (beforeLast || equalsLast) ){
                return true;
            }
        }
        return false;
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

    public List<Available_Date> RemoveFrom(List<Available_Date> oldDates){
        List<Available_Date> availability = new ArrayList<>();

            // there are FOUR (4) CASES
            // 1. wanted date is between available date
            //    we split the available date in two
            // 2. wanted date is at the beginning of an available date
            //    the nextDay of the wanted day becomes the FirstDay of the available date
            // 3. wanted date is at the end of an available date
            //    the dayBefore becomes the LastDay of the available date
            // 4. theres no overlapping
            //    we keep the available date as it is

            for (Available_Date oldDate : oldDates) {
                LocalDate oldFirstDay = oldDate.getFirstDay();
                LocalDate oldLastDay  = oldDate.getLastDay();
                
                // 1. wanted date is within an available date 
                if (FirstDay.isAfter(oldFirstDay) && LastDay.isBefore(oldLastDay)) {
                    availability.add(new Available_Date(oldFirstDay, FirstDay.minusDays(1)));
                    availability.add(new Available_Date(LastDay.plusDays(1), oldLastDay));
                }
                // 2. wanted date is at the beginning of available date
                else if (LastDay.isBefore(oldLastDay) && FirstDay.isEqual(oldFirstDay)) {
                    oldDate.setFirstDay(LastDay.plusDays(1));
                    availability.add(oldDate);
                }
                // 3. wanted date is at the end of an available date
                else if (FirstDay.isAfter(oldFirstDay) && LastDay.isEqual(oldLastDay)) {
                    oldDate.setLastDay(FirstDay.minusDays(1));
                    availability.add(oldDate);
                }
                // 4. wanted date is not equal to available date
                else if (!new Available_Date(FirstDay, LastDay).OverlapsWith(oldDate)) {
                    availability.add(oldDate);
                }
            }

        return availability;
    }

    // TESTING
    public String getTimePeriod(){
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        return "From: " + df.format(FirstDay) + " To: " + df.format(LastDay);
    }

    public static int DaysAvailable(List<Available_Date> li){
        int days = 0;
        for (Available_Date date : li){
            while (!date.FirstDay.isEqual(date.LastDay)){
                days++;
            }
        }
        return days;
    }

    public static void main(String[] args) {
        LocalDate m = LocalDate.of(2022,07,22);
        LocalDate n = LocalDate.of(2022,7,29);
        Available_Date a = new Available_Date(m,n);
        System.out.println(a.OverlapsWith(a));
    }


}
