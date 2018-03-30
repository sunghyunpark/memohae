package com.yssh.memohae;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by SungHyun on 2018-03-30.
 */

public class SettingManager {
    // LogCat tag
    private static String TAG = SettingManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    SharedPreferences.Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "memohae";
    private static final String KEY_BACKGROUND_COLOR = "background_color";


    public SettingManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setBackgroundColor(int colorId) {
        editor.putInt(KEY_BACKGROUND_COLOR, colorId);

        // commit changes
        editor.commit();

    }

    public int getBackgroundColor(){
        return pref.getInt(KEY_BACKGROUND_COLOR, R.color.background_color_default);
    }
}
