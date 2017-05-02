package com.stardust.enhancedfloaty;

import android.graphics.PixelFormat;
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

public class ResizableExpandableFloatyWindow implements FloatyWindow {

    private static final int INITIAL_WINDOW_PARAM_FLAG = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
    private static final String TAG = "ExpandableFloatyService";


    private ResizableExpandableFloaty mFloaty;
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

    public ResizableExpandableFloatyWindow(ResizableExpandableFloaty floaty) {
        mFloaty = floaty;
    }

    @Override
    public void onCreate(FloatyService service, WindowManager manager) {
        mWindowManager = manager;
        mWindowLayoutParams = createWindowLayoutParams();
        if (mFloaty == null) {
            throw new IllegalStateException("Must start this service by static method ResizableExpandableFloatyWindow.startService");
        }
        inflateWindowViews(service);
        initWindowView(service);
        initWindowBridge();
        initGesture();
        setKeyListener();
        setInitialPosition();
    }

    private void setInitialPosition() {
        boolean expand = mFloaty.isInitialExpanded();
        if (expand) {
            mExpandedViewX = mFloaty.getInitialX();
            mExpandedViewY = mFloaty.getInitialY();
            expand();
        } else {
            mCollapsedViewX = mFloaty.getInitialX();
            mCollapsedViewY = mFloaty.getInitialY();
            mWindowBridge.updatePosition(mCollapsedViewX, mCollapsedViewY);
        }
    }

    private void initWindowBridge() {
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
    }

    private void inflateWindowViews(FloatyService service) {
        mExpandedView = mFloaty.inflateExpandedView(service, this);
        mCollapsedView = mFloaty.inflateCollapsedView(service, this);
        mResizer = mFloaty.getResizerView(mExpandedView);
        mMoveCursor = mFloaty.getMoveCursorView(mExpandedView);
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

    private void initWindowView(FloatyService service) {
        mWindowView = View.inflate(service, R.layout.ef_expandable_floaty_container, null);
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
        mDragGesture = DragGesture.enableDrag(mCollapsedView, mWindowBridge, mFloaty.getCollapsedViewPressedAlpha(), mFloaty.getCollapsedViewUnpressedAlpha());
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

    public void disableWindowFocusAndWindowLimit() {
        mWindowLayoutParams.flags = INITIAL_WINDOW_PARAM_FLAG;
        mWindowManager.updateViewLayout(mWindowView, mWindowLayoutParams);
    }

    public void requestWindowFocus() {
        mWindowLayoutParams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowManager.updateViewLayout(mWindowView, mWindowLayoutParams);
        mWindowView.requestFocus();
    }

    public void enableWindowLimit() {
        mWindowLayoutParams.flags &= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        mWindowManager.updateViewLayout(mWindowView, mWindowLayoutParams);
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

