package com.stardust.enhancedfloaty;

import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by Stardust on 2017/4/19.
 */

public abstract class ResizableExpandableFloaty {

    private float mCollapsedHiddenWidthRadio = 0f;
    private float mCollapsedViewUnpressedAlpha = 0.7f;
    private float mCollapsedViewPressedAlpha = 1.0f;
    private boolean mShouldRequestFocusWhenExpand = true;

    public abstract View inflateCollapsedView(ResizableExpandableFloatyService service);

    public abstract View inflateExpandedView(ResizableExpandableFloatyService service);

    @Nullable
    public View getResizerView(View expandedView) {
        return null;
    }

    @Nullable
    public View getMoveCursorView(View expandedView) {
        return null;
    }

    public float getCollapsedHiddenWidthRadio() {
        return mCollapsedHiddenWidthRadio;
    }

    public void setCollapsedHiddenWidthRadio(float collapsedHiddenWidthRadio) {
        this.mCollapsedHiddenWidthRadio = collapsedHiddenWidthRadio;
    }

    public float getCollapsedViewUnpressedAlpha() {
        return mCollapsedViewUnpressedAlpha;
    }

    public void setCollapsedViewUnpressedAlpha(float collapsedViewUnpressedAlpha) {
        mCollapsedViewUnpressedAlpha = collapsedViewUnpressedAlpha;
    }

    public float getCollapsedViewPressedAlpha() {
        return mCollapsedViewPressedAlpha;
    }

    public void setCollapsedViewPressedAlpha(float collapsedViewPressedAlpha) {
        mCollapsedViewPressedAlpha = collapsedViewPressedAlpha;
    }

    public boolean shouldRequestFocusWhenExpand() {
        return mShouldRequestFocusWhenExpand;
    }

    public void setShouldRequestFocusWhenExpand(boolean requestFocusWhenExpand) {
        mShouldRequestFocusWhenExpand = requestFocusWhenExpand;
    }
}
