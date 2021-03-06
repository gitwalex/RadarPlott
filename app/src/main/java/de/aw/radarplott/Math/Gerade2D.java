package de.aw.radarplott.Math;

import android.util.Log;

/**
 * Die Klasse Gerade2D bietet Funktionen zur Berechnung von Werten im
 * 2-dimensionalen Raum
 *
 * @author Alexander Winkler
 */
public class Gerade2D {
    private static final String LOGTAG = null;
    private final Punkt2D punkt1, punkt2;
    /* Richtungsvektor rv und Normalenektor nv der Geraden */
    private final Vektor2D rv, nv;
    // Gerade2D ax + by +c = 0
    private final float a, b, c;
    // Gerade2D y = mx + n
    private final float m, n;
    // Gerade2D Punkt2D_A + lambda * Richtungsvektor

    /**
     * Erstellt eine Gerade durch zwei Punkte
     *
     * @param von
     *         1. Punkt, durch den die Gerade laeuft
     * @param nach
     *         2. Punkt, durch den die Gerade laeuft
     */
    public Gerade2D(Punkt2D von, Punkt2D nach) {
        this(von, nach, new Vektor2D(von, nach));
    }

    /**
     * Erstellt eine Gerade durch einen Punkt mit einem Richtungsvektor
     *
     * @param von
     *         Punkt, durch den die Gerade laeuft
     * @param v
     *         Richtungsvektor der Geraden
     */
    public Gerade2D(Punkt2D von, Vektor2D v) {
        this(von, new Punkt2D(von.getX() + v.getEndpunkt().getX(), von.getY()
                + v.getEndpunkt().getY()), v);
    }

    /**
     * Standardkonstruktor fuer eine Gerade, in den jeweiligen Konstruktoren
     * werden die Werte belegt
     *
     * @param von
     *         , nach Punkte, durch die die Gerade laeuft
     * @param Richtungsvektor
     *         der Geraden
     */
    private Gerade2D(Punkt2D von, Punkt2D nach, Vektor2D richtung) {
        if (von.equals(nach)) {
            Log.d(LOGTAG,
                    "Fehler bei Gerade(Punkt von, Punkt nach, Vektor richtung), Parameter: "
                            + von.toString() + " / " + nach.toString() + " / "
                            + richtung.toString());
            throw new IllegalArgumentException("Die Basispunkte einer Gerade "
                    + "duerfen nicht identisch sein");
        }
        float x1 = von.getX();
        float y1 = von.getY();
        float x2 = nach.getX();
        float y2 = nach.getY();
        punkt1 = von;
        punkt2 = nach;
        rv = richtung.getEinheitsvektor();
        float dx = x2 - x1;
        float dy = y2 - y1;
        a = -dy;
        b = dx;
        c = -(x1 * a + y1 * b);
        nv = new Vektor2D(new Punkt2D(a, b)).getEinheitsvektor();
        if (x2 != x1) {
            m = (y2 - y1) / (x2 - x1);
            n = y1 - x1 * m;
        } else {
            m = Float.NaN;
            n = Float.NaN;
        }
    }

    /**
     * Ermittelt den Abstand der Geraden zu einem Punkt P
     *
     * @param x
     * @param y
     *         Koordinaten des Punktes, zu dem der Abstand ermittelt werden soll
     *
     * @return Abstand
     */
    public float getAbstand(float x, float y) {
        float q = a * a + b * b;
        float p = a * x + b * y + c;
        return (float) (p / Math.sqrt(q));
    }

    /**
     * Ermittelt den Abstand der Geraden zu einem Punkt P
     *
     * @param p
     *         Punkt, zu dem der Abstand ermittelt werden soll
     *
     * @return Abstand
     */
    public float getAbstand(Punkt2D p) {
        return getAbstand(p.getX(), p.getY());
    }

