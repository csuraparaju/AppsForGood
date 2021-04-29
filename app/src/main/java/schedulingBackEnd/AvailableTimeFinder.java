package schedulingBackEnd;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class AvailableTimeFinder {

    private ArrayList<ParcelableEvent> allEvents;
    private ArrayList<Event> freeSlots = new ArrayList<Event>();

    private int exerciseDuration;

    public AvailableTimeFinder(ArrayList<ParcelableEvent> e, int exDuration) {
        allEvents = e;
        exerciseDuration = exDuration;
    }

    private static long getDurationBetweenEvents(DateTime db1, DateTime db2) {
        LocalDateTime ldt1 = LocalDateTime.ofInstant(Instant.ofEpochMilli(db1.getValue()), ZoneId.systemDefault());
        LocalDateTime ldt2 = LocalDateTime.ofInstant(Instant.ofEpochMilli(db2.getValue()), ZoneId.systemDefault());

        Duration duration = Duration.between(ldt1, ldt2);
        long seconds = duration.getSeconds();
        long info = seconds/60;

        return info;
    }

    public ArrayList<Event> getAvaliableSlots(){
        for(int i = 0; i<allEvents.size();i++){
            if(i+1 == allEvents.size())
            {
                return freeSlots;
            }

            ParcelableEvent currEvent = allEvents.get(i);
            ParcelableEvent nextEvent = allEvents.get(i+1);
            DateTime currEventEndTime = new DateTime(currEvent.getEnd());
            DateTime nextEventStartTime = new DateTime(nextEvent.getStart());

            long timeBetween = getDurationBetweenEvents(currEventEndTime, nextEventStartTime);

            if(timeBetween > exerciseDuration) {
                Event availableEvent = new Event()
                        .setSummary("Available work out time");

                DateTime startDateTime = new DateTime(currEventEndTime.getValue() + 30000);
                EventDateTime start = new EventDateTime()
                        .setDateTime(startDateTime)
                        .setTimeZone("America/New_York");
                availableEvent.setStart(start);

                DateTime endDateTime = new DateTime(currEventEndTime.getValue() + 1800000);
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