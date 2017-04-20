package com.stardust.enhancedfloaty;

/**
 * Created by Stardust on 2017/4/18.
 */

public interface WindowBridge {
    int getX();

    int getY();

    void updatePosition(int x, int y);

    int getWidth();

    int getHeight();

    void updateMeasure(int width, int height);

    int getScreenWidth();

    int getScreenHeight();
}