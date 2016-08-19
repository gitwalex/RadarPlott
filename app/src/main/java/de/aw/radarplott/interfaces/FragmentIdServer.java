package de.aw.radarplott.interfaces;

import de.aw.radarplott.main.ActivityRadarPlottErgebnis.FragmentChangeListener;

/**
 * FragmentIdServer. Liefert Informationen, wenn die FragmentId geaendert wird.
 * Ist der Fall, wenn ein neuer Tab gewaehlt wird. Interessierte Klassen koennen
 * sich als FragmentChangeListener registrieren
 * 
 * @author Alexander Winkler
 * 
 */
public interface FragmentIdServer {
	
	/**
	 * Registrieren als FragmentChangeListener
	 *
	 * @param fragmentChangeListener
	 *            Klasse, die das Interface RadarChangedListener implementiert
	 *            hat
	 * @return true, wenn Registrierung erfolgreich
	 */
	boolean addFragmentChangeListener(FragmentChangeListener fragmentChangeListener);

	String getFragmentIdAktuell();
	
	/**
	 * De-Registrieren als FragmentChangeListener
	 * 
	 * @param fragmentChangeListener
	 *            Klasse, die das Interface RadarChangedListener implementiert
	 *            hat
	 * @return true, wenn De-Registrierung erfolgreich
	 */
	boolean removeFragmentChangeListener(FragmentChangeListener fragmentChangeListener);
}
