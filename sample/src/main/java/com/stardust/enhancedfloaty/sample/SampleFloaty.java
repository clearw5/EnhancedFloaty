package com.stardust.enhancedfloaty.sample;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.ContextThemeWrapper;
import android.view.View;

import com.stardust.enhancedfloaty.R;
import com.stardust.enhancedfloaty.ResizableFloaty;
import com.stardust.enhancedfloaty.ResizableFloatyService;

/**
 * Created by Stardust on 2017/4/30.
 */

public class SampleFloaty extends ResizableFloaty {
    private View mResizer;
    private View mMoveCursor;

    @Override
    public View inflateView(final ResizableFloatyService service) {
        View view = View.inflate(new ContextThemeWrapper(service, R.style.AppTheme), R.layout.floating_window_expanded, null);
        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service.stopSelf();
            }
        });
        view.findViewById(R.id.move_or_resize).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMoveCursor.getVisibility() == View.VISIBLE) {
                    mMoveCursor.setVisibility(View.GONE);
                    mResizer.setVisibility(View.GONE);
                } else {
                    mMoveCursor.setVisibility(View.VISIBLE);
                    mResizer.setVisibility(View.VISIBLE);
                }
            }
        });
        return view;
    }

    @Nullable
    @Override
    public View getResizerView(View expandedView) {
        mResizer = expandedView.findViewById(R.id.resizer);
        return mResizer;
    }

    @Nullable
    @Override
    public View getMoveCursorView(View expandedView) {
        mMoveCursor = expandedView.findViewById(R.id.move_cursor);
        return mMoveCursor;
    }
}
