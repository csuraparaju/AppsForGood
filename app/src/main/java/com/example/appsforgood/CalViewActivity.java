package com.example.appsforgood;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.api.services.calendar.model.Event;

import java.util.ArrayList;
import java.util.List;

import schedulingBackEnd.AvailableTimeFinder;
import schedulingBackEnd.ParcelableEvent;
import uiBackEnd.EventCardAdapter;

public class CalViewActivity extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_viewer);

        Bundle extras = getIntent().getExtras();
        List<ParcelableEvent> collectedEventList = extras.getParcelableArrayList("events");
        List<ModifiedEvent> eventList = ModifiedEvent.convertParcelableList(collectedEventList);
        int exDuration = extras.getInt("exDuration");

        ArrayList<ModifiedEvent> availableSlots= new AvailableTimeFinder(eventList, exDuration).getAvailableSlots();
        for(int i=0; i<availableSlots.size(); i++){
            Log.d("TestLogs", availableSlots.get(i).getStartAsString());
        }

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        EventCardAdapter adapter = new EventCardAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setEvents(eventList);
    }
}
