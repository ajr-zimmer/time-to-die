package com.ttd.cain.timetodie.activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
//import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.ttd.cain.timetodie.R;
import com.ttd.cain.timetodie.utils.HttpHandler;
import com.ttd.cain.timetodie.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DisplayUserInfoActivity extends AppCompatActivity {

    static final String TAG = DisplayUserInfoActivity.class.getSimpleName();
    private ProgressDialog progressDialog;
    TextView countdownText;

    String country;
    String dateOfBirth;
    String sex;

    String age;
    String today;

    double yearsLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user_info);
        // Explicitly set so that users with API version < 21 can see the vector images
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        country = Utils.readSharedSetting(DisplayUserInfoActivity.this, CaptureUserInfoActivity.PREF_USER_COUNTRY, "The Void");
        dateOfBirth = Utils.readSharedSetting(DisplayUserInfoActivity.this, CaptureUserInfoActivity.PREF_USER_DOB, "Never Born");
        sex = Utils.readSharedSetting(DisplayUserInfoActivity.this, CaptureUserInfoActivity.PREF_USER_SEX, "No Sex");

        String stats = "Stats:\nPlace of Living = "+ country +"\n"+ "Start of Life = " + dateOfBirth +"\n"+ "Body Living = "+ sex;
        TextView userStats = (TextView) findViewById(R.id.userstats);
        userStats.setText(stats);

        // Modify country to be used as url parameter
        country = country.replaceAll(" ", "%20");

        age = calculateAge(dateOfBirth);
        new GetTimeLeft().execute();
    }

    // Inspired by http://howtodoinjava.com/for-fun-only/java-code-to-calculate-age-from-date-of-birth/
    private String calculateAge(String dateOfBirthString){
        int years = 0;
        int months = 0;
        int days = 0;

        Calendar dateOfBirth = Calendar.getInstance();
        String[] dates = dateOfBirthString.split("-");
        // add -1 to month because they are zero-indexed
        dateOfBirth.set(Integer.parseInt(dates[0]), Integer.parseInt(dates[1])-1, Integer.parseInt(dates[2]));
        Calendar now = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        today = sdf.format(now.getTime());

        years = now.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);
        // add +1 to month because they are zero-indexed
        int currentMonth = now.get(Calendar.MONTH)+1;
        int dateOfBirthMonth = dateOfBirth.get(Calendar.MONTH)+1;
        months = currentMonth - dateOfBirthMonth;

        int currentDay = now.get(Calendar.DATE);
        int dateOfBirthDay = dateOfBirth.get(Calendar.DATE);

        // dob month is later in the year than the current month (i.e. not a full year between)
        if(months < 0){
            years--;
            // number of months from dobMonth to currentMonth in the following year
            // e.g. Sept 1980 --> May 1981 = 8 months
            months = (12 - dateOfBirthMonth) + currentMonth;
            // not an exact number of months away, need to narrow down to number of days
            if(currentDay < dateOfBirthDay){ months--;}
        // same month, but not exactly 12 months worth of days apart
        } else if(months == 0 && currentDay < dateOfBirthDay){
            years--;
            months = 11;
        }
        // current day is farther along in the month than the dob
        if(currentDay > dateOfBirthDay){
            days = currentDay - dateOfBirthDay;
        // current day comes earlier in the month than the dob
        } else if(currentDay < dateOfBirthDay){
            //int today = now.get(Calendar.DAY_OF_MONTH);
            // there is one less month because the current day is less than an exact month away
            now.add(Calendar.MONTH, -1);
            days = (now.getActualMaximum(Calendar.DAY_OF_MONTH) - dateOfBirth.get(Calendar.DAY_OF_MONTH)) + now.get(Calendar.DAY_OF_MONTH);
        // same day
        } else {
            days = 0;
            // edge case where months has become 12
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
            progressDialog = new ProgressDialog(DisplayUserInfoActivity.this);
            progressDialog.setMessage("Calculating life left...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String timeLeftURL = String.format("http://api.population.io:80/1.0/life-expectancy/remaining/%s/%s/%s/%s/",
                    sex, country, today, age);
            // Making a request to url and getting response
            String jsonResponse = sh.makeServiceCall(timeLeftURL);

            //Log.i(TAG, "Response from url: " + jsonResponse);

            if (jsonResponse != null) {
                try {
                    JSONObject responseObject = new JSONObject(jsonResponse);
                    yearsLeft = responseObject.getDouble("remaining_life_expectancy");
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
            //Log.i(TAG, "Years left: "+ yearsLeft);

            // In the Gregorian calendar, a year has on average 365.2425 days.
            long millisLeftToLive = (long)(yearsLeft * (365.2425 * 24 * 60 * 60 * 1000));
            countdownText = (TextView) findViewById(R.id.countdowntimer);
            // set the start time and decrement by 1 second
            final CounterClass countDown = new CounterClass(millisLeftToLive, 1000);
            countDown.start();
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
            countdownText.setText(hms);
        }

        @Override
        public void onFinish(){ countdownText.setText("Completed. You Should Be Dead."); }
    }
}
