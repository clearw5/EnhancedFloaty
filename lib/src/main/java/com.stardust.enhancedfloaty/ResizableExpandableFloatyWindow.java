package com.stardust.enhancedfloaty;

import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.stardust.enhancedfloaty.gesture.DragGesture;
import com.stardust.enhancedfloaty.gesture.ResizeGesture;
import com.stardust.enhancedfloaty.util.WindowTypeCompat;
import com.stardust.lib.R;
import com.stardust.widget.ViewSwitcher;

/**
 * Created by Stardust on 2017/4/18.
 */

public class ResizableExpandableFloatyWindow extends FloatyWindow {

    private static final int INITIAL_WINDOW_PARAM_FLAG = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
    private static final String TAG = "ExpandableFloatyService";


    private ResizableExpandableFloaty mFloaty;
    private ViewSwitcher mCollapseExpandViewSwitcher;
    private View mCollapsedView;
    private View mExpandedView;
    private View mResizer;
    private View mMoveCursor;
    private DragGesture mDragGesture;
    private int mCollapsedViewX, mCollapsedViewY;
    private int mExpandedViewX, mExpandedViewY;

    private ViewStack mViewStack = new ViewStack(new ViewStack.CurrentViewSetter() {
        @Override
        public void setCurrentView(View v) {
            mCollapseExpandViewSwitcher.setSecondView(v);
        }
    });


    public ResizableExpandableFloatyWindow(ResizableExpandableFloaty floaty) {
        if (floaty == null) {
            throw new NullPointerException("floaty == null");
        }
        mFloaty = floaty;
    }


    @Override
    protected View onCreateView(FloatyService service) {
        inflateWindowViews(service);
        View windowView = View.inflate(service, R.layout.ef_expandable_floaty_container, null);
        windowView.setFocusableInTouchMode(true);
        mCollapseExpandViewSwitcher = windowView.findViewById(R.id.container);
        mCollapseExpandViewSwitcher.setMeasureAllChildren(false);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mCollapseExpandViewSwitcher.addView(mCollapsedView, params);
        mCollapseExpandViewSwitcher.addView(mExpandedView, params);
        mViewStack.setRootView(mExpandedView);
        return windowView;
    }

    @Override
    protected void onAttachToWindow(View view, WindowManager manager) {
        super.onAttachToWindow(view, manager);
        initGesture();
        setKeyListener();
        setInitialState();
    }

    private void setInitialState() {
        boolean expand = mFloaty.isInitialExpanded();
        if (expand) {
            mExpandedViewX = mFloaty.getInitialX();
            mExpandedViewY = mFloaty.getInitialY();
            expand();
        } else {
            mCollapsedViewX = mFloaty.getInitialX();
            mCollapsedViewY = mFloaty.getInitialY();
            getWindowBridge().updatePosition(mCollapsedViewX, mCollapsedViewY);
        }
    }

    @Override
    protected WindowBridge onCreateWindowBridge(WindowManager.LayoutParams params) {
        return new WindowBridge.DefaultImpl(params, getWindowManager(), getWindowView()) {
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
    }

    protected void inflateWindowViews(FloatyService service) {
        mExpandedView = mFloaty.inflateExpandedView(service, this);
        mCollapsedView = mFloaty.inflateCollapsedView(service, this);
        mResizer = mFloaty.getResizerView(mExpandedView);
        mMoveCursor = mFloaty.getMoveCursorView(mExpandedView);
    }

    protected WindowManager.LayoutParams onCreateWindowLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowTypeCompat.getPhoneWindowType(),
                INITIAL_WINDOW_PARAM_FLAG,
                PixelFormat.TRANSLUCENT);
        layoutParams.gravity = Gravity.TOP | Gravity.START;
        return layoutParams;
    }

    private void initGesture() {
        if (mResizer != null) {
           ResizeGesture.enableResize(mResizer, mExpandedView, getWindowBridge());
        }
        if (mMoveCursor != null) {
            DragGesture gesture = new DragGesture(getWindowBridge(), mMoveCursor);
            gesture.setPressedAlpha(1.0f);
        }
        mDragGesture = new DragGesture(getWindowBridge(), mCollapsedView);
        mDragGesture.setUnpressedAlpha(mFloaty.getCollapsedViewUnpressedAlpha());
        mDragGesture.setPressedAlpha(mFloaty.getCollapsedViewPressedAlpha());
        mDragGesture.setKeepToSide(true);
        mDragGesture.setKeepToSideHiddenWidthRadio(mFloaty.getCollapsedHiddenWidthRadio());
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
        if (mFloaty.shouldRequestFocusWhenExpand()) {
            requestWindowFocus();
        }
        mDragGesture.setKeepToSide(false);
        getWindowBridge().updatePosition(mExpandedViewX, mExpandedViewY);
    }

    public void collapse() {
        mCollapseExpandViewSwitcher.showFirst();
        disableWindowFocus();
        setWindowLayoutNoLimit();
        mDragGesture.setKeepToSide(true);
        getWindowBridge().updatePosition(mCollapsedViewX, mCollapsedViewY);
    }

    private void setKeyListener() {
        getWindowView().setOnKeyListener(new View.OnKeyListener() {
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


    public void disableWindowFocus() {
        WindowManager.LayoutParams windowLayoutParams = getWindowLayoutParams();
        windowLayoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        updateWindowLayoutParams(windowLayoutParams);
    }

    public void setWindowLayoutInScreen() {
        WindowManager.LayoutParams windowLayoutParams = getWindowLayoutParams();
        windowLayoutParams.flags|= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        updateWindowLayoutParams(windowLayoutParams);
    }

    public void requestWindowFocus() {
        WindowManager.LayoutParams windowLayoutParams = getWindowLayoutParams();
        windowLayoutParams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        updateWindowLayoutParams(windowLayoutParams);
        getWindowView().requestFocus();
    }

    public void setWindowLayoutNoLimit() {
        WindowManager.LayoutParams windowLayoutParams = getWindowLayoutParams();
        windowLayoutParams.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        updateWindowLayoutParams(windowLayoutParams);
    }

}

