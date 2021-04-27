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
    public static final int START_END = 0;
    public static final int START_AMOUNT = 1;

    private int type;
    private Calendar calendar;
    private DateTime startTime;
    private DateTime endTime;
    private String orderBy;
    private int maxResults;

    private Events events = null;

    public static class Builder{
        public EventCollector collector;

        public Builder(Calendar calendar, int type){
            collector = new EventCollector();
            collector.calendar = calendar;
            collector.type = type;
        }

        public Builder setStart(DateTime startTime){ collector.startTime = startTime; return this;}
        public Builder setEnd(DateTime endTime){ collector.endTime = endTime; return this;}
        public Builder setOrderBy(String orderBy){ collector.orderBy = orderBy; return this;}
        public Builder setMaxResults(int maxResults){ collector.maxResults = maxResults; return this;}

        public EventCollector build() {
            return collector;
        }
    }

    /**
     * The main processes of this class
     */
    public void run() {
        try {
            switch (type){
                case START_END :
                    events = calendar.events().list("primary")
                            .setTimeMin(startTime)
                            .setTimeMax(endTime)
                            .setOrderBy(orderBy)
                            .setSingleEvents(true)
                            .execute();
                    break;
                case START_AMOUNT :
                    events = calendar.events().list("primary")
                            .setMaxResults(maxResults)
                            .setTimeMin(startTime)
                            .setOrderBy(orderBy)
                            .setSingleEvents(true)
                            .execute();
                    break;
            }
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
        /*while(events == null){ // waiting for the results of run()
            Thread.currentThread().sleep(500);
        }*/
        return events;
    }
}
