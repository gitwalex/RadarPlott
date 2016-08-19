package de.aw.radarplott.berechnungen;

import de.aw.radarplott.Math.Punkt2D;
import de.aw.radarplott.Math.Vektor2D;

/**
 * Erweitert die Lage. Bietet Funktionen, um die Veraenderung zur
 * urspruenglichen Lagedarzustellen.
 * 
 * @author Alexander Winkler
 * 
 */

public class Manoever extends Lage {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final Kurslinie b3;
	/**
	 *
	 */
	private final Lage oldLage;
	
	/**
	 * Konstruktor fuer das Manoever 'Kursaenderung von A'
	 * 
	 * @param lage
	 *            Urspruengliche Lage
	 * @param eigKurs
	 *            Neuer Kurs von A
	 * @param manoeverminuten
	 *            Zeitpunkt des Manoevers in Minuten
	 */
	public Manoever(Lage lage, double neuerkurs, double manoeverminuten,
			double fahrt) {
		// Erstellt eine Lage aus der urspruenglichen Lage mit dem neuen Kurs
		// von A zum Zeitpunkt manoeverminuten
		// Erstellt eine Lage aus der urspruenglichen Lage mit dem neuen Kurs
		// von A zum Zeitpunkt manoeverminuten
		Punkt2D p = new Punkt2D(0, 0);
		northup = lage.northup;
		eIntervall = lage.eIntervall;
		eEigKurs = neuerkurs;
		eEigFahrt = fahrt;
		pos1_a = new Punkt2D(-(fahrt / (60 / eIntervall) * Math.sin(Math
				.toRadians(neuerkurs))),
				-(fahrt / (60 / eIntervall) * Math.cos(Math
						.toRadians(neuerkurs))));
		// wahre Position a bei zweiter Peilung
		pos2_a = p;
		// Position b zum Zeitpunkt Manoeverminuten
		Punkt2D manoeverOrt = lage.b2.getPunktinFahrtrichtungnachDauer(
				lage.b2.getAp(), manoeverminuten);
		Vektor2D manoeverVektor = new Vektor2D(lage.pos2_b, manoeverOrt);
		// relative (relative) Position b nach manoeverminuten bei erster
		// Peilung
		pos2_b = manoeverOrt;
		eDistanz2 = pos2_b.abstand(p);
		rasp2 = pos2_a.getPeilung(pos2_b);
		
		Vektor2D rpos0_b = new Vektor2D(p, lage.pos0_b);
		pos0_b = (manoeverVektor.addiereVektor(rpos0_b)).getEndpunkt();
		eDistanz1 = pos0_b.abstand(pos1_a);
		rasp1 = pos1_a.getPeilung(pos0_b);
		
		pos1_b = pos2_a.mitWinkel(eDistanz1, rasp1);
		berechneKurslinien(eIntervall);
		
		b3 = new Kurslinie(lage.b2.getAp(), b2.getRichtungsvektor(),
				lage.eEigFahrt);
		oldLage = lage;
	}
	
	/**
	 * Relative Kursaenderung des Gegners.
	 * 
	 * @return Relative Kursaenderung des Gegners.
	 */
	public double getDelta() {
		Vektor2D v1 = oldLage.b2.getRichtungsvektor();
		Vektor2D v2 = b2.getRichtungsvektor();
		// Winkel zwischen den relativen Kursen B berechnen
		return v1.getWinkel(v2);
	}
	
}
