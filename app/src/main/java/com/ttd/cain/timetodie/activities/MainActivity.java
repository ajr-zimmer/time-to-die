package com.ttd.cain.timetodie.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ttd.cain.timetodie.R;
import com.ttd.cain.timetodie.utils.HttpHandler;
import com.ttd.cain.timetodie.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    static final String TAG = MainActivity.class.getSimpleName();

    Intent initialUserInfoCapture;

    // URL to get country list
    private static ArrayList<String> countryList;
    public static ArrayList<String> getCountryList(){ return countryList;}
    public static void setCountryList(ArrayList<String> updatedCountryList){ countryList = updatedCountryList;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countryList = new ArrayList<String>();
    }

    /**
     * Async task class to get json by making HTTP call
     * Tutorial followed from: http://www.androidhive.info/2012/01/android-json-parsing-tutorial/
     */
    private class GetCountries extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String countryURL = "http://api.population.io:80/1.0/countries";
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(countryURL);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("countries");
                    ArrayList<String> countryList = getCountryList();
                    // Add default "hint" at the top of the spinner
                    countryList.add("Country of Residence, Please");

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        // adding country to country list
                        if(!contacts.getString(i).equals("World")){ // don't want to add option for entire planet
                            countryList.add(contacts.getString(i));
                        }
                    }
                    setCountryList(countryList);
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Start the tabbed CaptureUserInfoActivity that captures user input
            // Starting activity here so that the spinner is populated in time
            startActivity(initialUserInfoCapture);
            finish(); // finish this initial screen
        }

    }

    public void beginInfo(View view){
        if(Utils.isConnectedToNetwork(this)){
            initialUserInfoCapture = new Intent(this, CaptureUserInfoActivity.class);
            new GetCountries().execute();
        } else {
            // Notify the user that they need to have a connection
            Toast.makeText(this, "Network unavailable, please connect to a network", Toast.LENGTH_LONG).show();
        }
    }
}
