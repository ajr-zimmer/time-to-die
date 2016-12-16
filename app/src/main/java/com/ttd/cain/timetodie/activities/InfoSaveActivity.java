package com.ttd.cain.timetodie.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ttd.cain.timetodie.R;

public class InfoSaveActivity extends AppCompatActivity {
    //public final static String EXTRA_MESSAGE = "com.ttd.cain.timetodie.MESSAGE";

    EditText yearsAlive;
    TextView yearsShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_save);

        yearsAlive = (EditText) findViewById(R.id.years_alive);
        yearsShown = (TextView) findViewById(R.id.years_show);

    }

    /** Saves information in the text field on the current activity */
    public void saveYears(View view){
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("userage", yearsAlive.getText().toString());
        editor.apply();

        Toast.makeText(this, "Thanks for that ;)", Toast.LENGTH_LONG).show();
    }

    /** Displays information in the text field on the current activity */
    public void showYears(View view){
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        String age = sharedPref.getString("userage", "");
        yearsShown.setText(age);
    }

    /** Sends information in text field to the next activity for display */
    /*public void submitYears(View view){
        Intent intent = new Intent(this, DisplayUserInfoActivity.class);
        EditText editText = (EditText) findViewById(R.id.years_alive);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
    startActivity(intent);
    }*/
    /** Saves and moves on to country info activity */
    public void submitYears(View view){
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("userage", yearsAlive.getText().toString());
        editor.apply();

        Toast.makeText(this, "Submitting and continuing", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, ObtainCountryActivity.class);
        startActivity(intent);
    }


    // fun little method for toasts
    public void onButtonTap(View v){
        EditText editText = (EditText) findViewById(R.id.years_alive);
        String message = editText.getText().toString();
        Toast myToast = Toast.makeText(
                getApplicationContext(),
                //"Ouch!",
                message,
                Toast.LENGTH_LONG);
        myToast.show();
    }
}
