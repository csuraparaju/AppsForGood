package com.example.appsforgood;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Parcel;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Time;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import schedulingBackEnd.AccessTokenGetter;
import schedulingBackEnd.EventCollector;
import schedulingBackEnd.ParcelableEvent;

/**
 * The application's Main Activity. The location where inputs are taken in order to load and display
 * calendar events.
 *
 * @see AppCompatActivity
 * @author Christopher Walsh
 * @author Krish Suraparaju
 */
public class MainActivity extends AppCompatActivity {
    private static final String APPLICATION_NAME = "Apps For Good Calendar API Testing";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    // Shared Preference name constants
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String WAKE_HOUR = "wake hour";
    private static final String WAKE_MINUTE = "wake minute";
    private static final String SLEEP_HOUR = "sleep hour";
    private static final String SLEEP_MINUTE = "sleep minute";
    private static final String DURATION = "duration";

    // Request Code constants
    private static final int RQ_CHOOSE_TIMES = 1212;
    private static final int RQ_SIGN_IN = 8787;


    private GoogleSignInAccount account = null;

    /**
     * The user's Google Calendar. Collected by {@link #onActivityResult(int, int, Intent)}
     */
    private Calendar calendar = null;

    // Fields loaded by shared preferences.
    private static int wakeHour;
    private static int wakeMin;
    private static int sleepHour;
    private static int sleepMin;
    private static int workOutDuration;

    /**
     * Starts the sign in process by opening the activity {@link SignIn}. The user's google account is
     * collected in {@link #onActivityResult(int, int, Intent)}.
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, SignIn.class);
        if (account == null) { // Starts SignIn activity in order to sign user in with Google
            startActivityForResult(intent, RQ_SIGN_IN);
        }
        loadData();
        updateFields();

    }

    /**
     * Saves data selected in the activity to {@link SharedPreferences}.
     */
    public void saveData(){
        TimePicker wakeUpPicker = findViewById(R.id.inputWakeUpTime);
        TimePicker sleepPicker = findViewById(R.id.inputSleepTime);
        EditText durationField = findViewById(R.id.inputDuration);

        SharedPreferences sh = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE).edit();

        editor.putInt(WAKE_HOUR,wakeUpPicker.getHour());
        editor.putInt(WAKE_MINUTE,wakeUpPicker.getMinute());

        editor.putInt(SLEEP_HOUR,sleepPicker.getHour());
        editor.putInt(SLEEP_MINUTE,sleepPicker.getMinute());

        editor.putInt(DURATION,Integer.parseInt(durationField.getText().toString()));

