package schedulingBackEnd;

import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A class that allows for the user's access token to be collected from a request running parallel to the main thread
 */
public class AccessTokenGetter implements Callback {
    private String accessToken;

    public AccessTokenGetter(){
        accessToken = "";
    }

    /**
     * Prints an error code if the access token request fails
     * @param request
     * @param e
     */
    public void onFailure(final Request request, final IOException e) {
        Log.e("hi", e.toString());
    }

    /**
     * Sets accessToken to the value of the requested access token by parsing the response of the web request
     * @param response
     * @throws IOException
     */
    public void onResponse(Response response) throws IOException {
        try {
            JSONObject jsonObject = new JSONObject(response.body().string());
            final String message = jsonObject.toString(5);
            int tokenStart = message.indexOf("\"access_token\": \"")+"\"access_token\": \"".length();
            int tokenEnd = message.indexOf("\"expires_in\"")-8;
            accessToken = message.substring(tokenStart, tokenEnd);
        } catch (JSONException e) {
            Log.i("hi", "Failure :(");
            e.printStackTrace();
        }
    }

    /**
     * @return The access token collected by this class after the web request is completed
     */
    public String getAccessToken(){
        while (accessToken.equals("")){ // waits for web request completion
        }
        return accessToken;
    }
}
