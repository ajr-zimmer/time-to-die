package com.ttd.cain.timetodie.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ttd.cain.timetodie.R; // I suppose this is necessary


/** TODO: Although this is a general landing page,
 * the goal is to be able to sign in with Google account to manage login **/
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void beginInfo(View view){
        Intent intent = new Intent(this, UserInfoActivity.class);
        startActivity(intent);
    }
}
