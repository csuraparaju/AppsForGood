import java.util.*; 
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.*;
import java.time.Duration;



public class main {

    private static final int MINUTES_PER_HOUR = 60;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;
    private static int DURATION_OF_WORKOUT; 
    private static ArrayList<Event> allEvents = new ArrayList<Event>();
    static ArrayList<LocalDateTime> freeSlots = new ArrayList<LocalDateTime>();

    public main(){
        for(int i = 0; i < 15;i++){
            allEvents.add(new Event(0,23));
        }
    }
    
    public void addWorkOutDuration(int d)
    {
        DURATION_OF_WORKOUT = d; 
    }
    
    public void printTimes(){
        for(Event e:allEvents){
            System.out.println(e.getName() + ": " + e.stringifyTime(e.getDateTime()));
        }
    }
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
 
    private static int compare(Event firstEvent, Event secondEvent) {
        int result;
        if (firstEvent.getDateTime().isBefore(secondEvent.getDateTime())){
            result = -1;
        }
        else if(firstEvent.getDateTime().isAfter(secondEvent.getDateTime())){
            result = 1;
        }
        else{
            result = 0;
        }

        return result;
    }

    //Simple bubble sort algorithm to sort objects in the arraylist based on their time
    private static void sort(ArrayList<Event> in){
        int n = in.size();
        boolean sorted = false;
        
        while(!sorted){
            sorted = true;
            for(int i = 0; i < n-1; i++){
                if(compare(in.get(i),in.get(i+1)) == 1){
                    Collections.swap(in,i,i+1);
                    sorted = false;
                }
            }
        }

    }

    //compare first datetime obj with the next one in list and find free time slots in between
    public ArrayList<LocalDateTime> getAvaliableSlots(){
        sort(allEvents);
        for(int i = 0; i<allEvents.size();i++){
            if(i+1 == allEvents.size())
            {
                return freeSlots;
            }
            long[] timeBetween = getTimeBetween(allEvents.get(i).getDateTime(),allEvents.get(i+1).getDateTime());
            if(timeBetween[1] > DURATION_OF_WORKOUT){
                LocalDate ld = allEvents.get(i).getDateTime().plusMinutes(5).toLocalDate();
                LocalTime lt = allEvents.get(i).getDateTime().plusMinutes(5).toLocalTime();
                LocalDateTime ldt = LocalDateTime.of(ld, lt);
                freeSlots.add(ldt);
                }
            }

        return freeSlots;
    }

        //Note: Tried to implement a quicksort algorithm, but couldn't get it to work. Maybe you can read through and see where I went wrong? 


    // private static ArrayList<Event> concatenate(ArrayList<Event> leftHalf, ArrayList<Event> rightHalf){
		
    //     int mid = (int) Math.ceil((double)allEvents.size() / 2);
	// 	ArrayList<Event> list = new ArrayList<Event>();
		
	// 	for (int i = 0; i < leftHalf.size(); i++) {
	// 		list.add(leftHalf.get(i));
	// 	}
		
	// 	list.add(allEvents.get(mid));
		
	// 	for (int i = 0; i < rightHalf.size(); i++) {
	// 		list.add(rightHalf.get(i));
	// 	}
		
	// 	return list;
	// }
	
    // private static ArrayList<Event> quicksort(ArrayList<Event> in){
		     		
	// 	if(in.size() <= 1){
	// 		return in;
	// 	}

	// 	int middle = (int) Math.ceil((double)in.size() / 2);
	// 	LocalDateTime pivot = in.get(middle).getDateTime();
	// 	ArrayList<Event> leftHalf = new ArrayList<Event>();
	// 	ArrayList<Event> rightHalf = new ArrayList<Event>();
		
	// 	for (int i = 0; i < in.size(); i++) {
	// 		if(in.get(i).getDateTime().isBefore(pivot)){
	// 			if(i == middle){
	// 				continue;
	// 			}
	// 			leftHalf.add(in.get(i));
	// 		}
	// 		else{
	// 			rightHalf.add(in.get(i));
	// 		}
	// 	}
		
	// 	return concatenate(quicksort(leftHalf), quicksort(rightHalf));
	// }
 
}
