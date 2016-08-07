package de.alexanderwinkler.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

import de.alexanderwinkler.Math.Gerade2D;
import de.alexanderwinkler.Math.Kreis2D;
import de.alexanderwinkler.Math.Punkt2D;
import de.alexanderwinkler.berechnungen.Lage;
import de.alexanderwinkler.interfaces.Konstanten;

/**
 * Zeichnet ein quadratisches Radarbild.
 *
 * @author Alexander Winkler
 */
public class ViewRadarBasisBild extends View implements Konstanten {
    // haelt den Kontext der View
    private Lage lage;
    private OnScaleChangeListener mScaleChangeListener;
    private boolean northupOrientierung;
    // Variablen zum Zeichnen
    private Paint paint = new Paint();
    private int rastergroesse = 3;
    // Skalierungsfaktor/ Massstab: Abstand der Radarkreise in Pixel. Notwendig,
    // um verschiedene Aufloesungen zu bedienen
    private float scale;

    {
        // Holen der Kompassrose
        // Vorbelegungen zum Zeichnen
        paint.setAntiAlias(true);
        paint.setStyle(Style.STROKE);
        paint.setColor(colorRadarLinien);
    }

    /**
     * @param context
     */
    public ViewRadarBasisBild(Context context) {
        super(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public ViewRadarBasisBild(Context context, AttributeSet attrs) {
        super(context, attrs);
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
        // Zeichnen der Radarkreise
        for (int i = 0; i < rastergroesse; i++) {
            canvas.drawCircle(0, 0, scale * (i + 1), paint);
        }
        // Festlegen Skalierungsfaktor
        scale = Math.min((height - 10) / rastergroesse / 2, (width - 10) / rastergroesse / 2);
        // festlegen der Laenge der Linien Kreissektoren. Laenge 0.1f
        // heisst: 0.1 sm
        // Alle 2 Grad: Masstab (Pixel) / 40
        // Alle 10 Grad: Masstab (Pixel) / 20
        // Alle 30 Grad: Linie vom Mittelpunkt zum aeusseren Radarkreis
        float sektorlinienlaenge = 0.1f * scale;
        // mp: Mittelpunkt des Radarbildes. hp1, hp2: Hilfspunkte fuer
        // Zeichnen
        Punkt2D mp = new Punkt2D(), hp;
        // Array fuer Linien zur Kennzeichnung der Sektoren
        Punkt2D[] s = new Punkt2D[2];
        // Zeichnen der Sektoren
        for (int winkel = 0; winkel < 360; winkel += 2) {
            // Berechnen der Linien - Abstand 2 Grad
            hp = mp.mitWinkel(rastergroesse * scale, winkel);
            // Berechnen der Linien - Abstand 30 Grad, Linie ab Nullpunkt
            if (winkel % 30 == 0) {
                canvas.drawLine(0, 0, (float) hp.getX(), (float) hp.getY(), paint);
            }
            if (!hp.equals(mp)) {
                Gerade2D g = new Gerade2D(mp, hp);
                // Berechnen der Linien - Abstand 10 Grad
                if (winkel % 10 == 0) {
                    Kreis2D k = new Kreis2D(hp, sektorlinienlaenge * 2);
                    s = g.getSchnittpunkt(k);
                    canvas.drawLine((float) s[0].getX(), (float) s[0].getY(), (float) s[1].getX(),
                            (float) s[1].getY(), paint);
                }
                Kreis2D k = new Kreis2D(hp, sektorlinienlaenge);
                s = g.getSchnittpunkt(k);
                canvas.drawLine((float) s[0].getX(), (float) s[0].getY(), (float) s[1].getX(),
                        (float) s[1].getY(), paint);
            }
        }
        canvas.restore();
    }

    public void setNorthUpOrientierung(boolean northupOrientierung) {
        this.northupOrientierung = northupOrientierung;
        invalidate();
    }

    public void setRastergroesse(int rastergroesse) {
        this.rastergroesse = rastergroesse;
    }

    public interface OnScaleChangeListener {
        void onScaleChanged(float scale);
    }
}
