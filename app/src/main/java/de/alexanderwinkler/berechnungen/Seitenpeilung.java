package de.alexanderwinkler.berechnungen;

/**
 * Created by alex on 16.08.2016.
 */
public class Seitenpeilung extends Radarseitenpeilung {
    private final int seitenPeilung;
    private final int anliegenderKurs;

    public Seitenpeilung(int seitenPeilung, int anliegenderKurs, float distanz) {
        super(seitenPeilung + anliegenderKurs, distanz);
        this.seitenPeilung = seitenPeilung;
        this.anliegenderKurs = anliegenderKurs;
    }

    public int getAnliegenderKurs() {
        return anliegenderKurs;
    }

    public int getSeitenPeilung() {
        return seitenPeilung;
    }
}
