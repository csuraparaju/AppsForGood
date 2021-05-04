package com.example.appsforgood;

import com.google.api.services.calendar.model.Event;

public class ModifiedEvent {
    public String name;
    private long startTimeMilli;
    private long endTimeMilli;

    public ModifiedEvent(Event e) {
        name = e.getId();
        startTimeMilli = e.getStart().getDateTime().getValue();
        endTimeMilli = e.getEnd().getDateTime().getValue();
    }

    public String getName() {
        return name;
    }

    public long getStartTimeMilli() {
        return startTimeMilli;
    }

    public long getEndTimeMilli() {
        return endTimeMilli;
    }
}
