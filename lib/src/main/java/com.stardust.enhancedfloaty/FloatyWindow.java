package com.stardust.enhancedfloaty;

import android.view.WindowManager;

/**
 * Created by Stardust on 2017/5/1.
 */

public interface FloatyWindow {

    void onCreate(FloatyService service, WindowManager manager);

    void onServiceDestroy(FloatyService service);

    void close();
}
