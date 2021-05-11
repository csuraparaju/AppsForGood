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

    private long wakeUpTime;
    private long sleepTime;
    private int exerciseDuration;

    public AvailableTimeFinder(List<ModifiedEvent> e, int exDuration, long wakeUpTime, long sleepTime) {
        allEvents = e;
        exerciseDuration = exDuration;
        this.wakeUpTime = wakeUpTime;
        this.sleepTime = sleepTime;
    }

    private static long getDurationBetweenEvents(DateTime db1, DateTime db2) {
        LocalDateTime ldt1 = LocalDateTime.ofInstant(Instant.ofEpochMilli(db1.getValue()), ZoneId.systemDefault());
        LocalDateTime ldt2 = LocalDateTime.ofInstant(Instant.ofEpochMilli(db2.getValue()), ZoneId.systemDefault());

        Duration duration = Duration.between(ldt1, ldt2);
        long seconds = duration.getSeconds();
        long info = seconds / 60;

        return info;
    }

    public RecyclerViewData getAvailableSlots() {
        Log.d("testLogs", exerciseDuration+"");
        if (allEvents.size() != 0)
            if (allEvents.get(0).getStartTimeMilli() - wakeUpTime > exerciseDuration * 60 * 1000)
                eventsAndSlots.addPossibleEvent(
                        new ModifiedEvent("Possible workout time",
                                wakeUpTime,
                                allEvents.get(0).getStartTimeMilli()
                        ));

        for (int i = 0; i < allEvents.size(); i++) {

            eventsAndSlots.addRealEvent(allEvents.get(i));

            if (i + 1 == allEvents.size()) {
                if (sleepTime - allEvents.get(i).getEndTimeMilli() > exerciseDuration * 60 * 1000)
                    eventsAndSlots.addPossibleEvent(
                            new ModifiedEvent("Possible workout time",
                                    allEvents.get(i).getEndTimeMilli(),
                                    sleepTime
                            ));
                return eventsAndSlots;
            }

            ModifiedEvent currEvent = allEvents.get(i);
            ModifiedEvent nextEvent = allEvents.get(i + 1);
            DateTime currEventEndTime = new DateTime(currEvent.getEndTimeMilli());
            DateTime nextEventStartTime = new DateTime(nextEvent.getStartTimeMilli());

            long timeBetween = getDurationBetweenEvents(currEventEndTime, nextEventStartTime);

            if (timeBetween > exerciseDuration) {
                ModifiedEvent availableEvent = new ModifiedEvent("Possible workout time",
                        currEventEndTime.getValue(),
                        nextEventStartTime.getValue());

                eventsAndSlots.addPossibleEvent(availableEvent);
            }
        }

        return eventsAndSlots;
    }

}