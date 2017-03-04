package com.example.gilad.videomanager;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Gilad on 03/03/2017.
 */

public class DataStorage {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private final String PREFS_NAME = "VideoManager";

    public static final String FILTER = "filter";
    public static final String ALL_FOLDERS = "all_folders";
    public static final String SELECTED_FOLDER = "selected_folder";

    public static final String DEFAULT_STRING = "DEFAULT";
    public static final boolean DEFAULT_BOOL = false;

    public DataStorage(Context context) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);

        editor = sharedPreferences.edit();
    }

    void write(String key, String value)
    {
        editor.putString(key,value);

        editor.commit();
    }

    void write(String key, boolean value)
    {
        editor.putBoolean(key, value);

        editor.commit();
    }

    String readString(String key)
    {
        return sharedPreferences.getString(key,DEFAULT_STRING);
    }

    boolean readBool(String key)
    {
        return sharedPreferences.getBoolean(key, DEFAULT_BOOL);
    }
}
