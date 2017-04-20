package com.stardust.enhancedfloaty.gesture;

import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.stardust.enhancedfloaty.WindowBridge;

/**
 * Created by Stardust on 2017/4/18.
 */

public class DragGesture extends GestureDetector.SimpleOnGestureListener {

    public static DragGesture enableDrag(final View view, WindowBridge bridge) {
        final DragGesture gestureListener = new DragGesture(bridge, view) {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                view.setAlpha(1.0f);
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

        };
        final GestureDetectorCompat gestureDetector = new GestureDetectorCompat(view.getContext(), gestureListener);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    view.setAlpha(0.5f);
                    if (!gestureListener.mFlung && gestureListener.isKeepToSide()) {
                        gestureListener.keepToSide();
                    }
                }
                return true;
            }
        });
        return gestureListener;
    }

    private WindowBridge mWindowBridge;
    private boolean mKeepToSide;
    private View.OnClickListener mOnClickListener;
    private View mView;

    private int mInitialX;
    private int mInitialY;
    private float mInitialTouchX;
    private float mInitialTouchY;
    private float mKeepToSideHiddenWidthRadio = 0.5f;

    private boolean mFlung = false;

    public DragGesture(WindowBridge windowBridge, View view) {
        mWindowBridge = windowBridge;
        mView = view;
    }

    public void setKeepToSide(boolean keepToSide) {
        mKeepToSide = keepToSide;
    }

    public boolean isKeepToSide() {
        return mKeepToSide;
    }

    @Override
    public boolean onDown(MotionEvent event) {
        mInitialX = mWindowBridge.getX();
        mInitialY = mWindowBridge.getY();
        mInitialTouchX = event.getRawX();
        mInitialTouchY = event.getRawY();
        mFlung = false;
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        mWindowBridge.updatePosition(mInitialX + (int) ((e2.getRawX() - mInitialTouchX)),
                mInitialY + (int) ((e2.getRawY() - mInitialTouchY)));
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        mFlung = true;
        if (mKeepToSide)
            keepToSide();
        return false;
    }

    public void keepToSide() {
        int newX = mWindowBridge.getX();
        int hiddenWidth = (int) (mKeepToSideHiddenWidthRadio * mView.getWidth());
        if (newX > mWindowBridge.getScreenWidth() / 2)
            mWindowBridge.updatePosition(mWindowBridge.getScreenWidth() + mView.getWidth() - hiddenWidth, mWindowBridge.getY());
        else
            mWindowBridge.updatePosition(-hiddenWidth, mWindowBridge.getY());
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (mOnClickListener != null)
            mOnClickListener.onClick(mView);
        return super.onSingleTapConfirmed(e);
    }

    public void setOnDraggedViewClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }
}
