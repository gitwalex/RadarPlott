package de.alexanderwinkler.berechnungen;

/**
 * Created by alex on 16.08.2016.
 */
public class Radarseitenpeilung {
    private final int radarSeitenPeilung;
    private final float distanz;

    public Radarseitenpeilung(int radarSeitenPeilung, float distanz) {
        while (radarSeitenPeilung > 360) {
            radarSeitenPeilung -= 360;
        }
        this.radarSeitenPeilung = radarSeitenPeilung;
        this.distanz = distanz;
    }

    public float getDistanz() {
        return distanz;
    }

    public int getRadarSeitenPeilung() {
        return radarSeitenPeilung;
    }
}
