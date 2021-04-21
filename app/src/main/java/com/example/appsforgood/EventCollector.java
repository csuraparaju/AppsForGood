package com.example.appsforgood;

import android.util.Log;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;

/**
 * A Runnable class that allows Google calendar reading requests to take place on a thread separate from the main thread
 * as Android does not permit web requests on main threads.
 */
public class EventCollector implements Runnable{
    private Calendar calendar;
    private DateTime startTime;
    private String orderBy;
    private int maxResults;

    private Events events = null;

    /**
     *
     * @param calendar the calendar object to read from
     * @param startTime the start time of reading
     * @param orderBy a key string that determines the ordering of events in results
     * @param maxResults the maximum number of results that will be taken from the user's calendar
     */
    public EventCollector(Calendar calendar, DateTime startTime, String orderBy, int maxResults){
        this.calendar = calendar;
        this.startTime = startTime;
        this.orderBy = orderBy;
        this.maxResults = maxResults;
    }

    /**
     * The main processes of this class
     */
    public void run() {
        try {
            events = calendar.events().list("primary")
                    .setMaxResults(1)
                    .setTimeMin(startTime)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
        } catch (IOException e) {
            events = new Events();
            e.printStackTrace();
        }
    }

    /**
     * Gets the results of run() in the form of an Events object
     * @return a list of events on the user's calendar generated using the parameters determined in the constructor
     */
    public Events getResults() throws InterruptedException {
        while(events == null){ // waiting for the results of run()
            Thread.currentThread().sleep(500);
        }
        return events;
    }
}
