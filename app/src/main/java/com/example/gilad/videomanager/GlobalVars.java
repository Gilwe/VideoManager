package com.example.gilad.videomanager;

import android.app.Activity;

/**
 * Created by Gilad on 10/04/2017.
 */

public class GlobalVars {
    static private Activity curActivity;

    static FoundVideo foundVideo;

    public static Activity getCurActivity() {
        return curActivity;
    }

    public static void setCurActivity(Activity curActivity) {
        GlobalVars.curActivity = curActivity;
    }
}
