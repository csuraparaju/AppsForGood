package com.example.appsforgood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;


/**
 * The activity that allows the user to sign-in to their Google account, opened upon app start if no Google account is found.
 * @author Christopher Walsh
 */
public class SignIn extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_GOOGLE_SIGN_IN = 6767;

    /**
     * The {@link GoogleSignInClient} that is used to sign in with this activity
     */
    private GoogleSignInClient mGoogleSignInClient;

    /**
     * Run on activity start to specify {@link GoogleSignInOptions} and generate the {@link #mGoogleSignInClient}.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        findViewById(R.id.sign_in_button).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(getString(R.string.web_client_id))
                .requestEmail()
                .requestScopes(new Scope("https://www.googleapis.com/auth/calendar"))
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    /**
     * Runs on Sign In button click to run {@link #signIn}.
     * @param v
     */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    /**
     * Starts a Google Sign in activity to allow the user to sign-in to google and provide Google calendar authorization
     */
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    /**
     * Collects the result of the Google sign in activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    /**
     * Parses the result of the google sign in activity to set the result of this {@link SignIn} activity to an intent containing
     * a {@link GoogleSignInAccount}. This intent is then returned to the {@link MainActivity} that
     * started this activity.
     * @param completedTask data from the google SignIn activity
     * @see MainActivity#onActivityResult(int, int, Intent)
     */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Intent output = new Intent();
            output.putExtra("account", account);
            setResult(RESULT_OK, output);
            finish();

        } catch (ApiException e) {
            Log.w("ApiException", "signInResult:failed code=" + e.getStatusCode());
        }
    }
}