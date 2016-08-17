package de.alexanderwinkler.berechnungen;

import android.util.Log;

import java.io.Serializable;

import de.alexanderwinkler.Math.Kreis2D;
import de.alexanderwinkler.Math.Punkt2D;
import de.alexanderwinkler.Math.Vektor2D;
import de.alexanderwinkler.interfaces.Konstanten;

/**
 * Klasse zur Beschreibung einer Lage zwischen dem eigenen Schiff (A) und einem 'Gegner' (B).
 * Berechnet aus zwei Peilungen und einem dazwischenliegenden Intervall diverse Lage-Ergebnisse
 * (z.B. CPA)
 *
 * @author Alexander Winkler
 */
public class Lage implements Konstanten, Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public EigenesSchiff a;
    public GegnerSchiff b0;
    public GegnerSchiff b1;
    public GegnerSchiff b2;
    public GegnerSchiff cpa;
    protected float eEigKurs, eEigFahrt, eIntervall, rasp1, eDistanz1, rasp2, eDistanz2;
    protected Boolean northup;
    protected Punkt2D pos1_a, pos2_a, pos0_b, pos1_b, pos2_b;
    private EigenesSchiff eigenesSchiff;

    /**
     * Berechnet aus der Peilung eines Schiffes die aktuelle Lage.
     *
     * @param northUp
     *         true: Peilung erfolgt mit rechtweisend Nord
     * @param seitenpeilung1
     *         1. Radarseitenpeilung oder Seitenpeilung
     * @param seitenpeilung2
     *         2. Radarseitenpeilung oder Seitenpeilung
     */
    public Lage(boolean northUp, EigenesSchiff eigenesSchiff, Radarseitenpeilung seitenpeilung1,
                Radarseitenpeilung seitenpeilung2) {
        this.eigenesSchiff = eigenesSchiff;
        pos1_a = eigenesSchiff.getStartPosition();
        // wahre Position a bei zweiter Peilung
        pos2_a = eigenesSchiff.getAktPosition();
        // Position b bei erster Peilung
        pos0_b = pos1_a.mitWinkel(seitenpeilung1.getDistanz(),
                seitenpeilung1.getRadarSeitenPeilung());
        // wahre Position b bei erster Peilung
        pos1_b = pos2_a.mitWinkel(seitenpeilung2.getDistanz(),
                seitenpeilung2.getRadarSeitenPeilung());
        // Position b bei zweiter Peilung
        pos2_b = pos2_a.mitWinkel(seitenpeilung2.getDistanz(),
                seitenpeilung2.getRadarSeitenPeilung());
        this.northup = northup;
        b0 = new GegnerSchiff(pos0_b, pos1_b, eIntervall);
        // Wahre EigenesSchiff B
        b1 = new GegnerSchiff(pos0_b, pos2_b, eIntervall);
        // Relative EigenesSchiff B nur zwischen den peilungen
        b2 = new GegnerSchiff(pos1_b, pos2_b, eIntervall);
        // CPA-Linie
        cpa = new GegnerSchiff(pos2_a, a.getCPAOrt(b2), eIntervall);
        eEigKurs = a.getWinkelRW();
        eIntervall = a.getIntervall();
        eEigFahrt = a.getGeschwindigkeitRounded();
        Punkt2D p = new Punkt2D(0, 0);
        Vektor2D v = new Vektor2D(p, pos1_b);
        rasp1 = korrigiereWinkel(v.getWinkelRechtweisendNord() - eEigKurs);
        v = new Vektor2D(p, pos2_b);
        rasp2 = korrigiereWinkel(v.getWinkelRechtweisendNord() - eEigKurs);
        eDistanz1 = p.abstand(pos1_b);
        eDistanz2 = p.abstand(pos2_b);
    }

    /**
     * Bei Addition oder Subtraktion kann der Winkel ausserhalb des Intervals [0..360] liegen. Dies
     * wird hier korrigiert
     *
     * @param winkel
     *         Kann beliebige Werte enthalten
     *
     * @return Winkel im Intervall [0..360]
     */
    public static float korrigiereWinkel(float winkel) {
        while (winkel > 360) {
            winkel -= 360;
        }
        while (winkel < 0) {
            winkel += 360d;
        }
        return winkel;
    }

    /**
     * Passieren der EigenesSchiff
     *
     * @return Abstand, in der B die EigenesSchiff von A passiert
     */
    public Double getBCR() {
        Punkt2D p = a.getSchnittpunkt(b2);
        double d = pos2_a.abstand(p);
        if (!a.isPunktInFahrtrichtung(p)) {
            d = -d;
        }
        return d;
    }

    /**
     * Zeit bis zum passieren der EigenesSchiff. Ist negativ, wenn die EigenesSchiff bereits
     * passiert wurde
     *
     * @return Dauer bis zum passieren der EigenesSchiff (in Minuten)
     */
    public float getBCT() {
        Punkt2D p = a.getSchnittpunkt(b2);
        return b2.getDauer(p);
    }

    /**
     * Entfernung des CPA zwischen A und B
     *
     * @return Entfernung des CPA in Seemeilen
     */
    public double getCPA() {
        double d = b2.getAbstand(pos2_a);
        Punkt2D cpa = a.getCPAOrt(b2);
        if (!b2.isPunktInFahrtrichtung(cpa)) {
            d = -d;
        }
        return d;
    }

    /**
     * Entfernung des CPA von aktueller Position B
     *
     * @return Entfernung zum CPA in sm
     */
    public double getCPAEntfernung() {
        Punkt2D cpa = a.getCPAOrt(b2);
        return b2.getEntfernung(cpa);
    }

    /**
     * Entfernung vom aktuellen Punkt B zur Kursline A
     *
     * @return Entferung in sm
     */
    public double getEntfernungKurslinie() {
        Punkt2D p = a.getSchnittpunkt(b2);
        return b2.getEntfernung(p);
    }

    public GegnerSchiff getGegner() {
        return b1;
    }

    /**
     * Kurs A
     *
     * @return Kurs A rechtweisend
     */
    public int getKAa() {
        return (int) a.getWinkelRW();
    }

    /**
     * Wahrer Kurs B
     *
     * @return wahren Kurs B
     */
    public float getKB() {
        return b1.getWinkelRW();
    }

    /**
     * relativer Kurs B
     *
     * @return relativer Kurs B rechtweisend
     */
    public float getKBr() {
        return b2.getWinkelRW();
    }

    /**
     * Groesste Distanz zwischen den Peilungen auf die naechste ganze Zahl gerundet
     *
     * @return groesste Distanz
     */
    public int getMaxDistanz() {
        return (int) Math.max(Math.ceil(eDistanz1), Math.ceil(eDistanz2));
    }

    /**
     * Peilung zum CPA
     *
     * @return rechtweisende Peilung zum CPA
     */
    public float getPCPA() {
        Punkt2D p = a.getCPAOrt(b2);
        Vektor2D v = new Vektor2D(new Punkt2D(0, 0), p);
        return v.getWinkelRechtweisendNord();
    }

    /**
     * Seitenpeilung aktueller Punkt zum Zeitpunkt x + Intervall
     *
     * @return Seitenpeilung
     */
    public float getRASP2() {
        float angle = new Vektor2D(new Punkt2D(0, 0), b2.aktPosition).getWinkelRechtweisendNord();
        angle = 360 - a.getWinkelRW() + angle;
        return korrigiereWinkel(angle);
    }

    /**
     * Seitenpeilung CPA
     *
     * @return Seitenpeilung zum CPA
     */
    public float getSCPA() {
        float pcpa = getPCPA();
        return korrigiereWinkel(pcpa - eEigKurs);
    }

    /**
     * Dauer bis zum CPA. Negativ: CPA wurde bereits passiert, B entfernt sich
     *
     * @return Dauer bis CPA (in Minuten)
     */
    public float getTCPA() {
        Punkt2D p = a.getCPAOrt(b2);
        return b2.getDauer(p);
    }

    /**
     * Kurs A
     *
     * @return Kurs A
     */
    public double geteEigKurs() {
        return eEigKurs;
    }

    /**
     * Relative Geschwindigkeit B
     *
     * @return relaitve Geschwindigkeit B (in Knoten)
     */
    public float getvB() {
        return b1.getGeschwindigkeitRounded();
    }

    /**
     * Relative Geschwindigkeit B
     *
     * @return relative Geschwindigkeit B in Knoten
     */
    public float getvBr() {
        return b2.getGeschwindigkeitRounded();
    }

    /**
     * Ermittelt einen neuen Kurs, um eine neue Lage aufgrund eines gewuenschten CPA zu erreichen.
     *
     * @param newcpa
     *         der gewuenschte neue CPA
     *
     * @return der einzuschlagende Kurs fuer den neuen CPA.
     *
     * @throws IllegalArgumentException
     *         wenn der gewuenschte CPA durch keine denkbare Kursaenderung erreichbar ist.
     */
    public float newCPA(float newcpa) throws IllegalArgumentException {
        // Ein gewunschter CPA kann durch eine Kursaenderung A erreicht werden.
        // Daher wird aufgrund des newcpa der neue Kurs berechnet, und
        // zurueckgeliefert.
        // Ist der CPA nicht erreichbar, wird null geliefert.
        Punkt2D aktA = a.aktPosition;
        Punkt2D aktB = b2.aktPosition;
        // Aufspannen eines Kreises ueber dem relativen Kurs von B. Mittelpunkt
        // ist der Punkt zwischen dem aktuellen Punkt B und A. Radius ist die
        // Entfernung zwischen den Punkten /2.
        // Mittelpunkt ermitteln
        float x = (aktA.getX() + aktB.getX()) / 2;
        float y = (aktA.getY() + aktB.getY()) / 2;
        Punkt2D mittelpunkt = new Punkt2D(x, y);
        // Radius ist Abstand der Punkte vom Mittelpunkt
        float abstandA = Math.abs(mittelpunkt.abstand(aktA));
        // Kreis aufspannen
        Kreis2D k1 = new Kreis2D(mittelpunkt, abstandA);
        // Kreis um neuen CPA aufspannen
        Kreis2D k2 = new Kreis2D(aktA, newcpa);
        // Schnittpunkte berechnen
        Punkt2D[] erg = k2.schnittpunkt(k1);
        if (erg == null) {
            Log.d(LOGTAG, "Fehler bei Lage.newCPA(double newcpa), Parameter: " + newcpa);
            throw new IllegalArgumentException(
                    "Keine CPA-Berechnung moeglich. CPA nicht erreichbar!.");
        }
        float newKursA, delta1 = 0, delta2 = 0;
        // Neuer Relativkurs B
        float kursB = b2.getWinkelRW();
        Vektor2D v = new Vektor2D(b2.getAktPosition(), erg[0]);
        float delta = v.getWinkelRechtweisendNord() - kursB;
        if (delta < 0) {
            delta1 = delta;
        } else {
            delta2 = delta;
        }
        v = new Vektor2D(b2.getAktPosition(), erg[1]);
        delta = v.getWinkelRechtweisendNord() - kursB;
        if (delta < 0) {
            delta1 = delta;
        } else {
            delta2 = delta;
        }
        // Neuer Kurs A
        double kursA = getKAa();
        // Bei einer Kursaenderung nach Steuerbord knicken Relativbewegungen
        // der Kontakte, deren Relativ-Kurs Eigenem Kurs � 90� entspricht,
        // nach links ab.
        // Die Relativbewegungen der Kontakte mit entgegenkommenden Relativ-
        // Kursen  knicken nach rechts ab.
        if (Math.abs(kursB - kursA) > 90) {
            newKursA = getKAa() + delta2;
        } else {
            newKursA = getKAa() + delta1;
        }
        return newKursA;
    }

    public void setFahrtaenderung(double neueGeschwindigkeit) {
        // Bei einer Fahrtreduzierung knicken alle Relativbewegungen
        // vorlicher ab.
        // Bei einer Beschleunigung knicken sie so ab, dass sie achterlicher
        // erscheinen
    }
}
