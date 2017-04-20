package com.stardust.enhancedfloaty.sample;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.stardust.enhancedfloaty.R;
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
        ResizableFloatyService.startService(ctx, new ResizableFloatyService.ViewSupplier() {
            @Override
            public View inflateCollapsedView(Context context) {
                return View.inflate(context, R.layout.floating_window_collapsed, null);
            }

            @Override
            public View inflateExpandedView(Context context) {
                return View.inflate(context, R.layout.floating_window_expanded, null);
            }

            @Override
            public View getResizerView(View expandedView) {
                return expandedView.findViewById(R.id.resizer);
            }
        });
    }
}