    /**
     * Ermittelt den Abstand zweier Punkte auf der Geraden. Liegt einer der
     * Punkte nicht auf der Geraden, wird float.NaN zurueckgegeben
     *
     * @param p
     *         Ein Punkt auf der Geraden
     * @param q
     *         Zweiter Punkt auf der Geraden
     *
     * @return Abstand der Punkte. Liegt einer der Punkte nicht auf der Geraden, wird Float.NaN
     * zurueckgegebeb
     */
    public float getAbstand(Punkt2D p, Punkt2D q) {
        if (isPunktAufGerade(p) || isPunktAufGerade(q)) {
            return p.getDistance(q);
        } else {
            return Float.NaN;
        }
    }

    /**
     * Liefert den Lotpunkt auf der Geraden zum uebergebenen Punkt zurueck
     *
     * @param p
     *         Punkt, zu dem der Lotpunkt berechnet werden soll. Darf nicht auf der Geraden liegen
     *
     * @return Punkt auf der Gerade, der senkrecht zum uebergebenen Punkt liegt.
     */
    public Punkt2D getLotpunkt(Punkt2D p) {
        float abstand = getAbstand(p);
        float q1 = p.getX() + nv.getEndpunkt().getX() * abstand;
        float q2 = p.getY() + nv.getEndpunkt().getY() * abstand;
        Punkt2D q = new Punkt2D(q1, q2);
        if (!isPunktAufGerade(q)) {
            q1 = p.getX() - nv.getEndpunkt().getX() * abstand;
            q2 = p.getY() - nv.getEndpunkt().getY() * abstand;
            q = new Punkt2D(q1, q2);
        }
        if (!isPunktAufGerade(q)) {
            System.out.println(p.toString() + " nicht auf Gerade");
        }
        return q;
    }

    /**
     * Endpunkt der Geraden bei der Konstruktion
     *
     * @return Endpunkt
     */
    public Punkt2D getNach() {
        return punkt2;
    }

    /**
     * Normalenvektor des Richtungsvektors der Geraden
     *
     * @return liefert den Normalenvektor als Einheitsvektor zurueck
     */
    public Vektor2D getNormalenvektor() {
        return nv;
    }

    /**
     * Ermittelt zwei Punkte auf der Gerade, die einen Abstand d vom
     * uebergebenen Punkt p (der nicht auf der Geraden liegt) haben.
     *
     * @param p
     *         Punkt, zu dem der Abstand der Geraden berechnet werden soll. Darf nicht auf der
     *         Geraden liegen, ansonsten gibt die Methode false zurueck
     * @param d
     *         gewuenschter Abstand der Punkte auf der Geraden
     * @param erg
     *         Ergebnismenge. Ist die Ergebnismenge leer, wird false zurueckgegeben, ansonsten true
     *
     * @return true, wenn es zwei Punkte gibt. False, wenn der Punkt auf der Gearden liegt.
     */
    public boolean getPunkteAufGerade(Punkt2D p, float d, Punkt2D[] erg) {
        /*
         * gibt die zwei Punkte auf der Gerade zurueck, die einen getDistance d von
		 * p (der nicht auf der Geraden liegt) haben.
		 */
        if (isPunktAufGerade(p)) {
            return false;
        }
        erg = new Punkt2D[2];
        Kreis2D k = new Kreis2D(p, d);
        erg = getSchnittpunkt(k);
        return true;
    }

    /**
     * Richtungsvektor der Geraden als Einheitsvektor (Vektorlaenge = 1)
     *
     * @return liefert den Richtungsvektor als Einheitsvektor zurueck
     */
    public Vektor2D getRichtungsvektor() {
        return rv;
    }

    /**
     * Schnittpunkt zweier Geraden
     *
     * @param g
     *         Gerade, mit der ein Schnittpunkt ermittelt werden soll
     *
     * @return Schnittpunkt. Ist null, wenn Geraden parallel laufen
     */
    public Punkt2D getSchnittpunkt(Gerade2D g) {
        // liefert den Schnittpunkt zweier Geraden
        float x, y;
        if (g.a != 0) {
            y = (a / g.a * g.c - c) / (b - a / g.a * g.b);
            x = (g.b * y + g.c) / (-g.a);
        } else if (a != 0) {
            y = (g.a / a * c - g.c) / (g.b - g.a / a * b);
            x = (b * y + c) / (-a);
        } else {
            return null;
        }
        return new Punkt2D(x, y);
    }

