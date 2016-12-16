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

public class ObtainCountryActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.ttd.cain.timetodie.MESSAGE";

    EditText residenceCountry;
    TextView showCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obtain_country);

        residenceCountry = (EditText) findViewById(R.id.residence_country);
        showCountry = (TextView) findViewById(R.id.country_show);
        showCountry = (TextView) findViewById(R.id.country_show);

    }

    /** Saves information in the text field on the current activity */
    public void saveCountry(View view){
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("usercountry", residenceCountry.getText().toString());
        editor.apply();

        Toast.makeText(this, "Thanks for that ;)", Toast.LENGTH_LONG).show();
    }

    /** Displays information in the text field on the current activity */
    public void showCountry(View view){
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        String age = sharedPref.getString("usercountry", "");
        showCountry.setText(age);
    }

    /** Sends information in text field to the next activity for display */
    public void submitCountry(View view){
        Intent intent = new Intent(this, DisplayUserInfoActivity.class);
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String age = sharedPref.getString("userage", "");
        //EditText countryText = (EditText) findViewById(R.id.residence_country);
        //String message = age + "\n" + countryText.getText().toString();
        String country = sharedPref.getString("usercountry", "");
        String info = "You are "+age+" years old.\nYou are from "+country+"\nThank you very much ;)";
        intent.putExtra(EXTRA_MESSAGE, info);
        startActivity(intent);
    }
}
