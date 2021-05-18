package com.example.appsforgood;

import com.google.api.services.calendar.model.Event;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import schedulingBackEnd.ParcelableEvent;

/**
 * Class to define the local data model that is used throughout the application. An object of this class
 * is mainly used to take in data regarding an Event (such as name, start time, and end time) and store it in an easily
 * accessible format.
 *
 * @author Christopher Walsh
 * @author Krish Suraparaju
 */
public class ModifiedEvent {
    public String name;
    private long startTimeMilli;
    private long endTimeMilli;

    /**
     * Overloaded constructor used to initialize a {@link ModifiedEvent} object when Google Calendar's
     * Event object is not passed in.
     * @param name - string variable to assign the name of the event.
     * @param startTimeMilli - long variable to store the millisecond time since Unix Epoch for the event's start time
     * @param endTimeMilli - long variable to store the millisecond time since Unix Epoch for the event's end time.
     */
    public ModifiedEvent(String name, long startTimeMilli, long endTimeMilli){
        this.name = name;
        this.startTimeMilli = startTimeMilli;
        this.endTimeMilli = endTimeMilli;
    }

    /**
     * Overloaded constructor used to initialize a {@link ModifiedEvent} object using data from
     * Google Calendar's Event object.
     * @param e - Google Calendar Event object.
     */
    public ModifiedEvent(Event e) {
        name = e.getId();
        startTimeMilli = e.getStart().getDateTime().getValue();
        endTimeMilli = e.getEnd().getDateTime().getValue();
    }

    /**
     * Overloaded constructor used to initialize a {@link ModifiedEvent} object using data
     * from an {@link ParcelableEvent} object.
     * @param e - instance of {@link ParcelableEvent} object being passed in
     */
    public ModifiedEvent(ParcelableEvent e) {
        name = e.getId();
        startTimeMilli = e.getStart();
        endTimeMilli = e.getEnd();
    }

    /**
     * Getter method to access the object's name
     * @return name - string value to store name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter method to access the object's start time in milliseconds since Unix Epoch
     * @return startTimeMilli - long value to store millis
     */
    public long getStartTimeMilli() {
        return startTimeMilli;
    }

    /**
     * Getter method to access the object's end time in milliseconds since Unix Epoch
     * @return endTimeMilli - long value to store millis
     */
    public long getEndTimeMilli() {
        return endTimeMilli;
    }

    /**
     * Getter method to access the object's start time as a formatted string using the {@link #milliToTimeString(long)}
     * @return startTimeMilli - string value to store formatted start time
     */
    public String getStartAsString() {
        return milliToTimeString(startTimeMilli);
    }

    /**
     * Getter method to access the object's start time as a formatted string using the {@link #milliToTimeString(long)}
     * @return endTimeMilli - string value to store formatted end time
     */
    public String getEndAsString() {
        return milliToTimeString(endTimeMilli);
    }

    /**
     * Method to calculate the duration (in millis) of an event by subtracting the end time from start time.
     * @return this.endTimeMilli - this.startTimeMilli - long value to store duration of event in millis.
     */
    public long getDuration(){
        return this.endTimeMilli - this.startTimeMilli;
    }

    /**
     * Method to convert the current object ({@link ModifiedEvent}) to a
     * {@link ParcelableEvent} object.
     * @return ParcelableEvent - newly created {@link ParcelableEvent} object with data from the
     * {@link ModifiedEvent}
     * object.
     */
    public ParcelableEvent toParcelableEvent(){
        return new ParcelableEvent(this.name, this.startTimeMilli, this.endTimeMilli);
    }

    /**
     * Method to convert millisecond time to a formatted string. This is done by finding the system default timezone and calculating the
     * corresponding values for hours and minutes.
     * @param millis - long value to convert from millis to string.
     * @return - formatted string with hours and minutes of the current day.
     */
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

    /**
     * Helper method to convert a list of {@link schedulingBackEnd.ParcelableEvent} objects into a list containing
     * {@link ModifiedEvent} objects. The given List is traversed and a new "out" List is populated with
     * {@link ModifiedEvent} objects using the {@link #ModifiedEvent(ParcelableEvent)}
     * constructor.
     * @param events - List containing {@link ModifiedEvent} objects.
     * @return
     */
    public static List<ModifiedEvent> convertParcelableList(List<ParcelableEvent> events){
        List<ModifiedEvent> out = new ArrayList<ModifiedEvent>();
        for (int i = 0; i < events.size(); i++) {
            out.add(new ModifiedEvent(events.get(i)));
        }

        return out;
    }

    /**
     * Helper method to convert a list of {@link com.example.appsforgood.ModifiedEvent} objects into a list containing
     * {@link ParcelableEvent} objects. The given List is traversed and a new "out" List is populated with
     * {@link ParcelableEvent} objects using the {@link #toParcelableEvent()} method.
     * @param events - List containing {@link ModifiedEvent} objects.
     * @return
     */

    public static ArrayList<ParcelableEvent> convertToParcelableList(List<ModifiedEvent> events){
        ArrayList<ParcelableEvent> out = new ArrayList<ParcelableEvent>();
        for (int i = 0; i < events.size(); i++) {
            out.add(events.get(i).toParcelableEvent());
        }

        return out;
    }

    /**
     * Helper method to convert a list of Google Calendar's Event objects into a list containing
     * {@link ModifiedEvent} objects. The given List is traversed and a new "out" List is populated with
     * {@link ModifiedEvent} objects using the {@link #ModifiedEvent(Event)}
     *      * constructor.
     * @param events - List containing {@link ModifiedEvent} objects.
     * @return
     */
    public static List<ModifiedEvent> convertEventList(List<Event> events){
        List<ModifiedEvent> out = new ArrayList<ModifiedEvent>();
        for (int i = 0; i < events.size(); i++) {
            out.add(new ModifiedEvent(events.get(i)));
        }

        return out;
    }
}
