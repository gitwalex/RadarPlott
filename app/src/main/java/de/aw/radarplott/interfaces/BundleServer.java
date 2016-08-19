package de.aw.radarplott.interfaces;

import android.os.Bundle;

/**
 * Server, der bei Aufruf das aktuelle lagebundle liefert.
 * 
 * @author Alexander Winkler
 * 
 */
public interface BundleServer {
	
	/**
	 * Aktuelles Bundle
	 * 
	 * @return das aktuelle lagebundle
	 */
	Bundle getBundle();
}
