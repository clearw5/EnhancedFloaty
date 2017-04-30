package com.stardust.enhancedfloaty.sample;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.stardust.enhancedfloaty.R;
import com.stardust.enhancedfloaty.ResizableExpandableFloatyService;
import com.stardust.enhancedfloaty.ResizableFloatyService;
import com.stardust.enhancedfloaty.util.FloatingWindowPermissionUtil;


/**
 * Created by Stardust on 2017/4/18.
 */

public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingWindowPermissionUtil.goToFloatingWindowPermissionSettingIfNeeded(this);
        findViewById(R.id.resizable_floaty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFloaty(MainActivity.this);
            }
        });
        findViewById(R.id.expandable_floaty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startExpandableFloaty(MainActivity.this);
            }
        });
    }

    private static void startExpandableFloaty(Context ctx) {
        ResizableExpandableFloatyService.startService(ctx, new SampleExpandableFloaty());
    }

    private static void startFloaty(Context ctx) {
        ResizableFloatyService.startService(ctx, new SampleFloaty());
    }
}
