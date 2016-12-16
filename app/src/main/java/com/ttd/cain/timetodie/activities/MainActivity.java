package com.ttd.cain.timetodie.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ttd.cain.timetodie.R; // I suppose this is necessary
import com.ttd.cain.timetodie.utils.Utils;


/** TODO: Although this is a general landing page,
 * the goal is to be able to sign in with Google account to manage login **/
public class MainActivity extends AppCompatActivity {

    // TODO: Skip capturing user info if we have already collected it
    public static final String PREF_USER_FIRST_TIME = "user_first_time";
    boolean isUserFirstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void beginInfo(View view){
        isUserFirstTime = Boolean.valueOf(Utils.readSharedSetting(MainActivity.this, PREF_USER_FIRST_TIME, "true"));
        if(isUserFirstTime){
            // Start the tabbed CaptureUserInfoActivity that captures user input
            Intent initialUserInfoCapture = new Intent(this, CaptureUserInfoActivity.class);
            startActivity(initialUserInfoCapture);
        } else {
            // Skip the tabbed CaptureUserInfoActivity and go to the main countdown view
            Intent mainCountdownView = new Intent(this, DisplayUserInfoActivity.class);
            startActivity(mainCountdownView);
        }
    }
}
