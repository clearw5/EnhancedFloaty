package com.stardust.enhancedfloaty;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.stardust.enhancedfloaty.gesture.DragGesture;
import com.stardust.enhancedfloaty.gesture.ResizeGesture;
import com.stardust.lib.R;
import com.stardust.widget.ViewSwitcher;

/**
 * Created by Stardust on 2017/4/18.
 */

public class ResizableExpandableFloatyService extends Service {

    private static final int INITIAL_WINDOW_PARAM_FLAG = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
    private static final String TAG = "ExpandableFloatyService";

    private static ResizableExpandableFloaty floaty;

    public static void startService(Context context, ResizableExpandableFloaty floaty) {
        ResizableExpandableFloatyService.floaty = floaty;
        context.startService(new Intent(context, ResizableExpandableFloatyService.class));
    }

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowLayoutParams;
    private View mWindowView;
    private ViewSwitcher mCollapseExpandViewSwitcher;
    private View mCollapsedView;
    private View mExpandedView;
    private View mResizer;
    private View mMoveCursor;
    private ResizeGesture mResizeGesture;
    private DragGesture mDragGesture;
    private int mCollapsedViewX, mCollapsedViewY;
    private int mExpandedViewX, mExpandedViewY;

    private ViewStack mViewStack = new ViewStack(new ViewStack.CurrentViewSetter() {
        @Override
        public void setCurrentView(View v) {
            mCollapseExpandViewSwitcher.setSecondView(v);
        }
    });

    private WindowBridge mWindowBridge;

    @Override
    public void onCreate() {
        super.onCreate();
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowLayoutParams = createWindowLayoutParams();
        if (floaty == null) {
            throw new IllegalStateException("Must start this service by static method ResizableExpandableFloatyService.startService");
        }
        mExpandedView = floaty.inflateExpandedView(this);
        mCollapsedView = floaty.inflateCollapsedView(this);
        mResizer = floaty.getResizerView(mExpandedView);
        mMoveCursor = floaty.getMoveCursorView(mExpandedView);
        initWindowView();
        mWindowBridge = new WindowBridge.DefaultImpl(mWindowLayoutParams, mWindowManager, mWindowView) {
            @Override
            public void updatePosition(int x, int y) {
                super.updatePosition(x, y);
                if (mCollapseExpandViewSwitcher.getCurrentView() == mExpandedView) {
                    mExpandedViewX = x;
                    mExpandedViewY = y;
                } else {
                    mCollapsedViewX = x;
                    mCollapsedViewY = y;
                }
            }

        };
        initGesture();
        setKeyListener();
    }

    private WindowManager.LayoutParams createWindowLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                INITIAL_WINDOW_PARAM_FLAG,
                PixelFormat.TRANSLUCENT);
        layoutParams.gravity = Gravity.TOP | Gravity.START;
        return layoutParams;
    }

    private void initWindowView() {
        mWindowView = View.inflate(getApplicationContext(), R.layout.ef_expandable_floaty_container, null);
        mWindowView.setFocusableInTouchMode(true);
        mCollapseExpandViewSwitcher = (ViewSwitcher) mWindowView.findViewById(R.id.container);
        mCollapseExpandViewSwitcher.setMeasureAllChildren(false);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mCollapseExpandViewSwitcher.addView(mCollapsedView, params);
        mCollapseExpandViewSwitcher.addView(mExpandedView, params);
        mViewStack.setRootView(mExpandedView);
        mWindowManager.addView(mWindowView, mWindowLayoutParams);
    }

    private void initGesture() {
        if (mResizer != null) {
            mResizeGesture = ResizeGesture.enableResize(mResizer, mExpandedView, mWindowBridge);
        }
        if (mMoveCursor != null) {
            DragGesture.enableDrag(mMoveCursor, mWindowBridge, 1.0f, 1.0f);
        }
        mDragGesture = DragGesture.enableDrag(mCollapsedView, mWindowBridge, floaty.getCollapsedViewPressedAlpha(), floaty.getCollapsedViewUnpressedAlpha());
        mDragGesture.setKeepToSide(true);
        mDragGesture.setKeepToSideHiddenWidthRadio(floaty.getCollapsedHiddenWidthRadio());
        mDragGesture.setOnDraggedViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expand();
            }
        });
    }

    public void expand() {
        mCollapseExpandViewSwitcher.showSecond();
        //enableWindowLimit();
        if(floaty.shouldRequestFocusWhenExpand()){
            enableWindowFocus();
        }
        mDragGesture.setKeepToSide(false);
        mWindowBridge.updatePosition(mExpandedViewX, mExpandedViewY);
    }

    public void collapse() {
        mCollapseExpandViewSwitcher.showFirst();
        disableWindowFocusAndWindowLimit();
        mDragGesture.setKeepToSide(true);
        mWindowBridge.updatePosition(mCollapsedViewX, mCollapsedViewY);
    }

    private void setKeyListener() {
        mWindowView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    onBackPressed();
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_HOME) {
                    onHomePressed();
                    return true;
                }
                return false;
            }


        });
    }

    private void onBackPressed() {
        if (mViewStack.canGoBack()) {
            mViewStack.goBack();
        } else {
            collapse();
        }
    }

    private void onHomePressed() {
        mViewStack.goBackToFirst();
        collapse();
    }

    private void disableWindowFocusAndWindowLimit() {
        mWindowLayoutParams.flags = INITIAL_WINDOW_PARAM_FLAG;
        mWindowManager.updateViewLayout(mWindowView, mWindowLayoutParams);
    }

    private void enableWindowFocus() {
        mWindowLayoutParams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowManager.updateViewLayout(mWindowView, mWindowLayoutParams);
        mWindowView.requestFocus();
    }


    private void enableWindowLimit() {
        mWindowLayoutParams.flags &= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        mWindowManager.updateViewLayout(mWindowView, mWindowLayoutParams);
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

