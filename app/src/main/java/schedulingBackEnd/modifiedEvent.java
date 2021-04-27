package schedulingBackEnd;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class modifiedEvent {

    private ArrayList<Event> allEvents;
    private ArrayList<Event> freeSlots;
    private int workoutDuration;

    public modifiedEvent(ArrayList<Event> e, int time) {
        allEvents = e;
        workoutDuration = time;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private long[] getDurationBetweenEvents(DateTime db1, DateTime db2) {
        LocalDateTime ldt1 = LocalDateTime.ofInstant(Instant.ofEpochMilli(db1.getValue()), ZoneId.systemDefault());
        LocalDateTime ldt2 = LocalDateTime.ofInstant(Instant.ofEpochMilli(db2.getValue()), ZoneId.systemDefault());

        Duration duration = Duration.between(ldt1, ldt2);

        long seconds = duration.getSeconds();
        long hours = seconds / 3600;
        long minutes = ((seconds % 3600) / 60);
        long secs = (seconds % 60);

        long[] info = new long[]{hours, minutes, secs};

        for(int i = 0; i<info.length;i++){
            info[i] = Math.abs(info[i]);
        }
        return info;

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<Event> getAvaliableSlots(){
        for(int i = 0; i<allEvents.size();i++){
            if(i+1 == allEvents.size())
            {
                return freeSlots;
            }
            Event currEvent = allEvents.get(i);
            Event nextEvent = allEvents.get(i+1);

            long[] timeBetween = getDurationBetweenEvents(currEvent.getStart().getDateTime(),nextEvent.getEnd().getDateTime());

            if(currEvent.getEndTime().getHour() < nextEvent.getStartTime().getHour() || (int)timeBetween[1] > DURATION_OF_WORKOUT){
                LocalDate ldStart = allEvents.get(i).getEndTime().plusMinutes(5).toLocalDate();
                LocalTime ltStart = allEvents.get(i).getEndTime().plusMinutes(5).toLocalTime();
                LocalDateTime start = LocalDateTime.of(ldStart, ltStart);
                freeSlots.add(new Event(start));

            }
        }

        return freeSlots;
    }
}