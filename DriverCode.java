import java.util.ArrayList;
import java.time.LocalDateTime;

public class DriverCode{
    
    public static String stringifyTime(LocalDateTime ldt){
        String date = ldt.toString();
        String time = date.split("T")[1];
        return time;
    }
    public static void main(String[] args)
    {
        main ec1 = new main();
        ec1.addWorkOutDuration(30);
        ArrayList<LocalDateTime> freeSlots = ec1.getAvaliableSlots();
        ec1.printTimes();
        ArrayList<String> prettyTimes = new ArrayList<String>();
        for(LocalDateTime ldt: freeSlots)
        {   
            prettyTimes.add(stringifyTime(ldt));
        }
        System.out.println("The follwing time slots are free to use: " + prettyTimes.toString());
    }
}