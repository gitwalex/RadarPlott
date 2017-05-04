/**
 *
 */
package de.aw.radarplott.interfaces;

/**
 * @author Alexander Winkler
 */
public interface LageChangeListener {
    /**
     * Called upon a change of the current value.
     * Parameters
     *
     * @param senderId
     *         The SenderId associated with this listener.
     * @param manoeverkurs
     *         The new manoeverkurs.
     */
    void onLageChange(int senderId, float manoeverkurs);
}