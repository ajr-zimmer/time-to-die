package com.ttd.cain.timetodie.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ttd.cain.timetodie.R;
import com.ttd.cain.timetodie.utils.Utils;

public class DisplayUserInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user_info);

        String country = Utils.readSharedSetting(DisplayUserInfoActivity.this, CaptureUserInfoActivity.PREF_USER_COUNTRY, "The Void");
        String dateOfBirth = Utils.readSharedSetting(DisplayUserInfoActivity.this, CaptureUserInfoActivity.PREF_USER_DOB, "Never Born");
        String sex = Utils.readSharedSetting(DisplayUserInfoActivity.this, CaptureUserInfoActivity.PREF_USER_SEX, "No Sex");

        String message = "Place of Dying: "+ country +"\n"+ "Start of Dying: " + dateOfBirth +"\n"+ "Body Dying: "+ sex;
        TextView textView = new TextView(this);
        textView.setTextSize(20);
        textView.setText(message);

        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_display_message);
        layout.addView(textView);
    }
}
