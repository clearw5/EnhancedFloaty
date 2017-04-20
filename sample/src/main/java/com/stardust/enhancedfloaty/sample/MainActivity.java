package com.stardust.enhancedfloaty.sample;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.stardust.enhancedfloaty.ResizableFloatyService;


/**
 * Created by Stardust on 2017/4/18.
 */

public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(this);
        finish();
    }

    private static void startService(Context ctx) {
        ResizableFloatyService.startService(ctx, new SampleFloaty());
    }
}
