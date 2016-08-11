package de.alexanderwinkler.berechnungen;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Parcel;
import android.util.Log;

import de.alexanderwinkler.Math.Gerade2D;
import de.alexanderwinkler.Math.Kreis2D;
import de.alexanderwinkler.Math.Punkt2D;
import de.alexanderwinkler.Math.Vektor2D;
import de.alexanderwinkler.interfaces.Konstanten;
import de.alexanderwinkler.views.ViewRadarBasisBild;

/**
 * Die Klasse Kurslinie bietet Funktionen zum ermitteln von bestimmten Daten der Bewegung eines
 * Schiffes.
 *
 * @author Alexander Winkler
 */
public class Kurslinie extends Gerade2D implements Konstanten {
    /**
     *
     */
    public static final Creator<Kurslinie> CREATOR = new Creator<Kurslinie>() {
        @Override
        public Kurslinie createFromParcel(Parcel source) {
            return new Kurslinie(source);
        }

        @Override
        public Kurslinie[] newArray(int size) {
            return new Kurslinie[size];
        }
    };
    private static final Paint dottedLine;
    private static final Paint normalLine = new Paint();
    /* Startposition, aktuelle Position */
    private static final String LOGTAG = "de.alexanderwinkler";

    static {
        normalLine.setStrokeWidth(zeichnelagenormal);
        normalLine.setAntiAlias(true);
        normalLine.setStyle(Paint.Style.STROKE);
        normalLine.setColor(colorA);
        normalLine.setStrokeWidth(zeichnelagefett);
        dottedLine = new Paint(normalLine);
        dottedLine.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
    }

    /**
     *
     */
    protected final Punkt2D aktPosition;
    /**
     *
     */
    protected final Punkt2D startPosition;
    /**
     *
     */
    protected final double winkel;
    /**
     *
     */
    protected final double weglaenge;
    /**
     *
     */
    protected final double geschwindigkeit;
    /**
     *
     */
    protected final double intervall;
    private final Path oldpath = new Path();
    private final Path destpath = new Path();
    /**
     *
     *
     */
    private final Vektor2D richtungsvektor;

    /**
     * Erstellt eine Kurslinie anhand zweier (Peil-)Punkte in einem bestimmten Intervall
     *
     * @param von
     *         Standort zum Zeitpunkt x
     * @param nach
     *         Standort zum Zeitpunkt x + Intervall (=aktueller Standort)
     * @param intervall
     *         Intervall in Minuten zwischen den Punkten
     */
    public Kurslinie(Punkt2D von, Punkt2D nach, double intervall) {
        super(von, nach);
        aktPosition = nach;
        startPosition = von;
        richtungsvektor = super.getRichtungsvektor();
        winkel = Math.round(richtungsvektor.getWinkelRechtweisendNord());
        this.intervall = intervall;
        weglaenge = Math.abs(nach.abstand(von));
        geschwindigkeit = weglaenge * 60 / intervall;
    }

    /**
     * Erstellt eine Kurslinie anhand eines Punktes, des Richtungsvektors und der Geschwindigkeit.
     *
     * @param von
     *         Standort zum Zeitpunkt x
     * @param richtung
     *         Richtung
     * @param geschwindigkeit
     *         Geschwindigkeit
     */
    public Kurslinie(Punkt2D von, Vektor2D richtung, double geschwindigkeit) {
        super(von, richtung);
        aktPosition = von;
        // Startpunkt ist aktueller Punkt abzueglich Richtungsvektor
        double x1 = von.getX();
        double y1 = von.getY();
        double x2 = richtung.getEndpunkt().getX();
        double y2 = richtung.getEndpunkt().getY();
        startPosition = new Punkt2D(x2 - x1, y2 - y1);
        richtungsvektor = super.getRichtungsvektor();
        winkel = richtungsvektor.getWinkelRechtweisendNord();
        this.geschwindigkeit = geschwindigkeit;
        weglaenge = Math.abs(startPosition.abstand(von));
        intervall = weglaenge * 60 / geschwindigkeit;
    }