        editor.apply();

        Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();

    }

    /**
     * Loads data from {@link SharedPreferences}.
     */
    public void loadData(){

        SharedPreferences sh = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);

        wakeHour = sh.getInt(WAKE_HOUR,5);
        wakeMin = sh.getInt(WAKE_MINUTE,0);

        sleepHour = sh.getInt(SLEEP_HOUR,22);
        sleepMin = sh.getInt(SLEEP_MINUTE,0);

        workOutDuration = sh.getInt(DURATION,0);
    }

    /**
     * Updates the fields in the activity with data loaded from {@link SharedPreferences}.
     */
    public void updateFields(){
        TimePicker wakeUpPicker = findViewById(R.id.inputWakeUpTime);
        TimePicker sleepPicker = findViewById(R.id.inputSleepTime);
        EditText durationField = findViewById(R.id.inputDuration);

        wakeUpPicker.setHour(wakeHour);
        wakeUpPicker.setMinute(wakeMin);

        sleepPicker.setHour(sleepHour);
        sleepPicker.setMinute(sleepMin);

        durationField.setText(String.valueOf(workOutDuration));
    }


    /**
     * Runs on "Continue" button press, collects events form the user's google calendar one the
     * specified day. Starts {@link CalViewActivity} to display these events and possible exercise
     * times. The times selected in {@link CalViewActivity} are collected by {@link #onActivityResult(int, int, Intent)}.
     *
     * @param v
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public void OpenCalView(View v) throws IOException, GeneralSecurityException, InterruptedException {
        saveData();

        if (calendar == null) {
            String authCode = account.getServerAuthCode();
            calendar = getCalendar(getToken(authCode, "N3T1hB9SZbG92LIaXurmzFP9"));
        }

        Long dateMilli = collectDateMillis();

        Log.d("TimeTest", "collect: "+collectDateMillis());
        Log.d("TimeTest", "other: "+dateMilli);

        DateTime now = new DateTime(dateMilli);
        DateTime end = new DateTime(dateMilli + (24 * 60 * 60 * 1000));
        EventCollector nextEventGetter = new EventCollector.Builder(calendar, EventCollector.START_END)
                .setStart(now)
                .setEnd(end)
                .setOrderBy("startTime")
                .build();

        Thread collectorThread = new Thread(nextEventGetter);
        collectorThread.start();
        collectorThread.join();
        Events events = nextEventGetter.getResults();

        ArrayList<Event> eventList = (ArrayList<Event>) events.getItems();

        ArrayList<ParcelableEvent> parcelableEventList = new ArrayList<ParcelableEvent>();
        for (int i = 0; i < eventList.size(); i++) {
            parcelableEventList.add(new ParcelableEvent(eventList.get(i)));
        }

        EditText durationField = findViewById(R.id.inputDuration);
        String durStr = durationField.getText().toString();
        int duration;
        Log.d("TextLogs", durStr);
        if(durStr != "") duration = Integer.parseInt(durStr);
        else duration = 0;

        long wakeUpTime = dateMilli + collectWakeUpTime();
        long sleepTime = dateMilli + collectSleepTime();

        Intent intent = new Intent(this, CalViewActivity.class)
                .putParcelableArrayListExtra("events", parcelableEventList)
                .putExtra("exDuration", duration)
                .putExtra("wakeUpTime", wakeUpTime)
                .putExtra("sleepTime", sleepTime);

        startActivityForResult(intent, RQ_CHOOSE_TIMES);
    }

    /**
     * Gets the user's calendar via http request.
     *
     * @param accessToken a token that encodes the user's Google account information and authorization
     * @return a Calendar object that stores the user's Google calendar.
     * @throws IOException
     * @throws GeneralSecurityException
     */
    Calendar getCalendar(String accessToken) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();
        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, new GoogleCredential().setAccessToken(accessToken))
                .setApplicationName(APPLICATION_NAME)
                .build();
        return service;
    }

    /**
     * Contacts Google servers to exchange the user's authorization code (extracted from their {@link GoogleSignInAccount})
     * for the user's access token.
     *
     * @param authCode
     * @param clientSecret
     * @return an access token that encodes the credentials necessary to access a user's google Calendar
     * @see GoogleSignInAccount
     * @see OkHttpClient
     */
    public String getToken(String authCode, String clientSecret) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormEncodingBuilder()
                .add("grant_type", "authorization_code")
                .add("client_id", "442942742888-lfen4d6s3srtbjp5ephh19jnc6qn49nq.apps.googleusercontent.com")
                .add("client_secret", clientSecret)
                .add("redirect_uri", "")
                .add("code", authCode)
                .build();

        final Request request = new Request.Builder()
                .url("https://www.googleapis.com/oauth2/v4/token")
                .post(requestBody)
                .build();

        AccessTokenGetter tokenGetter = new AccessTokenGetter();
        client.newCall(request).enqueue(tokenGetter);
        String accessToken = tokenGetter.getAccessToken();

        return accessToken;
    }

    /**
     * Gets the wake up time from the corresponding {@link TimePicker}.
     * @return wake up time in milliseconds since 00:00:00
     */
    private long collectWakeUpTime() {
        TimePicker picker = findViewById(R.id.inputWakeUpTime);
        int hour = picker.getHour();
        int min = picker.getMinute();
        return hour * (60L * 60 * 1000) + min * (60L * 1000);
    }

    /**
     * Gets the sleep time from the corresponding {@link TimePicker}.
     * @return sleep time in milliseconds since 00:00:00
     */
    private long collectSleepTime() {
        TimePicker picker = findViewById(R.id.inputSleepTime);
        int hour = picker.getHour();
        int min = picker.getMinute();
        return hour * (60L * 60 * 1000) + min * (60L * 1000);
    }

    /**
     * Collects the date from the corresponding {@link DatePicker} and finds the number of
     * milliseconds between that dates start time (00:00:00) and the epoch (in the system's default
     * time zone).
     * @return the number of milliseconds between the 00:00:00 on the selected date and the epoch
     */
    private long collectDateMillis() {
        DatePicker picker = findViewById(R.id.datePicker);

        int day = picker.getDayOfMonth();
        int month = picker.getMonth() + 1;
        int year = picker.getYear();

        //DateTime time = DateTime.parseRfc3339()

        String dayStr = (day >= 10) ? String.valueOf(day) : "0" + day;
        String monthStr = (month >= 10) ? String.valueOf(month) : "0" + month;
        String yearStr = String.valueOf(year);
        for(int i = 0; i < 4 - yearStr.length(); i++) yearStr = "0" + yearStr;

        String fullDate = dayStr + "/" + monthStr + "/" + yearStr + " 00:00:00";

        Log.d("TimeTest", fullDate);

        LocalDateTime time = LocalDateTime.parse(fullDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        long millis = time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;

        return millis;
    }

    /**
     * Collects the results of activities started by {@link AppCompatActivity#startActivityForResult(Intent, int)}
     * in this activity. If request code equals {@link #RQ_SIGN_IN} this collects the user's
     * GoogleSignInAccount from the SignIn activity started in {@link #onCreate(Bundle)}. If request
     * code equals {@link #RQ_CHOOSE_TIMES} this collects the events selected in the {@link CalViewActivity}
     * started by {@link #OpenCalView(View)} and adds them to the user's {@link #calendar}.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (RQ_SIGN_IN): {
                if (resultCode == Activity.RESULT_OK) {
                    account = (GoogleSignInAccount) data.getParcelableExtra("account");
                    Toast.makeText(getApplicationContext(), account.getEmail(), Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case (RQ_CHOOSE_TIMES): {
                if (resultCode == Activity.RESULT_OK) {
                    List<ParcelableEvent> newParcelableEvents = data.getParcelableArrayListExtra("newEvents");
                    List<ModifiedEvent> newEvents = ModifiedEvent.convertParcelableList(newParcelableEvents);
                    for (int i = 0; i < newEvents.size(); i++) {
                        try {
                            addEventToCalendar(newEvents.get(i));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * Adds the specified event to the user's google calendar {@link #calendar}.
     * @param event the specified event to be added.
     * @throws InterruptedException
     */
    public void addEventToCalendar(ModifiedEvent event) throws InterruptedException {
        Log.d("TestLog", "Adding and event");
        Event calendarEvent = new Event()
                .setSummary(event.getName());

        DateTime startDateTime = new DateTime(event.getStartTimeMilli());
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("America/New_York");
        calendarEvent.setStart(start);

        DateTime endDateTime = new DateTime(event.getEndTimeMilli());
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("America/New_York");
        calendarEvent.setEnd(end);

        String calendarId = "primary";
        Thread writerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("TestLog", "running");
                Event calEvent = null;
                try {
                    calEvent = calendar.events().insert(calendarId, calendarEvent).execute();
                } catch (IOException e) {
                    Log.d("TestLog", "Error: " + e.toString());
                }
                Log.d("TestLog", "\"Event created: %s\\n\"" + calEvent.getHtmlLink());
            }
        });

        writerThread.start();
        writerThread.join();

        Log.d("TestLog", "Event added");
    }
}