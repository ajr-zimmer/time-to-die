package com.ttd.cain.timetodie.activities;

import android.content.Intent;
import android.icu.util.TimeUnit;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ttd.cain.timetodie.R;
import com.ttd.cain.timetodie.utils.HttpHandler;
import com.ttd.cain.timetodie.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DisplayUserInfoActivity extends AppCompatActivity {

    static final String TAG = DisplayUserInfoActivity.class.getSimpleName();
    private static String millisLeft;
    TextView countdownText;

    String country;
    String dateOfBirth;
    String sex;

    String today;
    String age;
    double yearsLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user_info);

        country = Utils.readSharedSetting(DisplayUserInfoActivity.this, CaptureUserInfoActivity.PREF_USER_COUNTRY, "The Void");
        dateOfBirth = Utils.readSharedSetting(DisplayUserInfoActivity.this, CaptureUserInfoActivity.PREF_USER_DOB, "Never Born");
        sex = Utils.readSharedSetting(DisplayUserInfoActivity.this, CaptureUserInfoActivity.PREF_USER_SEX, "No Sex");

        String stats = "Stats:\nPlace of Living = "+ country +"\n"+ "Start of Life = " + dateOfBirth +"\n"+ "Body Living = "+ sex;
        TextView userstats = (TextView) findViewById(R.id.userstats);
        userstats.setText(stats);

        // Modify country to be used as url parameter
        country = country.replaceAll(" ", "%20");

        age = calculateAge(dateOfBirth);
        System.out.println(age);
        new GetTimeLeft().execute(); // TODO: check if user still has network connectivity
    }

    // Inspired by http://howtodoinjava.com/for-fun-only/java-code-to-calculate-age-from-date-of-birth/
    private String calculateAge(String dobString){
        int years = 0;
        int months = 0;
        int days = 0;

        Calendar dob = Calendar.getInstance();
        String[] dates = dobString.split("-");
        dob.set(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]), Integer.parseInt(dates[2]));
        Calendar now = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        today = sdf.format(now.getTime());

        years = now.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        int currMonth = now.get(Calendar.MONTH)+1;
        int dobMonth = dob.get(Calendar.MONTH)+1;
        months = currMonth - dobMonth;
        // if difference is negative then reduce years by one and calculate number of months
        if(months < 0){
            years--;
            months = 12 - dobMonth + currMonth;
            if(now.get(Calendar.DATE) < dob.get(Calendar.DATE)){ months--;}
        } else if(months == 0 && now.get(Calendar.DATE) < dob.get(Calendar.DATE)){
            years--;
            months = 11;
        }
        // Calculate the days
        // TODO: this needs commenting
        if(now.get(Calendar.DATE) > dob.get(Calendar.DATE)){
            days = now.get(Calendar.DATE) - dob.get(Calendar.DATE);
        } else if(now.get(Calendar.DATE) < dob.get(Calendar.DATE)){
            int today = now.get(Calendar.DAY_OF_MONTH);
            now.add(Calendar.MONTH, -1);
            days = now.getActualMaximum(Calendar.DAY_OF_MONTH) - dob.get(Calendar.DAY_OF_MONTH) + today;
        } else {
            days = 0;
            if(months == 12){
                years++;
                months = 0;
            }
        }

        return String.format("%02dy%02dm%02dd", years, months, days);
    }

    /**
     * Async task class to get json by making HTTP call
     * Tutorial followed from: http://www.androidhive.info/2012/01/android-json-parsing-tutorial/
     */
    private class GetTimeLeft extends AsyncTask<Void, Void, Void> {

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
            String timeLeftURL = String.format("http://api.population.io:80/1.0/life-expectancy/remaining/%s/%s/%s/%s/",
                    sex, country, today, age);
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(timeLeftURL);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    yearsLeft = jsonObj.getDouble("remaining_life_expectancy");
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
            // In the Gregorian calendar, a year has on average 365.2425 days.
            System.out.println("Years left: "+ yearsLeft);
            long millisLeftToLive = (long)(yearsLeft * (365.2425 * 24 * 60 * 60 * 1000));
            countdownText = (TextView) findViewById(R.id.countdowntimer);
            countdownText.setText("00:00:00");
            // start time 3 minutes, decrement by 1 second
            final CounterClass timer = new CounterClass(millisLeftToLive, 1000);
            timer.start();
        }

    }

    public class CounterClass extends CountDownTimer {

        public CounterClass(long millisInFuture, long countDownInterval){
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished){
            long millis = millisUntilFinished;

            String hms = String.format("%02d Days\n%02d Hours\n%02d Minutes\n%02d Seconds", java.util.concurrent.TimeUnit.MILLISECONDS.toDays(millis),
                    java.util.concurrent.TimeUnit.MILLISECONDS.toHours(millis) - java.util.concurrent.TimeUnit.DAYS.toHours(java.util.concurrent.TimeUnit.MILLISECONDS.toDays(millis)),
                    java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(millis) - java.util.concurrent.TimeUnit.HOURS.toMinutes(java.util.concurrent.TimeUnit.MILLISECONDS.toHours(millis)),
                    java.util.concurrent.TimeUnit.MILLISECONDS.toSeconds(millis) - java.util.concurrent.TimeUnit.MINUTES.toSeconds(java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(millis)));
            //System.out.println(hms);
            countdownText.setText(hms);
        }

        @Override
        public void onFinish(){
            countdownText.setText("Completed. You Should Be Dead.");
        }

    }
}
