package de.aw.radarplott.interfaces;

import de.aw.radarplott.main.ActivityRadarPlottErgebnis.ManoeverListener;

/**
 * @author Alexander Winkler
 * 
 */
public interface ManoeverServer extends BundleServer {
	/**
	 * @param b
	 * @return
	 */
	boolean addManoeverListener(ManoeverListener b);
	
	/**
	 * @param b
	 * @return
	 */
	boolean removeManoeverListener(ManoeverListener b);
	
}
