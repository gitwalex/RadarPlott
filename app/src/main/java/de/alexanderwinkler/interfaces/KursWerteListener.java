package de.alexanderwinkler.interfaces;

import java.util.EventListener;

import de.alexanderwinkler.main.ActivityRadarPlottEingabe.EigenerKursListener;

/**
 * 
 * Interface zur Versorgung von Werten zum Aktuellen Kurs
 * 
 * @author Alexander Winkler
 */
public interface KursWerteListener extends EventListener {
	
	/**
	 * Aktueller Eigener Kurs, wie er eingegeben wurde
	 * 
	 * @return eigenen Kurs. -1, wenn noch kein Kurs eingegeben wurde
	 */
	public double getAktKurs();
	
	/**
	 * Wird bei Eingaben eines eigenen Kurses aufgerufen
	 * 
	 * @param kurs
	 */
	public void onKursWertChanged(double kurs);
	
	/**
	 * Registriert einen EigenenKursChangeListener
	 * 
	 * @param k
	 *            EigenerKursChangeListener
	 * @return true, wenn Funktion erfolgreich war
	 */
	public boolean addEigenerKursChangedListener(EigenerKursListener k);
	
	/**
	 * De-Registriert einen EigenenKursChangeListener
	 * 
	 * @param k
	 *            EigenerKursChangeListener
	 * @return true, wenn Funktion erfolgreich war
	 */
	public boolean removeEigenerKursChangedListener(EigenerKursListener k);
}
