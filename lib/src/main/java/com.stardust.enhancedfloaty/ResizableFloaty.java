package com.stardust.enhancedfloaty;

import android.content.Context;
import android.view.View;

/**
 * Created by Stardust on 2017/4/19.
 */

public abstract class ResizableFloaty {

    float collapsedHiddenWidthRadio = 0f;


    public abstract View inflateCollapsedView(Context context);

    public abstract View inflateExpandedView(Context context);

    public abstract View getResizerView(View expandedView);
}
