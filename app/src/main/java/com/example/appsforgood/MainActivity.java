package com.example.appsforgood;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Parcel;
import android.util.Log;
import android.view.View;

import android.widget.Button;
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
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import schedulingBackEnd.AccessTokenGetter;
import schedulingBackEnd.EventCollector;
import schedulingBackEnd.ParcelableEvent;

public class MainActivity extends AppCompatActivity {
    private static final String APPLICATION_NAME = "Apps For Good Calendar API Testing";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final int RQ_CHOOSE_TIMES = 1212;
    private static final int RQ_SIGN_IN = 8787;
    private GoogleSignInAccount account = null;

    private Calendar calendar = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, SignIn.class);
        if (account == null) { // Starts SignIn activity in order to sign user in with Google
            startActivityForResult(intent, RQ_SIGN_IN);
        }

        TimePicker wakeUpPicker = findViewById(R.id.inputWakeUpTime);
        wakeUpPicker.setHour(5);
        wakeUpPicker.setMinute(0);
        TimePicker sleepPicker = findViewById(R.id.inputSleepTime);
        sleepPicker.setHour(22);
        sleepPicker.setMinute(0);
    }

    /**
     * Runs on {} button press, displays the next event on the user's calendar in the TextView above.
     *
     * @param v
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public void OpenCalView(View v) throws IOException, GeneralSecurityException, InterruptedException {
        if (calendar == null) {
            String authCode = account.getServerAuthCode();
            calendar = getCalendar(getToken(authCode, "N3T1hB9SZbG92LIaXurmzFP9"));
        }

        Instant instant = Instant.ofEpochMilli(System.currentTimeMillis());
        int startHour = instant.atZone(ZoneId.systemDefault()).getHour();
        int startMin = instant.atZone(ZoneId.systemDefault()).getMinute();

        Long dateMilli = (24 * 60 * 60 * 1000) + System.currentTimeMillis() - startHour * (60 * 60 * 1000) - startMin * (60 * 1000);

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
     * Contacts Google servers to exchange the user's authorization code (extracted from their GoogleSignInAccount)
     * for the user's access token.
     *
     * @param authCode
     * @param clientSecret
     * @return an access token that encodes the credentials necessary to access a user's google Calendar
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

    private long collectWakeUpTime() {
        TimePicker picker = findViewById(R.id.inputWakeUpTime);
        int hour = picker.getHour();
        int min = picker.getMinute();
        return hour * (60L * 60 * 1000) + min * (60L * 1000);
    }

    private long collectSleepTime() {
        TimePicker picker = findViewById(R.id.inputSleepTime);
        int hour = picker.getHour();
        int min = picker.getMinute();
        return hour * (60L * 60 * 1000) + min * (60L * 1000);
    }

    /**
     * Collects the results of activities started by StartActivityForResult() in this activity. This currently collects the user's
     * GoogleSignInAccount from the SignIn activity started in this activity's OnCreate().
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