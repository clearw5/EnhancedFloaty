package com.stardust.enhancedfloaty;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by Stardust on 2017/4/19.
 */

public abstract class ResizableFloaty {

    private float mCollapsedHiddenWidthRadio = 0f;
    private float mCollapsedViewUnpressedAlpha = 0.7f;
    private float mCollapsedViewPressedAlpha = 1.0f;

    public abstract View inflateCollapsedView(ResizableFloatyService service);

    public abstract View inflateExpandedView(ResizableFloatyService service);

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
}
