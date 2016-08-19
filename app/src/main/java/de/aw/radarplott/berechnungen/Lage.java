package de.aw.radarplott.berechnungen;

import android.util.Log;

import java.io.Serializable;

import de.aw.radarplott.Math.Kreis2D;
import de.aw.radarplott.Math.Punkt2D;
import de.aw.radarplott.Math.Vektor2D;
import de.aw.radarplott.interfaces.Konstanten;
/**
 * Klasse zur Beschreibung einer Lage zwischen dem eigenen Schiff (A) und einem
 * 'Gegner' (B). Berechnet aus zwei Peilungen und einem dazwischenliegenden
 * Intervall diverse Lage-Ergebnisse (z.B. CPA)
 *
 * @author Alexander Winkler
 */
public class Lage implements Konstanten, Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public Kurslinie a, b0, b1, b2, cpa;
    protected double eEigKurs, eEigFahrt, eIntervall, rasp1, eDistanz1, rasp2,
            eDistanz2;
    protected double[] eingabewerte;
    protected Boolean northup;
    protected Punkt2D pos1_a, pos2_a, pos0_b, pos1_b, pos2_b;
    /**
     * Im Tag kann eine eindeutiger Tag gespeichert werden.
     */
    private String tag;

    public Lage () {
    }

    /**
     * Berechnet aus der Peilung eines Schiffes die aktuelle Lage.
     *
     * @param northup true: Peilung erfolgt mit rechtweisend Nord
     * @param intervall Dauer zwischen zwei Peilungen
     * @param punkt Punkt-Array (Reihenfolge beachten) - Wahre Position A bei
     * erster Peilung - Wahre Position A bei zweiter Peilung - Position B bei
     * erster Peilung - Wahre Position B bei erster Peilung - Position B bei
     * zweiter Peilung
     */
    public Lage (boolean northup, double intervall, Punkt2D... punkt) {
        pos1_a = punkt[0];
        pos2_a = punkt[1];
        pos0_b = punkt[2];
        pos1_b = punkt[3];
        pos2_b = punkt[4];
        this.northup = northup;
        berechneKurslinien(intervall);
        eEigKurs = a.getWinkelRW();
        eIntervall = a.getIntervall();
        eEigFahrt = a.getGeschwindigkeitRounded();
        Punkt2D p = new Punkt2D(0.0, 0.0);
        Vektor2D v = new Vektor2D(p, pos1_b);
        rasp1 = korrigiereWinkel(v.getWinkelRechtweisendNord() - eEigKurs);
        v = new Vektor2D(p, pos2_b);
        rasp2 = korrigiereWinkel(v.getWinkelRechtweisendNord() - eEigKurs);
        eDistanz1 = p.abstand(pos1_b);
        eDistanz2 = p.abstand(pos2_b);
    }

    /**
     * Berechnet aus der Peilung eines Schiffes die aktuelle Lage.
     *
     * @param northup true: Peilung erfolgt mit rechtweisend Nord
     * @param werte Double-Array mit folgenden Inhalt (Reihenfolge beachten): -
     * Kurs A (rechtweisend) - Fahrt A (in Knoten) - Intervall zwischen zwei
     * Peilungen (Minuten) - Seitenpeilung B zum Zeitpunkt x - Distanz B zum
     * Zeitpunkt x (Seemeilen) - Seitenpeilung B zum Zeitpunkt x + intervall
     * -Distanz B zum Zeitpunkt x + Intervall (Seemeilen) - Headup: Peilungen
     * sind Radarseitenpeilungen (true)
     */
    public Lage (boolean northup, double... werte) {
        eEigKurs = werte[0];
        eEigFahrt = werte[1];
        eIntervall = werte[2];
        rasp1 = werte[5];
        eDistanz1 = werte[6];
        rasp2 = werte[9];
        eDistanz2 = werte[10];
        this.northup = northup;
        eingabewerte = werte;
        init();
    }

    /**
     * Bei Addition oder Subtraktion kann der Winkel ausserhalb des Intervals [0..360] liegen. Dies
     * wird hier korrigiert
     *
     * @param Ursprungswinkel
     *         Kann beliebige Werte enthalten
     *
     * @return Winkel im Intervall [0..360]
     */
    public static double korrigiereWinkel(double d) {
        while (d > 360) {
            d -= 360;
        }
        while (d < 0) {
            d += 360d;
        }
        return d;
    }

    /**
     * Berechnet die Kurslinie zu einem bestimmten Intervall
     *
     * @param Intervall zwischen zwei Peilungen
     */
    public final void berechneKurslinien (double eIntervall) {
        // Kurslinie A aus zwei Werten
        a = new Kurslinie(pos1_a, pos2_a, eIntervall);
        a.setTag("A");
        // Kurslinie A im Verhaeltnis zur Kurslinie B
        b0 = new Kurslinie(pos0_b, pos1_b, eIntervall);
        b0.setTag("b0");
        // Wahre Kurslinie B
        b1 = new Kurslinie(pos0_b, pos2_b, eIntervall);
        b1.setTag("b1");
        // Relative Kurslinie B nur zwischen den peilungen
        b2 = new Kurslinie(pos1_b, pos2_b, eIntervall);
        b2.setTag("b2");
        // CPA-Linie
        cpa = new Kurslinie(pos2_a, a.getCPAOrt(b2), eIntervall);
        cpa.setTag("cpa");
    }

    /**
     * Passieren der Kurslinie
     *
     * @return Abstand, in der B die Kurslinie von A passiert
     */
    public Double getBCR () {
        Punkt2D p = a.getSchnittpunkt(b2);
        double d = pos2_a.abstand(p);
        if (!a.isPunktInFahrtrichtung(p)) {
            d = -d;
        }
        return d;
    }

    /**
     * Zeit bis zum passieren der Kurslinie. Ist negativ, wenn die Kurslinie
     * bereits passiert wurde
     *
     * @return Dauer bis zum passieren der Kurslinie (in Minuten)
     */
    public Double getBCT () {
        Punkt2D p = a.getSchnittpunkt(b2);
        return b2.getDauer(p);
    }

    /**
     * Entfernung des CPA zwischen A und B
     *
     * @return Entfernung des CPA in Seemeilen
     */
    public double getCPA () {
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
    public double getCPAEntfernung () {
        Punkt2D cpa = a.getCPAOrt(b2);
        double d = b2.getEntfernung(cpa);
        return d;
    }

    /**
     * Entfernung vom aktuellen Punkt B zur Kursline A
     *
     * @return Entferung in sm
     */
    public double getEntfernungKurslinie () {
        Punkt2D p = a.getSchnittpunkt(b2);
        double d = b2.getEntfernung(p);
        return d;
    }

    /**
     * Kurs A
     *
     * @return Kurs A rechtweisend
     */
    public int getKAa () {
        return (int) a.getWinkelRW();
    }

    /**
     * Wahrer Kurs B
     *
     * @return wahren Kurs B
     */
    public Double getKB () {
        return b1.getWinkelRW();
    }

    /**
     * relativer Kurs B
     *
     * @return relativer Kurs B rechtweisend
     */
    public Double getKBr () {
        return b2.getWinkelRW();
    }

    /**
     * Groesste Distanz zwischen den Peilungen auf die naechste ganze Zahl
     * gerundet
     *
     * @return groesste Distanz
     */
    public int getMaxDistanz () {
        return (int) Math.max(Math.ceil(eDistanz1), Math.ceil(eDistanz2));
    }

    /**
     * Peilung zum CPA
     *
     * @return rechtweisende Peilung zum CPA
     */
    public Double getPCPA () {
        Punkt2D p = a.getCPAOrt(b2);
        Vektor2D v = new Vektor2D(new Punkt2D(0.0, 0.0), p);
        return v.getWinkelRechtweisendNord();
    }

    /**
     * Seitenpeilung aktueller Punkt zum Zeitpunkt x + Intervall
     *
     * @return Seitenpeilung
     */
    public Double getRASP2 () {
        double angle = new Vektor2D(new Punkt2D(0, 0), b2.ap)
                .getWinkelRechtweisendNord();
        angle = 360d - a.getWinkelRW() + angle;
        return korrigiereWinkel(angle);
    }

    /**
     * Seitenpeilung CPA
     *
     * @return Seitenpeilung zum CPA
     */
    public Double getSCPA () {
        double pcpa = getPCPA();
        double scpa = korrigiereWinkel(pcpa - eEigKurs);
        return scpa;
    }

    /**
     * Dauer bis zum CPA. Negativ: CPA wurde bereits passiert, B entfernt sich
     *
     * @return Dauer bis CPA (in Minuten)
     */
    public Double getTCPA () {
        Punkt2D p = a.getCPAOrt(b2);
        return b2.getDauer(p);
    }

    public String getTag () {
        return tag;
    }

    /**
     * Kurs A
     *
     * @return Kurs A
     */
    public double geteEigKurs () {
        return eEigKurs;
    }

    /**
     * Relative Geschwindigkeit B
     *
     * @return relaitve Geschwindigkeit B (in Knoten)
     */
    public Double getvB () {
        return b1.getGeschwindigkeitRounded();
    }

    /**
     * Relative Geschwindigkeit B
     *
     * @return relative Geschwindigkeit B in Knoten
     */
    public Double getvBr () {
        return b2.getGeschwindigkeitRounded();
    }

    /**
     * Initialisiert Kurslinien
     */
    private final void init () {
        // wahre Position a bei erster Peilung
        pos1_a = new Punkt2D(-(eEigFahrt / (60 / eIntervall) * Math
                .sin(Math.toRadians(eEigKurs))),
                -(eEigFahrt / (60 / eIntervall) * Math
                        .cos(Math.toRadians(eEigKurs))));
        // wahre Position a bei zweiter Peilung
        pos2_a = new Punkt2D(0.0, 0.0);
        // Position b bei erster Peilung
        pos0_b = pos1_a.mitWinkel(eDistanz1, rasp1);
        // wahre Position b bei erster Peilung
        pos1_b = pos2_a.mitWinkel(eDistanz1, rasp1);
        // Position b bei zweiter Peilung
        pos2_b = pos2_a.mitWinkel(eDistanz2, rasp2);
        berechneKurslinien(eIntervall);
    }

    /**
     * Ermittelt einen neuen Kurs, um eine neue Lage aufgrund eines gewuenschten
     * CPA zu erreichen.
     *
     * @param newcpa der gewuenschte neue CPA
     * @return der einzuschlagende Kurs fuer den neuen CPA.
     *
     * @throws IllegalArgumentException wenn der gewuenschte CPA durch keine
     * denkbare Kursaenderung erreichbar ist.
     */
    public Double newCPA (double newcpa) throws IllegalArgumentException {
        // Ein gewunschter CPA kann durch eine Kursaenderung A erreicht werden.
        // Daher wird aufgrund des newcpa der neue Kurs berechnet, und
        // zurueckgeliefert.
        // Ist der CPA nicht erreichbar, wird null geliefert.
        Punkt2D aktA = a.ap;
        Punkt2D aktB = b2.ap;
        // Aufspannen eines Kreises ueber dem relativen Kurs von B. Mittelpunkt
        // ist der Punkt zwischen dem aktuellen Punkt B und A. Radius ist die
        // Entfernung zwischen den Punkten /2.
        // Mittelpunkt ermitteln
        double x = (aktA.getX() + aktB.getX()) / 2;
        double y = (aktA.getY() + aktB.getY()) / 2;
        Punkt2D mittelpunkt = new Punkt2D(x, y);
        // Radius ist Abstand der Punkte vom Mittelpunkt
        double abstandA = Math.abs(mittelpunkt.abstand(aktA));
        // Kreis aufspannen
        Kreis2D k1 = new Kreis2D(mittelpunkt, abstandA);
        // Kreis um neuen CPA aufspannen
        Kreis2D k2 = new Kreis2D(aktA, newcpa);
        // Schnittpunkte berechnen
        Punkt2D[] erg = k2.schnittpunkt(k1);
        if (erg == null) {
            Log.d(LOGTAG,
                    "Fehler bei Lage.newCPA(double newcpa), Parameter: " + newcpa);
            throw new IllegalArgumentException(
                    "Keine CPA-Berechnung moeglich. CPA nicht erreichbar!.");
        }
        double newKursA, delta1 = 0, delta2 = 0;
        // Neuer Relativkurs B
        double kursB = b2.getWinkelRW();
        Vektor2D v = new Vektor2D(b2.getAp(), erg[0]);
        double delta = v.getWinkelRechtweisendNord() - kursB;
        if (delta < 0) {
            delta1 = delta;
        } else {
            delta2 = delta;
        }
        v = new Vektor2D(b2.getAp(), erg[1]);
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

    public void setFahrtaenderung (double neueGeschwindigkeit) {
        // Bei einer Fahrtreduzierung knicken alle Relativbewegungen
        // vorlicher ab.
        // Bei einer Beschleunigung knicken sie so ab, dass sie achterlicher
        // erscheinen
    }

    public void setTag (String tag) {
        this.tag = tag;
    }
}