    /**
     * Schnittpunkte der Geraden mit einem {@link Kreis2D}
     *
     * @param k
     *         Kreis
     *
     * @return Schnittpunkte der Gerade mit dem Kreis. Gibt es keine Schnittpunkte, wird jeweils
     * null zurueckgeliefert. Bei genau einem Schnittpunkt (Tangente) sind beide Punkte identisch
     */
    public Punkt2D[] getSchnittpunkt(Kreis2D k) {
        /*
         * liefert die Schnittpunkte eines uebergebenen Kreises mit der Gerade
		 */
        Punkt2D[] v = new Punkt2D[2];
        v[0] = null;
        v[1] = null;
        float r = k.getRadius();
        float m1 = k.getMittelpunkt().getX();
        float m2 = k.getMittelpunkt().getY();
        if ((Math.round(b * 1E6)) / 1E6 != 0) {
            float s = -b * m2 - c;
            float t = (b * b * m1 + s * a) / (a * a + b * b);
            float z1 = r * r * b * b - b * b * m1 * m1 - s * s;
            float z2 = a * a + b * b;
            float z3 = t * t;
            float z4 = z1 / z2 + z3;
            float zwischenergebnis = Math.round(z4 * 1000000);
            if (zwischenergebnis >= 0) {
                zwischenergebnis = (float) Math.sqrt(zwischenergebnis / 1000000);
                zwischenergebnis = -zwischenergebnis;
                float x = zwischenergebnis + t;
                v[0] = new Punkt2D(x, (-c - a * x) / b);
                x = -zwischenergebnis + t;
                v[1] = new Punkt2D(x, (-c - a * x) / b);
            }
        } else {
            float x = c / a;
            float y = (float) (m2 + Math.sqrt(r * r - Math.pow(c / a - m1, 2)));
            v[0] = new Punkt2D(x, y);
            y = (float) (m2 - Math.sqrt(r * r - Math.pow(c / a - m1, 2)));
            v[1] = new Punkt2D(x, y);
        }
        return v;
    }

    /**
     * Startpunkt der Geraden bei der Konstruktion
     *
     * @return Startpunkt
     */
    public Punkt2D getVon() {
        return punkt1;
    }

    /**
     * Winkel der Geraden zur Y-Achse
     *
     * @return ermittelt den Winkel den die Gerade mit der y-Achse bildet (in 360 Grad-Darstellung)
     */
    protected float getWinkelRechtweisendNord() {
        return rv.getWinkelRechtweisendNord();
    }

    /**
     * Prueft, ob ein Punkt auf der Geraden liegt
     *
     * @param p
     *         Punkt
     *
     * @return true, wenn Punkt auf der Geraden liegt
     */
    public boolean isPunktAufGerade(Punkt2D p) {
        return Math.round(getAbstand(p) * 1E6) == 0.0;
    }

    /**
     * Rundet eine float auf 6 Stellen (1E6)
     *
     * @param d
     *         Wert, der gerundet werden soll
     *
     * @return gerundeter Wert
     */
    private float rundeWert(float d) {
        return (float) (Math.round(d * 1E6) / 1E6);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        float a = rundeWert(this.a);
        float b = rundeWert(this.b);
        float c = rundeWert(this.c);
        return ("Geradengleichung: " + a + " x  + " + b + " y + " + c
                + " = 0\nPunkt: " + punkt1.toString() + "Normalenvektor: "
                + getNormalenvektor().toString() + ", Richtungsvektor: "
                + rv.toString() + "\nm = " + rundeWert(m) + ", n = "
                + rundeWert(n) + "(y= " + rundeWert(m) + " x " + rundeWert(n) + ")");
    }

    /**
     * Verschiebt die Gerade parallel in einen neuen Punkt.
     *
     * @param p
     *         Punkt, in den die neue Gerade verschoben werden soll
     *
     * @return neue Gerade, verschoben in den Punkt
     */
    public Gerade2D verschiebeParallell(Punkt2D p) {
        /*
         * verschiebt die uebergebene Gerade parallel in den Punkt p
		 */
        Gerade2D g = new Gerade2D(p, getRichtungsvektor());
        return g;
    }
}
