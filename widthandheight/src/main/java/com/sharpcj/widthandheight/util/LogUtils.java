package com.sharpcj.widthandheight.util;

import android.util.Log;

public class LogUtils {
    public static final boolean ON = true;


    public static void v(String tag, String message) {
        if (ON) {
            Log.v("AirHockey_" + tag, message);
        }
    }

    public static void d(String tag, String message) {
        if (ON) {
            Log.d("AirHockey_" + tag, message);
        }
    }

    public static void i(String tag, String message) {
        if (ON) {
            Log.i("AirHockey_" + tag, message);
        }
    }

    public static void w(String tag, String message) {
        Log.w("AirHockey_" + tag, message);
    }

    public static void e(String tag, String message) {
        Log.e("AirHockey_" + tag, message);
    }
}
