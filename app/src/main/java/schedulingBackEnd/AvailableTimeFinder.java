package schedulingBackEnd;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.appsforgood.ModifiedEvent;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import uiBackEnd.RecyclerViewData;

public class AvailableTimeFinder {

    private List<ModifiedEvent> allEvents;
    private RecyclerViewData eventsAndSlots = new RecyclerViewData();

    private int exerciseDuration;

    public AvailableTimeFinder(List<ModifiedEvent> e, int exDuration) {
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

    public RecyclerViewData getAvailableSlots(){
        for(int i = 0; i<allEvents.size();i++){

            eventsAndSlots.addRealEvent(allEvents.get(i));

            if(i+1 == allEvents.size())
            {
                return eventsAndSlots;
            }

            ModifiedEvent currEvent = allEvents.get(i);
            ModifiedEvent nextEvent = allEvents.get(i+1);
            DateTime currEventEndTime = new DateTime(currEvent.getEndTimeMilli());
            DateTime nextEventStartTime = new DateTime(nextEvent.getStartTimeMilli());

            long timeBetween = getDurationBetweenEvents(currEventEndTime, nextEventStartTime);

            if(timeBetween > exerciseDuration) {
                ModifiedEvent availableEvent = new ModifiedEvent("Possible workout time",
                        currEventEndTime.getValue(),
                        nextEventStartTime.getValue());

                eventsAndSlots.addPossibleEvent(availableEvent);
            }
        }

        return eventsAndSlots;
    }

}