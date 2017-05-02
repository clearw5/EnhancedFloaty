package com.stardust.enhancedfloaty;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Stardust on 2017/5/1.
 */

public class FloatyService extends Service {


    public static CopyOnWriteArrayList<FloatyWindow> windows = new CopyOnWriteArrayList<>();

    public static void addWindow(FloatyWindow window) {
        windows.add(window);
        if (instance != null) {
            window.onCreate(instance, instance.mWindowManager);
        }
    }

    public static void removeWindow(FloatyWindow window) {
        windows.remove(window);
    }

    private static FloatyService instance;
    private WindowManager mWindowManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        for (FloatyWindow delegate : windows) {
            delegate.onCreate(this, mWindowManager);
        }
        instance = this;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
        for (FloatyWindow delegate : windows) {
            delegate.onServiceDestroy(this);
        }
    }
}
