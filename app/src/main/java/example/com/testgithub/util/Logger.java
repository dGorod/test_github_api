package example.com.testgithub.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import example.com.testgithub.BuildConfig;

/**
 * Created by Dmitriy Gorodnytskiy on 29-Oct-15.
 */
public class Logger {
    private static final String TAG = "testgithub";

    public static boolean isDebuggable(Context context) {
        return  (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
    }

    @SuppressWarnings("unused")
    public static void v(Object... params) {
        if (BuildConfig.DEBUG)
            Log.v(TAG, buildString(params));
    }

    @SuppressWarnings("unused")
    public static void d(Object... params) {
        if (BuildConfig.DEBUG)
            Log.d(TAG, buildString(params));
    }

    @SuppressWarnings("unused")
    public static void e(Object... params) {
        if (BuildConfig.DEBUG)
            Log.e(TAG, buildString(params));
    }

    @SuppressWarnings("unused")
    public static void i(Object... params) {
        if (BuildConfig.DEBUG)
            Log.i(TAG, buildString(params));
    }

    @SuppressWarnings("unused")
    public static void w(Object... params) {
        if (BuildConfig.DEBUG)
            Log.w(TAG, buildString(params));
    }

    private static String buildString(Object[] params) {
        StringBuilder sb = new StringBuilder();
        for (Object param : params) {
            sb.append(param);
            sb.append(" ");
        }
        return sb.toString();
    }
}