import java.util.*;
import java.time.*;
import java.util.concurrent.ThreadLocalRandom;
import java.time.LocalDateTime;

/**
 * Sample Event class that mocks the Event object returned by Google Calendar API
 * @author Krish Suraparaju
 */

public class Event {
    private String ID;
    private int minTime;
    private int maxTime;
    private long startTimeMil;
    private long endTimeMil;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    /**
     * Constructor - creates an Event object given a min and max time range as ints.
     * @param min - minimum time range for the Event object
     * @param max - maximum time range for the Event object
     */
    public Event(int min, int max){
        minTime = min;
        maxTime = max;
        LocalDateTime start = generateRandStartTime();
        generateRandEndTime(start);
        generateRandID();
    }
    /**
     * Overloaded constructor - creates an Event object given a LocalDateTime object. endTime is 
     * automatically set to null becuase of a lot of complicated reasons. I will fix this soon. 
     * @param start - minimum time range for the Event object
     */
    public Event(LocalDateTime start){
        startTime = start;
        endTime = null;
        generateRandID();
    }
    
    /**
     * Method to generate a random string of chars that represents the ID of the Event object. 
     */
    private void generateRandID(){

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
        
        ID = generatedString;
    }

    /** Method to generate a random LocalDateTimeObject between the given min and max times. 
     *  This method specifically will generate a start time of the Event.
     *  @return LocalDateTime - object to store the start time of the event.
     */
    private LocalDateTime generateRandStartTime() {
        LocalDateTime upperBound = LocalDate.now().atTime(minTime, 0);
        LocalDateTime lowerBound = LocalDate.now().atTime(maxTime,0);

        startTimeMil = upperBound.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        endTimeMil = lowerBound.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        long randomMillis = ThreadLocalRandom.current().nextLong(startTimeMil, endTimeMil);
        startTime = Instant.ofEpochMilli(randomMillis).atZone(ZoneId.systemDefault()).toLocalDateTime();

        return startTime;
        
    }

    
    /** Method to generate a random LocalDateTimeObject between the given LocalDateTime object and the max range. 
     *  This method specifically is used to generate the end time of the Event to. A parameter of of LocalDateTime exists
     *  to ensure that the end time of the event is not generated before the start time. 
     *  @param start - the value that is returned by the generateRandStartTime() method. 
     */
    private void generateRandEndTime(LocalDateTime start) {
        LocalDateTime upperBound = start;
        LocalDateTime lowerBoud;
        if(upperBound.getMinute() >= 30)
        {
            lowerBoud = LocalDate.now().atTime(start.getHour()+1,50);

        }
        else{
            lowerBoud = LocalDate.now().atTime(start.getHour()+1,0);
        }

        startTimeMil = upperBound.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        endTimeMil = lowerBoud.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        long randomMillis = ThreadLocalRandom.current().nextLong(startTimeMil, endTimeMil);
        endTime = Instant.ofEpochMilli(randomMillis).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /** Method to convert LocalDateTime object to string and pretty print it.
     *  @param ldt - LocalDateTime object to be printed
     *  @return String - pretty printed string
     */
    public String stringifyTime(LocalDateTime ldt){
        String date = ldt.toString();
        String time = date.split("T")[1].replaceAll("%.0f","");
        return time;
    }

    /** Getter method to access the ID of the Event object.
     *  @return String
     */
    public String getID(){
        return ID;
    }
    
    /** Getter method to access the start time of the Event
     *  @return LocalDateTime
     */
    public LocalDateTime getStartTime(){
        return startTime;

    }
    
    /** Getter method to access the end time of the Event.
     *  @return LocalDateTime
     */
    public LocalDateTime getEndTime(){
        return endTime;

    }

}
