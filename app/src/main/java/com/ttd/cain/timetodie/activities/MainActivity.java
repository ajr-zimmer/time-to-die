package com.ttd.cain.timetodie.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
//import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ttd.cain.timetodie.R;
import com.ttd.cain.timetodie.utils.HttpHandler;
import com.ttd.cain.timetodie.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends AppCompatActivity {

    static final String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog progressDialog;

    Intent captureUserInfo;

    private static ArrayList<String> countries;
    public static ArrayList<String> getCountries(){ return countries;}
    public static void setCountries(ArrayList<String> updatedCountryList){ countries = updatedCountryList;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Explicitly set so that users with API version < 21 can see the vector images
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        countries = new ArrayList<String>();
    }

    /**
     * Async task class to get json by making HTTP call
     * Tutorial followed from: http://www.androidhive.info/2012/01/android-json-parsing-tutorial/
     */
    private class GetCountries extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Obtaining country information...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String countriesURL = "http://api.population.io:80/1.0/countries";
            // Making a request to url and getting response
            String jsonResponse = sh.makeServiceCall(countriesURL);

            //Log.i(TAG, "Response from url: " + jsonResponse);

            if (jsonResponse != null) {
                try {
                    JSONObject responseObject = new JSONObject(jsonResponse);

                    // Getting JSON Array node
                    JSONArray countriesFromAPI = responseObject.getJSONArray("countries");
                    ArrayList<String> countries = getCountries();

                    // looping through All Contacts
                    for (int i = 0; i < countriesFromAPI.length(); i++) {
                        // adding country to country list
                        if(!countriesFromAPI.getString(i).equals("World")){ // don't want to add option for entire planet
                            countries.add(countriesFromAPI.getString(i));
                        }
                    }
                    // Sort countries into alphabetical order
                    Collections.sort(countries);
                    // Add default "hint" at the top of the spinner
                    countries.add(0, "Country of Residence, Please");
                    setCountries(countries);
                } catch (final JSONException e) {
                    //Log.e(TAG, "Json parsing error: " + e.getMessage());
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
                //Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check the logs for possible errors!",
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
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            // Starting activity here so that the spinner is populated in time
            startActivity(captureUserInfo);
            finish();
        }

    }

    public void beginInfo(View view){
        if(Utils.isUserConnectedToNetwork(this)){
            captureUserInfo = new Intent(this, CaptureUserInfoActivity.class);
            new GetCountries().execute();
        } else {
            Toast.makeText(this, "Network unavailable, please connect to a network", Toast.LENGTH_LONG).show();
        }
    }
}
