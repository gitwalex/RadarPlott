package de.alexanderwinkler.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.alexanderwinkler.Math.Punkt2D;
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
    private static final Paint dottedLine;
    private static final Paint normalLine = new Paint();

    static {
        normalLine.setStrokeWidth(zeichnelagenormal);
        normalLine.setAntiAlias(true);
        normalLine.setStyle(Paint.Style.STROKE);
        normalLine.setColor(colorA);
        normalLine.setStrokeWidth(zeichnelagefett);
        dottedLine = new Paint(normalLine);
        dottedLine.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
    }

    // haelt den Kontext der View
    private List<Lage> lagelist = new ArrayList<>();
    private Kurslinie mEigeneKurslinie;
    private ScaleGestureDetector mScaleDetector;
    private boolean northupOrientierung;
    // Variablen zum Zeichnen
    private Paint paint = new Paint();
    private Path pathRinge = new Path();
    private Path pathSektor = new Path();
    private Path pathSektorLinie10Grad = new Path();
    private Path pathSektorLinie2Grad = new Path();
    private int scale;
    private float sektorlinienlaenge = 40.0f;

    {
        // Holen der Kompassrose
        // Vorbelegungen zum Zeichnen
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

    private void initPath() {
        pathRinge.reset();
        pathSektor.reset();
        pathSektorLinie2Grad.reset();
        pathSektorLinie10Grad.reset();
        for (int i = 1; i <= RADARRINGE; i++) {
            float innerScale = scale * i / RADARRINGE;
            float startsektorlinie2grad = innerScale - sektorlinienlaenge / 2;
            float endsektorlinie2grad = innerScale + sektorlinienlaenge / 2;
            float startsektorlinie10grad = innerScale - sektorlinienlaenge;
            float endsektorlinie10grad = innerScale + sektorlinienlaenge;
            pathRinge.addCircle(0, 0, innerScale, Path.Direction.CW);
            // Festlegen Laenge der sektorlienien auf dem Aussenkreis
            // Alle 2 Grad: halbe sektorlinienlaenge
            // Alle 10 Grad: sektorlinienlaenge
            // Alle 30 Grad: Linie vom Mittelpunkt zum aeusseren sichtbaren Radarkreis
            pathSektor.moveTo(0, 0);
            pathSektor.lineTo(0, innerScale);
            pathSektorLinie10Grad.moveTo(0, startsektorlinie10grad);
            pathSektorLinie10Grad.lineTo(0, endsektorlinie10grad);
            // Berechnen der Linien - Abstand 2 Grad
            pathSektorLinie2Grad.moveTo(0, startsektorlinie2grad);
            pathSektorLinie2Grad.lineTo(0, endsektorlinie2grad);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        // Hintergrundfarbe setzen
        canvas.drawColor(colorRadarBackground);
        // Mittelpunkt des Radars liegt in der Mitte des Bildes (Canvas).
        int width = getWidth();
        int height = getHeight();
        canvas.translate(width / 2, height / 2);
        if (mEigeneKurslinie != null) {
            canvas.drawPath(mEigeneKurslinie.getStartPath(scale), dottedLine);
            canvas.drawPath(mEigeneKurslinie.getDestPath(scale), normalLine);
        }
        canvas.drawPath(pathRinge, paint);
        for (int winkel = 0; winkel < 360; winkel += 2) {
            if (winkel % 30 == 0) {
                canvas.drawPath(pathSektor, paint);
            } else {
                if (winkel % 10 == 0) {
                    canvas.drawPath(pathSektorLinie10Grad, paint);
                }
            }
            canvas.drawPath(pathSektorLinie2Grad, paint);
            canvas.rotate(2);
        }
        //        // Zeichnen der Radarkreise
        //        for (int i = 1; i <= RADARRINGE; i++) {
        //            float innerScale =  scale * i  / RADARRINGE;
        //            float startsektorlinie2grad = innerScale - sektorlinienlaenge / 2;
        //            float endsektorlinie2grad = innerScale + sektorlinienlaenge / 2;
        //            float startsektorlinie10grad = innerScale - sektorlinienlaenge;
        //            float endsektorlinie10grad = innerScale + sektorlinienlaenge;
        //            canvas.drawCircle(0, 0, innerScale, paint);
        //            // Festlegen Laenge der sektorlienien auf dem Aussenkreis
        //            // Alle 2 Grad: halbe sektorlinienlaenge
        //            // Alle 10 Grad: sektorlinienlaenge
        //            // Alle 30 Grad: Linie vom Mittelpunkt zum aeusseren sichtbaren Radarkreis
        //            for (int winkel = 0; winkel < 360; winkel += 2) {
        //                if (winkel % 30 == 0) {
        //                    // Berechnen und Zeichnen der Sektorenlinien - Abstand 30 Grad, Linie ab Nullpunkt
        //                    canvas.drawLine(0, 0, 0, innerScale, paint);
        //                } else {
        //                    if (winkel % 10 == 0) {
        //                        // Berechnen und zeichnen der Linien - Abstand 10 Grad
        //                        canvas.drawLine(0, startsektorlinie10grad, 0, endsektorlinie10grad, paint);
        //                    }
        //                }
        //                // Berechnen der Linien - Abstand 2 Grad
        //                canvas.drawLine(0, startsektorlinie2grad, 0, endsektorlinie2grad, paint);
        //                canvas.rotate(2);
        //            }
        //        }
        canvas.restore();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        scale = Math.min(h, w) * 2;
        initPath();
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
        invalidate();
    }

    public void setNorthUpOrientierung(boolean northupOrientierung) {
        this.northupOrientierung = northupOrientierung;
        invalidate();
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale *= detector.getScaleFactor();
            initPath();
            invalidate();
            return true;
        }
    }
}
