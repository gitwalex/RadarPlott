/**
 * 
 */
package de.alexanderwinkler.interfaces;

/**
 * @author Alexander Winkler
 * 
 */

public interface LageChangeListener {
	/**
	 * Called upon a change of the current value.
	 * 
	 * Parameters
	 * 
	 * @param senderId
	 *            The SenderId associated with this listener.
	 * @param manoeverkurs
	 *            The new manoeverkurs.
	 */
	public void onLageChange(int senderId, double manoeverkurs);
}