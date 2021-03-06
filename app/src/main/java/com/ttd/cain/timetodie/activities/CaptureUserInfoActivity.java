package com.ttd.cain.timetodie.activities;

import android.animation.ArgbEvaluator;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ttd.cain.timetodie.R;
import com.ttd.cain.timetodie.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.view.Gravity.CENTER;

public class CaptureUserInfoActivity extends AppCompatActivity {

    static final String TAG = CaptureUserInfoActivity.class.getSimpleName();

    public static final String PREF_USER_COUNTRY = "user_country";
    private static String userCountry = "";
    private static int countryIndex = 0;

    public static final String PREF_USER_DOB = "user_dob";
    private static String userDOB = "";
    // Do not place Android context classes in static fields
    private static Button dobButton; // not the best way, could cause a memory leak

    public static final String PREF_USER_SEX = "user_sex";
    private static String userSex = "";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    ImageButton mBackBtn, mNextBtn;

    ImageView zero, one, two, three;
    ImageView[] indicators;

    CoordinatorLayout mCoordinator;

    int page = 0; // to track page position


    public static String getUserCountry(){ return userCountry; }
    public static void setUserCountry(String country){ userCountry = country; }
    public static int getCountryIndex(){ return countryIndex; }
    public static void setCountryIndex(int index){ countryIndex = index; }

    public static String getUserDOB(){ return userDOB; }
    public static void setUserDOB(String dob){ userDOB = dob;}
    public static Button getDobButton(){ return dobButton; }
    public static void setDobButton(Button btn){ dobButton = btn;}

    public static String getUserSex(){ return userSex; }
    public static void setUserSex(String sex){ userSex = sex; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_user_info);
        // Explicitly set so that users with API version < 21 can see the vector images
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mBackBtn = (ImageButton) findViewById(R.id.tutorial_btn_back);
        mNextBtn = (ImageButton) findViewById(R.id.tutorial_btn_next);

        zero = (ImageView) findViewById(R.id.tutorial_indicator_0);
        one = (ImageView) findViewById(R.id.tutorial_indicator_1);
        two = (ImageView) findViewById(R.id.tutorial_indicator_2);
        three = (ImageView) findViewById(R.id.tutorial_indicator_3);

        mCoordinator = (CoordinatorLayout) findViewById(R.id.main_content);

        indicators = new ImageView[]{zero, one, two, three};

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setCurrentItem(page);
        updateIndicators(page);

        final int colour1 = ContextCompat.getColor(this, R.color.hot_pink);
        final int colour2 = ContextCompat.getColor(this, R.color.purple);
        final int colour3 = ContextCompat.getColor(this, R.color.deep_purple);
        final int colour4 = ContextCompat.getColor(this, R.color.black);

        final int[] colourList = new int[]{colour1, colour2, colour3, colour4};

