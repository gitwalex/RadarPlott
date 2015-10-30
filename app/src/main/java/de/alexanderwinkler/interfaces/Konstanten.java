package de.alexanderwinkler.interfaces;

import android.graphics.Color;
import de.alexanderwinkler.R;

public interface Konstanten {
	
	/**
	 * 
	 */
	final String Version = "2.0";
	/**
	 * 
	 */
	final String ERGEBNISACTIVITY = "RADAR_PLOTT_ERGEBNIS";
	/**
	 * ID's der Eingabefelder. Werden zum Initialisieren und pruefen benutzt.
	 */
	final int[] eingabewerteGesamtIDs = { R.id.eEigKurs, R.id.eEigFahrt,
			R.id.eIntervall, R.id.eSeitenpeilung1, R.id.anliegenderKurs1,
			R.id.eRadarSeitenpeilung1, R.id.eDistanz1, R.id.eSeitenpeilung2,
			R.id.anliegenderKurs2, R.id.eRadarSeitenpeilung2, R.id.eDistanz2 };
	/**
	 * ID's der Eingabefelder eigenes Schiff. Werden zum Initialisieren und
	 * pruefen benutzt.
	 */
	final int[] eingabewerteEigSchifIDs = { R.id.eEigKurs, R.id.eEigFahrt,
			R.id.eIntervall };
	/**
	 * ID's der Eingabefelder Gegener. Werden zum Initialisieren und pruefen
	 * benutzt.
	 */
	final int[] eingabewerteGegnerIDs = { R.id.eSeitenpeilung1,
			R.id.anliegenderKurs1, R.id.eRadarSeitenpeilung1, R.id.eDistanz1,
			R.id.eSeitenpeilung2, R.id.anliegenderKurs2,
			R.id.eRadarSeitenpeilung2, R.id.eDistanz2 };
	/**
	 * Logtag.
	 */
	final String LOGTAG = "de.alexanderwinkler";
	/**
	 * DebugMode einschalten? Derzeit wirkunglos...
	 */
	final boolean debugMode = true;
	
	// Konstanten zum Zeichnen
	/**
	 * 
	 */
	final int zeichnelagefett = 5;
	/**
	 * 
	 */
	final int zeichnelagenormal = 1;
	/**
	 * 
	 */
	final int colorA = Color.GREEN;
	/**
	 * 
	 */
	final int colorB1 = Color.BLUE;
	/**
	 * 
	 */
	final int colorB2 = Color.RED;
	/**
	 * 
	 */
	final int colorCPA = R.color.Brown;
	/**
	 * 
	 */
	final int colorRadarLinien = Color.LTGRAY;
	/**
	 * 
	 */
	final int colorRadarBackground = Color.WHITE;
	
	// Konstanten fuer Preferencs
	/**
	 * 
	 */
	final String PREF_RADARKREISE = "pref_1";
	/**
	 * 
	 */
	final String PREF_RADARORIENTIERUNG = "pref_2";
	
	// Konstanten fuer Tabs
	/**
	 * 
	 */
	final String KEYGEGNERB = TabHostKonstanten.TAB1;
	/**
	 * 
	 */
	final String KEYGEGNERC = TabHostKonstanten.TAB2;
	/**
	 * 
	 */
	final String KEYGEGNERD = TabHostKonstanten.TAB3;
	
	// Konstanten fur Zugriff auf Bundle
	/**
	 * 
	 */
	final String KEYLAGE = "KEYLAGE";
	/**
	 * 
	 */
	final String KEYBUNDLE = "LAGEBUNDLE";
	/**
	 * 
	 */
	final String KEYMANOEVER = "MANOEVER";
	/**
	 * Key fuer FragmentId der Fragmente, die mehrmals benutzt werden
	 */
	final String KEYFRAGMENTID = "frag_Id";
	/**
	 * Key fuer FragmentId des TabHost fuer die Eingabe von Daten
	 */
	final String KEYTABEINGABEFRAGMENTID = "frag_TabHost";
	
	/**
	 * Key fuer FragmentId des Fragments zur Eingabe der Daten des eigenen
	 * Schiffes.
	 * 
	 */
	final String KEYFRAGMENTEIGSCHIFF = "frag_eigSchiff";
	
	// Konstanten fuer Sender Manoever
	/**
	 * 
	 */
	final int SENDEROnTouch = -1;
	/**
	 * 
	 */
	final int SENDERLageView = 1;
	
	// Sonstige Konstanten
	/**
	 * 
	 */
	final String KEYEINGABEOK = "1";
	/**
	 * 
	 */
	final String KEYMANOEVERMINUTEN = "2";
	/**
	 * 
	 */
	final String KEYMANOEVERKURSA = "3";
	/**
	 * 
	 */
	final String KEYMANOEVERCPA = "4";
	/**
	 * 
	 */
	final String KEYDUALPANE = "5";
	/**
	 * 
	 */
	final String KEYEINGABE = "6";
	/**
	 * 
	 */
	final String KEYISINITIALIZED = "7";
	
	/**
	 * Key fuer retten Anzahl Tabs.
	 */
	final String KEYNUMBEROFTABS = "numberOfTabs";
}
