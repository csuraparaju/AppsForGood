package com.example.appsforgood;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;

import java.util.ArrayList;
import java.util.List;

import schedulingBackEnd.AvailableTimeFinder;
import schedulingBackEnd.ParcelableEvent;
import uiBackEnd.EventCardAdapter;
import uiBackEnd.RecyclerViewData;

public class CalViewActivity extends AppCompatActivity {
    public static final int ADD_NOTE_REQUEST_CODE = 1;
    EventCardAdapter adapter;
    List<ModifiedEvent> newEvents;

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_viewer);

        Bundle extras = getIntent().getExtras();
        List<ParcelableEvent> collectedEventList = extras.getParcelableArrayList("events");
        List<ModifiedEvent> eventList = ModifiedEvent.convertParcelableList(collectedEventList);
        int exDuration = extras.getInt("exDuration");
        long wakeUpTime = extras.getLong("wakeUpTime");
        long sleepTime = extras.getLong("sleepTime");

        RecyclerViewData recyclerViewData =
                new AvailableTimeFinder(eventList, exDuration, wakeUpTime, sleepTime)
                        .getAvailableSlots();

        for(int i=0; i<recyclerViewData.size(); i++){
            Log.d("TestLogs", recyclerViewData.getEvent(i).getStartAsString());
        }

        Log.d("TestLogs","From Google.DateTime: "+ new DateTime(0).toStringRfc3339());
        Log.d("TestLogs","From ModifiedEvent: "+ new ModifiedEvent("a",0,0).getStartAsString());

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new EventCardAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setEvents(recyclerViewData);
        adapter.setOnCardClickListener(new EventCardAdapter.OnCardClickListener() {
            @Override
            public void onCardClick(ModifiedEvent event, int index) {
                Intent intent = new Intent(CalViewActivity.this, AddNoteActivity.class);
                intent.putExtra("possibleEvent", event.toParcelableEvent());
                intent.putExtra("exerciseTime", exDuration);
                intent.putExtra("index", index);
                startActivityForResult(intent, ADD_NOTE_REQUEST_CODE);
            }
        });

        newEvents = new ArrayList<ModifiedEvent>();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK){
            return;
        }

        switch (requestCode){
            case ADD_NOTE_REQUEST_CODE:
                ModifiedEvent newEvent = new ModifiedEvent((ParcelableEvent) data.getParcelableExtra("event"));
                int index = data.getIntExtra("index", -1);
                adapter.replaceEvent(newEvent, true, index);
                newEvents.add(newEvent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_event_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_event:
                close();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void close(){
        Intent data = new Intent();
        data.putParcelableArrayListExtra("newEvents", ModifiedEvent.convertToParcelableList(newEvents));
        setResult(RESULT_OK, data);
        finish();
    }
}
