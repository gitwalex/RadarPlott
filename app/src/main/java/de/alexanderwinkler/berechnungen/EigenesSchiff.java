package de.alexanderwinkler.berechnungen;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Parcel;

import de.alexanderwinkler.Math.Punkt2D;
import de.alexanderwinkler.interfaces.Konstanten;
import de.alexanderwinkler.views.ViewRadarBasisBild;

/**
 * Die Klasse EigenesSchiff bietet Funktionen zum ermitteln von bestimmten Daten der Bewegung eines
 * Schiffes.
 *
 * @author Alexander Winkler
 */
public class EigenesSchiff extends Kurslinie implements Konstanten, KurslinienDrawer {
    /**
     *
     */
    public static final Creator<EigenesSchiff> CREATOR = new Creator<EigenesSchiff>() {
        @Override
        public EigenesSchiff createFromParcel(Parcel source) {
            return new EigenesSchiff(source);
        }

        @Override
        public EigenesSchiff[] newArray(int size) {
            return new EigenesSchiff[size];
        }
    };
    /* Startposition, aktuelle Position */
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

    private final Path startpath = new Path();
    private final Path destpath = new Path();
    private final Path positionPath = new Path();
    /**
     *
     *
     */
    private float nextPosX;
    private float nextPosY;
    private float startPosX;
    private float startPosY;

    /**
     * Erstellt eine EigenesSchiff anhand zweier (Peil-)Punkte in einem bestimmten Intervall
     *
     * @param von
     *         Standort zum Zeitpunkt x
     * @param intervall
     *         Intervall in Minuten zwischen den Punkten
     */
    public EigenesSchiff(Punkt2D von, float intervall) {
        super(von, new Punkt2D(0, 0), intervall);
        initScale();
    }

    protected EigenesSchiff(Parcel in) {
        super(in);
        initScale();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void drawKurslinie(Canvas canvas, float scale) {
        canvas.save();
        startpath.reset();
        destpath.reset();
        startpath.moveTo(0, 0);
        startpath.lineTo(-startPosX * scale, startPosY * scale);
        destpath.moveTo(0, 0);
        destpath.lineTo(-nextPosX * scale, nextPosY * scale);
        canvas.drawPath(startpath, dottedLine);
        canvas.drawPath(destpath, normalLine);
        canvas.restore();
    }

    @Override
    public void drawPosition(Canvas canvas) {
        canvas.save();
        positionPath.reset();
        positionPath.addCircle(0, 0, 40f, Path.Direction.CW);
        canvas.drawPath(positionPath, normalLine);
        canvas.restore();
    }

    private void initScale() {
        startPosX = startPosition.getX() / ViewRadarBasisBild.RADARRINGE;
        startPosY = startPosition.getY() / ViewRadarBasisBild.RADARRINGE;
        nextPosX = richtungsvektor.getEinheitsvektor().getEndpunkt().getX();
        nextPosY = richtungsvektor.getEinheitsvektor().getEndpunkt().getY();
    }
}