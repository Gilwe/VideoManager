package com.example.gilad.videomanager;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Gilad on 03/03/2017.
 */

public class DataStorage {
    public Activity activity;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private final String PREFS_NAME = "VideoManager";

    public static final String KEY_FILTER = "filter";
    public static final String KEY_ALL_FOLDERS = "all_folders";
    public static final String KEY_SELECTED_FOLDER = "selected_folder";

    public DataStorage(Context context) {
         sharedPreferences =
                context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);

        editor = sharedPreferences.edit();

        activity = (Activity) context;
    }

    void setFilter(String value)
    {
        editor.putString(KEY_FILTER,value);
        editor.commit();
    }

    void setAllFolders(boolean value)
    {
        editor.putBoolean(KEY_ALL_FOLDERS, value);
        editor.commit();
    }

    void setSelectedFolder(String value)
    {
        editor.putString(KEY_SELECTED_FOLDER, value);
        editor.commit();
    }

    String getString(String key)
    {
        int keyNum = activity.getResources().getIdentifier(key,"string",activity.getPackageName());
        String defaultValue = activity.getResources().getString(keyNum);

        return sharedPreferences.getString(key,defaultValue);
    }

    boolean getBool(String key)
    {
        int keyNum = activity.getResources().getIdentifier(key,"string",activity.getPackageName());
        Boolean defaultValue = Boolean.valueOf(activity.getResources().getString(keyNum));

        return sharedPreferences.getBoolean(key,defaultValue);
    }
}
