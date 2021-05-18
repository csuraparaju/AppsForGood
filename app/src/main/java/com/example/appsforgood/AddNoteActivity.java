package com.example.appsforgood;

import android.content.Intent;
import android.os.Bundle;
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

/**
 * Handles the interface that allows the user to add a new exercise event to their schedule during
 * an available time slot provided by the activities parent {@link CalViewActivity}.
 *
 * @see AppCompatActivity
 */
public class AddNoteActivity extends AppCompatActivity {
    private EditText titlePicker;
    private TimePicker timePicker;
    private TextView textViewTimeConstraints;
    private ModifiedEvent possibleEvent;
    private int allowedDuration;
    private int index;

    /**
     * Run on activity start to set the title and content of this activity. This method also creates
     * this events menu.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_exercise_event);

        possibleEvent = new ModifiedEvent((ParcelableEvent) this.getIntent().getParcelableExtra("possibleEvent"));
        allowedDuration = this.getIntent().getIntExtra("exerciseTime", 10);
        index = this.getIntent().getIntExtra("index", -1);
        String date = this.getIntent().getStringExtra("date");

        titlePicker = findViewById(R.id.edit_text_title);
        timePicker = findViewById(R.id.TimePicker_start_time);
        textViewTimeConstraints = findViewById(R.id.textView_boundary_times);

        Instant instant = Instant.ofEpochMilli(possibleEvent.getStartTimeMilli());
        int startHour = instant.atZone(ZoneId.systemDefault()).getHour();
        int startMin = instant.atZone(ZoneId.systemDefault()).getMinute();

        titlePicker.setText("New Exercise Event");

        timePicker.setHour(startHour);
        timePicker.setMinute(startMin);

        ModifiedEvent allowedTimes =
            new ModifiedEvent("allowedTimes",
                    possibleEvent.getStartTimeMilli(),
                    possibleEvent.getEndTimeMilli() - allowedDuration * 60 *1000);

        setTitle("Add Event on "+ date);
        textViewTimeConstraints.setText(allowedTimes.getStartAsString()+" and "+allowedTimes.getEndAsString());
    }

    /**
     * Run on save button press to collect the imputed data from this activity and return a new
     * event to the {@link CalViewActivity} that started this activity.
     *
     * @see CalViewActivity#onActivityResult(int, int, Intent)
     */
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

    /**
     * Adds a save button to the created menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_event_menu, menu);
        return true;
    }

    /**
     * Handles clicks on menu buttons, namely the save button.
     * @param item
     * @return
     */
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
