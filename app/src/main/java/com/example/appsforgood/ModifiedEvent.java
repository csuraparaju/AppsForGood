package com.example.appsforgood;

import com.google.api.services.calendar.model.Event;

import java.util.ArrayList;
import java.util.List;

import schedulingBackEnd.ParcelableEvent;

public class ModifiedEvent {
    public String name;
    private long startTimeMilli;
    private long endTimeMilli;

    public ModifiedEvent(String name, long startTimeMilli, long endTimeMilli){
        this.name = name;
        this.startTimeMilli = startTimeMilli;
        this.endTimeMilli = endTimeMilli;
    }

    public ModifiedEvent(Event e) {
        name = e.getId();
        startTimeMilli = e.getStart().getDateTime().getValue();
        endTimeMilli = e.getEnd().getDateTime().getValue();
    }

    public ModifiedEvent(ParcelableEvent e) {
        name = e.getId();
        startTimeMilli = e.getStart();
        endTimeMilli = e.getEnd();
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

    public String getStartAsString() {
        return milliToTimeString(startTimeMilli);
    }

    public String getEndAsString() {
        return milliToTimeString(endTimeMilli);
    }


    public static String milliToTimeString(long millis) {
        int timeMillis = (int) (millis % (24 * 60 * 60 * 1000));

        int hours = timeMillis / (60 * 60 * 1000);
        int mins = (timeMillis / (60 * 1000)) % 60;

        if (hours < 12) {
            if (hours == 0) hours = 12;
            return String.format("%1$d:%2$02d am", hours, mins);
        } else {
            return String.format("%1$d:%2$02d pm", hours % 12, mins);
        }
    }

    public static List<ModifiedEvent> convertParcelableList(List<ParcelableEvent> events){
        List<ModifiedEvent> out = new ArrayList<ModifiedEvent>();
        for (int i = 0; i < events.size(); i++) {
            out.add(new ModifiedEvent(events.get(i)));
        }

        return out;
    }

    public static List<ModifiedEvent> convertEventList(List<Event> events){
        List<ModifiedEvent> out = new ArrayList<ModifiedEvent>();
        for (int i = 0; i < events.size(); i++) {
            out.add(new ModifiedEvent(events.get(i)));
        }

        return out;
    }
}
