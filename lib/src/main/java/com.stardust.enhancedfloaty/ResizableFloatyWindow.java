package com.stardust.enhancedfloaty;

import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.stardust.enhancedfloaty.gesture.DragGesture;
import com.stardust.enhancedfloaty.gesture.ResizeGesture;
import com.stardust.lib.R;

/**
 * Created by Stardust on 2017/4/30.
 */

public class ResizableFloatyWindow implements FloatyWindow {

    private static final String TAG = "ResizableFloatyWindow";

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowLayoutParams;
    private ViewGroup mWindowView;
    private View mView;
    private View mResizer;
    private View mMoveCursor;
    private WindowBridge mWindowBridge;
    private ResizableFloaty mFloaty;

    public ResizableFloatyWindow(ResizableFloaty floaty) {
        mFloaty = floaty;
    }

    @Override
    public void onCreate(FloatyService service, WindowManager manager) {
        mWindowManager = manager;
        mWindowLayoutParams = createWindowLayoutParams();
        if (mFloaty == null) {
            throw new IllegalStateException("Must start this service by static method ResizableExpandableFloatyWindow.startService");
        }
        initWindowView(service);
        mWindowBridge = new WindowBridge.DefaultImpl(mWindowLayoutParams, mWindowManager, mWindowView);
        initGesture();
    }

    private void initWindowView(FloatyService service) {
        mWindowView = (ViewGroup) View.inflate(service, R.layout.ef_floaty_container, null);
        mView = mFloaty.inflateView(service, this);
        mResizer = mFloaty.getResizerView(mView);
        mMoveCursor = mFloaty.getMoveCursorView(mView);
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
            DragGesture gesture = new DragGesture(mWindowBridge, mMoveCursor);
            gesture.setPressedAlpha(1.0f);
        }
    }


    @Override
    public void onServiceDestroy(FloatyService service) {
        close();
    }

    @Override
    public void close() {
        mWindowManager.removeView(mWindowView);
        FloatyService.removeWindow(this);
    }
}
