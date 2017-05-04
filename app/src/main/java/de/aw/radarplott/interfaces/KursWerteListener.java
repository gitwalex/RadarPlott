package de.aw.radarplott.interfaces;

import java.util.EventListener;

import de.aw.radarplott.main.ActivityRadarPlottEingabe.EigenerKursListener;

/**
 * Interface zur Versorgung von Werten zum Aktuellen Kurs
 *
 * @author Alexander Winkler
 */
public interface KursWerteListener extends EventListener {
    /**
     * Registriert einen EigenenKursChangeListener
     *
     * @param k
     *         EigenerKursChangeListener
     *
     * @return true, wenn Funktion erfolgreich war
     */
    boolean addEigenerKursChangedListener(EigenerKursListener k);

    /**
     * Aktueller Eigener Kurs, wie er eingegeben wurde
     *
     * @return eigenen Kurs. -1, wenn noch kein Kurs eingegeben wurde
     */
    float getAktKurs();

    /**
     * Wird bei Eingaben eines eigenen Kurses aufgerufen
     *
     * @param kurs
     */
    void onKursWertChanged(float kurs);

    /**
     * De-Registriert einen EigenenKursChangeListener
     *
     * @param k
     *         EigenerKursChangeListener
     *
     * @return true, wenn Funktion erfolgreich war
     */
    boolean removeEigenerKursChangedListener(EigenerKursListener k);
}
