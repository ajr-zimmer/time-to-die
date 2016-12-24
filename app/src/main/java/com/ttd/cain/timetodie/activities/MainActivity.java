package com.ttd.cain.timetodie.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ttd.cain.timetodie.R; // I suppose this is necessary
import com.ttd.cain.timetodie.utils.HttpHandler;
import com.ttd.cain.timetodie.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/** TODO: Although this is a general landing page,
 * the goal is to be able to sign in with Google account to manage login **/
public class MainActivity extends AppCompatActivity {

    static final String TAG = MainActivity.class.getSimpleName();
    // URL to get country list
    private static ArrayList<String> countryList;
    //private ProgressDialog pDialog;

    // TODO: Skip capturing user info if we have already collected it
    public static final String PREF_USER_FIRST_TIME = "user_first_time";
    boolean isUserFirstTime;

    public static ArrayList<String> getCountryList(){ return countryList;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countryList = new ArrayList<String>();

        // TODO: encourage users to be connected to internet in order for counter to work properly
        new GetCountries().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     * Tutorial followed from: http://www.androidhive.info/2012/01/android-json-parsing-tutorial/
     */
    private class GetCountries extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            /*pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();*/

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

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        // adding country to country list
                        countryList.add(contacts.getString(i));
                    }
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
            // Dismiss the progress dialog
            //if (pDialog.isShowing())
            //    pDialog.dismiss();
            /**
             * Updating parsed JSON data into list
             * */

        }

    }

    public void beginInfo(View view){
        // TODO: stop user from continuing if there is not internet connection
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
