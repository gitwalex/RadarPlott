package de.aw.radarplott.berechnungen;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import de.aw.radarplott.Math.Gerade2D;
import de.aw.radarplott.Math.Kreis2D;
import de.aw.radarplott.Math.Punkt2D;
import de.aw.radarplott.Math.Vektor2D;
import de.aw.radarplott.interfaces.Konstanten;

/**
 * Die Klasse Kurslinie bietet Funktionen zum ermitteln von bestimmten Daten der
 * Bewegung eines Schiffes.
 *
 * @author Alexander Winkler
 */
public class Kurslinie extends Gerade2D {
    private static final String LOGTAG = "de.alexanderwinkler";
    /* Startposition, aktuelle Position */
    /**
     *
     */
    protected final Punkt2D aktuellerStandort;
    /**
     *
     */
    protected final Punkt2D startPunkt;
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
    /**
     *
     */
    private final Vektor2D richtungsvektor;
    /**
     * Moeglichkeit zur eindeutigen kennzeichnung
     */
    private Object tag;
    /**
     * Erstellt eine Kurslinie anhand zweier (Peil-)Punkte in einem bestimmten
     * Intervall
     *
     * @param von
     *            Standort zum Zeitpunkt x
     * @param nach
     *            Standort zum Zeitpunkt x + Intervall (=aktueller Standort)
     * @param intervall
     *            Intervall in Minuten zwischen den Punkten
     */
    /**
     * @param von
     * @param nach
     * @param intervall
     */
    public Kurslinie(Punkt2D von, Punkt2D nach, float intervall) {
        super(von, nach);
        aktuellerStandort = nach;
        startPunkt = von;
        richtungsvektor = super.getRichtungsvektor();
        winkel = richtungsvektor.getWinkelRechtweisendNord();
        this.intervall = intervall;
        weglaenge = Math.abs(nach.getDistance(von));
        geschwindigkeit = weglaenge * 60 / intervall;
    }

    /**
     * Erstellt eine Kurslinie anhand eines Punktes, des Richtungsvektors und
     * der Geschwindigkeit.
     *
     * @param von
     *         Standort zum Zeitpunkt x
     * @param richtung
     *         Richtungsvektor
     * @param geschwindigkeit
     *         Geschwindigkeit
     */
    public Kurslinie(Punkt2D von, Vektor2D richtung, float geschwindigkeit) {
        super(von, richtung);
        aktuellerStandort = von;
        // Startpunkt ist aktueller Punkt abzueglich Richtungsvektor
        float x1 = von.getX();
        float y1 = von.getY();
        float x2 = richtung.getEndpunkt().getX();
        float y2 = richtung.getEndpunkt().getY();
        startPunkt = new Punkt2D(x2 - x1, y2 - y1);
        richtungsvektor = super.getRichtungsvektor();
        winkel = richtungsvektor.getWinkelRechtweisendNord();
        this.geschwindigkeit = geschwindigkeit;
        weglaenge = Math.abs(startPunkt.getDistance(von));
        intervall = weglaenge * 60 / geschwindigkeit;
    }

    /**
     * Aktueller Standort
     *
     * @return aktueller Standort
     */
    public Punkt2D getAktuellerStandort() {
        return aktuellerStandort;
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
        Punkt2D p = k.getSchnittpunkt(g);
        return p;
    }

