package schedulingBackEnd;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class modifiedEvent {

    private ArrayList<Event> allEvents;
    private ArrayList<Event> freeSlots;

    public modifiedEvent(ArrayList<Event> e) {
        allEvents = e;
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
            DateTime currEventDateTime = currEvent.getEnd().getDateTime();
            DateTime nextEventDateTime = nextEvent.getEnd().getDateTime();

            LocalDateTime currLdt = LocalDateTime.ofInstant(Instant.ofEpochMilli(currEventDateTime.getValue()), ZoneId.systemDefault());
            LocalDateTime nextLdt = LocalDateTime.ofInstant(Instant.ofEpochMilli(nextEventDateTime.getValue()), ZoneId.systemDefault());

            long[] timeBetween = getDurationBetweenEvents(currEvent.getStart().getDateTime(),nextEvent.getEnd().getDateTime());

            if(currLdt.getHour() < nextLdt.getHour() || (int)timeBetween[1] > 30){
                Event availableEvent = new Event()
                        .setSummary("Available work out time");

                DateTime startDateTime = new DateTime(currEventDateTime.getValue()+30000);
                EventDateTime start = new EventDateTime()
                        .setDateTime(startDateTime)
                        .setTimeZone("America/New_York");
                availableEvent.setStart(start);

                DateTime endDateTime = new DateTime(currEventDateTime.getValue()+1800000);
                EventDateTime end = new EventDateTime()
                        .setDateTime(endDateTime)
                        .setTimeZone("America/New_York");
                availableEvent.setEnd(end);

                freeSlots.add(availableEvent);
            }
        }

        return freeSlots;
    }

}