    protected Kurslinie(Parcel in) {
        super(in);
        this.aktPosition = in.readParcelable(Punkt2D.class.getClassLoader());
        this.startPosition = in.readParcelable(Punkt2D.class.getClassLoader());
        this.winkel = in.readDouble();
        this.weglaenge = in.readDouble();
        this.geschwindigkeit = in.readDouble();
        this.intervall = in.readDouble();
        this.richtungsvektor = in.readParcelable(Vektor2D.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Zeichne die Linie Fett.
     */
    public void drawFett() {
        normalLine.setStrokeWidth(zeichnelagefett);
    }

    /**
     * Zeichne die Linie Normal.
     */
    public void drawNormal() {
        normalLine.setStrokeWidth(zeichnelagenormal);
    }

    /**
     * Aktueller Standort
     *
     * @return aktueller Standort
     */
    public Punkt2D getAktPosition() {
        return aktPosition;
    }

    /**
     * Ermittelt den CPA einer Kurslinie zu einer anderen
     *
     * @param k
     *         Kurslinie, zu der der CPA ermittelt
     *
     * @return CPA
     */
    public Punkt2D getCPAOrt(Kurslinie k) {
        // Normalenvektor der uebergebenen Kurslinie ermitteln
        Vektor2D v = k.getNormalenvektor();
        // Gerade durch den Nullpunkt mit Normalenvektor legen
        Gerade2D g = new Gerade2D(new Punkt2D(0, 0), v);
        // Schnittpunkt zwischen der neuen Geraden und der uebergebenen
        // Kurslinie ist der CPA
        return k.getSchnittpunkt(g);
    }

    /**
     * Dauer, bis ein Punkt erreicht wird. Ist negativ, wenn der uebergebene Punkt nicht in
     * Fahrtrichtung liegt (= Punkt wurde bereits passiert).
     *
     * @param p
     *         Punkt auf der Kurslinie
     *
     * @return Dauer bis zum Erreichen des Punktes in Minuten
     *
     * @throws IllegalArgumentException
     *         (), wenn der Punkt nicht auf der Kurslinie liegt.
     */
    public double getDauer(Punkt2D p) {
        if (!isPunktAufGerade(p)) {
            Log.d(LOGTAG, "Fehler bei Kurslinie.getDauer(Punkt p), Parameter: " + p.toString());
            throw new IllegalArgumentException("Punkt liegt nicht auf Kurslinie");
        }
        // Punkt liegt auf Geraden: Abstand berechnen
        double abstand = aktPosition.abstand(p);
        // Dauer berechnen. Absolutwert. Da Geschwindigkeit in Seemeilen
        // gerechnet wird, wird hier die Zeit in Stunden gerechnent.
        double zeit = Math.abs(abstand / geschwindigkeit);
        if (!isPunktInFahrtrichtung(p)) {
            // Punkt liegt nicht in Fahrtrichtung: Negativer Wert
            zeit = -zeit;
        }
        // Zeit in Minuten
        return 60 * zeit;
    }

    /**
     * Absolute (damit immer positiv) Entfernung zu einem Punkt.
     *
     * @param punktaufkurslinie
     *         Punkt, der auf der Kurslinie liegt.
     *
     * @return Entfernung in Seemeilen
     *
     * @throws IllegalArgumentException
     *         (), wenn der Punkt nicht auf der Kurslinie liegt.
     */
    public double getEntfernung(Punkt2D punktaufkurslinie) {
        if (!isPunktAufGerade(punktaufkurslinie)) {
            Log.d(LOGTAG,
                    "Fehler bei Kurslinie.getEntfernung(Punkt p), Parameter: " + punktaufkurslinie
                            .toString());
            throw new IllegalArgumentException("Punkt liegt nicht auf Kurslinie");
        }
        double lambda;
        // Punkt liegt auf der Geraden. Berechnen, wie gross die Entfernung ist
        if (richtungsvektor.getEndpunkt().getX() != 0) {
            lambda = (punktaufkurslinie.getX() - aktPosition.getX()) / richtungsvektor.getEndpunkt()
                    .getX();
        } else {
            lambda = (punktaufkurslinie.getY() - aktPosition.getY()) / richtungsvektor.getEndpunkt()
                    .getY();
        }
        return Math.abs(lambda);
    }

    /**
     * Geschwindigkeit auf der Kurslinie
     *
     * @return Geschwindigkeit in Knoten
     */
    public double getGeschwindigkeit() {
        return (geschwindigkeit);
    }

    /**
     * Geschwindigkeit auf der Kurslinie
     *
     * @return Geschwindigkeit in Knoten
     */
    public double getGeschwindigkeitRounded() {
        return rundeWert(geschwindigkeit);
    }

    /**
     * Intervall zwischen den beiden beobachteten/gemessenen Punkten
     *
     * @return Intervall in Minuten
     */
    public double getIntervall() {
        return intervall;
    }

    /**
     * Liefert einen Punkt, der in Fahrtrichtung in einer bestimmten Entfernung zu einem
     * uebergebenen Punkt liegt
     *
     * @param p
     *         Punkt auf der Kurslinie
     * @param d
     *         Entfernung zum uebergebenen Punkt
     *
     * @return Punkt, der in Entfernung d zum uebergebenen Punkt liegt
     *
     * @throws IllegalArgumentException
     *         (), wenn der Punkt nicht auf der Kurslinie liegt.
     */
    public Punkt2D getPunktinFahrtrichtung(Punkt2D p, double d) {
        if (!isPunktAufKurslinie(p)) {
            Log.d(LOGTAG,
                    "Fehler bei Kurslinie.getPunktinFahrtRichtung(Punkt p, double d), Parameter: " + p
                            .toString() + " / " + d);
            throw new IllegalArgumentException("Punkt liegt nicht auf Kurslinie");
        }
        Vektor2D v = super.getRichtungsvektor().getEinheitsvektor();
        double x = p.getX() + d * v.getEndpunkt().getX();
        double y = p.getY() + d * v.getEndpunkt().getY();
        return new Punkt2D(x, y);
    }

    /**
     * Liefert einen Punkt, der naechst in Fahrtrichtung vom aktuellen Punkt in einem bestimmten
     * Abstand zum Nullpunkt liegt
     *
     * @param abstand
     *         Abstand des Punktes zum Nullpunkt
     *
     * @return Punkt, der im Abstand d zum Nullpunkt liegt
     *
     * @throws IllegalArgumentException
     *         (), wenn kein Punkt im entsprechenden Abstand auf der Kurslinie liegt
     */
    public Punkt2D getPunktinFahrtrichtungmitAbstand(double abstand) {
        Kreis2D k = new Kreis2D(new Punkt2D(0, 0), abstand);
        Punkt2D[] s = getSchnittpunkt(k);
        if (!isPunktInFahrtrichtung(s[0])) {
            s[0] = null;
        }
        if (!isPunktInFahrtrichtung(s[1])) {
            s[1] = null;
        }
        if (s[0] == null & s[1] == null) {
            Log.d(LOGTAG,
                    "Fehler bei Kurslinie.getPunktinFahrtrichtungmitAbstand(double abstand), Parameter: " + abstand);
            throw new IllegalArgumentException(
                    "Kein Punkt in entsprechendem Abstand in Fahrtrichtung auf der Kurslinie");
        }
        Punkt2D erg = s[0];
        if (getEntfernung(s[0]) > getEntfernung(s[1])) {
            erg = s[1];
        }
        return erg;
    }

    /**
     * Liefert einen Punkt, der in Fahrtrichtung von einem Punkt nach einer bestimmten Zeit in
     * Minuten erreicht wird.
     *
     * @param p
     *         Punkt auf der Kurslinie
     * @param minuten
     *         Dauer vom uebergebenen Punkt
     *
     * @return Punkt, der in Dauer erreicht ist.
     *
     * @throws IllegalArgumentException
     *         (), wenn der Punkt nicht auf der Kurslinie liegt.
     */
    public Punkt2D getPunktinFahrtrichtungnachDauer(Punkt2D p, double minuten) {
        if (!isPunktAufKurslinie(p)) {
            Log.d(LOGTAG,
                    "Fehler bei Kurslinie.getPunktinFahrtrichtungnachDauer(Punkt p, double minuten)" + ", Parameter: " + p
                            .toString() + " / " + minuten);
            throw new IllegalArgumentException("Punkt liegt nicht auf Kurslinie");
        }
        double x = p.getX();
        double y = p.getY();
        x = x + richtungsvektor.getEndpunkt().getX() * geschwindigkeit * minuten / 60d;
        y = y + richtungsvektor.getEndpunkt().getY() * geschwindigkeit * minuten / 60d;
        return new Punkt2D(x, y);
    }

    /**
     * Liefert die Seitenpeilung eines Punktes zur Kurslinie
     *
     * @param p
     *         Punkt, der nicht auf der Kurslinie liegt
     *
     * @return Seitenpeilung in Grad (Degrees)
     *
     * @throws IllegalArgumentException
     *         (), wenn der Punkt nicht auf der Kurslinie liegt.
     */
    public double getSeitenpeilung(Punkt2D p) {
        if (!isPunktAufKurslinie(p)) {
            Log.d(LOGTAG,
                    "Fehler bei Kurslinie.getSeitenpeilung(Punkt p), Parameter: " + p.toString());
            throw new IllegalArgumentException("Punkt liegt auf Kurslinie");
        }
        Vektor2D v = new Vektor2D(getLotpunkt(p));
        return v.getWinkelRechtweisendNord();
    }

    /**
     * Erster Punkt, der beobachtet/errechnet wurde.
     *
     * @return Startpunkt zum Zeitpunkt x
     */
    public Punkt2D getStartPosition() {
        return startPosition;
    }

    /**
     * Tangente(n) an einen
     *
     * @param k
     * @param manoeverort
     *         Ort, an dem ein Manoever ausgefuehrt wird.
     *
     * @return 2 Geraden. Sind identisch, wenn der Punkt auf dem Kreis liegt.
     */
    // TODO Kommentar anpassen
    public Gerade2D[] getTangente(Kreis2D k, Punkt2D manoeverort) {

		/*
         * liefert die beiden Geraden zurueck, die als Tangente an den
		 * uebergebenen Kreis fuehren. Massgeblich ist dabei ein Ort, der einen
		 * bestimmten Abstand vom Mittelpunkt des Kreises hat
		 */
        Gerade2D[] l = new Gerade2D[2];
        Punkt2D[] p = k.getTangente(manoeverort);
        l[0] = new Gerade2D(manoeverort, p[0]);
        l[1] = new Gerade2D(manoeverort, p[1]);
        return l;
    }

    /**
     * Weglaenge zwischen den beiden beobachteten/errechneten Punkten. Wird im Konstruktor
     * errechnet.
     *
     * @return Weglaenge in Seemeilen
     */
    public double getWegLaenge() {
        return weglaenge;
    }

    /**
     * Rechtweisender Kurs
     *
     * @return Kurs in Grad (Degrees)
     */
    public double getWinkelRW() {
        return rundeWert(winkel);
    }

    /**
     * Prueft, ob ein Punkt auf der Kurslinie liegt. Toleranz ist 1E6.
     *
     * @param p
     *         Punkt
     *
     * @return true, wenn der Punkt auf der Kurslinie liegt, ansonsten false.
     */
    public boolean isPunktAufKurslinie(Punkt2D p) {
        if (p == null) {
            return false;
        }
        double x = p.getX();
        double y = p.getY();
        double d = Math.round(getAbstand(x, y) * 1E6);
        return (d == 0);
    }

    /**
     * Prueft, ob ein Punkt in Fahrtrichtung liegt.
     *
     * @param p
     *         zu pruefender Punkt
     *
     * @return true, wenn der Punkt in Fahrtrichtung liegt. Sonst false.
     */
    public boolean isPunktInFahrtrichtung(Punkt2D p) {
        if (!isPunktAufKurslinie(p)) {
            return false;
        }
        double wegx = richtungsvektor.getEndpunkt().getX();
        double wegy = richtungsvektor.getEndpunkt().getY();
        double lambda;
        if (wegx != 0) {
            lambda = (p.getX() - aktPosition.getX()) / wegx;
        } else {
            lambda = (p.getY() - aktPosition.getY()) / wegy;
        }
        return lambda > 0;
    }

    public void onDraw(Canvas canvas, float scale) {
        oldpath.reset();
        destpath.reset();
        canvas.save();
        float mScale = scale / ViewRadarBasisBild.RADARRINGE;
        float aktPosX = (float) aktPosition.getX() * mScale;
        float aktPosY = (float) aktPosition.getY() * mScale;
        float startPosX = (float) startPosition.getX() * mScale;
        float startPosY = (float) startPosition.getY() * mScale;
        float nextPosX =
                (float) (aktPosition.getX() + (richtungsvektor.getEinheitsvektor().getEndpunkt()
                        .getX()) * scale);
        float nextPosY =
                (float) (aktPosition.getY() + (richtungsvektor.getEinheitsvektor().getEndpunkt()
                        .getY() * scale));
        onDrawPosition(canvas, aktPosX, -aktPosY);
        oldpath.moveTo(aktPosX, -aktPosY);
        oldpath.lineTo(startPosX, -startPosY);
        canvas.drawPath(oldpath, dottedLine);
        destpath.moveTo(aktPosX, -aktPosY);
        destpath.lineTo(nextPosX, -nextPosY);
        canvas.drawPath(destpath, normalLine);
        canvas.restore();
    }

    /**
     * Zeichnet einen Kreis an die aktuelle Position.
     *
     * @param canvas
     *         Canvas, auf den gezeichnet wird.
     */
    public void onDrawPosition(Canvas canvas, float posX, float posY) {
        canvas.drawCircle(posX, posY, 40f, normalLine);
    }

    /**
     * Rundet ein Double auf 2 Stellen nach dem Komma
     *
     * @param d
     *         Wert, der gerundet werden soll
     *
     * @return Auf zwei Stellen gerunderter Wert
     */
    private double rundeWert(double d) {
        return Math.round(d * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        double w, l;
        w = getWinkelRechtweisendNord();
        l = getWegLaenge();
        return ("Winkel: " + rundeWert(w) + " , Laenge: " + rundeWert(
                l) + ", Geschwindigkeit: " + rundeWert(geschwindigkeit) + ", Von: " + startPosition
                .toString() + "Akt: " + aktPosition.toString() + " N:" + getNormalenvektor()
                .toString() + " B: " + getRichtungsvektor().toString() + "\n");
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.aktPosition, flags);
        dest.writeParcelable(this.startPosition, flags);
        dest.writeDouble(this.winkel);
        dest.writeDouble(this.weglaenge);
        dest.writeDouble(this.geschwindigkeit);
        dest.writeDouble(this.intervall);
        dest.writeParcelable(this.richtungsvektor, flags);
    }
}