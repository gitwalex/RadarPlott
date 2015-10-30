package de.alexanderwinkler.Math;

import java.io.Serializable;

/**
 * 
 * Die Klasse Point2D beschreibt einen Punkt in einem zwei-dimensionalen
 * Koordinatensystem
 * 
 */
public class Punkt2D implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double x;
	private double y;
	
	/**
	 * Erstellt einen Punkt im Nullpunkt eines 2D-Koordinatensystems
	 */
	public Punkt2D() {
		/*
		 * Nullpunkt
		 */
		this(0d, 0d);
	}
	
	/**
	 * Konstruktor, der einen Punkt in einem Koordinatensystem erstellt.
	 * 
	 * @param x
	 *            Punkt auf der X-Achse
	 * @param y
	 *            Punkt auf der Y-Achse
	 */
	public Punkt2D(double x, double y) {
		/*
		 * Legt einen Punkt mit den Koordinaten (x , y) an
		 */
		this.setX(x);
		this.setY(y);
	}
	
	/**
	 * Konstruktor, der einen Punkt in einem Koordinatensystem erstellt.
	 * 
	 * @param p
	 *            Point2D
	 */
	public Punkt2D(Punkt2D p) {
		/*
		 * Legt einen Punkt mit den Koordinaten (x , y) an
		 */
		setX(p.getX());
		setY(p.getY());
	}
	
	/**
	 * Liefert den Abstand eines uebergebenen Punktes zum Punkt zurueck
	 * 
	 * @param p
	 *            Punkt, zu dem der Abstand berechnet werden soll
	 * @return Abstand der beiden Punkte
	 */
	public final double abstand(Punkt2D p) {
		/*
		 * Anwendung Satz des Phytagoras
		 */
		double a = (p.x - x) * (p.x - x);
		double b = (p.y - y) * (p.y - y);
		return Math.sqrt(a + b);
	}
	
	/**
	 * X-Koordinate des Punktes
	 * 
	 * @return X-Koordinate des Punktes
	 */
	public final double getX() {
		return x;
	}
	
	/**
	 * Y-Koordinate des Punktes
	 * 
	 * @return Y-Koordinate des Punktes
	 */
	public final double getY() {
		return y;
	}
	
	public double getPeilung(Punkt2D p) {
		Vektor2D v = new Vektor2D(this, p);
		return v.getWinkelRechtweisendNord();
	}
	
	/**
	 * Aendert die X-Koordinate des Punktes
	 * 
	 * @param x
	 *            neue X-Koordinate des Punktes
	 */
    private void setX(double x) {
        this.x = x;
    }
	
	/**
	 * Aendert die Y-Koordinate des Punktes
     *
     * @param y
     *            neue Y-Koordinate des Punktes
     */
    private void setY(double y) {
        this.y = y;
    }
	
	/**
	 * Gibt einen Punkt zurueck, der in einem bestimmten Abstand (distanz) und
	 * einem bestimmten Winkel (winkel) zum aktuellen Punkt liegt.
	 * 
	 * @param distanz
	 *            Distanz des neuen Punktes zum aktuellen Punkt
	 * @param winkel
	 *            Winkel in Radiant, zu dem sich der neuen Punkt zum aktuellen
	 *            Punkt liegt
	 * @return neuer Punkt
	 */
	public final Punkt2D mitWinkel(double distanz, double winkel) {
		return new Punkt2D(x + distanz * Math.sin(Math.toRadians(winkel)), y
				+ distanz * Math.cos(Math.toRadians(winkel)));
		
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

    /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Punkt2D other = (Punkt2D) obj;
        if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x)) return false;
        return Double.doubleToLongBits(y) == Double.doubleToLongBits(other.y);
    }
}
