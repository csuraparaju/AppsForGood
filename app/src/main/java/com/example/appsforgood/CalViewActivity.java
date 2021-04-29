package com.example.appsforgood;

import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.api.services.calendar.model.Event;

import java.util.ArrayList;

import schedulingBackEnd.AvailableTimeFinder;
import schedulingBackEnd.ParcelableEvent;
import uiBackEnd.EventView;

public class CalViewActivity extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_viewer);

        Bundle extras = getIntent().getExtras();
        ArrayList<ParcelableEvent> eventList = extras.getParcelableArrayList("events");
        int exDuration = extras.getInt("exDuration");

        LinearLayout col1 = findViewById(R.id.col1);
        LinearLayout col2 = findViewById(R.id.col2);
        LinearLayout col3 = findViewById(R.id.col3);

        col1.addView(new EventView(getApplicationContext(), new Event()));
        col2.addView(new EventView(getApplicationContext(), new Event()));
        col3.addView(new EventView(getApplicationContext(), new Event()));

        ArrayList<Event> avaliableSlots= new AvailableTimeFinder(eventList, exDuration).getAvaliableSlots();
        for(int i=0; i<avaliableSlots.size(); i++){
            Log.d("TestLogs", avaliableSlots.get(i).getStart().getDateTime().toStringRfc3339());
        }
    }


}
