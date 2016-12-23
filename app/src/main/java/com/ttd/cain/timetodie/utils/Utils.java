package com.ttd.cain.timetodie.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by cain on 26/11/16.
 */

public class Utils {

    private static final String PREFERENCES_FILE = "timetodie_settings";

    public static String readSharedSetting(Context ctx, String settingName, String defaultValue){
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(settingName, defaultValue);
    }

    public static void saveSharedSetting(Context ctx, String settingName, String settingValue){
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

}
