package com.stardust.enhancedfloaty;

import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by Stardust on 2017/4/30.
 */

public abstract class ResizableFloaty {


    public abstract View inflateView(FloatyService floatyService, ResizableFloatyWindow service);

    @Nullable
    public View getResizerView(View view) {
        return null;
    }

    @Nullable
    public View getMoveCursorView(View view) {
        return null;
    }
}