        final ArgbEvaluator evaluator = new ArgbEvaluator();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){
                // Update background colour of each section
                int colourUpdate = (Integer) evaluator.evaluate(positionOffset, colourList[position],
                        colourList[position == 3 ? position : position + 1]);
                mViewPager.setBackgroundColor(colourUpdate);
            }

            @Override
            public void onPageSelected(int position){
                page = position;
                updateIndicators(page);
                switch(position){
                    case 0:
                        mViewPager.setBackgroundColor(colour1);
                        break;
                    case 1:
                        mViewPager.setBackgroundColor(colour2);
                        break;
                    case 2:
                        mViewPager.setBackgroundColor(colour3);
                        break;
                    case 3:
                        mViewPager.setBackgroundColor(colour4);
                        break;
                }
                // Show back chevron on any page but the first
                // This is because I have explicitly set the back btn to be invisible on startup
                mBackBtn.setVisibility(position != 0 ? View.VISIBLE : View.GONE);
                // Hide next chevron on last section
                mNextBtn.setVisibility(position == 3 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(int state){

            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                page--;
                mViewPager.setCurrentItem(page, true);
            }
        });

        mNextBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                page++;
                mViewPager.setCurrentItem(page, true);
            }
        });

        clearPreviousUserPrefs();
    }

    // Matches the grey circles to the page that the user is currently on
    void updateIndicators(int position){
        for(int i=0; i<indicators.length; i++){
            indicators[i].setBackgroundResource(i == position ?
                    R.drawable.indicator_selected : R.drawable.indicator_unselected);
        }
    }

    void clearPreviousUserPrefs(){
        Utils.saveSharedSetting(this, CaptureUserInfoActivity.PREF_USER_COUNTRY, "");
        Utils.saveSharedSetting(this, CaptureUserInfoActivity.PREF_USER_DOB, "");
        Utils.saveSharedSetting(this, CaptureUserInfoActivity.PREF_USER_SEX, "");
        setUserCountry("");
        setCountryIndex(0);
        setUserDOB("");
        setUserSex("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /**
         * Handle action bar item clicks here. The action bar will
         * automatically handle clicks on the Home/Up button, so long
         * as you specify a parent activity in AndroidManifest.xml
         */
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        // Images in each section
        ImageView sectionImg;
        int[] bgs = new int[]{R.drawable.ic_globe_black_512dp, R.drawable.ic_hourglass_black_1000dp,
                R.drawable.ic_people_black_512dp, R.drawable.ic_skull_multi_72dp};

        LinearLayout replaceableInput;

        public PlaceholderFragment() { }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_capture_user_info, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

            // Append section titles
            String[] titles = getResources().getStringArray(R.array.tuttitle_array);
            textView.append(titles[getArguments().getInt(ARG_SECTION_NUMBER)-1]);

            // Place images into sections
            sectionImg = (ImageView) rootView.findViewById(R.id.section_img);
            sectionImg.setBackgroundResource(bgs[getArguments().getInt(ARG_SECTION_NUMBER)-1]);

            // Get text to be placed into text bodies
            TextView txtHowTo;
            String[] textBodies = getResources().getStringArray(R.array.tutbody_array);

            // Changes instructions/steps based on section
            txtHowTo = (TextView) rootView.findViewById(R.id.section_body);
            txtHowTo.setText(textBodies[getArguments().getInt(ARG_SECTION_NUMBER)-1]);

            // Creates different input methods based on section
            replaceableInput = (LinearLayout) rootView.findViewById(R.id.replaceable);
            generateSectionInputMethods(getArguments().getInt(ARG_SECTION_NUMBER));

            return rootView;
        }

        public void generateSectionInputMethods(int sectionNumber){
            /** User has swiped to the Country section*/
            if(sectionNumber == 1){
                replaceableInput.addView(generateCountryInput());

                /** User has swiped to the Date of Birth section*/
            } else if(sectionNumber == 2){
                replaceableInput.addView(generateDateOfBirthInput());

                /** User has swiped to the Sex section*/
            } else if(sectionNumber == 3){
                replaceableInput.addView(generateSexInput());

                /** User has swiped to the final "Motivate!" section*/
            } else {
                replaceableInput.addView(generateMotivateButton());
            }
        }

        public Spinner generateCountryInput(){
            Spinner countryInput = new Spinner(getActivity());
            // Create an ArrayAdapter using the ArrayList of countries and a default spinner layout
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, MainActivity.getCountries());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            countryInput.setAdapter(adapter);
            countryInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position != 0){ // Anything that is not the spinner "hint"
                        CaptureUserInfoActivity.setUserCountry(parent.getItemAtPosition(position).toString());
                        setCountryIndex(position);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    /**
                     * Callback method to be invoked when the selection disappears from this view.
                     * The selection can disappear for instance when touch is activated or when the adapter becomes empty.
                     */
                }
            });
            // Explicitly set the selection to show what country has been selected, even when the user swipes away
            countryInput.setSelection(getCountryIndex());
            return countryInput;
        }

        public Button generateDateOfBirthInput(){
            Button dateOfBirthButton = new Button(getActivity());
            if(getUserDOB().isEmpty()){
                // Show default prompt if nothing has been chosen
                dateOfBirthButton.setText(R.string.dob_btn_text);
            } else {
                // Set text of button to the date selected by the user
                dateOfBirthButton.setText(getResources().getString(R.string.dob_btn_display, getUserDOB()));
            }
            dateOfBirthButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create new fragment to display the date picker
                    DialogFragment newFragment = new DateOfBirthFragment();
                    newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
                }
            });
            // Set here so that the onDateSet method can change the text of the button
            CaptureUserInfoActivity.setDobButton(dateOfBirthButton);
            return dateOfBirthButton;
        }

        public RadioGroup generateSexInput(){
            final RadioButton[] rb = new RadioButton[2];
            String[] sexes = getResources().getStringArray(R.array.sexes);
            RadioGroup rg = new RadioGroup(getActivity());
            rg.setOrientation(RadioGroup.HORIZONTAL);
            rg.setGravity(CENTER);
            for(int i=0; i<2; i++){
                rb[i] = new RadioButton(getActivity());
                rb[i].setText(sexes[i]);
                rb[i].setId(i + 100); // need to be careful to avoid id collisions
                rg.addView(rb[i]);
            }
            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    for(int i=0; i<group.getChildCount(); i++){
                        RadioButton btn = (RadioButton) group.getChildAt(i);
                        if(btn.getId() == checkedId){
                            CaptureUserInfoActivity.setUserSex(btn.getText().toString().toLowerCase());
                            return;
                        }
                    }
                }
            });
            return rg;
        }

        public Button generateMotivateButton(){
            Button motivateBtn = new Button(getActivity());
            motivateBtn.setText(R.string.motivate_btn_text);
            motivateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "Country = " + CaptureUserInfoActivity.getUserCountry());
                    Log.i(TAG, "DOB = " + CaptureUserInfoActivity.getUserDOB());
                    Log.i(TAG, "Sex = " + CaptureUserInfoActivity.getUserSex());
                    // Check if information has been entered for the previous sections
                    if(CaptureUserInfoActivity.getUserCountry().isEmpty()){
                        Toast.makeText(getContext(), "Please input your country of residence", Toast.LENGTH_SHORT).show();
                    } else if (CaptureUserInfoActivity.getUserDOB().isEmpty()){
                        Toast.makeText(getContext(), "Please input your date of birth", Toast.LENGTH_SHORT).show();
                    } else if(CaptureUserInfoActivity.getUserSex().isEmpty()){
                        Toast.makeText(getContext(), "Please input your sex", Toast.LENGTH_SHORT).show();
                    } else {
                        Utils.saveSharedSetting(getActivity(), CaptureUserInfoActivity.PREF_USER_COUNTRY, CaptureUserInfoActivity.getUserCountry());
                        Utils.saveSharedSetting(getActivity(), CaptureUserInfoActivity.PREF_USER_DOB, CaptureUserInfoActivity.getUserDOB());
                        Utils.saveSharedSetting(getActivity(), CaptureUserInfoActivity.PREF_USER_SEX, CaptureUserInfoActivity.getUserSex());

                        // Make sure the user still has a network connection for the next REST call
                        if(Utils.isUserConnectedToNetwork(getActivity())){
                            Intent displayUserInfo = new Intent(getActivity(), DisplayUserInfoActivity.class);
                            startActivity(displayUserInfo);
                        } else {
                            Toast.makeText(getActivity(), "Network unavailable, please connect to a network", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
            return motivateBtn;
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) { super(fm); }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
                case 3:
                    return "SECTION 4";
            }
            return null;
        }
    }

    public static class DateOfBirthFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        DatePickerDialog dialog;
        Calendar current;
        int year;
        int month;
        int day;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            // Use current date as default date
            current = Calendar.getInstance();
            setDateIfPreviouslySet();
            year = current.get(Calendar.YEAR);
            month = current.get(Calendar.MONTH);
            day = current.get(Calendar.DAY_OF_MONTH);
            dialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Dialog, this, year, month, day);
            restrictDateInput();
            return dialog;
        }

        public void setDateIfPreviouslySet(){
            if(!CaptureUserInfoActivity.getUserDOB().isEmpty()){
                String[] date = CaptureUserInfoActivity.getUserDOB().split("-");
                // the -1 is there to set the month back to the zero-indexed format
                current.set(Integer.parseInt(date[0]), Integer.parseInt(date[1])-1, Integer.parseInt(date[2]));
            }
        }

        public void restrictDateInput(){
            Calendar oldestDOB = Calendar.getInstance();
            oldestDOB.set(year-125,0,1); // rolling window of 125 years
            dialog.getDatePicker().setMinDate(oldestDOB.getTimeInMillis());
            dialog.getDatePicker().setMaxDate(new Date().getTime());
        }

        public void onDateSet(DatePicker view, int year, int month, int day){
            // Using date formatting to handle leading zeros and zero-indexed months
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar temp = Calendar.getInstance();
            temp.set(year,month,day);
            CaptureUserInfoActivity.setUserDOB(sdf.format(temp.getTime()));
            CaptureUserInfoActivity.getDobButton().setText(getResources().getString(R.string.dob_btn_display, CaptureUserInfoActivity.getUserDOB()));
        }
    }


}