import java.util.*; 
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;

/**
 * Main class that models the mock scheduling algorithm.
 * @author Krish Suraparaju
 */

public class mainProcess {

    //Final variables for time calculations
    private static final int MINUTES_PER_HOUR = 60;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;
    private static int DURATION_OF_WORKOUT; //Passed in by the user

    //ArrayList that stores randomly generated Event objects
    private static ArrayList<Event> allEvents = new ArrayList<Event>();
    
    //Arraylist that will store the avaliable workout times as Event objects
    static ArrayList<Event> freeSlots = new ArrayList<Event>();

    /** Constructor - generates an ArrayList of random Event objects with random start and end dates.
     *  The size of this is quite big becuase the removeOverLaps() method call will decrease the size of the list
     *  quite significantly.
     */
    public mainProcess(){

        for(int i = 0; i<200; i++)
        {
            allEvents.add(new Event(6,22));

        }
        removeOverLaps(allEvents);
    }

    
    
    /** Allows the user to input how long they want to work out for. The default was set to 30 mins for testing purposes.
     *  @param d: intended duration of workout.
     */
    public void addWorkOutDuration(int d)
    {
        DURATION_OF_WORKOUT = d; 
    }
    
    /** Removes overlaps in Event objects. Two Event objects are considered to be overlapping if the start date of the 
     *  second object is before the end date of the first object.
     *  @param events: Arraylist of events that need to be processed.
     *  @return events: Arraylist of events that do not have any overlap.
     */
    private static ArrayList<Event> removeOverLaps(ArrayList<Event> events){


        for(int i = 0; i<events.size()-1;i++)
        {
            if(events.get(i+1).getStartTime().isBefore(events.get(i).getEndTime()))
            {
                events.remove(i+1);
                i--;
            }
        }

        return events;

    }
    
    /** Method to pretty print the ID, start time, and end time of each event object in the arraylist.
     */
    public void printTimes(){
        for(Event e:allEvents){
            System.out.println("Event ID: " + e.getID() + ", Scheduled Start Time: "+ e.stringifyTime(e.getStartTime())+ ", Scheduled End Time: "+ e.stringifyTime(e.getEndTime()));
        }
    }

    
    /** Method to calculate the duration between two LocalDateTime objects. 
     *  @param dob - first localDateTime object.
     *  @param now - second localDateTime object.
     *  @return long[] - long array consisting of the hours, minutes, and seconds in between two LocalDateTime objects.
     */
    private static long[] getTimeBetween(LocalDateTime dob, LocalDateTime now) {
        LocalDateTime today = LocalDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), dob.getHour(), dob.getMinute(), dob.getSecond());
        Duration duration = Duration.between(today, now);
        
        long seconds = duration.getSeconds();
        long hours = seconds / SECONDS_PER_HOUR;
        long minutes = ((seconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE);
        long secs = (seconds % SECONDS_PER_MINUTE);

        long[] info = new long[]{hours, minutes, secs};

        for(int i = 0; i<info.length;i++){
            info[i] = Math.abs(info[i]);
        }
        return info;

    }

    /** Helper method for the quicksort algorithm. concatenates two partitioned ArrayLists given from recursive quicksort.
     * @param leftHalf - left half of the current partition.
     * @param rightHalf - right half of the current partition.
     * @return ArrayList<Event> - concatenated ArrayList.
     */
    private static ArrayList<Event> concatenate(ArrayList<Event> leftHalf, ArrayList<Event> rightHalf){
		
        int mid = (int) Math.ceil((double)allEvents.size() / 2);
           ArrayList<Event> list = new ArrayList<Event>();
           
           for (int i = 0; i < leftHalf.size(); i++) {
               list.add(leftHalf.get(i));
           }
           
           list.add(allEvents.get(mid));
           
           for (int i = 0; i < rightHalf.size(); i++) {
               list.add(rightHalf.get(i));
           }
           
           return list;
       }
       
       
    /** Quicksort algorithm for sorting the Event objects in the ArrayList so that they are in chronological order. 
    * @param in - input ArrayList that needs to be sorted.
    * @return ArrayList<Event> - sorted ArrayList.
    */
    private static ArrayList<Event> quicksort(ArrayList<Event> in){
                        
        if(in.size() <= 1){
            return in;
        }

        int middle = (int) Math.ceil((double)in.size() / 2);
        LocalDateTime pivot = in.get(middle).getStartTime();
        ArrayList<Event> leftHalf = new ArrayList<Event>();
        ArrayList<Event> rightHalf = new ArrayList<Event>();
        
        for (int i = 0; i < in.size(); i++) {
            if(in.get(i).getStartTime().isBefore(pivot)){
                if(i == middle){
                    continue;
                }
                leftHalf.add(in.get(i));
            }
            else{
                rightHalf.add(in.get(i));
            }
        }
        
        return concatenate(quicksort(leftHalf), quicksort(rightHalf));
    }

    /** Method that will check if the time between two event Objects is large enough to fit a 30 minute workout.
     *  If it is, the method will suggest a specified time (LocalDateTime object) in between these two events.
     *  Note to reader: This method passes MOST test cases, but there are still some cases where some randomly
     *  generated events will work properly. This issue is being work on, and it will be fixed soon. 
     *  @return ArrayList<LocalDateTime> - ArrayList of suggested times for the user to workout. 
     */
    public ArrayList<Event> getAvaliableSlots(){
        quicksort(allEvents);
        for(int i = 0; i<allEvents.size();i++){
            if(i+1 == allEvents.size())
            {
                return freeSlots;
            }
            Event currEvent = allEvents.get(i);
            Event nextEvent = allEvents.get(i+1);
            long[] timeBetween = getTimeBetween(currEvent.getEndTime(),nextEvent.getEndTime());
            if(currEvent.getEndTime().getHour() < nextEvent.getStartTime().getHour() || (int)timeBetween[1] > DURATION_OF_WORKOUT){
                LocalDate ldStart = allEvents.get(i).getEndTime().plusMinutes(5).toLocalDate();
                LocalTime ltStart = allEvents.get(i).getEndTime().plusMinutes(5).toLocalTime();
                LocalDateTime start = LocalDateTime.of(ldStart, ltStart);                
                freeSlots.add(new Event(start));

                }
            }

        return freeSlots;
    }

    /** Main method to run/test the algorithm. 
     */
    public static void main(String[] args)
    {
        mainProcess ex1 = new mainProcess();
        ex1.addWorkOutDuration(30);
        ex1.printTimes();
        ArrayList<Event> freeSlots = ex1.getAvaliableSlots();

        for(Event e: freeSlots){
            if(freeSlots.size() == 1)
            {
                System.out.println("No free times found.");
            }
            else{
                System.out.println("Suggested workout time: " + e.stringifyTime(e.getStartTime()));
            }
        }
    }
 
}
