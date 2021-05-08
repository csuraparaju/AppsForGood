package com.example.appsforgood;

import com.google.api.services.calendar.model.Event;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
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

    public long getDuration(){
        return this.endTimeMilli - this.startTimeMilli;
    }

    public ParcelableEvent toParcelableEvent(){
        return new ParcelableEvent(this.name, this.startTimeMilli, this.endTimeMilli);
    }

    public static String milliToTimeString(long millis) {
        Instant instant = Instant.ofEpochMilli(millis);

        int hours = instant.atZone(ZoneId.systemDefault()).getHour();
        int mins = instant.atZone(ZoneId.systemDefault()).getMinute();

        if (hours < 12) {
            if (hours == 0) hours = 12;
            return String.format("%1$d:%2$02d am", hours, mins);
        } else {
            hours = hours % 12;
            if (hours == 0) hours = 12;
            return String.format("%1$d:%2$02d pm", hours, mins);
        }
    }

    public static List<ModifiedEvent> convertParcelableList(List<ParcelableEvent> events){
        List<ModifiedEvent> out = new ArrayList<ModifiedEvent>();
        for (int i = 0; i < events.size(); i++) {
            out.add(new ModifiedEvent(events.get(i)));
        }

        return out;
    }

    public static ArrayList<ParcelableEvent> convertToParcelableList(List<ModifiedEvent> events){
        ArrayList<ParcelableEvent> out = new ArrayList<ParcelableEvent>();
        for (int i = 0; i < events.size(); i++) {
            out.add(events.get(i).toParcelableEvent());
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
