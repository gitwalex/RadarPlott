package de.alexanderwinkler.berechnungen;

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
    /* Startposition, aktuelle Position */
    private static final String LOGTAG = "de.alexanderwinkler";
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
    protected final float winkel;
    /**
     *
     */
    protected final float weglaenge;
    /**
     *
     */
    protected final float geschwindigkeit;
    /**
     *
     */
    protected final float intervall;
    private final Path startpath = new Path();
    private final Path destpath = new Path();
    private final Path positionPath = new Path();
    /**
     *
     *
     */
    private final Vektor2D richtungsvektor;
    private float aktPosX;
    private float aktPosY;
    /***
     * Varieblen fuer Scalierung
     */
    private float mLastScale;
    private float nextPosX;
    private float nextPosY;
    private float startPosX;
    private float startPosY;

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
    public Kurslinie(Punkt2D von, Punkt2D nach, float intervall) {
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
    public Kurslinie(Punkt2D von, Vektor2D richtung, float geschwindigkeit) {
        super(von, richtung);
        aktPosition = von;
        // Startpunkt ist aktueller Punkt abzueglich Richtungsvektor
        float x1 = von.getX();
        float y1 = von.getY();
        float x2 = richtung.getEndpunkt().getX();
        float y2 = richtung.getEndpunkt().getY();
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
        this.winkel = in.readFloat();
        this.weglaenge = in.readFloat();
        this.geschwindigkeit = in.readFloat();
        this.intervall = in.readFloat();
        this.richtungsvektor = in.readParcelable(Vektor2D.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
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
    public float getDauer(Punkt2D p) {
        if (!isPunktAufGerade(p)) {
            Log.d(LOGTAG, "Fehler bei Kurslinie.getDauer(Punkt p), Parameter: " + p.toString());
            throw new IllegalArgumentException("Punkt liegt nicht auf Kurslinie");
        }
        // Punkt liegt auf Geraden: Abstand berechnen
        float abstand = aktPosition.abstand(p);
        // Dauer berechnen. Absolutwert. Da Geschwindigkeit in Seemeilen
        // gerechnet wird, wird hier die Zeit in Stunden gerechnent.
        float zeit = Math.abs(abstand / geschwindigkeit);
        if (!isPunktInFahrtrichtung(p)) {
            // Punkt liegt nicht in Fahrtrichtung: Negativer Wert
            zeit = -zeit;
        }
        // Zeit in Minuten
        return 60 * zeit;
    }

    public Path getDestPath(float scale) {
        destpath.reset();
        if (mLastScale != scale) {
            initScale(scale);
        }
        destpath.moveTo(aktPosX, -aktPosY);
        destpath.lineTo(nextPosX, -nextPosY);
        return destpath;
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
    public float getGeschwindigkeit() {
        return geschwindigkeit;
    }

    /**
     * Geschwindigkeit auf der Kurslinie
     *
     * @return Geschwindigkeit in Knoten
     */
    public float getGeschwindigkeitRounded() {
        return rundeWert(geschwindigkeit);
    }

    /**
     * Intervall zwischen den beiden beobachteten/gemessenen Punkten
     *
     * @return Intervall in Minuten
     */
    public float getIntervall() {
        return intervall;
    }

    public Path getPositionPath(float scale) {
        positionPath.reset();
        if (mLastScale != scale) {
            initScale(scale);
        }
        positionPath.addCircle(aktPosX, aktPosY, 40f, Path.Direction.CW);
        return positionPath;
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
    public Punkt2D getPunktinFahrtrichtung(Punkt2D p, float d) {
        if (!isPunktAufKurslinie(p)) {
            Log.d(LOGTAG,
                    "Fehler bei Kurslinie.getPunktinFahrtRichtung(Punkt p, double d), Parameter: " + p
                            .toString() + " / " + d);
            throw new IllegalArgumentException("Punkt liegt nicht auf Kurslinie");
        }
        Vektor2D v = super.getRichtungsvektor().getEinheitsvektor();
        float x = p.getX() + d * v.getEndpunkt().getX();
        float y = p.getY() + d * v.getEndpunkt().getY();
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
    public Punkt2D getPunktinFahrtrichtungmitAbstand(float abstand) {
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
    public Punkt2D getPunktinFahrtrichtungnachDauer(Punkt2D p, float minuten) {
        if (!isPunktAufKurslinie(p)) {
            Log.d(LOGTAG,
                    "Fehler bei Kurslinie.getPunktinFahrtrichtungnachDauer(Punkt p, double minuten)" + ", Parameter: " + p
                            .toString() + " / " + minuten);
            throw new IllegalArgumentException("Punkt liegt nicht auf Kurslinie");
        }
        float x = p.getX();
        float y = p.getY();
        x = x + richtungsvektor.getEndpunkt().getX() * geschwindigkeit * minuten / 60f;
        y = y + richtungsvektor.getEndpunkt().getY() * geschwindigkeit * minuten / 60f;
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

    public Path getStartPath(float scale) {
        startpath.reset();
        if (mLastScale != scale) {
            initScale(scale);
        }
        startpath.moveTo(aktPosX, -aktPosY);
        startpath.lineTo(startPosX, -startPosY);
        return startpath;
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
    public float getWinkelRW() {
        return rundeWert(winkel);
    }

    private void initScale(float scale) {
        mLastScale = scale;
        float mScale = scale / ViewRadarBasisBild.RADARRINGE;
        aktPosX = aktPosition.getX() * mScale;
        aktPosY = aktPosition.getY() * mScale;
        startPosX = startPosition.getX() * mScale;
        startPosY = startPosition.getY() * mScale;
        nextPosX = aktPosition.getX() + (richtungsvektor.getEinheitsvektor().getEndpunkt()
                .getX()) * scale;
        nextPosY = aktPosition.getY() + (richtungsvektor.getEinheitsvektor().getEndpunkt()
                .getY() * scale);
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

    /**
     * Rundet ein Double auf 2 Stellen nach dem Komma
     *
     * @param d
     *         Wert, der gerundet werden soll
     *
     * @return Auf zwei Stellen gerunderter Wert
     */
    private float rundeWert(double d) {
        return (float) (Math.round(d * 100.0) / 100.0);
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