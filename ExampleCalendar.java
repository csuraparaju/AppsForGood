import java.util.*;

public class ExampleCalendar {

    final int timeMin = 6000;
    final int timeMax = 2200; 
    Map<Integer, Boolean> hashmap = new HashMap<Integer, Boolean>();
    ArrayList<Integer> notAllowedTimes = new ArrayList<Integer>();
    ArrayList<Integer> acceptableTimes = new ArrayList<Integer>();
    public ExampleCalendar(){
    }
    
    public void generateExamples()
    {
        //Generate some example data
        hashmap.put(Integer.valueOf(0000), Boolean.valueOf(true));
        hashmap.put(Integer.valueOf(1000), Boolean.valueOf(true));
        hashmap.put(Integer.valueOf(2000), Boolean.valueOf(false));
        hashmap.put(Integer.valueOf(1300), Boolean.valueOf(true));
        hashmap.put(Integer.valueOf(1700), Boolean.valueOf(false));
        hashmap.put(Integer.valueOf(1750), Boolean.valueOf(true));
        hashmap.put(Integer.valueOf(1500), Boolean.valueOf(true));
        hashmap.put(Integer.valueOf(1400), Boolean.valueOf(true));


    }

    public void addExcludedTimes(ArrayList<Integer> n)
    {
        for(Integer val : n)
        {
            notAllowedTimes.add(val);
        } 
    }
    public ArrayList<Integer> findTimeSlots()
    {
        
        for (Integer val : hashmap.keySet() ) {
            if(hashmap.get(val) && !notAllowedTimes.contains(val))
            {
                if(Integer.compare(Integer.valueOf(val),timeMin) != -1 || Integer.compare(Integer.valueOf(val),timeMin) != 1)
                {
                    acceptableTimes.add(val);
                }
            }
        }
        for(int i = 0; i<acceptableTimes.size();i++)
            {
            if(Integer.valueOf(acceptableTimes.get(i)) == 0)
            {
                acceptableTimes.remove(i);
            }
        }
        return acceptableTimes;

    }

 
}
