package com.example.appsforgood;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Parcel;
import android.view.View;

import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import schedulingBackEnd.AccessTokenGetter;
import schedulingBackEnd.EventCollector;
import schedulingBackEnd.ParcelableEvent;

public class MainActivity extends AppCompatActivity {
    private static final String APPLICATION_NAME = "Apps For Good Calendar API Testing";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final int RQ_SIGN_IN = 8787;
    private GoogleSignInAccount account = null;
    
    private Calendar calendar = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, SignIn.class);
        if(account == null) { // Starts SignIn activity in order to sign user in with Google
            startActivityForResult(intent, RQ_SIGN_IN);
        }
    }

    /**
     * Runs on "Display Next Event" button press, displays the next event on the user's calendar in the TextView above.
     * @param v
     * @throws IOException
     * @throws GeneralSecurityException
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void displayNextEvent(View v) throws IOException, GeneralSecurityException, InterruptedException {

        if(calendar == null) {
            String authCode = account.getServerAuthCode();
            calendar = getCalendar(getToken(authCode, "N3T1hB9SZbG92LIaXurmzFP9"));
        }

        DateTime now = new DateTime(System.currentTimeMillis());
        EventCollector nextEventGetter = new EventCollector.Builder(calendar, EventCollector.START_AMOUNT)
                .setStart(now)
                .setMaxResults(6)
                .setOrderBy("startTime")
                .build();

        Thread collectorThread = new Thread(nextEventGetter);
        collectorThread.start();
        collectorThread.join();
        Events events = nextEventGetter.getResults();

        ArrayList<Event> eventList = (ArrayList<Event>) events.getItems();

        /*ArrayList<Event> avaliableSlots= new ModifiedEvent(eventList, 30).getAvaliableSlots();
        for(int i=0; i<avaliableSlots.size(); i++){
            Log.d("TestLogs", avaliableSlots.get(i).getStart().getDateTime().toStringRfc3339());
        }*/

        ArrayList<ParcelableEvent> parcelableEventList = new ArrayList<ParcelableEvent>();
        for(int i = 0; i<eventList.size(); i++){
            parcelableEventList.add(new ParcelableEvent(eventList.get(i)));
        }

        Intent intent = new Intent(this, CalViewActivity.class)
                .putParcelableArrayListExtra("events", parcelableEventList)
                .putExtra("exDuration", 30);

        startActivity(intent);
    }

    /**
     * Gets the user's calendar via http request.
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
     * @param authCode
     * @param clientSecret
     * @return an access token that encodes the credentials necessary to access a user's google Calendar
     */
    public String getToken(String authCode, String clientSecret){
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormEncodingBuilder()
                .add("grant_type", "authorization_code")
                .add("client_id", "442942742888-lfen4d6s3srtbjp5ephh19jnc6qn49nq.apps.googleusercontent.com")
                .add("client_secret", clientSecret)
                .add("redirect_uri","")
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
     * Collects the results of activities started by StartActivityForResult() in this activity. This currently collects the user's
     * GoogleSignInAccount from the SignIn activity started in this activity's OnCreate().
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (RQ_SIGN_IN) : {
                if (resultCode == Activity.RESULT_OK) {
                    account = (GoogleSignInAccount) data.getParcelableExtra("account");
                    Toast.makeText(getApplicationContext(),account.getEmail(),Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
}