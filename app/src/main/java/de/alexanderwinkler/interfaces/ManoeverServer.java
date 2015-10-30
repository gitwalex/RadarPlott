package de.alexanderwinkler.interfaces;

import de.alexanderwinkler.main.ActivityRadarPlottErgebnis.ManoeverListener;

/**
 * @author Alexander Winkler
 * 
 */
public interface ManoeverServer extends BundleServer {
	/**
	 * @param b
	 * @return
	 */
	public boolean addManoeverListener(ManoeverListener b);
	
	/**
	 * @param b
	 * @return
	 */
	public boolean removeManoeverListener(ManoeverListener b);
	
}
