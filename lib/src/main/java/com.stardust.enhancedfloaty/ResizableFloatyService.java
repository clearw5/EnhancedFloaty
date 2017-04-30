package com.stardust.enhancedfloaty;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.stardust.enhancedfloaty.gesture.DragGesture;
import com.stardust.enhancedfloaty.gesture.ResizeGesture;
import com.stardust.lib.R;

/**
 * Created by Stardust on 2017/4/30.
 */

public class ResizableFloatyService extends Service {

    private static final String TAG = "ResizableFloatyService";
    private static ResizableFloaty floaty;

    public static void startService(Context ctx, ResizableFloaty floaty) {
        ResizableFloatyService.floaty = floaty;
        ctx.startService(new Intent(ctx, ResizableFloatyService.class));
    }


    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowLayoutParams;
    private FrameLayout mWindowView;
    private View mView;
    private View mResizer;
    private View mMoveCursor;
    private WindowBridge mWindowBridge;

    @Override
    public void onCreate() {
        super.onCreate();
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowLayoutParams = createWindowLayoutParams();
        if (floaty == null) {
            throw new IllegalStateException("Must start this service by static method ResizableExpandableFloatyService.startService");
        }
        initWindowView();
        mWindowBridge = new WindowBridge.DefaultImpl(mWindowLayoutParams, mWindowManager, mWindowView);
        initGesture();
    }

    private void initWindowView() {
        mWindowView = (FrameLayout) View.inflate(getApplicationContext(), R.layout.ef_floaty_container, null);
        mView = floaty.inflateView(this);
        mResizer = floaty.getResizerView(mView);
        mMoveCursor = floaty.getMoveCursorView(mView);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mWindowView.addView(mView, params);
        mWindowView.setFocusableInTouchMode(true);
        mWindowManager.addView(mWindowView, mWindowLayoutParams);
    }

    private WindowManager.LayoutParams createWindowLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        layoutParams.gravity = Gravity.TOP | Gravity.START;
        return layoutParams;
    }


    private void initGesture() {
        if (mResizer != null) {
            ResizeGesture.enableResize(mResizer, mView, mWindowBridge);
        }
        if (mMoveCursor != null) {
            DragGesture.enableDrag(mMoveCursor, mWindowBridge, 1.0f, 1.0f);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWindowManager.removeView(mWindowView);
        Log.v(TAG, "onDestroy");
    }
}
