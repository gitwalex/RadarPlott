package de.alexanderwinkler.berechnungen;

import android.graphics.Canvas;

/**
 * Created by alex on 16.08.2016.
 */
public interface KurslinienDrawer {
    void drawKurslinie(Canvas canvas, float scale);

    void drawPosition(Canvas canvas);
}
