package com.stardust.enhancedfloaty;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.stardust.enhancedfloaty.gesture.DragGesture;
import com.stardust.enhancedfloaty.gesture.ResizeGesture;
import com.stardust.lib.R;
import com.stardust.widget.ViewSwitcher;

import java.io.Serializable;

/**
 * Created by Stardust on 2017/4/18.
 */

public class ResizableFloatyService extends Service {

    public interface ViewSupplier extends Serializable {

        View inflateCollapsedView(Context context);

        View inflateExpandedView(Context context);

        View getResizerView(View expandedView);
    }

    private static final String EXTRA_VIEW_SUPPLIER = "Eating, still love you 17.4.18";
    private static final int INITIAL_WINDOW_PARAM_FLAG = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

    public static void startService(Context context, ViewSupplier supplier) {
        context.startService(new Intent(context, ResizableFloatyService.class)
                .putExtra(EXTRA_VIEW_SUPPLIER, supplier));
    }

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowLayoutParams;
    private View mWindowView;
    private ViewSwitcher mCollapseExpandViewSwitcher;
    private View mCollapsedView;
    private View mExpandedView;
    private View mResizer;
    private ResizeGesture mResizeGesture;
    private DragGesture mDragGesture;
    private ViewStack mViewStack = new ViewStack(new ViewStack.CurrentViewSetter() {
        @Override
        public void setCurrentView(View v) {
            mCollapseExpandViewSwitcher.setSecondView(v);
        }
    });

    private WindowBridge mWindowBridge = new WindowBridge() {
        DisplayMetrics mDisplayMetrics;

        @Override
        public int getX() {
            return mWindowLayoutParams.x;
        }

        @Override
        public int getY() {
            return mWindowLayoutParams.y;
        }

        @Override
        public void updatePosition(int x, int y) {
            mWindowLayoutParams.x = x;
            mWindowLayoutParams.y = y;
            mWindowManager.updateViewLayout(mWindowView, mWindowLayoutParams);
        }

        @Override
        public int getWidth() {
            return mWindowView.getWidth();
        }

        @Override
        public int getHeight() {
            return mWindowView.getHeight();
        }

        @Override
        public void updateMeasure(int width, int height) {
            mWindowLayoutParams.width = width;
            mWindowLayoutParams.height = height;
            mWindowManager.updateViewLayout(mWindowView, mWindowLayoutParams);
        }

        @Override
        public int getScreenWidth() {
            ensureDisplayMetrics();
            return mDisplayMetrics.widthPixels;
        }

        @Override
        public int getScreenHeight() {
            ensureDisplayMetrics();
            return mDisplayMetrics.heightPixels;
        }

        private void ensureDisplayMetrics() {
            if (mDisplayMetrics == null) {
                mDisplayMetrics = new DisplayMetrics();
                mWindowManager.getDefaultDisplay().getMetrics(mDisplayMetrics);
            }
        }
    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ViewSupplier supplier = (ViewSupplier) intent.getSerializableExtra(EXTRA_VIEW_SUPPLIER);
        if (supplier == null) {
            throw new IllegalStateException("Must start this service by static method ResizableFloatyService.startService");
        }
        mExpandedView = supplier.inflateExpandedView(this);
        mCollapsedView = supplier.inflateCollapsedView(this);
        mResizer = supplier.getResizerView(mExpandedView);
        initWindowView();
        initGesture();
        setKeyListener();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowLayoutParams = createWindowLayoutParams();
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
        mWindowView = View.inflate(getApplicationContext(), R.layout.resizable_floaty_container, null);
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
        mResizeGesture = ResizeGesture.enableResize(mResizer, mExpandedView, mWindowBridge);
        mDragGesture = DragGesture.enableDrag(mCollapsedView, mWindowBridge);
        mResizeGesture.setResizeEnabled(false);
        mDragGesture.setKeepToSide(true);
        mDragGesture.setOnDraggedViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableWindowFocus();
                expand();
            }
        });
    }

    private void expand() {
        mCollapseExpandViewSwitcher.showSecond();
        mResizeGesture.setResizeEnabled(true);
        mDragGesture.setKeepToSide(false);
    }

    private void collapse() {
        mCollapseExpandViewSwitcher.showFirst();
        disableWindowFocus();
        mResizeGesture.setResizeEnabled(false);
        mDragGesture.setKeepToSide(true);
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

    private void disableWindowFocus() {
        mWindowLayoutParams.flags = INITIAL_WINDOW_PARAM_FLAG;
        mWindowManager.updateViewLayout(mWindowView, mWindowLayoutParams);
    }

    private void enableWindowFocus() {
        mWindowLayoutParams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowManager.updateViewLayout(mWindowView, mWindowLayoutParams);
        mWindowView.requestFocus();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

