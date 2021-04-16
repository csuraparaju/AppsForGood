package com.example.appsforgood;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;

public class EventCollector implements Runnable{
    private Calendar calendar;
    private DateTime startTime;
    private DateTime endTime;
    private String orderBy;
    private int maxResults;

    private Events events = null;

    public EventCollector(Calendar calendar, DateTime startTime, String orderBy, int maxResults){
        this.calendar = calendar;
        this.startTime = startTime;
        this.endTime = endTime;
        this.orderBy = orderBy;
        this.maxResults = maxResults;
    }

    public void run() {
        try {
            events = calendar.events().list("primary")
                    .setMaxResults(1)
                    .setTimeMin(startTime)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Events getResults(){
        while(events == null){}
        return events;
    }
}
