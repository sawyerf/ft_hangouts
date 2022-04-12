package com.example.ft_hangouts.app;

import android.app.Activity;
import android.app.Application;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.sql.Timestamp;

public class App extends android.app.Application implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "DESBARRES";
    private static final String SHARED_PREF_NAME = "ft_hangouts";
    private static final String SHARED_PREF_COLOR = "COLOR_TOOLBAR";

    private static Activity mActivity;
    private Integer layerActivity = -1;
    private Timestamp timestamp = null;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        mActivity = activity;
        layerActivity++;
        if (timestamp != null) {
            Toast.makeText(activity, "" + timestamp, Toast.LENGTH_SHORT).show();
            timestamp = null;
        }
        // Log.d(TAG, "onActivityResumed:" + layerActivity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        layerActivity--;
        if (layerActivity == 0) {
            timestamp = new Timestamp(System.currentTimeMillis());
        }
        // Log.d(TAG, "onActivityStopped: " + layerActivity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        mActivity = null;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) { }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) { }

    @Override
    public void onActivityDestroyed(Activity activity) { }
}