    /**
     * Dauer, bis ein Punkt erreicht wird. Ist negativ, wenn der uebergebene
     * Punkt nicht in Fahrtrichtung liegt (= Punkt wurde bereits passiert).
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
            Log.d(LOGTAG, "Fehler bei Kurslinie.getDauer(Punkt p), Parameter: "
                    + p.toString());
            throw new IllegalArgumentException(
                    "Punkt liegt nicht auf Kurslinie");
        }
        // Punkt liegt auf Geraden: Abstand berechnen
        float abstand = aktuellerStandort.getDistance(p);
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
    public float getEntfernung(Punkt2D punktaufkurslinie) {
        if (!isPunktAufGerade(punktaufkurslinie)) {
            Log.d(LOGTAG,
                    "Fehler bei Kurslinie.getEntfernung(Punkt p), Parameter: "
                            + punktaufkurslinie.toString());
            throw new IllegalArgumentException(
                    "Punkt liegt nicht auf Kurslinie");
        }
        float lambda;
        // Punkt liegt auf der Geraden. Berechnen, wie gross die Entfernung ist
        if (richtungsvektor.getEndpunkt().getX() != 0) {
            lambda = (punktaufkurslinie.getX() - aktuellerStandort.getX())
                    / richtungsvektor.getEndpunkt().getX();
        } else {
            lambda = (punktaufkurslinie.getY() - aktuellerStandort.getY())
                    / richtungsvektor.getEndpunkt().getY();
        }
        return Math.abs(lambda);
    }

    /**
     * Geschwindigkeit auf der Kurslinie
     *
     * @return Geschwindigkeit in Knoten
     */
    public float getGeschwindigkeit() {
        return (geschwindigkeit);
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

    /**
     * Liefert einen Punkt, der in Fahrtrichtung in einer bestimmten Entfernung
     * zu einem uebergebenen Punkt liegt
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
                    "Fehler bei Kurslinie.getPunktinFahrtRichtung(Punkt p, float d), Parameter: "
                            + p.toString() + " / " + d);
            throw new IllegalArgumentException(
                    "Punkt liegt nicht auf Kurslinie");
        }
        Vektor2D v = super.getRichtungsvektor().getEinheitsvektor();
        float x = p.getX() + d * v.getEndpunkt().getX();
        float y = p.getY() + d * v.getEndpunkt().getY();
        return new Punkt2D(x, y);
    }

    /**
     * Liefert einen Punkt, der naechst in Fahrtrichtung vom aktuellen Punkt in
     * einem bestimmten Abstand zum Nullpunkt liegt
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
                    "Fehler bei Kurslinie.getPunktinFahrtrichtungmitAbstand(float getDistance), Parameter: " +
                            abstand);
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
     * Liefert einen Punkt, der in Fahrtrichtung von einem Punkt nach einer
     * bestimmten Zeit in Minuten erreicht wird.
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
                    "Fehler bei Kurslinie.getPunktinFahrtrichtungnachDauer(Punkt p, float minuten)" +
                            ", Parameter: " + p
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
    public float getSeitenpeilung(Punkt2D p) {
        if (!isPunktAufKurslinie(p)) {
            Log.d(LOGTAG,
                    "Fehler bei Kurslinie.getSeitenpeilung(Punkt p), Parameter: "
                            + p.toString());
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
    public Punkt2D getStartPunkt() {
        return startPunkt;
    }

    /**
     * Holt ein Tag fuer eine Kurslinie
     *
     * @return Tag
     */
    public Object getTag() {
        return tag;
    }

    /**
     * Setzt ein Tag fuer eine Kurslinie.Moeglichkeit zur eindeutigen
     * Kennzeichnung einr Kurslinie
     *
     * @param tag
     *         Tag
     */
    public void setTag(Object tag) {
        this.tag = tag;
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
     * Weglaenge zwischen den beiden beobachteten/errechneten Punkten. Wird im
     * Konstruktor errechnet.
     *
     * @return Weglaenge in Seemeilen
     */
    public float getWegLaenge() {
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
        float x = p.getX();
        float y = p.getY();
        float d = Math.round(getAbstand(x, y) * 1E6);
        return (d == 0);
    }

    /**
     * Prueft, ob ein Punkt in Fahrtrichtung liegt.
     *
     * @param p
     *
     * @return
     *
     * @throws IllegalArgumentException
     *         (), wenn der Punkt nicht auf der Kurslinie liegt.
     */
    public boolean isPunktInFahrtrichtung(Punkt2D p) {
        if (!isPunktAufKurslinie(p)) {
            return false;
        }
        float wegx = richtungsvektor.getEndpunkt().getX();
        float wegy = richtungsvektor.getEndpunkt().getY();
        float lambda;
        if (wegx != 0) {
            lambda = (p.getX() - aktuellerStandort.getX()) / wegx;
        } else {
            lambda = (p.getY() - aktuellerStandort.getY()) / wegy;
        }
        return lambda > 0;
    }

    /**
     * Rundet ein float auf 2 Stellen nach dem Komma
     *
     * @param d
     *         Wert, der gerundet werden soll
     *
     * @return Auf zwei Stellen gerunderter Wert
     */
    private float rundeWert(float d) {
        return (float) (Math.round(d * 100.0) / 100.0);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.alexanderwinkler.Math.Gerade2D#toString()
     */
    @Override
    public String toString() {
        float w, l;
        w = getWinkelRechtweisendNord();
        l = getWegLaenge();
        return ("Winkel: " + rundeWert(w) + " , Laenge: " + rundeWert(l)
                + ", Geschwindigkeit: " + rundeWert(geschwindigkeit)
                + ", Von: " + startPunkt.toString() + "Akt: " + aktuellerStandort.toString() + " N:"
                + getNormalenvektor().toString() + " B: "
                + getRichtungsvektor().toString() + "\n");
    }

    /**
     * Zeichnet eine Kurslinie
     *
     * @author Alexander Winkler
     */
    public class Draw implements Konstanten {
        private final Path path = new Path();
        private Punkt2D endpunkt;
        private Paint paint;
        private Punkt2D startpunkt;

        /**
         * Zeichnet eine Kurslinie. Kurslinie beginnt im aktuellen Punkt und
         * endet auf dem aussersten Raster
         *
         * @param paint
         *         Paint-Parameter. Wird nicht veraendert
         * @param rastergroesse
         *         Groesse des Radarrasters.
         * @param fahrtrichtung
         *         true: Zeichnet die Linie vom aktuellen Punkt zum in Fahrtrichtung gelegenen
         *         Raster false: Zeichnet die Linie vom aktuellen Punkt zum in gegensaetzlicher
         *         Fahrtrichtung gelegenen Raster
         */
        public Draw(Paint paint, int rastergroesse, float scale,
                    boolean fahrtrichtung) {
            this.paint = new Paint(paint);
            Punkt2D von = null, nach = null;
            Punkt2D[] erg = getSchnittpunkt(new Kreis2D(new Punkt2D(0, 0),
                    rastergroesse));
            float delta1 = getEntfernung(erg[0]);
            float delta2 = getEntfernung(erg[1]);
            if (delta1 >= delta2) {
                von = erg[1];
                nach = erg[0];
            } else {
                von = erg[0];
                nach = erg[1];
            }
            if (!isPunktInFahrtrichtung(nach)) {
                Punkt2D t = von;
                von = nach;
                nach = t;
            }
            if (fahrtrichtung) {
                endpunkt = nach;
            } else {
                endpunkt = von;
            }
            startpunkt = aktuellerStandort;
            initPath(scale);
        }

        /**
         * Zeichnet eine Kurslinie. Kurslinie beginnt und endet jeweils auf den
         * aussersten Rastern.
         *
         * @param paint
         *         Paint-Parameter. Wird nicht veraendert
         * @param rastergroesse
         *         Groesse des Radarrasters.
         */
        public Draw(int rastergroesse, float scale, Paint paint) {
            this.paint = new Paint(paint);
            Punkt2D[] erg = getSchnittpunkt(new Kreis2D(new Punkt2D(0, 0),
                    rastergroesse));
            if (erg != null) {
                startpunkt = erg[0];
                endpunkt = erg[1];
                initPath(scale);
            }
        }

        /**
         * Zeichnet eine Kurslinie vom beim Initalisieren der Kursline genannten
         * Startpunkt zum aktuellen Punkt.
         *
         * @param paint
         *         Paint-Parameter. Wird nicht veraendert
         */
        public Draw(Paint paint, float scale) {
            this.paint = new Paint(paint);
            endpunkt = aktuellerStandort;
            startpunkt = startPunkt;
            initPath(scale);
        }

        /**
         * Zeichne die Linie Fett.
         */
        public void drawFett() {
            paint.setStrokeWidth(zeichnelagefett);
        }

        /**
         * Zeichne die Linie Normal.
         */
        public void drawNormal() {
            paint.setStrokeWidth(zeichnelagenormal);
        }

        /**
         *
         */
        public void initPath(float scale) {
            float x1 = (float) startpunkt.getX() * scale;
            float y1 = -(float) startpunkt.getY() * scale;
            if (endpunkt != null) {
                float x2 = (float) endpunkt.getX() * scale;
                float y2 = -(float) endpunkt.getY() * scale;
                path.moveTo(x1, y1);
                path.lineTo(x2, y2);
            }
        }

        /**
         * Zeichnet die Kurslinie
         *
         * @param canvas
         *         Canvas, auf den gezeichnet wird.
         */
        public void onDrawLine(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        /**
         * Zeichnet einen Kreis an die aktuelle Position.
         *
         * @param canvas
         *         Canvas, auf den gezeichnet wird.
         * @param minutes
         *         Zeitpunkt, der markiert werden soll.
         */
        public void onDrawPosition(Canvas canvas, float scale, int minutes) {
            Punkt2D pos = getPunktinFahrtrichtungnachDauer(getAktuellerStandort(), minutes);
            float cx = (float) pos.getX() * scale;
            float cy = (float) pos.getY() * scale;
            canvas.drawCircle(cx, -cy, 0.1f, paint);
        }
    }
}