package de.aw.radarplott.Math;
/**
 * @author alex
 */

/**
 * Die Klasse Vektor2D beschreibt einen Vektor in einem zweidimensionalen
 * Koordinatensystem. Ein Vektor hat eine Laenge, einen Zielpunkt sowie eine
 * Richtung. Der Startpunkt liegt immer im Nullpunkt.
 */
public class Vektor2D {
    private Punkt2D endpunkt;

    /**
     * Erstellt einen Vektor mit Ursprung im Nullpunkt, Zielpunkt ist der
     * Uebergebene Point2D
     *
     * @param p
     *         Zielpunkt des Vektors
     */
    public Vektor2D(Punkt2D p) {
        this.endpunkt = p;
    }

    /**
     * Erstellt einen Nullvektor, Start- und Zielpunkt liegen im Nullpunkt des
     * Koordinatensystems
     */
    public Vektor2D() {
        this(new Punkt2D(0, 0));
    }

    /**
     * Erstellt einen neuen Vektor anhand zweier Punkte.
     *
     * @param von
     *         Startpunkt
     * @param nach
     *         Endpunkt
     */
    public Vektor2D(Punkt2D von, Punkt2D nach) {
        /*
         * Erstellt einen neuen Vektor2D durch Subtraktion
		 */
        this(new Punkt2D(nach.getX() - von.getX(), nach.getY() - von.getY()));
    }

    public Vektor2D(Punkt2D punkt, float richtungswinkel) {
        this(punkt,
                new Punkt2D((float) Math.sin(richtungswinkel), (float) Math.cos(richtungswinkel)));
    }

    /**
     * Addiert einen Vektor
     *
     * @param v
     *         Vektor, zu dem dieser Vektor addiert werden soll
     *
     * @return Das Ergebnis der Addition
     */
    public final Vektor2D addiereVektor(Vektor2D v) {
        return new Vektor2D(new Punkt2D(endpunkt.getX() + v.getEndpunkt().getX(),
                endpunkt.getY() + v.getEndpunkt().getY()));
    }

    /**
     * Pruefung auf Gleichheit
     *
     * @param objekt
     *         Objekt,welches verglichen werden soll
     */
    @Override
    public boolean equals(Object objekt) {
        if (objekt == this) {
            return true;
        }
        if (!(objekt instanceof Vektor2D)) {
            return false;
        }
        Punkt2D p1 = getEinheitsvektor().getEndpunkt();
        Vektor2D v = (Vektor2D) objekt;
        Punkt2D p2 = v.getEinheitsvektor().getEndpunkt();
        return (p1.equals(p2));
    }

    /**
     * Erstellt einen neuen Vektor mit gleicher Richtung, aber der Laenge 1
     * (Einheitsvektor)
     *
     * @return Vektor mit der Laenge eins und der gleichen Richtung
     */
    public final Vektor2D getEinheitsvektor() {
        float l = getLaenge();
        return new Vektor2D(new Punkt2D(endpunkt.getX() / l, endpunkt.getY() / l));
    }

    /**
     * Endpunkt des Vektors
     *
     * @return Endpunkt des Vektors
     */
    public final Punkt2D getEndpunkt() {
        return endpunkt;
    }

    /**
     * Liefert die Laenge des Vektors zurueck
     *
     * @return Laenge des Vektors
     */
    public final float getLaenge() {
        return (float) Math.hypot(getEndpunkt().getX(), getEndpunkt().getY());
    }

    /**
     * Berechnet das Skalarprodukt zweier Vektoren
     *
     * @param v
     *
     * @return das Skalarprodukt
     */
    public final float getSkalar(Vektor2D v) {
        return v.getEndpunkt().getX() * this.getEndpunkt().getX() + v.getEndpunkt().getY() * this
                .getEndpunkt().getY();
    }

    /**
     * Liefert den Winkel zwischen zwei Vektoren zurueck
     *
     * @param v
     *         Vektor, der zur Winkelberechnung benutzt werden soll
     *
     * @return float Winkel in Degrees
     */
    public final float getWinkel(Vektor2D v) {
        return (float) Math.toDegrees(Math.acos(getSkalar(v) / (getLaenge() * v.getLaenge())));
    }

    /**
     * Ermittelt den Winkel zwischen der Y-Achse und dem Vektor in Radiant.
     * Achtung - Besonderheit atan2: Koordinaten muessen vertauscht
     * angegeben werden
     *
     * @return Winkel zwischen Vektor und Y-Achse in 360-Grad-Darstellung
     */
    public final float getWinkelRechtweisendNord() {
        return (float) ((450 - Math.toDegrees(Math.atan2(endpunkt.getY(), endpunkt.getX()))) % 360);
    }

    @Override
    public int hashCode() {
        return endpunkt.hashCode();
    }

    /**
     * Laenge des Vektors
     *
     * @return laenge des Vektors
     */
    public final float length() {
        return (float) Math.hypot(endpunkt.getX(), endpunkt.getY());
    }

    /**
     * Dreht die Richtung eines Vektors um 180 Grad
     *
     * @return ein neuer Vektor mit umgedrehter Richtung.
     */
    public final Vektor2D negate() {
        return new Vektor2D(new Punkt2D(-endpunkt.getX(), -endpunkt.getY()));
    }

    /**
     * Subtrahiert einen Vektor
     *
     * @param v
     *         Vektor , der vom aktuellen Vektor subtrahiert werden soll
     *
     * @return Das Ergebnis der Subtraktion
     */
    public final Vektor2D subtract(Vektor2D v) {
        return new Vektor2D(new Punkt2D(endpunkt.getX() - v.getEndpunkt().getX(),
                endpunkt.getY() - v.getEndpunkt().getY()));
    }

    @Override
    public String toString() {
        return "Vektor: " + endpunkt.toString();
    }
}
