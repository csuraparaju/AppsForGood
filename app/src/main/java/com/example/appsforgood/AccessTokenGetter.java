package com.example.appsforgood;

import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AccessTokenGetter implements Callback {
    private String accessToken;

    public AccessTokenGetter(){
        accessToken = "";
    }

    public void onFailure(final Request request, final IOException e) {
        Log.e("hi", e.toString());
    }

    public void onResponse(Response response) throws IOException {
        try {
            JSONObject jsonObject = new JSONObject(response.body().string());
            final String message = jsonObject.toString(5);
            Log.i("hi", message);
            int tokenStart = message.indexOf("\"access_token\": \"")+"\"access_token\": \"".length();
            int tokenEnd = message.indexOf("\"expires_in\"")-8;
            //Log.i("hi", message.substring(tokenStart, tokenEnd));
            accessToken = message.substring(tokenStart, tokenEnd);
        } catch (JSONException e) {
            Log.i("hi", "Failure :(");
            e.printStackTrace();
        }
    }

    public String getAccessToken(){
        while (accessToken.equals("")){

        }
        return accessToken;
    }
}
