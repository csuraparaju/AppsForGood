import java.util.*;
import java.time.*;
import java.util.concurrent.ThreadLocalRandom;
import java.time.LocalDateTime;

//Class to mimic the Event object returned by the Google Calendar API
public class Event {
    private String name;
    private int minTime;
    private int maxTime;
    private long startTimeMil;
    private long endTimeMil;
    private LocalDateTime info;

    public Event(int min, int max){
        minTime = min;
        maxTime = max;
        generateRandDate();
        generateRandomName();
    }

    private void generateRandomName(){

        int leftLimit = 97; 
        int rightLimit = 122; 
        int targetStringLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int) 
                (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String generatedString = buffer.toString();
        
        name = generatedString;
    }

    //Generates a random date between the given min and max times on any given date
    private void generateRandDate() {
        LocalDateTime todayAtMin = LocalDate.now().atTime(minTime, 0);
        LocalDateTime todayAtMax = LocalDate.now().atTime(maxTime,0);

        startTimeMil = todayAtMin.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        endTimeMil = todayAtMax.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        long randomMillis = ThreadLocalRandom.current().nextLong(startTimeMil, endTimeMil);
        info = Instant.ofEpochMilli(randomMillis).atZone(ZoneId.systemDefault()).toLocalDateTime();
        
    }

    public String getName(){
        return name;
    }

    public LocalDateTime getDateTime(){
        return info;

    }

    public String stringifyTime(LocalDateTime ldt){
        String date = ldt.toString();
        String time = date.split("T")[1];
        return time;
    }
}
