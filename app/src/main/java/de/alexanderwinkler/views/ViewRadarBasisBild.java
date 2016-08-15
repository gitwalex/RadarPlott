package de.alexanderwinkler.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.alexanderwinkler.Math.Punkt2D;
import de.alexanderwinkler.R;
import de.alexanderwinkler.berechnungen.Kurslinie;
import de.alexanderwinkler.berechnungen.Lage;
import de.alexanderwinkler.interfaces.Konstanten;

/**
 * Zeichnet ein quadratisches Radarbild.
 *
 * @author Alexander Winkler
 */
public class ViewRadarBasisBild extends View implements Konstanten {
    public final static int RADARRINGE = 9;
    private Bitmap kompassrose;
    // haelt den Kontext der View
    private List<Lage> lagelist = new ArrayList<>();
    private Kurslinie mEigeneKurslinie;
    private float mHeadUpRotation;
    private ScaleGestureDetector mScaleDetector;
    private boolean northupOrientierung = true;
    // Variablen zum Zeichnen
    private Paint paint = new Paint();
    private float scale;
    private float sektorlinienlaenge = 40.0f;

    {
        // Holen der Kompassrose
        // Vorbelegungen zum Zeichnen
        paint.setTextSize(40);
        paint.setFakeBoldText(true);
        paint.setAntiAlias(true);
        paint.setStyle(Style.STROKE);
        paint.setColor(colorRadarLinien);
    }

    public ViewRadarBasisBild(Context context) {
        super(context);
    }

    public ViewRadarBasisBild(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        int width = getWidth();
        int height = getHeight();
        canvas.translate(width / 2, height / 2);
        // Hintergrundfarbe setzen
        canvas.drawColor(colorRadarBackground);
        // Mittelpunkt des Radars liegt in der Mitte des Bildes (Canvas).
        if (!northupOrientierung) {
            canvas.rotate(mHeadUpRotation);
        }
        canvas.drawBitmap(kompassrose, -kompassrose.getWidth() / 2, -kompassrose.getHeight() / 2,
                paint);
        for (int i = 1; i <= RADARRINGE; i++) {
            float innerScale = scale * i / RADARRINGE;
            canvas.drawCircle(0, 0, innerScale, paint);
        }
        for (int winkel = 0; winkel < 180; winkel += 2) {
            for (int i = 1; i <= RADARRINGE; i++) {
                float innerScale = scale * i / RADARRINGE;
                float startsektorlinie2grad = innerScale - sektorlinienlaenge / 2;
                float endsektorlinie2grad = innerScale + sektorlinienlaenge / 2;
                float startsektorlinie10grad = innerScale - sektorlinienlaenge;
                float endsektorlinie10grad = innerScale + sektorlinienlaenge;
                // Festlegen Laenge der sektorlienien auf dem Aussenkreis
                // Alle 2 Grad: halbe sektorlinienlaenge
                // Alle 10 Grad: sektorlinienlaenge
                // Alle 30 Grad: Linie vom Mittelpunkt zum aeusseren sichtbaren Radarkreis
                // Berechnen der Linien - Abstand 2 Grad
                if (winkel % 30 == 0) {
                    canvas.drawLine(0, -innerScale, 0, innerScale, paint);
                } else {
                    if (winkel % 10 == 0) {
                        canvas.drawLine(0, startsektorlinie10grad, 0, endsektorlinie10grad, paint);
                        canvas.drawLine(0, -startsektorlinie10grad, 0, -endsektorlinie10grad,
                                paint);
                    } else {
                        canvas.drawLine(0, startsektorlinie2grad, 0, endsektorlinie2grad, paint);
                        canvas.drawLine(0, -startsektorlinie2grad, 0, -endsektorlinie2grad, paint);
                    }
                }
            }
            canvas.rotate(2);
        }
        canvas.restore();
        if (mEigeneKurslinie != null) {
            //            mEigeneKurslinie.drawStartPath(canvas, scale, northupOrientierung);
            //            mEigeneKurslinie.drawDestPath(canvas, scale, northupOrientierung);
            mEigeneKurslinie.drawPosition(canvas, scale, northupOrientierung);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Resources res = getContext().getResources();
        kompassrose = BitmapFactory.decodeResource(res, R.drawable.kompassrose);
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        scale = Math.min(h, w) * 2;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            if (mEigeneKurslinie != null) {
                float x = event.getX();
                float y = event.getY();
                Punkt2D pos = new Punkt2D(x, y);
                if (mEigeneKurslinie.isPunktAufKurslinie(pos)) {
                    invalidate();
                    return true;
                }
            }
        }
        return mScaleDetector.onTouchEvent(event);
    }

    public void setEigeneKurslinie(Kurslinie kurslinie) {
        this.mEigeneKurslinie = kurslinie;
        mHeadUpRotation = -mEigeneKurslinie.getWinkelRW();
        invalidate();
    }

    public boolean toggleNorthUpOrientierung() {
        northupOrientierung = !northupOrientierung;
        invalidate();
        return northupOrientierung;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale *= detector.getScaleFactor();
            invalidate();
            return true;
        }
    }
}
