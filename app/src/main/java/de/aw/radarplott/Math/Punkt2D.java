package de.aw.radarplott.Math;

/**
 * Die Klasse Point2D beschreibt einen Punkt in einem zwei-dimensionalen
 * Koordinatensystem
 */
public class Punkt2D {
    private final float x;
    private final float y;

    /**
     * Erstellt einen Punkt im Nullpunkt eines 2D-Koordinatensystems
     */
    public Punkt2D() {
        this(0, 0);
    }

    /**
     * Konstruktor, der einen Punkt in einem Koordinatensystem erstellt.
     *
     * @param x
     *         Punkt auf der X-Achse
     * @param y
     *         Punkt auf der Y-Achse
     */
    public Punkt2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Konstruktor, der einen Punkt in einem Koordinatensystem erstellt.
     *
     * @param p
     *         Point2D
     */
    public Punkt2D(Punkt2D p) {
        this(p.getX(), p.getY());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Punkt2D punkt2D = (Punkt2D) o;
        if (Float.compare(punkt2D.x, x) != 0) return false;
        return Float.compare(punkt2D.y, y) == 0;
    }

    /**
     * Liefert den Abstand eines uebergebenen Punktes zum Punkt zurueck
     * Anwendung Satz des Phytagoras
     *
     * @param p
     *         Punkt, zu dem der Abstand berechnet werden soll
     *
     * @return Abstand der beiden Punkte
     */
    public final float getDistance(Punkt2D p) {
        float a = (p.x - x) * (p.x - x);
        float b = (p.y - y) * (p.y - y);
        return (float) Math.sqrt(a + b);
    }

    public float getPeilung(Punkt2D p) {
        Vektor2D v = new Vektor2D(this, p);
        return v.getWinkelRechtweisendNord();
    }

    /**
     * Gibt einen Punkt zurueck, der in einem bestimmten Abstand (distanz) und
     * einem bestimmten Winkel (winkel) zum aktuellen Punkt liegt.
     *
     * @param distanz
     *         Distanz des neuen Punktes zum aktuellen Punkt
     * @param winkel
     *         Winkel in Radiant, zu dem sich der neuen Punkt zum aktuellen Punkt liegt
     *
     * @return neuer Punkt
     */
    public final Punkt2D getPunkt(float distanz, float winkel) {
        return new Punkt2D(x + distanz * (float) Math.sin(Math.toRadians(winkel)),
                y + distanz * (float) Math.cos(Math.toRadians(winkel)));
    }

    /**
     * X-Koordinate des Punktes
     *
     * @return X-Koordinate des Punktes
     */
    public final float getX() {
        return x;
    }

    /**
     * Y-Koordinate des Punktes
     *
     * @return Y-Koordinate des Punktes
     */
    public final float getY() {
        return y;
    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "(" + Math.round(x * 1E6) / 1E6 + ", " + Math.round(y * 1E6)
                / 1E6 + ")";
    }
}
