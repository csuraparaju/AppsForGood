package com.example.appsforgood;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.time.Instant;
import java.time.ZoneId;

import schedulingBackEnd.ParcelableEvent;

public class AddNoteActivity extends AppCompatActivity {
    private EditText titlePicker;
    private TimePicker timePicker;
    private TextView textViewTimeConstraints;
    private ModifiedEvent possibleEvent;
    private int allowedDuration;
    private int index;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_exercise_event);

        possibleEvent = new ModifiedEvent((ParcelableEvent) this.getIntent().getParcelableExtra("possibleEvent"));
        allowedDuration = this.getIntent().getIntExtra("exerciseTime", 10);
        index = this.getIntent().getIntExtra("index", -1);

        titlePicker = findViewById(R.id.edit_text_title);
        timePicker = findViewById(R.id.TimePicker_start_time);
        textViewTimeConstraints = findViewById(R.id.textView_boundary_times);

        Instant instant = Instant.ofEpochMilli(possibleEvent.getStartTimeMilli());
        int startHour = instant.atZone(ZoneId.systemDefault()).getHour();
        int startMin = instant.atZone(ZoneId.systemDefault()).getMinute();

        timePicker.setHour(startHour);
        timePicker.setMinute(startMin);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24);
        setTitle("Add Event on ");
        textViewTimeConstraints.setText(possibleEvent.getStartAsString()+" and "+possibleEvent.getEndAsString());
    }

    private void saveEvent(){
        String title = titlePicker.getText().toString();
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        Instant instant = Instant.ofEpochMilli(possibleEvent.getStartTimeMilli());
        int startHour = instant.atZone(ZoneId.systemDefault()).getHour();
        int startMin = instant.atZone(ZoneId.systemDefault()).getMinute();

        Long dateMilli = possibleEvent.getStartTimeMilli() - startHour * (60*60*1000) - startMin * (60*1000);
        Long chosenTime = dateMilli + hour * (60 * 60 * 1000) + minute * (60 * 1000);
        Long chosenEndTime = chosenTime + allowedDuration * (60 * 1000);

        if(chosenTime >= possibleEvent.getStartTimeMilli() && chosenEndTime <= possibleEvent.getEndTimeMilli()){
            ModifiedEvent resultingEvent = new ModifiedEvent(title, chosenTime, chosenEndTime);

            Intent result = new Intent();
            result.putExtra("event", resultingEvent.toParcelableEvent());
            result.putExtra("index", index);
            setResult(RESULT_OK, result);
            finish();
        }
        else{
            Toast.makeText(getApplicationContext(),"Not a valid time",Toast.LENGTH_SHORT).show();
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
                saveEvent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
