package de.alexanderwinkler.interfaces;

import de.alexanderwinkler.main.ActivityRadarPlottErgebnis.FragmentChangeListener;

/**
 * FragmentIdServer. Liefert Informationen, wenn die FragmentId geaendert wird.
 * Ist der Fall, wenn ein neuer Tab gewaehlt wird. Interessierte Klassen koennen
 * sich als FragmentChangeListener registrieren
 * 
 * @author Alexander Winkler
 * 
 */
public interface FragmentIdServer {
	
	public String getFragmentIdAktuell();
	
	/**
	 * Registrieren als FragmentChangeListener
	 * 
	 * @param fragmentChangeListener
	 *            Klasse, die das Interface RadarChangedListener implementiert
	 *            hat
	 * @return true, wenn Registrierung erfolgreich
	 */
	public boolean addFragmentChangeListener(
			FragmentChangeListener fragmentChangeListener);
	
	/**
	 * De-Registrieren als FragmentChangeListener
	 * 
	 * @param fragmentChangeListener
	 *            Klasse, die das Interface RadarChangedListener implementiert
	 *            hat
	 * @return true, wenn De-Registrierung erfolgreich
	 */
	public boolean removeFragmentChangeListener(
			FragmentChangeListener fragmentChangeListener);
}
