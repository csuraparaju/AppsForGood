package schedulingBackEnd;

import android.util.Log;

import com.example.appsforgood.ModifiedEvent;
import com.google.api.client.util.DateTime;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import uiBackEnd.RecyclerViewData;

/**
 * Determines, stores, and provides the available time slots in a user's schedule on a given day
 *
 * @author Krish Suraparaju
 */
public class AvailableTimeFinder {

    private List<ModifiedEvent> allEvents;

    private long wakeUpTime;
    private long sleepTime;
    private int exerciseDuration;

    /**
     * Constructs an {@link AvailableTimeFinder} that can find available time slots in a schedule
     * given the parameters
     *
     * @param e the list of {@link ModifiedEvent}s that make up the user's schedule on a given day
     * @param exDuration the target duration for found exercise events
     * @param wakeUpTime the user's wake up time on the given day (millis)
     * @param sleepTime the user's sleep time on the given day (millis)
     */
    public AvailableTimeFinder(List<ModifiedEvent> e, int exDuration, long wakeUpTime, long sleepTime) {
        allEvents = e;
        exerciseDuration = exDuration;
        this.wakeUpTime = wakeUpTime;
        this.sleepTime = sleepTime;
    }

    /**
     * Find the duration between two {@link DateTime}s
     *
     * @param db1 first imputed {@link DateTime}
     * @param db2 second imputed {@link DateTime}
     * @return the amount of time between the times (minutes)
     */
    private static long getDurationBetweenEvents(DateTime db1, DateTime db2) {
        LocalDateTime ldt1 = LocalDateTime.ofInstant(Instant.ofEpochMilli(db1.getValue()), ZoneId.systemDefault());
        LocalDateTime ldt2 = LocalDateTime.ofInstant(Instant.ofEpochMilli(db2.getValue()), ZoneId.systemDefault());

        Duration duration = Duration.between(ldt1, ldt2);
        long seconds = duration.getSeconds();
        long info = seconds / 60;

        return info;
    }

    /**
     * Find available time slots in the user's schedule of events ({@link #allEvents}) that are
     * longer than the selected {@link #exerciseDuration} and between the selected {@link #wakeUpTime}
     * and {@link #sleepTime}.
     *
     * @return an instance of {@link RecyclerViewData} that stores the original events in the
     * original schedule and the determined available time slots.
     * @see {@link RecyclerViewData}
     */
    public RecyclerViewData getAvailableSlots() {
        RecyclerViewData eventsAndSlots = new RecyclerViewData();
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

            if (timeBetween >= exerciseDuration) {
                ModifiedEvent availableEvent = new ModifiedEvent("Possible workout time",
                        currEventEndTime.getValue(),
                        nextEventStartTime.getValue());

                eventsAndSlots.addPossibleEvent(availableEvent);
            }
        }

        return eventsAndSlots;
    }

}